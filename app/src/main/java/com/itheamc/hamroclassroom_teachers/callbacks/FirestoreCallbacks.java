package com.itheamc.hamroclassroom_teachers.callbacks;

import com.itheamc.hamroclassroom_teachers.models.Assignment;
import com.itheamc.hamroclassroom_teachers.models.Notice;
import com.itheamc.hamroclassroom_teachers.models.School;
import com.itheamc.hamroclassroom_teachers.models.Student;
import com.itheamc.hamroclassroom_teachers.models.Subject;
import com.itheamc.hamroclassroom_teachers.models.Submission;
import com.itheamc.hamroclassroom_teachers.models.User;

import java.util.List;

public interface FirestoreCallbacks {
    void onSuccess(
            User user,
            Student student,
            School school,
            List<School> schools,
            List<Subject> subjects,
            List<Assignment> assignments,
            Submission submissions,
            List<Notice> notices);
    void onFailure(Exception e);
}
