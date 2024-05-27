// Environment code for project Garage.mas2j

import java.io.*;
import java.util.*;
import java.util.logging.*;

import jason.asSyntax.*;
import jason.environment.*;
import jason.asSyntax.parser.*;

import jason.environment.grid.Location;

public class GarageEnvironment extends Environment {

    GarageModel model;

    private Term up    = DefaultTerm.parse("go(up)");
    private Term down  = DefaultTerm.parse("go(down)");
    private Term right = DefaultTerm.parse("go(right)");
    private Term left  = DefaultTerm.parse("go(left)");
    private Term pickupcar = DefaultTerm.parse("pickupcar");
    private Term dropcar = DefaultTerm.parse("dropcar");

    @Override
        public void init(String[] args) {
            super.init(args);

            try {

                BufferedReader mapFile = new BufferedReader(new FileReader(GarageModel.mapPath));

                String[] dim = mapFile.readLine().split(" ");
                int mapx = Integer.parseInt(dim[0]);
                int mapy = Integer.parseInt(dim[1]);

                model = new GarageModel(mapx, mapy);
                model.setEnvironment(this);

                mapFile.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            if(args.length>=1 && args[0].equals("gui")) {
                GarageView view = new GarageView(model, this);
                model.setView(view);
            }

            updatePercepts();

        }

    @Override
        public void stop() {
            deletePercepts();
            super.stop();
        }

    public void updatePercepts() {

        deletePercepts();

        try {

            addPercept("navigator", ASSyntax.parseLiteral("dimension("+model.getWidth()+","+model.getHeight()+")"));

            Location valetLoc = model.getAgPos(0);
            addPercept("valet", ASSyntax.parseLiteral("position("+valetLoc.x+","+valetLoc.y+")"));

            for(int i=0; i<model.getWidth(); ++i) {
                for(int j=0; j<model.getHeight(); ++j) {

                    if(model.isFree(i,j) && !(model.hasObject(model.PARKINGSPOT,i,j))) {
                        addPercept("navigator", ASSyntax.parseLiteral("~obstacle("+i+","+j+")"));
                    } else {
                        addPercept("navigator", ASSyntax.parseLiteral("obstacle("+i+","+j+")"));
                    }

                    if(model.hasObject(model.PARKINGSPOT,i,j) && model.hasObject(model.CAR,i,j)) {
                        addPercept("surveillance", ASSyntax.parseLiteral("takenparkingspot("+i+","+j+")"));
                        if(model.getCarAt(i,j).leaving) {
                            addPercept("surveillance", ASSyntax.parseLiteral("carLeaving("+i+","+j+")"));
                        }
                    }

                    if(model.hasObject(model.PARKINGSPOT,i,j) && !(model.hasObject(model.CAR,i,j))) {
                        addPercept("surveillance", ASSyntax.parseLiteral("emptyparkingspot("+i+","+j+")"));
                    }

                    if(model.hasObject(model.GATE,i,j)) {
                        addPercept("surveillance", ASSyntax.parseLiteral("gate("+i+","+j+")"));
                        if((model.getCarAt(i,j)!=null) && (model.getCarAt(i,j).leaving==false)) {
                            addPercept("surveillance", ASSyntax.parseLiteral("carArrived("+i+","+j+")"));
                        }
                    }
                    if(model.carCarriedByAgent!=null) {
                        String percept = "car("+model.carCarriedByAgent.toString()+")";
                        addPercept("valet", ASSyntax.parseLiteral(percept));
                    } else {
                        addPercept("valet", ASSyntax.parseLiteral("nocar"));
                    }
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    } 

    private void deletePercepts() {
        clearPercepts();
        clearPercepts("navigator");
        clearPercepts("surveillance");
        clearPercepts("valet");
    }


    @Override
        public boolean executeAction(String agName, Structure action) {
            try {
                Thread.sleep(100);
                if(agName.equals("valet")) {
                    if(action.equals(up)) return model.moveAgentUp(0);
                    if(action.equals(down)) return model.moveAgentDown(0);
                    if(action.equals(left)) return model.moveAgentLeft(0);
                    if(action.equals(right)) return model.moveAgentRight(0);
                    if(action.equals(dropcar)) return model.dropAgentCar(0);
                    if(action.equals(pickupcar)) return model.pickupAgentCar(0);
                    
                    return super.executeAction(agName, action);
                }
            } catch (InterruptedException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
}


