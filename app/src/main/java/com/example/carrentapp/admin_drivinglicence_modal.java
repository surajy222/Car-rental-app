package com.example.carrentapp;

import java.io.Serializable;
import java.util.HashMap;

public class admin_drivinglicence_modal implements Serializable {
    public HashMap<String, String> Images;
    public String admin, DriverName, DriverLicenceNo,valid,carno;

    public admin_drivinglicence_modal(String admin, String driverName, String driverLicenceNo,String Valid, String Carno, HashMap<String, String> images) {
        this.admin = admin;
        DriverName = driverName;
        DriverLicenceNo = driverLicenceNo;
        valid = Valid;
        carno = Carno;
        Images = images;
    }

    public admin_drivinglicence_modal() {
    }
}