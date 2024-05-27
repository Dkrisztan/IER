import jason.environment.grid.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import java.awt.event.*;

    
public class GarageView extends GridWorldView {

    private GarageModel model;
    private GarageEnvironment environment;
 
    public GarageView(GarageModel model, GarageEnvironment environment) {
        super(model, "Intelligent Garage", 700);
        this.model = model;
        this.environment = environment;
        defaultFont = new Font("Arial", Font.BOLD, 14); // change default font
        setVisible(true);
        repaint();
    }
    
    //Built in
    //public static final int CLEAN = 0;
    //public static final int AGENT = 2;
    //public static final int OBSTACLE = 4;
    public static final int PARKINGSPOT = 8;
    public static final int CAR = 16;
    public static final int GATE = 32;

    @Override
    public void initComponents(int width) {
        super.initComponents(width);
        getCanvas().addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                int x = e.getX() / cellSizeW;
                int y = e.getY() / cellSizeH;
                if(model.inGrid(x,y)) {
                    if(model.hasObject(model.GATE, x,y)) {
                        if(!model.generateCar(x,y)) {
                            System.out.println("[environment] No customer left to simulate, please append new ones to your customers.txt!");
                        }
                    } else if(model.hasObject(model.CAR, x,y)) {
                        model.getCarAt(x,y).leaving = true;
                    }
                }
                update(x,y);
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

            case GarageModel.PARKINGSPOT: 
                g.setColor(Color.black);
                drawString(g, x, y, defaultFont, "TABLE");  
                break;

            case GarageModel.CAR:
                g.setColor(Color.red);
                drawString(g, x, y, defaultFont, "CUSTOMER");  
                break;
            
            case GarageModel.GATE:
                g.setColor(Color.green);
                drawString(g, x, y, defaultFont, "G");  
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
}
