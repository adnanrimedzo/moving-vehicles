package com.tba.movingvehicles.service;

import com.tba.movingvehicles.model.Vehicle;

import com.tba.movingvehicles.model.Direction;
import com.tba.movingvehicles.model.Vehicles;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class VehicleService {

    int caseNumber;

    List<Vehicle> vehicleList = null;
    List<Future> runnigVehicles = null;
    ExecutorService executor = null;
    Connection connection = null;
    Queue queue = null;
    MessageProducer producer = null;
    Session session = null;

    public VehicleService() throws JMSException {
        vehicleList = new ArrayList<Vehicle>(0);
        runnigVehicles = new ArrayList<Future>(0);
        executor = Executors.newCachedThreadPool();

        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory
                ("tcp://localhost:61616");
        connection = factory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        queue = session.createQueue("test_queue");
        producer = session.createProducer(queue);
    }


    public void listCases() throws JMSException {
        System.out.print(
                "1. Add a vehicle\n" +
                        "2. Change vehicle direction\n" +
                        "3. Animate vehicles\n");
        getCaseNumber();
    }

    private Enum<Direction> getDirection() {
        System.out.print("Please select direction for the vehicle with number between 1 and 4; UP(1), DOWN(2), LEFT(3), RIGHT(4) \n");
        String input = null;
        int number;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            input = bufferedReader.readLine();
            number = Integer.parseInt(input);
            if (number < 1 || number > 4)
                throw new NumberFormatException();

            switch (number) {
                case 1:
                    return Direction.UP;
                case 2:
                    return Direction.DOWN;
                case 3:
                    return Direction.LEFT;
                case 4:
                    return Direction.RIGHT;
            }

        } catch (NumberFormatException ex) {
            System.out.println("Not valid a number !");
            getDirection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Direction.UP;
    }

    private void getCaseNumber() throws JMSException {
        System.out.println("Please select a case between 1 and 3: ");
        String input = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            input = bufferedReader.readLine();
            caseNumber = Integer.parseInt(input);
            if (caseNumber < 1 || caseNumber > 3)
                throw new NumberFormatException();

            runCase();
        } catch (NumberFormatException ex) {
            System.out.println("Not valid a number !");
            getCaseNumber();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getVehicleNumber() {
        System.out.println("Please select vehicle between 1 and " + vehicleList.size() + ": ");
        String input = null;
        int number = 1;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            input = bufferedReader.readLine();
            number = Integer.parseInt(input);
            if (number < 1 || number > vehicleList.size())
                throw new NumberFormatException();

            return number;
        } catch (NumberFormatException ex) {
            System.out.println("Not valid a number !");
            getVehicleNumber();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return number;
    }

    private void runCase() throws JMSException {
        switch (caseNumber) {
            case 1:
                useCase1();
                break;
            case 2:
                useCase2();
                break;
            case 3:
                useCase3();
                break;
            default:
                useCase1();
                break;
        }
        getCaseNumber();
    }

    public void useCase1() {
        listVehicles();
        Vehicle vehicle = addVehicle(getDirection());
        System.out.println("Vehicle has been added with number: " + vehicle.getVehicleNumber() + " direction: " + vehicle.getDirection());
    }

    public void useCase2() throws JMSException {
        listVehicles();
        int number = getVehicleNumber();
        Enum direction=getDirection();
        TextMessage message = session.createTextMessage("");
        producer.send(message);
        System.out.println("Vehicle's direction with number: " + vehicle.getVehicleNumber() + " changed to " + vehicle.getDirection());
    }

    public void useCase3() {
        AnimatedVehicles(vehicleList);
    }

    private Vehicle addVehicle(Enum<Direction> direction) {

        Vehicle vehicle = new Vehicle(vehicleList.size() + 1);
        vehicle.setDirection(direction);

        vehicleList.add(vehicle);
        //runnigVehicles.add(future);

        return vehicle;
    }

    private void listVehicles() {

        if (vehicleList.isEmpty()) {
            System.out.println("There is no vehicle currently!");
            return;
        }

        vehicleList.forEach(v -> {
            System.out.println("Vehicle number: " + v.getVehicleNumber() + " and direction: " + v.getDirection());
        });
    }


    public void AnimatedVehicles(List<Vehicle> vehicleList) {
        this.vehicleList = vehicleList;
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException |
                        IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }

                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(new Vehicles(vehicleList, executor));
                frame.setSize(800, 800);
                frame.setVisible(true);
            }
        });
    }

    @Override
    public void finalize() {
        connection.stop();
        executor.shutdownNow();
    }

}
