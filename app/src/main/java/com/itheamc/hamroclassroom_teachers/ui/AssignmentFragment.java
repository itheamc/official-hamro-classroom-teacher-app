package com.itheamc.hamroclassroom_teachers.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.HandlerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.UploadTask;
import com.itheamc.hamroclassroom_teachers.R;
import com.itheamc.hamroclassroom_teachers.adapters.ImageAdapter;
import com.itheamc.hamroclassroom_teachers.callbacks.FirestoreCallbacks;
import com.itheamc.hamroclassroom_teachers.callbacks.ImageCallbacks;
import com.itheamc.hamroclassroom_teachers.callbacks.StorageCallbacks;
import com.itheamc.hamroclassroom_teachers.databinding.FragmentAssignmentBinding;
import com.itheamc.hamroclassroom_teachers.handlers.FirestoreHandler;
import com.itheamc.hamroclassroom_teachers.handlers.StorageHandler;
import com.itheamc.hamroclassroom_teachers.models.Assignment;
import com.itheamc.hamroclassroom_teachers.models.Notice;
import com.itheamc.hamroclassroom_teachers.models.School;
import com.itheamc.hamroclassroom_teachers.models.Student;
import com.itheamc.hamroclassroom_teachers.models.Subject;
import com.itheamc.hamroclassroom_teachers.models.Submission;
import com.itheamc.hamroclassroom_teachers.models.User;
import com.itheamc.hamroclassroom_teachers.utils.IdGenerator;
import com.itheamc.hamroclassroom_teachers.utils.ImageUtils;
import com.itheamc.hamroclassroom_teachers.utils.NotifyUtils;
import com.itheamc.hamroclassroom_teachers.utils.ViewUtils;
import com.itheamc.hamroclassroom_teachers.viewmodels.MainViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AssignmentFragment extends Fragment implements StorageCallbacks, FirestoreCallbacks, ImageCallbacks {
    private static final String TAG = "AssignmentFragment";
    private FragmentAssignmentBinding assignmentBinding;
    private NavController navController;
    private ActivityResultLauncher<Intent> imagePickerResultLauncher;
    private List<Uri> imagesUri;
    private ImageAdapter imageAdapter;
    private MainViewModel viewModel;

    /*
    Integer to store the uploaded image qty
     */
    int uploadCount = 0;
    /*
   List to store the uploaded image url
    */
    private List<String> imagesList;

    /*
    TextInputLayout
     */
    private TextInputLayout titleInputLayout;
    private TextInputLayout descInputLayout;

    /*
    EditTexts
     */
    private EditText titleEdittext;
    private EditText descEditText;

    /*
    Strings
     */
    private String _assignment_id;
    private String _title = "";
    private String _desc = "";

    /*
    Boolean
     */
    private boolean is_uploading = false;   //To handle the image remove


    // Constructor
    public AssignmentFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        assignmentBinding = FragmentAssignmentBinding.inflate(inflater, container, false);
        return assignmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing NavController and MainViewModel
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Initializing Image adapter and setting to the recycler view
        imageAdapter = new ImageAdapter(this);
        assignmentBinding.assignmentRecyclerView.setAdapter(imageAdapter);

        // Initializing InputLayout and Edittext
        titleInputLayout = assignmentBinding.titleInputLayout;
        descInputLayout = assignmentBinding.descInputLayout;

        titleEdittext = titleInputLayout.getEditText();
        descEditText = descInputLayout.getEditText();

        // Initializing imageList
        imagesList = new ArrayList<>();

        // Setting assignment Id
        _assignment_id = IdGenerator.generateId();

        // Activity Result launcher to listen the result of the multi image picker
        imagePickerResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (assignmentBinding == null) return;
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        if (data == null) return;

                        ViewUtils.hideViews(assignmentBinding.imagePickerButton);     // To hide the image picker
                        imagesUri = new ArrayList<>();

                        // Get the Images from data
                        ClipData mClipData = data.getClipData();
                        if (mClipData != null) {
                            int count = mClipData.getItemCount();
                            for (int i = 0; i < count; i++) {
                                // adding imageUri in array
                                Uri imageUri = mClipData.getItemAt(i).getUri();
                                imagesUri.add(imageUri);
                            }

                            submitImagesToImageAdapter();   // Submitting image to adapter
                            return;
                        }

                        Uri imageUri = data.getData();
                        imagesUri.add(imageUri);
                        submitImagesToImageAdapter();   // Submitting image to adapter

                    } else {
                        NotifyUtils.showToast(getContext(), "Unable to pick images");
                    }
                });

        /*
        Setting OnClickListener
         */
        assignmentBinding.imagePickerButton.setOnClickListener(v -> showImagePicker());
        assignmentBinding.addAssignmentButton.setOnClickListener(v -> {
            if (!isInputsValid()) {
                if (getContext() != null) NotifyUtils.showToast(getContext(), "Please enter title");
                return;
            }

            ViewUtils.showProgressBar(assignmentBinding.progressBarContainer);
            ViewUtils.disableViews(assignmentBinding.addAssignmentButton, titleInputLayout, descInputLayout);
            if (imagesUri == null || imagesUri.size() < 1) {
                storeOnFirestore();
                return;
            }

            handleImageUpload();
        });


    }


    /*
    Function to submit data to image adapter
     */
    private void submitImagesToImageAdapter() {
        if (imagesUri != null) imageAdapter.submitList(imagesUri);
    }


    /**
     * --------------------------------------------------------------------------
     * Function to handle image upload to cloud storage
     * It will be triggered continuously until all the images will be uploaded
     */
    private void handleImageUpload() {
        Assignment assignment = viewModel.getAssignment();
        Subject subject = viewModel.getSubject();
        Bitmap bitmap = ImageUtils.getInstance(getActivity()).getBitmap(imagesUri.get(uploadCount));
        if (bitmap != null) {
            if (!is_uploading) is_uploading = true;
            StorageHandler.getInstance(this)
                    .uploadImage(bitmap,
                            "image" + "-" + (uploadCount + 1) + ".jpg",
                            subject.get_id(),
                            _assignment_id);
        }
    }

    /**
     * --------------------------------------------------------------------------
     * Function to handle Firestore upload
     * It will bi triggered only after all the images uploaded
     */
    private void storeOnFirestore() {
        assignmentBinding.uploadedProgress.setText(R.string.finalizing_uploads);
        Subject subject = viewModel.getSubject();

        // Creating new assignment object
        Assignment assignment = new Assignment(
                _assignment_id,
                imagesList,
                new ArrayList<>(),
                _title,
                _desc,
                subject.get_id(),
                new Date(),
                new Date(),
                0
        );

        FirestoreHandler.getInstance(this)
                .addAssignment(subject.get_id(), assignment);
    }


    /**
     * ----------------------------------------------------------------------------
     * Function to display the progress update in textview while loading
     */
    private void updateUploadProgress(double progress) {
        if (assignmentBinding == null) return;

        String message = String.format(Locale.ENGLISH, "Uploading (%d/%d) Images", uploadCount + 1, imagesUri.size());
        HandlerCompat.createAsync(Looper.getMainLooper())
                .post(() -> {
                    assignmentBinding.uploadedProgress
                            .setText(message);
                });
    }

    /**
     * -----------------------------------------------------------------------------
     * Function to start the image picker intent
     */
    private void showImagePicker() {
        // initialising intent
        Intent intent = new Intent();

        // setting type to select to be image
        intent.setType("image/*");

        // allowing multiple image to be selected
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickerResultLauncher.launch(Intent.createChooser(intent, "Select Images"));
    }

    /**
     * -----------------------------------------------------------------------------
     * Function to verify inputs
     */
    private boolean isInputsValid() {
        if (titleEdittext != null) _title = titleEdittext.getText().toString().trim();
        if (descEditText != null) _desc = descEditText.getText().toString().trim();

        return !TextUtils.isEmpty(_title);
    }

    /**
     * ---------------------------------------------------------------------------
     * Function to make edittext clear
     */
    private void clearEdittext() {
        if (assignmentBinding == null) return;

        ViewUtils.clearEditTexts(titleEdittext, descEditText);
        if (imagesUri != null) {
            imagesUri.clear();
            imageAdapter.submitList(new ArrayList<>());
        }
        ViewUtils.visibleViews(assignmentBinding.imagePickerButton);    // To Show the image picker button
    }


    /**
     * -------------------------------------------------------------------------
     * These are the methods implemented from the StorageCallbacks
     * -------------------------------------------------------------------------
     */

    @Override
    public void onSuccess(String imageUrl) {
        if (imagesList != null) imagesList.add(imageUrl);
        uploadCount += 1;
        if (uploadCount < imagesUri.size()) {
            handleImageUpload();
            return;
        }

        // Storing to cloud Firestore
        storeOnFirestore();
    }

    @Override
    public void onFailure(Exception e) {
        if (assignmentBinding == null) return;

        if (getContext() != null) NotifyUtils.showToast(getContext(), "Upload Failed");
        is_uploading = false;
        ViewUtils.hideProgressBar(assignmentBinding.progressBarContainer);
        ViewUtils.enableViews(assignmentBinding.addAssignmentButton, titleInputLayout, descInputLayout);

    }

    @Override
    public void onCanceled() {
        if (assignmentBinding == null) return;

        if (getContext() != null) NotifyUtils.showToast(getContext(), "You canceled the upload!");
        is_uploading = false;
        ViewUtils.hideProgressBar(assignmentBinding.progressBarContainer);
    }

    @Override
    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
        if (assignmentBinding == null) return;
        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
        NotifyUtils.logDebug(TAG, "Upload is " + progress + "% done");
        updateUploadProgress(progress);
    }

    /**
     * -------------------------------------------------------------------
     * This method is implemented from the ImageViewCallback
     * -------------------------------------------------------------------
     */

    @Override
    public void onRemove(int _position) {
        if (is_uploading) return;
        imagesUri.remove(_position);
        imageAdapter.notifyItemRemoved(_position);
        NotifyUtils.logDebug(TAG, imagesUri.toString());
        if (imagesUri.size() == 0) ViewUtils.visibleViews(assignmentBinding.imagePickerButton);
    }

    /**
     * ------------------------------------------------------------------------------------------
     * This method is implemented from the FirestoreCallbacks
     * -  Due to the same name and same arguments onFailure(Exception e) there is a only one
     * on failure method for StorageCallbacks and FirestoreCallbacks
     * - If something went wrong above OnFailure(Exception e) will be triggered
     * ------------------------------------------------------------------------------------------
     */
    @Override
    public void onSuccess(User user, List<School> schools, List<Student> students, List<Subject> subjects, List<Assignment> assignments, List<Submission> submissions, List<Notice> notices) {
        if (assignmentBinding == null) return;

        ViewUtils.hideProgressBar(assignmentBinding.progressBarContainer);
        ViewUtils.enableViews(assignmentBinding.addAssignmentButton, titleInputLayout, descInputLayout);
        if (getContext() != null) NotifyUtils.showToast(getContext(), "Added Successfully");
        uploadCount = 0;
        is_uploading = false;
        clearEdittext();
    }

    /*
    Overrided function to view destroy
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        assignmentBinding = null;
    }
}