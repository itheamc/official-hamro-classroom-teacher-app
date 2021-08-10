package com.itheamc.hamroclassroom_teachers.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputLayout;
import com.itheamc.hamroclassroom_teachers.R;
import com.itheamc.hamroclassroom_teachers.adapters.AssignmentAdapter;
import com.itheamc.hamroclassroom_teachers.callbacks.AssignmentCallbacks;
import com.itheamc.hamroclassroom_teachers.callbacks.FirestoreCallbacks;
import com.itheamc.hamroclassroom_teachers.databinding.AddLinkBottomSheetBinding;
import com.itheamc.hamroclassroom_teachers.databinding.FragmentAssignmentsBinding;
import com.itheamc.hamroclassroom_teachers.databinding.UpdateTitleBinding;
import com.itheamc.hamroclassroom_teachers.handlers.FirestoreHandler;
import com.itheamc.hamroclassroom_teachers.models.Assignment;
import com.itheamc.hamroclassroom_teachers.models.Notice;
import com.itheamc.hamroclassroom_teachers.models.School;
import com.itheamc.hamroclassroom_teachers.models.Student;
import com.itheamc.hamroclassroom_teachers.models.Submission;
import com.itheamc.hamroclassroom_teachers.models.Subject;
import com.itheamc.hamroclassroom_teachers.models.User;
import com.itheamc.hamroclassroom_teachers.utils.NotifyUtils;
import com.itheamc.hamroclassroom_teachers.utils.ViewUtils;
import com.itheamc.hamroclassroom_teachers.viewmodels.MainViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AssignmentsFragment extends Fragment implements AssignmentCallbacks, FirestoreCallbacks  {
    private static final String TAG = "AssignmentsFragment";
    private FragmentAssignmentsBinding assignmentsBinding;
    private NavController navController;
    private AssignmentAdapter assignmentAdapter;
    private MainViewModel viewModel;
    private Subject subject;

    /*
   For Bottom Sheet -- Schools list
    */
    private BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior;
    private UpdateTitleBinding bottomSheetBinding;
    private TextInputLayout updatedTitleInputLayout;
    private EditText updateTitleEditText;
    private Button updateTitleButton;


    public AssignmentsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        assignmentsBinding = FragmentAssignmentsBinding.inflate(inflater, container, false);
        return assignmentsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
        Initializing NavController and MainViewModel
         */
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        /*
        Initializing Bottom Sheet and its views
         */
        ConstraintLayout bottomSheetLayout = (ConstraintLayout) assignmentsBinding.titleUpdateBottomSheetLayout.findViewById(R.id.updateTitleBottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBinding = UpdateTitleBinding.bind(bottomSheetLayout);
        updatedTitleInputLayout = bottomSheetBinding.updateTitleInputLayout;
        updateTitleButton = bottomSheetBinding.updateTitleButton;
        updateTitleEditText = updatedTitleInputLayout.getEditText();

        updateTitleButton.setOnClickListener(v -> updateAssignment());


        assignmentAdapter = new AssignmentAdapter(this);
        assignmentsBinding.assignmentsRecyclerView.setAdapter(assignmentAdapter);

        // Setting subject value from the ViewModel
        subject = viewModel.getSubject();

        /*
        Setting OnRefreshListener on the swipe-refresh layout
         */
        assignmentsBinding.swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.overlay);
        assignmentsBinding.swipeRefreshLayout.setOnRefreshListener(this::getAssignments);

        /*
        Getting assignments from cloud
         */
        getAssignments();
    }

    /*
    Function to get subjects
     */
    private void getAssignments() {
        if (subject != null) {
            FirestoreHandler.getInstance(this).getAssignments(subject.get_id());
            ViewUtils.handleProgressBar(assignmentsBinding.progressBarContainer);
        }
    }

    /*
   Function to update assignment title
    */
    private void updateAssignment() {
        String subId = null;
        String assignId = null;
        String updatedTitle = updateTitleEditText.getText().toString();
        if (viewModel.getSubject() != null) subId = viewModel.getSubject().get_id();
        if (viewModel.getAssignment() != null) assignId = viewModel.getAssignment().get_id();

        if (subId == null || assignId == null || TextUtils.isEmpty(updatedTitle)) {
            if (getContext() != null) NotifyUtils.showToast(getContext(), getString(R.string.went_wrong_message));
            return;
        }

        if (updatedTitle.equals(viewModel.getAssignment().get_title())) {
            if (getContext() != null) NotifyUtils.showToast(getContext(), "You haven't make any change on title");
            return;
        }

        FirestoreHandler.getInstance(this).updateAssignmentTitle(assignId, updatedTitle);
        ViewUtils.handleProgressBar(bottomSheetBinding.progressBarContainer);
        ViewUtils.disableViews(updatedTitleInputLayout, updateTitleButton);

    }


    /**
     * -------------------------------------------------------------------
     * These are the methods implemented from the AssignmentViewCallbacks
     * -------------------------------------------------------------------
     */
    @Override
    public void onSubmissionsClick(int _position) {
        setAssignment(_position);
        navController.navigate(R.id.action_assignmentsFragment_to_submissionsFragment);
    }

    @Override
    public void onClick(int _position) {
        setAssignment(_position);
        navController.navigate(R.id.action_assignmentsFragment_to_submissionsFragment);
    }

    @Override
    public void onLongClick(int _position) {
        setAssignment(_position);
        ViewUtils.handleBottomSheet(bottomSheetBehavior);
        if (updateTitleEditText != null && viewModel.getAssignment() != null)
            updateTitleEditText.setText(viewModel.getAssignment().get_title());
    }

    // Custom function to set assignment in ViewModel
    private void setAssignment(int _position) {
        Assignment assignment = null;
        if (viewModel.getAssignments() != null)
            assignment = viewModel.getAssignments().get(_position);
        if (assignment != null) viewModel.setAssignment(assignment);
    }

    /**
     * -------------------------------------------------------------------
     * These are the methods implemented from the FirestoreCallbacks
     * -------------------------------------------------------------------
     */
    @Override
    public void onSuccess(User user, List<School> schools, List<Student> students, List<Subject> subjects, List<Assignment> assignments, List<Submission> submissions, List<Notice> notices) {
        if (assignmentsBinding == null) return;

        if (assignments != null) {
            ViewUtils.handleProgressBar(assignmentsBinding.progressBarContainer);
            ViewUtils.handleRefreshing(assignmentsBinding.swipeRefreshLayout);
            if (assignments.size() == 0) {
                ViewUtils.handleNoItemFound(assignmentsBinding.noItemFoundLayout);
                return;
            }
            viewModel.setAssignments(assignments);
            assignmentAdapter.submitList(assignments);
            return;
        }

        ViewUtils.handleProgressBar(bottomSheetBinding.progressBarContainer);
        ViewUtils.enableViews(updatedTitleInputLayout, updateTitleButton);
        viewModel.getAssignment().set_title(updateTitleEditText.getText().toString());
        if (getContext() != null) NotifyUtils.showToast(getContext(), "Updated Successfully");
        ViewUtils.handleBottomSheet(bottomSheetBehavior);
        getAssignments();
    }

    @Override
    public void onFailure(Exception e) {
        if (assignmentsBinding == null) return;
        ViewUtils.hideProgressBar(assignmentsBinding.progressBarContainer);
        ViewUtils.handleRefreshing(assignmentsBinding.swipeRefreshLayout);
        ViewUtils.hideProgressBar(bottomSheetBinding.progressBarContainer);
        if (getContext() != null)
            NotifyUtils.showToast(getContext(), getString(R.string.went_wrong_message));
        NotifyUtils.logError(TAG, "onFailure: ", e);
    }


    /**
     * FUnction overrided to handle the action menu
     *
     * @param menu     --
     * @param inflater --
     */

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_assignment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_menu) {
            navController.navigate(R.id.action_assignmentsFragment_to_assignmentFragment);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /*
    Overrided method to handle view destroy
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        assignmentsBinding = null;
    }

}