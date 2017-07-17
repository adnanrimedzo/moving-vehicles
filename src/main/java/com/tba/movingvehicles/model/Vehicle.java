package com.tba.movingvehicles.model;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.Session;
import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import java.util.concurrent.Callable;
import javax.jms.*;
import javax.jms.Queue;

public class Vehicle extends JPanel {
    private Color color;
    private Enum direction;
    private Callable vehicleCallable;
    private int vehicleNumber;

    private int diameter;
    private long delay;
    private int vx;
    private int vy;

    public Vehicle(int number) {
        setVehicleNumber(number);

        Random rand = new Random();
        setColor(new Color(rand.nextInt(0xFFFFFF)));

        setDirection(Direction.UP);

        setDiameter(30);
        setDelay(100);
        setVx(10 - (int) Math.round((Math.random() * 20)));
        setVy(10 - (int) Math.round((Math.random() * 20)));

        setVehicleCallable(new Callable() {
            @Override
            public Integer call() throws Exception {
                ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory
                        ("tcp://0.0.0.0:61616");

                Connection connection = factory.createConnection();

                connection.start();

                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Queue queue = session.createQueue("vehicleque");
                MessageConsumer consumer = session.createConsumer(queue);

                try {

                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            int x = (int) (Math.round(Math.random() * getParent().getWidth()));
                            int y = (int) (Math.round(Math.random() * getParent().getHeight()));

                            setLocation(x, y);
                        }
                    });
                } catch (InterruptedException exp) {
                    exp.printStackTrace();
                } catch (InvocationTargetException exp) {
                    exp.printStackTrace();
                }

                while (isVisible()) {

                    TextMessage message = (TextMessage) consumer.receive(500);

                    DirectionMessage directionMessage = null;

                    if(message !=null)
                    directionMessage = DirectionMessage.getClassFromJson(message.getText());
                    if (directionMessage != null && directionMessage.getVehicleNumber() == getVehicleNumber()) {
                        setDirection(directionMessage.getDirection());
                        System.out.println("I am vehicle with number: " + directionMessage.getVehicleNumber() + " my direction changed to " + directionMessage.getDirection());
                    }

                    try {
                        Thread.sleep(getDelay());
                    } catch (InterruptedException e) {
                        System.out.println("interrupted");
                    }

                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            @Override
                            public void run() {
                                move();
                                repaint();
                            }
                        });
                    } catch (InterruptedException exp) {
                        exp.printStackTrace();
                    } catch (InvocationTargetException exp) {
                        exp.printStackTrace();
                    }
                }

                return 1;
            }
        });

    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int x = getX();
        int y = getY();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(getColor());
        g.fillOval(0, 0, 30, 30); //adds color to circle
        g.setColor(Color.black);
        g2.drawOval(0, 0, 30, 30); //draws circle
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(30, 30);
    }

    public void run() {


    }

    public void move() {

        int x = getX();
        int y = getY();

        if (getDirection().equals(Direction.DOWN)) {
            if (y + getDiameter() + getVy() > getParent().getHeight()) {
                y = 0;
            } else {
                y += getVy();
            }
        } else if (getDirection().equals(Direction.UP)) {
            if (y - getVy() < 0) {
                y = getParent().getHeight();
            } else {
                y -= getVy();
            }
        } else if (getDirection().equals(Direction.LEFT)) {
            if (x - getVx() < 0) {
                x = getParent().getWidth();
            } else {
                x -= getVx();
            }
        } else if (getDirection().equals(Direction.RIGHT)) {
            if (x + getDiameter() + getVx() > getParent().getWidth()) {
                x = 0;
            } else {
                x += getVx();
            }
        }

        setSize(getPreferredSize());
        setLocation(x, y);

    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Enum getDirection() {
        return direction;
    }

    public void setDirection(Enum direction) {
        this.direction = direction;
    }

    public Callable getVehicleCallable() {
        return vehicleCallable;
    }

    public void setVehicleCallable(Callable vehicleCallable) {
        this.vehicleCallable = vehicleCallable;
    }

    public int getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(int vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public int getDiameter() {
        return diameter;
    }

    public void setDiameter(int diameter) {
        this.diameter = diameter;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public int getVx() {
        return vx;
    }

    public void setVx(int vx) {
        this.vx = vx;
    }

    public int getVy() {
        return vy;
    }

    public void setVy(int vy) {
        this.vy = vy;
    }
}
