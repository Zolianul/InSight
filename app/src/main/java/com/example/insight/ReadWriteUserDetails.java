package com.example.insight;
//done
public class ReadWriteUserDetails {
    public String dateOfBirth,gender,phone;

    public ReadWriteUserDetails(){}

    public ReadWriteUserDetails(String textDob, String textGender, String textPhone){
        this.dateOfBirth=textDob;
        this.gender=textGender;
        this.phone=textPhone;
    }
}
