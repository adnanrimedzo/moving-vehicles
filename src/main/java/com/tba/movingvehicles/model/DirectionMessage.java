package com.tba.movingvehicles.model;

import com.google.gson.Gson;

public class DirectionMessage {
    private int vehicleNumber;
    private String direction;

    public DirectionMessage() {
    }

    public int getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(int vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Enum getDirection() {

        return Direction.valueOf(direction);
    }

    public void setDirection(Enum direction) {
        this.direction = direction.toString();
    }

    public static String getJson(DirectionMessage directionMessage) {
        try {
            Gson gson = new Gson();
            return gson.toJson(directionMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static DirectionMessage getClassFromJson(String json) {
        try {
            Gson gson = new Gson();
            DirectionMessage directionMessage = gson.fromJson(json, DirectionMessage.class);
            return directionMessage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
