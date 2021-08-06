package com.itheamc.hamroclassroom_teachers.handlers;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.os.HandlerCompat;
import androidx.fragment.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.itheamc.hamroclassroom_teachers.R;
import com.itheamc.hamroclassroom_teachers.callbacks.LoginCallbacks;
import com.itheamc.hamroclassroom_teachers.utils.NotifyUtils;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginHandler {
    private static final String TAG = "LoginHandler";
    private static LoginHandler instance;
    private final FirebaseAuth mAuth;
    private final LoginManager loginManager;
    private final Fragment fragment;
    private final LoginCallbacks loginCallbacks;
    private final ExecutorService executorService;
    private final Handler handler;
    private final ActivityResultLauncher<Intent> signInActivityResultLauncher;


    // Constructor
    protected LoginHandler(@NonNull Fragment fragment, FirebaseAuth mAuth, LoginCallbacks loginCallbacks, ActivityResultLauncher<Intent> signInActivityResultLauncher) {
        this.fragment = fragment;
        this.mAuth = mAuth;
        this.loginCallbacks = loginCallbacks;
        this.loginManager = LoginManager.getInstance();
        this.executorService = Executors.newFixedThreadPool(4);
        this.handler = HandlerCompat.createAsync(Looper.getMainLooper());
        this.signInActivityResultLauncher = signInActivityResultLauncher;
    }

    // Function to create and return the instance of the LoginHandler Object
    public static LoginHandler getInstance(@NonNull Fragment fragment, FirebaseAuth mAuth, LoginCallbacks loginCallbacks, ActivityResultLauncher<Intent> signInActivityResultLauncher) {
        if (instance == null) {
            instance = new LoginHandler(fragment, mAuth, loginCallbacks, signInActivityResultLauncher);
        }

        return instance;
    }

    /**
     * ____________________________CHECK USER LOGIN STATUS_______________________
     * This function will checked whether user already logged in or not
     */
    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }


    /**
     * _____________________FACEBOOK LOGIN HANDLING FUNCTIONS________________________
     * These are functions that will handle the facebook login
     */

    // Function to handle facebook login
    public void fbLoginHandler(@NonNull CallbackManager callbackManager) {
        loginManager.logInWithReadPermissions(fragment, Arrays.asList("email", "public_profile"));
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                NotifyUtils.logDebug(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                notifyLoginFailure("fbLoginHandler - onError --You cancelled the login");
            }

            @Override
            public void onError(FacebookException error) {
                notifyLoginFailure("fbLoginHandler - onError --" + error.getMessage());
            }
        });
    }

    // Handle Facebook Access Token
    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(executorService, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            NotifyUtils.logDebug(TAG, "handleFacebookAccessToken() -> onComplete - signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            if (user != null) {
                                notifyLoginSuccess(user);
                            } else {
                                notifyLoginFailure("handleFacebookAccessToken() -> onUserNull --User not found" );
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            notifyLoginFailure("handleFacebookAccessToken() -> signInWithCredential:failure --" + task.getException().getMessage());

                        }

                    }
                });
    }


    /**
     * ___________________GOOGLE SIGN IN HANDLER_________________________________
     * These are the functions that will handle the user login
     * using the google
     */

    public void googleLoginHandler() {
        if (fragment.getActivity() == null || fragment.getContext() == null) {
            return;
        }
        GoogleSignInOptions signInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(fragment.getActivity().getResources().getString(R.string.web_client_id))
                .build();

        // Build a GoogleSignInClient with the options specified by gso.

        GoogleSignInClient signInClient = GoogleSignIn.getClient(fragment.getContext(), signInOptions);
        Intent signInIntent = signInClient.getSignInIntent();
        signInActivityResultLauncher.launch(signInIntent);
    }

    public void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(executorService, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            NotifyUtils.logDebug(TAG, "firebaseAuthWithGoogle() -> signInWithCredential:onComplete");
                            FirebaseUser user = task.getResult().getUser();
                            if (user != null) {
                                notifyLoginSuccess(user);
                            } else {
                                notifyLoginFailure("firebaseAuthWithGoogle() -> onUserNull --User not found" );
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            notifyLoginFailure("firebaseAuthWithGoogle() -> signInWithCredential:failure --" + task.getException().getMessage());

                        }
                    }
                });
    }



    /**
     * _____________________________________SIGN OUT HANDLER_______________________
     * This function will handle the sign out
     */
    // Function to handle sign out
    private void signOutNow() {
        mAuth.signOut();
    }


    /**
     * Function to notify login success
     */
    private void notifyLoginSuccess(@NonNull FirebaseUser user) {
        handler.post(() -> {
            loginCallbacks.onLoginSuccess(user);
        });
    }

    /**
     * Function to notify login failure
     */
    private void notifyLoginFailure(@NonNull String error_message) {
        handler.post(() -> {
            loginCallbacks.onLoginFailure(error_message);
        });
    }
}
