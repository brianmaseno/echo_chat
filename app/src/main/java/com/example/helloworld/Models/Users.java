package com.example.helloworld.Models;

public class Users {
    public String fullname;
    public String email;
    public String phonenumber;


    public Users(String fullname, String email, String phonenumber) {
        this.fullname = fullname;
        this.email = email;
        this.phonenumber = phonenumber;
    }

    public Users() {

    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
}
