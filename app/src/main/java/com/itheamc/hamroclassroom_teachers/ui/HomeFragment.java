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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.itheamc.hamroclassroom_teachers.R;
import com.itheamc.hamroclassroom_teachers.adapters.SubjectAdapter;
import com.itheamc.hamroclassroom_teachers.callbacks.FirestoreCallbacks;
import com.itheamc.hamroclassroom_teachers.callbacks.SubjectCallbacks;
import com.itheamc.hamroclassroom_teachers.databinding.AddLinkBottomSheetBinding;
import com.itheamc.hamroclassroom_teachers.databinding.FragmentHomeBinding;
import com.itheamc.hamroclassroom_teachers.handlers.FirestoreHandler;
import com.itheamc.hamroclassroom_teachers.models.Assignment;
import com.itheamc.hamroclassroom_teachers.models.Notice;
import com.itheamc.hamroclassroom_teachers.models.School;
import com.itheamc.hamroclassroom_teachers.models.Student;
import com.itheamc.hamroclassroom_teachers.models.Subject;
import com.itheamc.hamroclassroom_teachers.models.Submission;
import com.itheamc.hamroclassroom_teachers.models.User;
import com.itheamc.hamroclassroom_teachers.utils.LocalStorage;
import com.itheamc.hamroclassroom_teachers.utils.NotifyUtils;
import com.itheamc.hamroclassroom_teachers.utils.ViewUtils;
import com.itheamc.hamroclassroom_teachers.viewmodels.MainViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;



public class HomeFragment extends Fragment implements SubjectCallbacks, FirestoreCallbacks, View.OnClickListener {
    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding homeBinding;
    private NavController navController;
    private MainViewModel viewModel;
    private SubjectAdapter subjectAdapter;
    private Subject subject;

    /*
   For Bottom Sheet -- Schools list
  */
    private BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior;
    private AddLinkBottomSheetBinding bottomSheetBinding;
    private TextInputLayout linkInputLayout;
    private Button updateLinkButton;

    /*
    Floating Action Button
     */
    private ExtendedFloatingActionButton addSubject;
    /*
    Strings
     */
    private String _link = null;


    // Constructor
    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        return homeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing NavController and MainViewModel
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

         /*
        Initializing Bottom Sheet and its views
         */
        ConstraintLayout bottomSheetLayout = (ConstraintLayout) homeBinding.linkBottomSheetCoordinatorLayout.findViewById(R.id.linkBottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBinding = AddLinkBottomSheetBinding.bind(bottomSheetLayout);
        linkInputLayout = bottomSheetBinding.linkInputLayout;
        updateLinkButton = bottomSheetBinding.updateLinkButton;


        subjectAdapter = new SubjectAdapter(this);
        homeBinding.homeRecyclerView.setAdapter(subjectAdapter);

        addSubject = homeBinding.addSubjectFloatingButton;

        /*
        Setting OnClickListener on views
         */
        updateLinkButton.setOnClickListener(this);
        addSubject.setOnClickListener(this);


        homeBinding.swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.overlay);
        /*
        Setting OnRefreshListener on the swipe-refresh layout
         */
        homeBinding.swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.setSubjects(null);
            handleSubjects();

        });

        /*
        Subjects handling
         */
        handleSubjects();

    }

    /*
    Function to handle subjects
     */
    private void handleSubjects() {
        if (viewModel.getSubjects() != null) {
            subjectAdapter.submitList(viewModel.getSubjects());
            return;
        }
        if (getActivity() == null) return;
        FirestoreHandler.getInstance(this).getSubjects(LocalStorage.getInstance(getActivity()).getUserId());
        ViewUtils.showProgressBar(homeBinding.progressBarContainer);
    }

    /**
     * ------------------------------------------------------------------------------
     * onClick() method overrided from the View.OnclickListener
     * @param v - view clicked
     */

    @Override
    public void onClick(View v) {
        int _id = v.getId();
        if (_id == updateLinkButton.getId()) {
            updateLink();
        } else if (_id == addSubject.getId()) {
            viewModel.setSubjectUpdating(false);
            viewModel.setSubject(null);
            navController.navigate(R.id.action_homeFragment_to_subjectFragment);
        }
    }


    /*
    Function to update link
     */
    private void updateLink() {
        EditText linkEdittext = linkInputLayout.getEditText();
        if (linkEdittext == null) return;

        _link = linkEdittext.getText().toString().trim();

        if (TextUtils.isEmpty(_link)) {
            if (getContext() != null) NotifyUtils.showToast(getContext(), "Paste your link here..");
            return;
        }

        FirestoreHandler.getInstance(this).updateLink(subject.get_id(), _link);
        ViewUtils.showProgressBar(bottomSheetBinding.progressBarContainer);

    }


    /**
     * ---------------------------------------------------------------------
     * These are the methods implemented from the SubjectViewCallbacks
     * @param _position - position of the item where clicked happen
     */
    @Override
    public void onClick(int _position) {

    }

    @Override
    public void onAddLinkClick(int _position) {
        subject = viewModel.getSubjects().get(_position);
        ViewUtils.handleBottomSheet(bottomSheetBehavior);
    }

    @Override
    public void onAssignmentsClick(int _position) {
        subject = viewModel.getSubjects().get(_position);
        viewModel.setSubject(subject);
        navController.navigate(R.id.action_homeFragment_to_assignmentsFragment);
    }

    @Override
    public void onLongClick(int _position) {
        viewModel.setSubjectUpdating(true);
        subject = viewModel.getSubjects().get(_position);
        viewModel.setSubject(subject);
        navController.navigate(R.id.action_homeFragment_to_subjectFragment);
    }

    /**
     * -------------------------------------------------------------------
     * These are the methods implemented from the FirestoreCallbacks
     * @param user - null
     * @param schools - null
     * @param subjects - Only subjects will be nonnull and used here
     * @param students - null
     * @param assignments - null
     * @param submissions - null
     */
    @Override
    public void onSuccess(User user, List<School> schools, List<Student> students, List<Subject> subjects, List<Assignment> assignments, List<Submission> submissions, List<Notice> notices) {
        if (homeBinding == null) return;

        if (subjects != null) {
            ViewUtils.hideProgressBar(homeBinding.progressBarContainer);
            ViewUtils.handleRefreshing(homeBinding.swipeRefreshLayout);

            if (subjects.size() == 0) {
                ViewUtils.handleNoItemFound(homeBinding.noItemFoundLayout);
                return;
            }
            subjectAdapter.submitList(subjects);
            viewModel.setSubjects(subjects);
            return;
        }

        ViewUtils.hideProgressBar(bottomSheetBinding.progressBarContainer);   // Disabling link update progress bar
        if (getContext() != null) NotifyUtils.showToast(getContext(), "Updated Successfully");
        linkInputLayout.getEditText().setText("");
    }

    @Override
    public void onFailure(Exception e) {
        if (homeBinding == null) return;

        ViewUtils.hideProgressBar(homeBinding.progressBarContainer);
        ViewUtils.handleRefreshing(homeBinding.swipeRefreshLayout);
        if (getContext() != null) NotifyUtils.showToast(getContext(), e.getMessage());
        NotifyUtils.logError(TAG, "onFailure: ", e);
    }


    // OnViewDestroy Function
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        homeBinding = null;
    }
}