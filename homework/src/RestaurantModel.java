import java.io.*;
import java.util.*;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

public class RestaurantModel extends GridWorldModel {

    public static final String mapPath = "map.txt";
    public static final String customers = "customers.txt";
    
    public static final int VALET = 0;
    public static final int TABLE = 8;
    public static final int CUSTOMER = 16;
    public static final int GATE = 32;

    public List<Customer> cars;
    
    public Customer carCarriedByAgent = null;

    private RestaurantEnvironment environment;

    public RestaurantModel(int mapx, int mapy) {
        
        super(mapx, mapy, 1);    
       
        try {

            BufferedReader mapFile = new BufferedReader(new FileReader(mapPath));
            mapFile.readLine(); //Dimensions
        
            for(int i=0; i<width; ++i) {

                String line = mapFile.readLine();

                for(int j=0; j<height; ++j) {
                    
                    switch(line.charAt(j)) {
                        case 'W': add(OBSTACLE, i,j); break;
                        case 'E': add(TABLE, i,j); break;
                        case 'G': add(GATE, i,j); break;
                        default: break;
                    }

                }

            }

            mapFile.close(); 


            BufferedReader carsFile = new BufferedReader(new FileReader(customers));

            cars = new ArrayList<>();

            String line;
            while((line = carsFile.readLine()) != null) {
                String[] carData = line.split(";");
                Customer car = new Customer(carData[0], carData[1]);
                cars.add(car);
            }

            carsFile.close();

 
loop:       for(int i=0; i<width; ++i) {
                for(int j=0; j<height; ++j) {
                    if(hasObject(GATE,i,j)) {
                        setAgPos(VALET, i, j);  
                        break loop;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setEnvironment(RestaurantEnvironment environment) {
        this.environment = environment;
    }

    public boolean moveAgentUp(int agent) {
        return moveAgent(agent, -1, 0);
    }
    public boolean moveAgentDown(int agent) {
        return moveAgent(agent, +1, 0);
    }
    public boolean moveAgentLeft(int agent) {
        return moveAgent(agent, 0, -1);
    }
    public boolean moveAgentRight(int agent) {
        return moveAgent(agent, 0, +1);
    }
      
    private boolean moveAgent(int agent, int xdif, int ydif) {
        if(agent!=0) return false;

        Location agLoc = getAgPos(agent);
        Location newAgLoc = new Location(agLoc.x+xdif, agLoc.y+ydif);
        
        if(!inGrid(newAgLoc)) return false;

        if(!(isFree(newAgLoc) || (hasObject(TABLE, newAgLoc) && !hasObject(CUSTOMER, newAgLoc)))) return false;

        setAgPos(agent, newAgLoc);

        environment.updatePercepts();
 
        return true;
    }

    public boolean pickupAgentCar(int agent) {
        Location agLoc = getAgPos(agent);
        System.out.println("[environment] Car is being picked up from ("+agLoc.x+","+agLoc.y+").");
        carCarriedByAgent = getCarAt(agLoc);
        if(carCarriedByAgent==null) {
                System.out.println("[environment] Could not find any cars to be picked up at ("+agLoc.x+","+agLoc.y+").");
                return false;
        }
        remove(CUSTOMER,carCarriedByAgent.location);
        carCarriedByAgent.location = null;
        environment.updatePercepts(); 
        return true;
    }

    public boolean dropAgentCar(int agent) {
        if(carCarriedByAgent==null) return false;
        Location agLoc = getAgPos(agent);
        if(carCarriedByAgent.leaving && hasObject(GATE, agLoc)) {
            carCarriedByAgent.leaving = false;
        } else {
            carCarriedByAgent.location = agLoc;
            add(CUSTOMER, carCarriedByAgent.location);
        }
        carCarriedByAgent = null;
        environment.updatePercepts();
        return true;
    }
    
    public List<Customer> incomingCars() {
        List<Customer> ret = new ArrayList<>();
        for(Customer car : cars) {
            if(!car.leaving && hasObject(GATE, car.location)) {
                ret.add(car);
            }
        }
        return ret; 
    }


    public List<Customer> leavingCars() {
        List<Customer> ret = new ArrayList<>();
        for(Customer car : cars) {
            if(car.leaving && hasObject(TABLE, car.location)) {
                ret.add(car);
            }
        }
        return ret;
    }

    public Customer getCarAt(int x, int y) {
        return getCarAt(new Location(x,y));
    }
    
    public Customer getCarAt(Location location) {
        for(Customer car : cars) {
            if((car.location != null) && (car.location.x == location.x) && (car.location.y == location.y)) {
               return car; 
            }
        }
        return null;
    }

    public boolean generateCar(int x, int y) {
        for(Customer car : cars) {
            if(car.location == null) {
                System.out.println("[environment] Generating arriving car at ("+x+","+y+")");
                car.location = new Location(x,y);
                car.leaving = false;
                add(CUSTOMER,x,y);
                environment.updatePercepts();
                return true;
            }
        }
        return false;
    }
}
