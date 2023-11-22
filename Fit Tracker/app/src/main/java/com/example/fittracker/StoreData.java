package com.example.fittracker;

public class StoreData {

    String username, gender, weight, height, dateOfBirth, emailId, password;

    public StoreData() {

    }

    public StoreData(String username, String gender, String weight, String height, String dateOfBirth, String emailId, String password) {
        this.username = username;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
        this.dateOfBirth = dateOfBirth;
        this.emailId = emailId;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
