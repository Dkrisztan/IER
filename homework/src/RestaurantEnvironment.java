
import java.io.*;
import java.util.*;
import java.util.logging.*;

import jason.asSyntax.*;
import jason.environment.*;
import jason.asSyntax.parser.*;

import jason.environment.grid.Location;

public class RestaurantEnvironment extends Environment {

    RestaurantModel model;

    private Term up    = DefaultTerm.parse("go(up)");
    private Term down  = DefaultTerm.parse("go(down)");
    private Term right = DefaultTerm.parse("go(right)");
    private Term left  = DefaultTerm.parse("go(left)");
    private Term pickuporder = DefaultTerm.parse("pickuporder");
    private Term dropcar = DefaultTerm.parse("dropcar");

    @Override
    public void init(String[] args) {
        super.init(args);

        try {

            BufferedReader mapFile = new BufferedReader(new FileReader(RestaurantModel.mapPath));

            String[] dim = mapFile.readLine().split(" ");
            int mapx = Integer.parseInt(dim[0]);
            int mapy = Integer.parseInt(dim[1]);

            model = new RestaurantModel(mapx, mapy);
            model.setEnvironment(this);

            mapFile.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(args.length>=1 && args[0].equals("gui")) {
            RestaurantView view = new RestaurantView(model, this);
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

            Location robotLoc = model.getAgPos(0);
            addPercept("robot", ASSyntax.parseLiteral("position("+robotLoc.x+","+robotLoc.y+")"));

            for(int i=0; i<model.getWidth(); ++i) {
                for(int j=0; j<model.getHeight(); ++j) {

                    if(model.isFree(i,j) && !(model.hasObject(model.TABLE,i,j))) {
                        addPercept("navigator", ASSyntax.parseLiteral("~obstacle("+i+","+j+")"));
                    } else {
                        addPercept("navigator", ASSyntax.parseLiteral("obstacle("+i+","+j+")"));
                    }

                    if(model.hasObject(model.TABLE,i,j) && model.hasObject(model.CUSTOMER,i,j)) {
                        addPercept("surveillance", ASSyntax.parseLiteral("takenparkingspot("+i+","+j+")"));
                        if(model.getCustomerAt(i,j).ordering) {
                            addPercept("surveillance", ASSyntax.parseLiteral("orderAt("+i+","+j+")"));
                        }
                    }

                    if(model.hasObject(model.TABLE,i,j) && !(model.hasObject(model.CUSTOMER,i,j))) {
                        addPercept("surveillance", ASSyntax.parseLiteral("emptyparkingspot("+i+","+j+")"));
                    }

                    if(model.hasObject(model.GATE,i,j)) {
                        addPercept("surveillance", ASSyntax.parseLiteral("bar("+i+","+j+")"));
                        if((model.getCustomerAt(i,j)!=null) && (model.getCustomerAt(i,j).ordering==false)) {
                            addPercept("surveillance", ASSyntax.parseLiteral("carArrived("+i+","+j+")"));
                        }
                    }
                    if(model.carCarriedByAgent!=null) {
                        String percept = "car("+model.carCarriedByAgent.toString()+")";
                        addPercept("robot", ASSyntax.parseLiteral(percept));
                    } else {
                        addPercept("robot", ASSyntax.parseLiteral("nocar"));
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
        clearPercepts("robot");
    }


    @Override
    public boolean executeAction(String agName, Structure action) {
        try {
            Thread.sleep(100);
            if(agName.equals("robot")) {
                if(action.equals(up)) return model.moveAgentUp(0);
                if(action.equals(down)) return model.moveAgentDown(0);
                if(action.equals(left)) return model.moveAgentLeft(0);
                if(action.equals(right)) return model.moveAgentRight(0);
                if(action.equals(dropcar)) return model.dropAgentCar(0);
                if(action.equals(pickuporder)) return model.pickupAgentCar(0);
                
                return super.executeAction(agName, action);
            }
        } catch (InterruptedException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}


