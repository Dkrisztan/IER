public class Coord {
    public int x, y;

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }
   
    public Coord(String c) {
        String[] coords = c.substring(1,c.length()-1).split(",");
        this.x = Integer.parseInt(coords[0]);
        this.y = Integer.parseInt(coords[1]); 
    }
 
    public String toString() {
        return "("+x+","+y+")";
    }
}
