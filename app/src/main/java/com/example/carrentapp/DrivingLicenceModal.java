package com.example.carrentapp;

import java.io.Serializable;
import java.util.HashMap;

public class DrivingLicenceModal implements Serializable {
    public HashMap<String, String> Images;
    public String User, DriverName, DriverLicenceNo;

    public DrivingLicenceModal(String User, String driverName, String driverLicenceNo, HashMap<String, String> images) {
        this.User = User;
        DriverName = driverName;
        DriverLicenceNo = driverLicenceNo;
        Images = images;
    }

    public DrivingLicenceModal() {
    }
}
