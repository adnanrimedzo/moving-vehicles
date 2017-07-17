package com.tba.movingvehicles;

import com.tba.movingvehicles.service.VehicleService;

public class MainApplication {
    public static void main(String[] args) {
        VehicleService vehicleService = new VehicleService();
        vehicleService.listCases();
    }
}
