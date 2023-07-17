package com.hein.entity;

import com.google.firebase.firestore.Exclude;

import java.util.HashMap;
import java.util.Map;

public  class UserProfile {

    public String dob;
    public String email;
    public String gender;
    public String job;
    public String name;
    public String phone;
    public String avatar;


    public UserProfile(String name,String email, String dob,String job, String phone,String gender,    String avatar) {
        this.dob = dob;
        this.email = email;
        this.gender = gender;
        this.job = job;
        this.name = name;
        this.phone = phone;
        this.avatar = avatar;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("email", email);
        result.put("dob", dob);
        result.put("job", job);
        result.put("phone", phone);
        result.put("gender", gender);
        result.put("avatar", avatar);

        return result;
    }
}
