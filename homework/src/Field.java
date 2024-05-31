public class Field {

	public enum Type {
		Road,
		Wall,
		Table,
		Gate,
		Bar
	}	

	public Type type;
	
	public Customer customer;
	public String agent;
	
	public boolean obstacle() {
		return !(type == Type.Table && customer==null && agent==null);
	}	
}
