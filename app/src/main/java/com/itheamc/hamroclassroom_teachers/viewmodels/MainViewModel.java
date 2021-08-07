package com.itheamc.hamroclassroom_teachers.viewmodels;

import androidx.lifecycle.ViewModel;

import com.itheamc.hamroclassroom_teachers.models.Assignment;
import com.itheamc.hamroclassroom_teachers.models.Notice;
import com.itheamc.hamroclassroom_teachers.models.School;
import com.itheamc.hamroclassroom_teachers.models.Student;
import com.itheamc.hamroclassroom_teachers.models.Subject;
import com.itheamc.hamroclassroom_teachers.models.Submission;
import com.itheamc.hamroclassroom_teachers.models.Student;
import com.itheamc.hamroclassroom_teachers.models.User;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {
    /*
    Objects
     */
    private User user;
    private Student student;
    private Subject subject;
    private Assignment assignment;
    private Submission submission;
    private School school;

    /*
    Lists
     */
    private List<School> schools;
    private List<Student> students;
    private List<Subject> subjects;
    private List<Assignment> assignments;
    private List<Submission> submissions;
    private List<Notice> notices;

    /*
   Boolean
    */
    private boolean isSubjectUpdating;


    /*
    Getters and Setters
     */
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public List<School> getSchools() {
        return schools;
    }

    public void setSchools(List<School> schools) {
        this.schools = schools;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }

    public List<Notice> getNotices() {
        return notices;
    }

    public void setNotices(List<Notice> notices) {
        this.notices = notices;
    }

    public boolean isSubjectUpdating() {
        return isSubjectUpdating;
    }

    public void setSubjectUpdating(boolean subjectUpdating) {
        isSubjectUpdating = subjectUpdating;
    }

    /*
            Function to update subject in List<Subject> subjects
             */
    public void modifySubjectItems(Subject _subject) {
        List<Subject> subjectList = new ArrayList<>();
        for (Subject sub: subjects) {
            if (sub.get_id().equals(_subject.get_id())) {
                subjectList.add(_subject);
                continue;
            }
            subjectList.add(sub);
        }

        this.subjects = new ArrayList<>();
        this.subjects = subjectList;
    }
}
