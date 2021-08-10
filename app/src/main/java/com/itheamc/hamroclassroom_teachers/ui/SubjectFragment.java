package com.itheamc.hamroclassroom_teachers.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.itheamc.hamroclassroom_teachers.R;
import com.itheamc.hamroclassroom_teachers.adapters.SchoolAdapter;
import com.itheamc.hamroclassroom_teachers.callbacks.FirestoreCallbacks;
import com.itheamc.hamroclassroom_teachers.callbacks.SchoolCallbacks;
import com.itheamc.hamroclassroom_teachers.databinding.FragmentSubjectBinding;
import com.itheamc.hamroclassroom_teachers.databinding.SchoolBottomSheetBinding;
import com.itheamc.hamroclassroom_teachers.handlers.FirestoreHandler;
import com.itheamc.hamroclassroom_teachers.models.Assignment;
import com.itheamc.hamroclassroom_teachers.models.Notice;
import com.itheamc.hamroclassroom_teachers.models.School;
import com.itheamc.hamroclassroom_teachers.models.Student;
import com.itheamc.hamroclassroom_teachers.models.Submission;
import com.itheamc.hamroclassroom_teachers.models.Subject;
import com.itheamc.hamroclassroom_teachers.models.User;
import com.itheamc.hamroclassroom_teachers.utils.NotifyUtils;
import com.itheamc.hamroclassroom_teachers.utils.IdGenerator;
import com.itheamc.hamroclassroom_teachers.utils.TimePickers;
import com.itheamc.hamroclassroom_teachers.utils.ViewUtils;
import com.itheamc.hamroclassroom_teachers.viewmodels.MainViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SubjectFragment extends Fragment implements FirestoreCallbacks, SchoolCallbacks, View.OnClickListener {
    private static final String TAG = "SubjectFragment";
    private FragmentSubjectBinding subjectBinding;
    private NavController navController;
    private MainViewModel viewModel;

    /*
    For Bottom Sheet -- Schools list
   */
    private BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior;
    private SchoolBottomSheetBinding bottomSheetBinding;
    private SchoolAdapter schoolAdapter;

    /*
    TextInput Layouts
     */
    private TextInputLayout subjectInputLayout;
    private TextInputLayout classInputLayout;
    private TextInputLayout schoolInputLayout;
    private TextInputLayout classTimeInputLayout;

    /*
    EditTexts
     */
    EditText subEditText;
    EditText classEditText;
    EditText schoolEditText;
    EditText timeEditText;

    /*
    Buttons
     */
    private Button addEditBtn;

    /*
    Strings
     */
    private String _subject = null;
    private String _class = null;
    private String _school = null;
    private String _time = null;

    /*
    User and School
     */
    private User user = null;
    private School school;


    // Constructor
    public SubjectFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        subjectBinding = FragmentSubjectBinding.inflate(inflater, container, false);
        return subjectBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing NavController and MainViewModel
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        /*
        Initializing TextInput Layouts
         */
        subjectInputLayout = subjectBinding.subjectInputLayout;
        classInputLayout = subjectBinding.classInputLayout;
        schoolInputLayout = subjectBinding.schoolInputLayout;
        classTimeInputLayout = subjectBinding.timeInputLayout;

        /*
        Initializing EditTexts
         */
        subEditText = subjectInputLayout.getEditText();
        classEditText = classInputLayout.getEditText();
        schoolEditText = schoolInputLayout.getEditText();
        timeEditText = classTimeInputLayout.getEditText();

        /*
        Initializing Buttons
         */
        addEditBtn = subjectBinding.addEditButton;

        /*
        Updating edit texts if it is going to update
         */
        processingEditTexts();

         /*
        Initializing Bottom Sheet and its views
         */
        ConstraintLayout bottomSheetLayout = (ConstraintLayout) subjectBinding.bottomSheetCoordinatorLayout.findViewById(R.id.schoolBottomSheetLayout);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBinding = SchoolBottomSheetBinding.bind(bottomSheetLayout);

        schoolAdapter = new SchoolAdapter(this);
        bottomSheetBinding.schoolRecyclerView.setAdapter(schoolAdapter);


        /*
        Setting onClickListener and OnFocusChangeListener on views
         */
        addEditBtn.setOnClickListener(this);

        schoolInputLayout.setOnClickListener(this);
        classTimeInputLayout.setOnClickListener(this);
        schoolEditText.setOnClickListener(this);
        timeEditText.setOnClickListener(this);


    }


    /**
     * --------------------------------------------------------------------------
     * Method implemented from View.OnClickListener
     * @param v - it represents the view that is being clicked
     */
    @Override
    public void onClick(View v) {
        int _id = v.getId();
        if (_id == schoolInputLayout.getId() || v == schoolInputLayout.getEditText()) handleBottomSheet();
        else if (_id == addEditBtn.getId()) {
            handleAddNow();
        } else if (_id == classTimeInputLayout.getId() || v == classTimeInputLayout.getEditText()) {
            DialogFragment newFragment = new TimePickers(subjectBinding);
            if (getActivity() != null) newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
        } else {
            NotifyUtils.logDebug(TAG, "Unspecified view is clicked!!");
        }
    }


    /*
    ---------------------------------------
    Function to set the known value in the edit text to make changes
     */
    private void processingEditTexts() {
        if (!viewModel.isSubjectUpdating()) return;

        Subject subject = viewModel.getSubject();
        if (subject == null) return;

        if (subEditText != null) subEditText.setText(subject.get_name());
        if (classEditText != null) classEditText.setText(subject.get_class());
        if (schoolEditText != null) schoolEditText.setText(subject.get_school().get_name());
        if (timeEditText != null) timeEditText.setText(subject.get_start_time());
        addEditBtn.setText(getString(R.string.update));
    }


    /*
    ---------------------------------------
    Function to handle the subject creation
     */
    private void handleAddNow() {
        if (!isInputsValid()) {
            if (getContext() != null) NotifyUtils.showToast(getContext(), "Please fill all the details!!");
            return;
        }

        if (!viewModel.isSubjectUpdating()) {
//            if (getActivity() != null) user = LocalStorage.getInstance(getActivity()).getUser();

            if (user == null) {
                FirestoreHandler.getInstance(this).getUser(FirebaseAuth.getInstance().getUid());
                return;
            }
        }

        addSubject();
    }

    /*
    --------------------------------------
    Finally adding subject to cloud firestore
     */
    private void addSubject() {
        if (!viewModel.isSubjectUpdating()) {
            Subject subject = new Subject(
                    IdGenerator.generateRandomId(),
                    _subject,
                    _class,
                    user.get_id(),
                    null,
                    school.get_id(),
                    null,
                    "",
                    _time,
                    new Date(),
                    0,
                    false,
                    false
            );

            FirestoreHandler.getInstance(this).addSubject(subject);
            ViewUtils.handleProgressBar(subjectBinding.subjectProgressBarContainer);
            return;
        }

        // If updating
        Subject subject = viewModel.getSubject();

        Map<String, Object> data = new HashMap<>();
        String _name = subEditText.getText().toString().trim();
        String _class = classEditText.getText().toString().trim();
        String _time = timeEditText.getText().toString().trim();

        if (!TextUtils.isEmpty(_name) && !_name.equals(subject.get_name())) data.put("_name", _name);
        if (!TextUtils.isEmpty(_class) && !_class.equals(subject.get_class())) data.put("_class", _class);
        if (school != null) data.put("_school", Arrays.asList(this.school.get_id(), this.school.get_name()));
        if (!TextUtils.isEmpty(_time) && !_time.equals(subject.get_start_time())) data.put("_start_time", _time);

        if (data.isEmpty()) {
            if (getContext() != null) NotifyUtils.showToast(getContext(), "You have not make any changes");
            return;
        }

        FirestoreHandler.getInstance(this).updateSubject(subject.get_id(), data);
        ViewUtils.handleProgressBar(subjectBinding.subjectProgressBarContainer);
    }


    /*
    --------------------------------------
    Function to verify inputs
     */
    private boolean isInputsValid() {
        // Setting the value to the string variables
        if (subEditText != null) _subject = subEditText.getText().toString().trim();
        if (classEditText != null) _class = classEditText.getText().toString().trim();
        if (schoolEditText != null) _school = schoolEditText.getText().toString().trim();
        if (timeEditText != null) _time = timeEditText.getText().toString().trim();

        return !TextUtils.isEmpty(_subject) &&
                !TextUtils.isEmpty(_class) &&
                !TextUtils.isEmpty(_school) &&
                !TextUtils.isEmpty(_time);

    }

    /**
     * -----------------------------------------------------------------------
     * Function to show or hide bottomsheet
     */
    private void handleBottomSheet() {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            if (viewModel.getSchools() != null) {
                schoolAdapter.submitList(viewModel.getSchools());
                return;
            }
            if (bottomSheetBinding.progressBarContainer.getVisibility() == View.GONE) bottomSheetBinding.progressBarContainer.setVisibility(View.VISIBLE);
            FirestoreHandler.getInstance(this).getSchools();
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }


    /**
     * --------------------------------------------------------------------------
     * Method implemented from SchoolViewCallbacks
     * @param _position - clicked item position in a list
     */
    @Override
    public void onClick(int _position) {
        this.school = viewModel.getSchools().get(_position);
        EditText timeEdittext = subjectBinding.schoolInputLayout.getEditText();
        if (timeEdittext != null) timeEdittext.setText(this.school.get_name());
        handleBottomSheet();
    }


    /**
     * -------------------------------------------------------------------
     * These are the methods implemented from the FirestoreCallbacks
     * -------------------------------------------------------------------
     */
    @Override
    public void onSuccess(User user, List<School> schools, List<Student> students, List<Subject> subjects, List<Assignment> assignments, List<Submission> submissions, List<Notice> notices) {
        if (subjectBinding == null) return;

        if (schools != null) {
            schoolAdapter.submitList(schools);
            viewModel.setSchools(schools);
            ViewUtils.hideProgressBar(bottomSheetBinding.progressBarContainer);
            return;
        }
        if (user != null) {
//            if (getActivity() != null) LocalStorage.getInstance(getActivity()).storeUser(user);
            this.user = user;
            viewModel.setUser(user);
            addSubject();
            return;
        }

        ViewUtils.hideProgressBar(subjectBinding.subjectProgressBarContainer);
        ViewUtils.clearEditTexts(subEditText, classEditText, schoolEditText, timeEditText);   // Calling function to clear the EditTexts after adding
        if (!viewModel.isSubjectUpdating()) {
            NotifyUtils.showToast(getContext(), "Added Successfully");
        }
        else  {
            NotifyUtils.showToast(getContext(), "Updated Successfully");
            viewModel.setSubjectUpdating(false);
            viewModel.setSubject(null);
            addEditBtn.setText(getString(R.string.add_now));
        }
    }

    @Override
    public void onFailure(Exception e) {
        if (subjectBinding == null) return;
        ViewUtils.hideProgressBar(subjectBinding.subjectProgressBarContainer);
        NotifyUtils.showToast(getContext(), getString(R.string.went_wrong_message));
        NotifyUtils.logDebug(TAG, "onUserInfoRetrievedError: - " + e.getMessage());
    }



     /*
    Override method to handle view destroy
     */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subjectBinding = null;
    }

}