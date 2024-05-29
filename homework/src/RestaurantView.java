import jason.environment.grid.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import java.awt.event.*;

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

    //Built in
    //public static final int CLEAN = 0;
    //public static final int AGENT = 2;
    //public static final int OBSTACLE = 4;
    public static final int TABLE = 8;
    public static final int CUSTOMER = 16;
    public static final int GATE = 32;

    @Override
    public void initComponents(int width) {
        super.initComponents(width);
        getCanvas().addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                int x = e.getX() / cellSizeW;
                int y = e.getY() / cellSizeH;
                if(model.inGrid(x, y)) {
                    if(model.hasObject(model.GATE, x, y)) {
                        if(!model.generateCar(x, y)) {
                            System.out.println("[environment] No customer left to simulate, please append new ones to your customers.txt!");
                        }
                    } else if(model.hasObject(model.CUSTOMER, x, y)) {
                        model.getCarAt(x, y).leaving = true;
                    }
                }
                update(x, y);
                environment.updatePercepts();
            }
            public void mouseExited(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
        });
    }

    @Override
    public void draw(Graphics g, int x, int y, int object) {
        switch (object) {

            case RestaurantModel.TABLE:
                g.setColor(Color.black);
                drawStringAbove(g, x, y, defaultFont, "TABLE");
                break;

            case RestaurantModel.CUSTOMER:
                g.setColor(Color.red);
                drawStringBelow(g, x, y, defaultFont, "CSTMR");
                break;

            case RestaurantModel.GATE:
                g.setColor(Color.green);
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
