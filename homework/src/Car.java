import jason.environment.grid.Location;

public class Car {

	public String owner;
	public String numberPlate;
    public boolean leaving;

    public Location location;
	
	public Car(String owner, String numberPlate) {
		this.owner = owner;
		this.numberPlate = numberPlate;
        this.leaving = false;
        this.location = null;	
	}

    public String toString() {
        return "\""+owner+"\",\""+numberPlate+"\"";
    }
	
}
