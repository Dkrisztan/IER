import jason.environment.grid.Location;

public class Customer {

	public String customer;
	public String order;
    public boolean leaving;

    public Location location;
	
	public Customer(String customer, String order) {
		this.customer = customer;
		this.order = order;
        this.leaving = false;
        this.location = null;	
	}

    public String toString() {
        return "\""+customer+"\",\""+order+"\"";
    }
	
}
