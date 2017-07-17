package com.tba.movingvehicles;

import com.tba.movingvehicles.service.VehicleService;

import javax.jms.JMSException;

public class MainApplication {
    public static void main(String[] args) {
        VehicleService vehicleService = null;
        try {
            vehicleService = new VehicleService();
            vehicleService.listCases();
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
