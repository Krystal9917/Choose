package com.info.choose.entity;

public class User {
    private String id;
    private String name;
    private String sex;
    private String major;
    private String grade;
    // 1 for teacher and 0 for student


    public User(String id, String name, String sex, String major, String grade) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.major = major;
        this.grade = grade;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

}

