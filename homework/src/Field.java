public class Field {

	public enum Type {
		Road,
		Wall,
		Table,
		Gate
	}	

	public Type type;
	
	public Car car;
	public String agent;
	
	public boolean obstacle() {
		return !(type == Type.Table && car==null && agent==null);
	}	
}
