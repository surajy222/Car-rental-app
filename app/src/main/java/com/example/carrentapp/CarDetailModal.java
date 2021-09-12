package com.example.carrentapp;

import java.io.Serializable;
import java.util.HashMap;

public class CarDetailModal implements Serializable {
    public String CarBrand, ModelName, NoOfPassengers, CarAverage, FuelType, NoOfAirbags, TotalLuggageBags, CarRate, ACOptions;
    public HashMap<String, String> Images;

    public CarDetailModal(String carBrand, String modelName, String noOfPassengers, String carAverage, String fuelType, String noOfAirbags, String totalLuggageBags, String carRate, String ACOptions, HashMap<String, String> images) {
        CarBrand = carBrand;
        ModelName = modelName;
        NoOfPassengers = noOfPassengers;
        CarAverage = carAverage;
        FuelType = fuelType;
        NoOfAirbags = noOfAirbags;
        TotalLuggageBags = totalLuggageBags;
        CarRate = carRate;
        this.ACOptions = ACOptions;
        Images = images;
    }

    public CarDetailModal() {
    }


}
