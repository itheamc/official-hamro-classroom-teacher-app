package com.itheamc.hamroclassroom_teachers.models;

import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.DiffUtil;

import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Student {
    private String _id;
    private String _name;
    private String _gender;
    private String _image;
    private String _phone;
    private String _email;
    private String _address;
    private String _guardian;
    private String _class;
    private String _section;
    private String _roll_number;
    private String _school_ref;
    private School _school;
    private List<String> _subjects_ref;
    private List<Subject> _subjects;
    private List<String> _submissions_ref;      // Consists the id of the assignment being submitted
    private List<Submission> _submissions;
    private Date _joined_on;

    // Constructor
    public Student() {
    }


    // Constructor with parameters
    public Student(String _id, String _name, String _gender, String _image, String _phone, String _email, String _address, String _guardian, String _class, String _section, String _roll_number, String _school_ref, School _school, List<String> _subjects_ref, List<Subject> _subjects, List<String> _submissions_ref, List<Submission> _submissions, Date _joined_on) {
        this._id = _id;
        this._name = _name;
        this._gender = _gender;
        this._image = _image;
        this._phone = _phone;
        this._email = _email;
        this._address = _address;
        this._guardian = _guardian;
        this._class = _class;
        this._section = _section;
        this._roll_number = _roll_number;
        this._school_ref = _school_ref;
        this._school = _school;
        this._subjects_ref = _subjects_ref;
        this._subjects = _subjects;
        this._submissions_ref = _submissions_ref;
        this._submissions = _submissions;
        this._joined_on = _joined_on;
    }

    // Getters and Setters
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_gender() {
        return _gender;
    }

    public void set_gender(String _gender) {
        this._gender = _gender;
    }

    public String get_image() {
        return _image;
    }

    public void set_image(String _image) {
        this._image = _image;
    }

    public String get_phone() {
        return _phone;
    }

    public void set_phone(String _phone) {
        this._phone = _phone;
    }

    public String get_email() {
        return _email;
    }

    public void set_email(String _email) {
        this._email = _email;
    }

    public String get_address() {
        return _address;
    }

    public void set_address(String _address) {
        this._address = _address;
    }

    public String get_guardian() {
        return _guardian;
    }

    public void set_guardian(String _guardian) {
        this._guardian = _guardian;
    }

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    public String get_section() {
        return _section;
    }

    public void set_section(String _section) {
        this._section = _section;
    }

    public String get_roll_number() {
        return _roll_number;
    }

    public void set_roll_number(String _roll_number) {
        this._roll_number = _roll_number;
    }

    public String get_school_ref() {
        return _school_ref;
    }

    public void set_school_ref(String _school_ref) {
        this._school_ref = _school_ref;
    }

    public School get_school() {
        return _school;
    }

    public void set_school(School _school) {
        this._school = _school;
    }

    public List<String> get_subjects_ref() {
        return _subjects_ref;
    }

    public void set_subjects_ref(List<String> _subjects_ref) {
        this._subjects_ref = _subjects_ref;
    }

    public List<Subject> get_subjects() {
        return _subjects;
    }

    public void set_subjects(List<Subject> _subjects) {
        this._subjects = _subjects;
    }

    public List<String> get_submissions_ref() {
        return _submissions_ref;
    }

    public void set_submissions_ref(List<String> _submissions_ref) {
        this._submissions_ref = _submissions_ref;
    }

    public List<Submission> get_submissions() {
        return _submissions;
    }

    public void set_submissions(List<Submission> _submissions) {
        this._submissions = _submissions;
    }

    public Date get_joined_on() {
        return _joined_on;
    }

    public void set_joined_on(Date _joined_on) {
        this._joined_on = _joined_on;
    }

    // Overriding toString() method
    @Override
    public String toString() {
        return "Student{" +
                "_id='" + _id + '\'' +
                ", _name='" + _name + '\'' +
                ", _gender='" + _gender + '\'' +
                ", _image='" + _image + '\'' +
                ", _phone='" + _phone + '\'' +
                ", _email='" + _email + '\'' +
                ", _address='" + _address + '\'' +
                ", _guardian='" + _guardian + '\'' +
                ", _class='" + _class + '\'' +
                ", _section='" + _section + '\'' +
                ", _roll_number='" + _roll_number + '\'' +
                ", _school_ref='" + _school_ref + '\'' +
                ", _school=" + _school +
                ", _subjects_ref=" + _subjects_ref +
                ", _subjects=" + _subjects +
                ", _submissions_ref=" + _submissions_ref +
                ", _submissions=" + _submissions +
                ", _joined_on=" + _joined_on +
                '}';
    }

    // equals() Method


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(get_id(), student.get_id()) &&
                Objects.equals(get_name(), student.get_name()) &&
                Objects.equals(get_gender(), student.get_gender()) &&
                Objects.equals(get_image(), student.get_image()) &&
                Objects.equals(get_phone(), student.get_phone()) &&
                Objects.equals(get_email(), student.get_email()) &&
                Objects.equals(get_address(), student.get_address()) &&
                Objects.equals(get_guardian(), student.get_guardian()) &&
                Objects.equals(get_class(), student.get_class()) &&
                Objects.equals(get_section(), student.get_section()) &&
                Objects.equals(get_roll_number(), student.get_roll_number()) &&
                Objects.equals(get_school_ref(), student.get_school_ref()) &&
                Objects.equals(get_school(), student.get_school()) &&
                Objects.equals(get_subjects_ref(), student.get_subjects_ref()) &&
                Objects.equals(get_subjects(), student.get_subjects()) &&
                Objects.equals(get_submissions_ref(), student.get_submissions_ref()) &&
                Objects.equals(get_submissions(), student.get_submissions()) &&
                Objects.equals(get_joined_on(), student.get_joined_on());
    }

    // DiffUtil.ItemCallbacks
    public static DiffUtil.ItemCallback<Student> studentItemCallback = new DiffUtil.ItemCallback<Student>() {
        @Override
        public boolean areItemsTheSame(@NonNull Student oldItem, @NonNull Student newItem) {
            return newItem.equals(oldItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Student oldItem, @NonNull Student newItem) {
            return false;
        }
    };


    // Binding Adapter
    @BindingAdapter("android:imageButtonSrc")
    public static void setImage(ImageButton imageButton, String imageUrl) {
        Picasso.get()
                .load(imageUrl)
                .into(imageButton);
    }
}
