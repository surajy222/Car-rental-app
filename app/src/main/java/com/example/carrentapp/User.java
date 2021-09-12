package com.example.carrentapp;

public class User {
    public String Name, UserName, Email, PhoneNo, Password, State, City, Street, age, Postalcode;

    public User() {

    }

    public User(String Name, String UserName, String Email, String PhoneNo, String Password, String State, String City, String Street, String age, String Postalcode) {
        this.Name = Name;
        this.UserName = UserName;
        this.Email = Email;
        this.PhoneNo = PhoneNo;
        this.State = State;
        this.City = City;
        this.Street = Street;
        this.age = age;
        this.Postalcode = Postalcode;


    }
}
