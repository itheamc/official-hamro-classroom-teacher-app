package com.itheamc.hamroclassroom_teachers.handlers;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.os.HandlerCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.itheamc.hamroclassroom_teachers.callbacks.FirestoreCallbacks;
import com.itheamc.hamroclassroom_teachers.models.Assignment;
import com.itheamc.hamroclassroom_teachers.models.Notice;
import com.itheamc.hamroclassroom_teachers.models.School;
import com.itheamc.hamroclassroom_teachers.models.Student;
import com.itheamc.hamroclassroom_teachers.models.Subject;
import com.itheamc.hamroclassroom_teachers.models.Submission;
import com.itheamc.hamroclassroom_teachers.models.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FirestoreHandler {
    private final FirebaseFirestore firestore;
    private final Handler handler;
    private final FirestoreCallbacks callbacks;
    private final ExecutorService executorService;

    // Constructor
    public FirestoreHandler(@NonNull FirestoreCallbacks firestoreCallbacks) {
        this.callbacks = firestoreCallbacks;
        this.firestore = FirebaseFirestore.getInstance();
        this.handler = HandlerCompat.createAsync(Looper.getMainLooper());
        this.executorService = Executors.newFixedThreadPool(4);
    }

    // Getter for instance
    public static FirestoreHandler getInstance(@NonNull FirestoreCallbacks firestoreCallbacks) {
        return new FirestoreHandler(firestoreCallbacks);
    }

    /**
     * Function to get user info from the cloud Firestore
     * --------------------------------------------------------------------------------------
     */
    public void getUser(String userId) {
        firestore.collection("teachers")
                .document(userId)
                .get()
                .addOnSuccessListener(executorService, documentSnapshot -> {
                    if (documentSnapshot != null) {
                        User user = new User();
                        user = documentSnapshot.toObject(User.class);
                        notifyOnSuccess(user, null, null, null, null, null, null);
                    } else {
                        notifyOnFailure(new Exception("User not found"));
                    }

                })
                .addOnFailureListener(executorService, this::notifyOnFailure);
    }


    /**
     * Function to store user in the Firestore
     * --------------------------------------------------------------------------------------
     */
    public void storeUser(User user) {
        firestore.collection("teachers")
                .document(user.get_id())
                .set(user)
                .addOnSuccessListener(executorService, unused -> notifyOnSuccess(
                        user,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null))
                .addOnFailureListener(executorService, this::notifyOnFailure);
    }

    /**
     * Function to update user in the Firestore
     * --------------------------------------------------------------------------------------
     */
    public void updateUser(String _uid, Map<String, Object> data) {
        firestore.collection("teachers")
                .document(_uid)
                .update(data)
                .addOnSuccessListener(executorService, unused -> notifyOnSuccess(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null))
                .addOnFailureListener(executorService, this::notifyOnFailure);
    }


    /**
     * Function to get subjects list from the cloud firestore
     * --------------------------------------------------------------------------------------
     */
    public void getSubjects(String userId) {
        firestore.collection("subjects")
                .whereArrayContains("_teacher", userId)
                .get()
                .addOnSuccessListener(executorService, queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        List<Subject> subjects = queryDocumentSnapshots.toObjects(Subject.class);
                        notifyOnSuccess(null, null, null, subjects, null, null, null);
                    } else {
                        notifyOnFailure(new Exception("Subjects not found"));
                    }
                })
                .addOnFailureListener(executorService, this::notifyOnFailure);
    }

    /**
     * Function to add subject in the Firestore
     * --------------------------------------------------------------------------------------
     */
    public void addSubject(Subject subject) {
        firestore.collection("subjects")
                .document(subject.get_id())
                .set(subject)
                .addOnSuccessListener(executorService, unused -> notifyOnSuccess(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null))
                .addOnFailureListener(executorService, this::notifyOnFailure);
    }

    /**
     * Function to update subject in the Firestore
     * --------------------------------------------------------------------------------------
     */
    public void updateSubject(String _subjectId, Map<String, Object> data) {
        firestore.collection("subjects")
                .document(_subjectId)
                .update(data)
                .addOnSuccessListener(executorService, unused -> notifyOnSuccess(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null))
                .addOnFailureListener(executorService, this::notifyOnFailure);
    }

    /**
     * Function to update Meeting app link in subject
     * --------------------------------------------------------------------------------------
     */
    public void updateLink(String _sid, String _link) {
        firestore.collection("subjects")
                .document(_sid)
                .update("_join_url", _link)
                .addOnSuccessListener(executorService, unused -> notifyOnSuccess(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null))
                .addOnFailureListener(executorService, this::notifyOnFailure);
    }


    /**
     * Function to get assignments list from the cloud firestore
     * --------------------------------------------------------------------------------------
     */
    public void getAssignments(String subjectId) {
        firestore.collection("subjects")
                .document(subjectId)
                .collection("assignments")
                .get()
                .addOnSuccessListener(executorService, queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        List<Assignment> assignments = queryDocumentSnapshots.toObjects(Assignment.class);
                        notifyOnSuccess(null, null, null, null, assignments, null, null);
                    } else {
                        notifyOnFailure(new Exception("Assignments not found"));
                    }
                })
                .addOnFailureListener(executorService, this::notifyOnFailure);
    }


    /**
     * Function to add assignment in the Firestore
     * --------------------------------------------------------------------------------------
     */
    public void addAssignment(String subjectId, Assignment assignment) {
        firestore.collection("subjects")
                .document(subjectId)
                .collection("assignments")
                .document(assignment.get_id())
                .set(assignment)
                .addOnSuccessListener(executorService, unused -> notifyOnSuccess(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null))
                .addOnFailureListener(executorService, this::notifyOnFailure);
    }


    /**
     * Function to update assignment title
     * --------------------------------------------------------------------------------------
     */
    public void updateAssignmentTitle(String subjectId, String assignmentId, String updatedTitle) {
        firestore.collection("subjects")
                .document(subjectId)
                .collection("assignments")
                .document(assignmentId)
                .update("_title", updatedTitle)
                .addOnSuccessListener(executorService, unused -> notifyOnSuccess(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null))
                .addOnFailureListener(executorService, this::notifyOnFailure);
    }


    /**
     * Function to get submissions list from the cloud firestore
     * --------------------------------------------------------------------------------------
     */
    public void getSubmissions(String subjectId, String assignmentId) {
        firestore
                .collection("subjects")
                .document(subjectId)
                .collection("assignments")
                .document(assignmentId)
                .collection("submissions")
                .get()
                .addOnSuccessListener(executorService, queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        List<Submission> submissions = queryDocumentSnapshots.toObjects(Submission.class);
                        notifyOnSuccess(null, null, null, null, null, submissions, null);
                    } else {
                        notifyOnFailure(new Exception("No submissions arrived yet"));
                    }
                })
                .addOnFailureListener(executorService, this::notifyOnFailure);
    }



    /**
     * Function to update submission in the Firestore
     * --------------------------------------------------------------------------------------
     */
    public void updateSubmission(String subjectId, String assignmentId, String submissionId, Map<String, Object> data) {
        firestore.collection("subjects")
                .document(subjectId)
                .collection("assignments")
                .document(assignmentId)
                .collection("submissions")
                .document(submissionId)
                .update(data)
                .addOnSuccessListener(executorService, unused -> {
                    notifyOnSuccess(null, null, null, null, null, null, null);
                })
                .addOnFailureListener(executorService, this::notifyOnFailure);
    }



    /**
     * Function to get Student from the Firestore
     * --------------------------------------------------------------------------------------
     */
    public void getStudents(String studentId) {
        firestore.collection("students")
                .whereEqualTo("_id", studentId)
                .get()
                .addOnSuccessListener(executorService, queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        List<Student> students = queryDocumentSnapshots.toObjects(Student.class);
                        if (students.size() > 0) {
                            notifyOnSuccess(null, null, students, null, null, null, null);
                        } else {
                            notifyOnFailure(new Exception("Teacher not found"));
                        }
                    } else {
                        notifyOnFailure(new Exception("Teacher not found"));
                    }
                })
                .addOnFailureListener(executorService, this::notifyOnFailure);
    }

    /**
     * Function to get notifications list from the cloud firestore
     * --------------------------------------------------------------------------------------
     */
    public void getNotifications(String schoolId, String _class) {
        firestore.collection("notifications")
                .whereArrayContains("_school", schoolId)
                .whereEqualTo("_class", _class)
                .get()
                .addOnSuccessListener(executorService, queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        List<Notice> notices = queryDocumentSnapshots.toObjects(Notice.class);
                        notifyOnSuccess(null, null, null, null, null, null, notices);
                    } else {
                        notifyOnFailure(new Exception("Subjects not found"));
                    }
                })
                .addOnFailureListener(executorService, this::notifyOnFailure);
    }





    /**
     * Function to get schools list from the cloud firestore
     * --------------------------------------------------------------------------------------
     */
    public void getSchools() {
        firestore.collection("schools")
                .get()
                .addOnSuccessListener(executorService, queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        List<School> schools = queryDocumentSnapshots.toObjects(School.class);
                        notifyOnSuccess(null, schools, null, null, null, null, null);
                    } else {
                        notifyOnFailure(new Exception("Schools not found"));
                    }
                })
                .addOnFailureListener(executorService, this::notifyOnFailure);
    }

    /**
     * Function to get schools list from the cloud firestore
     * --------------------------------------------------------------------------------------
     */
    public void getSchool(String _schoolId) {
        firestore.collection("schools")
                .whereEqualTo("_id", _schoolId)
                .get()
                .addOnSuccessListener(executorService, queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        List<School> schools = queryDocumentSnapshots.toObjects(School.class);
                        if (schools.isEmpty()) {
                            notifyOnFailure(new Exception("Schools not found"));
                        } else {
                            notifyOnSuccess(null, schools, null, null, null, null, null);
                        }
                    } else {
                        notifyOnFailure(new Exception("Schools not found"));
                    }
                })
                .addOnFailureListener(executorService, this::notifyOnFailure);
    }


    /**
     * Function to notify whether getUser() is success or failure
     * --------------------------------------------------------------------------------------
     */
    private void notifyOnSuccess(User user,
                                 List<School> schools,
                                 List<Student> students,
                                 List<Subject> subjects,
                                 List<Assignment> assignments,
                                 List<Submission> submissions,
                                 List<Notice> notices) {
        handler.post(() -> {
            callbacks.onSuccess(user, schools, students, subjects, assignments, submissions, notices);
        });
    }

    private void notifyOnFailure(Exception e) {
        handler.post(() -> {
            callbacks.onFailure(e);
        });
    }
}
