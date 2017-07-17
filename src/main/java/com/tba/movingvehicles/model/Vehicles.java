package com.tba.movingvehicles.model;


import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.*;

public class Vehicles extends JPanel {

    public Vehicles(List<Vehicle> vehicleList, ExecutorService executor) {
        setLayout(null);
        vehicleList.forEach(v -> {
            Future future = executor.submit(v.getVehicleCallable());
            add(v);
        });
    }
}