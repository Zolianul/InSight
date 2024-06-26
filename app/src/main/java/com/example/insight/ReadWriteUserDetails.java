package com.example.insight;
//done
public class ReadWriteUserDetails {
    public String dateOfBirth,gender, phoneNumber;

    public ReadWriteUserDetails(){}

    public ReadWriteUserDetails(String text_dateOfBirth, String text_Gender, String text_PhoneNumber){
        this.dateOfBirth= text_dateOfBirth;
        this.gender= text_Gender;
        this.phoneNumber = text_PhoneNumber;
    }
}
