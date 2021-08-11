package com.itheamc.hamroclassroom_teachers.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputLayout;
import com.itheamc.hamroclassroom_teachers.R;
import com.itheamc.hamroclassroom_teachers.adapters.SliderAdapter;
import com.itheamc.hamroclassroom_teachers.callbacks.FirestoreCallbacks;
import com.itheamc.hamroclassroom_teachers.databinding.FragmentSubmissionBinding;
import com.itheamc.hamroclassroom_teachers.handlers.FirestoreHandler;
import com.itheamc.hamroclassroom_teachers.models.Assignment;
import com.itheamc.hamroclassroom_teachers.models.Notice;
import com.itheamc.hamroclassroom_teachers.models.School;
import com.itheamc.hamroclassroom_teachers.models.Student;
import com.itheamc.hamroclassroom_teachers.models.Subject;
import com.itheamc.hamroclassroom_teachers.models.Submission;
import com.itheamc.hamroclassroom_teachers.models.User;
import com.itheamc.hamroclassroom_teachers.utils.NotifyUtils;
import com.itheamc.hamroclassroom_teachers.utils.ViewUtils;
import com.itheamc.hamroclassroom_teachers.viewmodels.MainViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmissionFragment extends Fragment implements FirestoreCallbacks {
    private static final String TAG = "SubmissionFragment";
    private FragmentSubmissionBinding submissionBinding;
    private NavController navController;
    private MainViewModel viewModel;
    private SliderAdapter sliderAdapter;

    /*
    TextInputLayout
     */
    private TextInputLayout commentInputLayout;

    /*
    EditText
     */
    private EditText commentEditText;


    // Constructor
    public SubmissionFragment() {
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
        submissionBinding = FragmentSubmissionBinding.inflate(inflater, container, false);
        return submissionBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing NavController and MainViewModel
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Initializing TextInputLayout and EditText
        commentInputLayout = submissionBinding.commentInputLayout;
        commentEditText = commentInputLayout.getEditText();

        // Initializing slider adapter...
        sliderAdapter = new SliderAdapter();

        submissionBinding.imageViewPager.setAdapter(sliderAdapter);

        Submission submission = viewModel.getSubmission();
        if (submission != null) {
            if (submission.get_images() != null) sliderAdapter.submitList(submission.get_images());
            submissionBinding.setSubmission(submission);
        }


        submissionBinding.finishCheckingButton.setOnClickListener(v -> updateSubmissionStatus());
    }


    /*
    Function to update the submission status
     */
    private void updateSubmissionStatus() {
        String _comment = null;
        if (commentEditText != null) _comment = commentEditText.getText().toString().trim();

        String _submissionId = viewModel.getSubmission().get_id();

        if (_submissionId == null) {
            if (getContext() != null) NotifyUtils.showToast(getContext(), getString(R.string.went_wrong_message));
            return;
        }

        Map<String, Object> _data = new HashMap<>();
        if (_comment != null && !TextUtils.isEmpty(_comment)) _data.put("_comment", _comment);
        _data.put("_checked", true);
        _data.put("_checked_date", new Date());

        FirestoreHandler.getInstance(this).updateSubmission(_submissionId, _data);
        ViewUtils.handleProgressBar(submissionBinding.progressBarContainer);
        ViewUtils.disableViews(submissionBinding.commentInputLayout, submissionBinding.finishCheckingButton);
    }


    /**
     * These are the methods implemented from the FirestoreCallbacks
     * @param user -
     * @param schools -
     * @param students -
     * @param subjects -
     * @param assignments -
     * @param submissions -
     * @param notices -
     */
    @Override
    public void onSuccess(User user, List<School> schools, List<Student> students, List<Subject> subjects, List<Assignment> assignments, List<Submission> submissions, List<Notice> notices) {
        if (submissionBinding == null) return;

        if (getContext() != null) NotifyUtils.showToast(getContext(), "Checked");
        ViewUtils.hideProgressBar(submissionBinding.progressBarContainer);
        ViewUtils.clearEditTexts(commentEditText);
    }

    @Override
    public void onFailure(Exception e) {
        if (submissionBinding == null) return;

        ViewUtils.hideProgressBar(submissionBinding.progressBarContainer);
        ViewUtils.enableViews(submissionBinding.commentInputLayout, submissionBinding.finishCheckingButton);
        if (getContext() != null) NotifyUtils.showToast(getContext(), getString(R.string.went_wrong_message));
    }


    // Overrided to manage the view destroy
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        submissionBinding = null;
    }


}