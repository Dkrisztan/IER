import java.io.*;
import java.util.*;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

public class RestaurantModel extends GridWorldModel {

    public static final String mapPath = "map.txt";
    public static final String customers = "customers.txt";
    
    public static final int robot = 0;
    public static final int TABLE = 8;
    public static final int CUSTOMER = 16;
    public static final int GATE = 32;
    public static final int BAR = 64;

    public List<Customer> orders;
    public Customer orderCarriedByAgent = null;
    private RestaurantEnvironment environment;

    public RestaurantModel(int mapx, int mapy) {
        
        super(mapx, mapy, 1);    
       
        try {
            BufferedReader mapFile = new BufferedReader(new FileReader(mapPath));
            mapFile.readLine();
        
            for(int i=0; i<width; ++i) {
                String line = mapFile.readLine();

                for(int j=0; j<height; ++j) {
                    switch(line.charAt(j)) {
                        case 'W': add(OBSTACLE, i,j); break;
                        case 'E': add(TABLE, i,j); break;
                        case 'G': add(GATE, i,j); break;
                        case 'B': add(BAR, i,j); break;
                        default: break;
                    }
                }
            }

            mapFile.close(); 

            BufferedReader ordersFile = new BufferedReader(new FileReader(customers));

            orders = new ArrayList<>();

            String line;
            while((line = ordersFile.readLine()) != null) {
                String[] customerData = line.split(";");
                Customer customer = new Customer(customerData[0], customerData[1]);
                orders.add(customer);
            }

            ordersFile.close();

 
loop:       for(int i=0; i<width; ++i) {
                for(int j=0; j<height; ++j) {
                    if(hasObject(GATE,i,j)) {
                        setAgPos(robot, 4, 2);  
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

    // Function for getting all the free tables
    public List<Location> getFreeTables() {
        List<Location> freeTables = new ArrayList<>();
        for(int i=0; i<width; ++i) {
            for(int j=0; j<height; ++j) {
                if(hasObject(TABLE, i, j) && !hasObject(CUSTOMER, i, j)) {
                    freeTables.add(new Location(i,j));
                }
            }
        }
        return freeTables;
    }

    public boolean pickupAgentCar(int agent) {
        Location agLoc = getAgPos(agent);
        System.out.println("[environment] Order is being picked up from ("+agLoc.x+","+agLoc.y+").");
        orderCarriedByAgent = getCustomerAt(agLoc);
        if(orderCarriedByAgent==null) {
            System.out.println("[environment] Could not find any orders to be picked up at ("+agLoc.x+","+agLoc.y+").");
            return false;
        }
        Customer customer = getCustomerAt(agLoc);
        customer.ordering = false;
        
        environment.updatePercepts(); 
        return true;
    }

    public boolean dropAgentCar(int agent) {
        if(orderCarriedByAgent==null) return false;
        Location agLoc = getAgPos(agent);
        if(orderCarriedByAgent.ordering && hasObject(GATE, agLoc)) {
            orderCarriedByAgent.ordering = false;
        } else {
            orderCarriedByAgent.location = agLoc;
            add(CUSTOMER, orderCarriedByAgent.location);
        }
        orderCarriedByAgent = null;
        environment.updatePercepts();
        return true;
    }
    
    public List<Customer> incomingCars() {
        List<Customer> ret = new ArrayList<>();
        for(Customer customer : orders) {
            if(!customer.ordering && hasObject(GATE, customer.location)) {
                ret.add(customer);
            }
        }
        return ret; 
    }

    public List<Customer> orderingCars() {
        List<Customer> ret = new ArrayList<>();
        for(Customer customer : orders) {
            if(customer.ordering && hasObject(TABLE, customer.location)) {
                ret.add(customer);
            }
        }
        return ret;
    }

    public Customer getCustomerAt(int x, int y) {
        return getCustomerAt(new Location(x,y));
    }
    
    public Customer getCustomerAt(Location location) {
        for(Customer customer : orders) {
            if((customer.location != null) && (customer.location.x == location.x) && (customer.location.y == location.y)) {
               return customer; 
            }
        }
        return null;
    }

    public boolean generateCar(int x, int y) {
        try {
            for(Customer customer : orders) {
                if(customer.location == null) {
                    System.out.println("[environment] Generating arriving customer at ("+x+","+y+")");
                    customer.location = new Location(x,y);
                    customer.ordering = false;
                    add(CUSTOMER,x,y);
                    environment.updatePercepts();
                    Thread.sleep(1000);
                    if (this.getFreeTables().size() == 0) {
                        System.out.println("[environment] No empty tables available.");
                    } else {
                        List <Location> emptyTables = this.getFreeTables();
                        Random rand = new Random();
                        int randomIndex = rand.nextInt(emptyTables.size()); 
                        remove(CUSTOMER, new Location(x, y));
                        add(CUSTOMER, emptyTables.get(randomIndex));
                        customer.location = emptyTables.get(randomIndex);
                        environment.updatePercepts();
                    }
                    return true;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void removeCustomer(int x, int y) {
        if(hasObject(CUSTOMER, x, y)){
            remove(CUSTOMER, x, y);
            environment.updatePercepts();
            System.out.println("[environment] Customer left from ("+x+","+y+")");
        }
    }
}
