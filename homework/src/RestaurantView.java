import jason.environment.grid.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.Timer;
import java.awt.event.*;
import java.util.*;

public class RestaurantView extends GridWorldView {

    private RestaurantModel model;
    private RestaurantEnvironment environment;

    public RestaurantView(RestaurantModel model, RestaurantEnvironment environment) {
        super(model, "Intelligent Restaurant", 700);
        this.model = model;
        this.environment = environment;
        defaultFont = new Font("Arial", Font.BOLD, 14);
        setVisible(true);
        repaint();
    }
    
    public static final int TABLE = 8;
    public static final int CUSTOMER = 16;
    public static final int GATE = 32;
    public static final int BAR = 64;

    @Override
    public void initComponents(int width) {
        super.initComponents(width);
        getCanvas().addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                int x = e.getX() / cellSizeW;
                int y = e.getY() / cellSizeH;
                if(model.inGrid(x,y)) {
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        model.removeCustomer(x, y);// right click will be removing the customer from the restaurant
                        environment.updatePercepts();
                    } else if (e.getButton() == MouseEvent.BUTTON1 && model.hasObject(model.CUSTOMER, x, y)) { // left click is for ordering
                        model.getCustomerAt(x, y).ordering = true;
                        System.out.println("[environment] Customer: " + model.getCustomerAt(x, y).customer + " Order: " + model.getCustomerAt(x, y).order);
                        update(x, y);
                        environment.updatePercepts();
                    }
                }
            }
            public void mouseExited(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
        });

        Timer timer = new Timer(7000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!model.generateCar(8, 4)) {
                    System.out.println("[environment] No customer left to simulate, please append new ones to your customers.txt!");
                }
                update(8, 4);
                environment.updatePercepts();
            }
        });
        timer.start();
    }

    @Override
    public void draw(Graphics g, int x, int y, int object) {
        switch (object) {

            case RestaurantModel.TABLE:
                g.setColor(new Color(196, 164, 132));
                g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
                g.setColor(Color.black);
                drawStringAbove(g, x, y, defaultFont, "TABLE");
                break;

            case RestaurantModel.CUSTOMER:
                g.setColor(Color.red);
                drawStringBelow(g, x, y, defaultFont, "CSTMR");
                break;

            case RestaurantModel.GATE:
                Color gateColor = new Color(0, 128, 0);
                g.setColor(gateColor);
                drawStringAbove(g, x, y, defaultFont, "ENTRY");
                break;

            case RestaurantModel.BAR:
                g.setColor(new Color(119, 64, 65));
                g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
                g.setColor(new Color(255,105,180));
                drawStringAbove(g, x, y, defaultFont, "BAR");
                break;

            default:
                g.setColor(Color.gray);
                g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
                break;
        }
    }

    @Override
    public void drawAgent(Graphics g, int x, int y, Color c, int id) {
        g.setColor(Color.blue);
        super.drawString(g, x, y, defaultFont, "Robot");
    }

    private void drawStringBelow(Graphics g, int x, int y, Font font, String str) {
        g.setFont(font);
        int xPos = x * cellSizeW + cellSizeW / 7;
        int yPos = y * cellSizeH + cellSizeH / 2 + 19;
        g.drawString(str, xPos, yPos);
    }

    private void drawStringAbove(Graphics g, int x, int y, Font font, String str) {
        g.setFont(font);
        int xPos = x * cellSizeW + cellSizeW / 7;
        int yPos = y * cellSizeH + cellSizeH / 2 - 13;
        g.drawString(str, xPos, yPos);
    }
}
