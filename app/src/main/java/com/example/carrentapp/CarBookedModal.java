package com.example.carrentapp;

import java.io.Serializable;

public class CarBookedModal implements Serializable {
    public String User, CarId, FromDate, ToDate, FromTime, ToTime, FromStop, ToStop;

    public CarBookedModal(String user, String carId, String fromDate, String toDate, String fromTime, String toTime, String fromStop, String toStop) {
        User = user;
        CarId = carId;
        FromDate = fromDate;
        ToDate = toDate;
        FromTime = fromTime;
        ToTime = toTime;
        FromStop = fromStop;
        ToStop = toStop;
    }

    public CarBookedModal() {
    }
}
