import jason.asSyntax.*;
import jason.architecture.*;
import java.util.*;
import java.lang.Math;
import jason.asSemantics.Message;
import java.util.regex.*;

public class NavigatorArchitecture extends AgArch {

    int mapx;
    int mapy;
    boolean[][] map;

    static String up = "up";
    static String down = "down";
    static String left = "left";
    static String right = "right";
 
    @Override
    public Collection<Literal> perceive() {
        Collection<Literal> perceptCollection = super.perceive();
        parsePercepts(perceptCollection);
        //printPercepts();
        return perceptCollection;
    }

    private void cleanPercepts() {
        mapx=0;
        mapy=0;
        map=null;
    }

    private void parsePercepts(Collection<Literal> perceptCollection) {
        if(perceptCollection == null) return; 

        cleanPercepts();

        //Transforming Objects back to Literals and those back to Strings.
        Object[] perceptObjs = perceptCollection.toArray();
        String[] percepts = new String[perceptObjs.length];
        for(int i=0; i<perceptObjs.length; ++i) {
            percepts[i] = ((Literal)(perceptObjs[i])).toString();
        }

        //Parsing the one dimension percept first, initializing map with dimensions.
        for(int i=0; i<percepts.length; ++i) {
            if(percepts[i].charAt(0) == 'd') {
                String[] result = percepts[i].split("dimension");
                Coord dim =  new Coord(result[1]);
                mapx = dim.x;
                mapy = dim.y;
                map = new boolean[mapx][mapy];
            }
        }

        //Parsing obstacle percepts, anything that is not a dimension percept considered an obstacle percept.
        for(int i=0; i<percepts.length; ++i) {
            if(percepts[i].charAt(0) == 'd') continue;
            String[] result = percepts[i].split("obstacle");
            Coord coord = new Coord(result[1]);
            map[coord.x][coord.y] = result[0].equals("~");
        }

    }

    private void printPercepts() {
        System.out.println("Navigator currently sees this map:");
        if(map == null) System.out.println("Map is not perceived.");
        for(int i=0; i<mapx; ++i) {
            for(int j=0; j<mapy; ++j) {
                System.out.print(map[i][j] ? 1 : 0);
            }
            System.out.println("");
        }
    }

    private List<String> astar(int startx, int starty, int endx, int endy) {
        if(startx==endx && starty==endy) return new ArrayList<String>();

        if(map==null) return null;

        //Distance array for Dijkstra's algorithm
        int[][] g = new int[mapx][mapy];
        for(int i=0; i<mapx; ++i) {
            for(int j=0; j<mapy; ++j) {
                g[i][j] = -1;
            }
        }
        g[startx][starty] = 0; 

        //Added heuristics to array g for A*
        int[][] f = new int[mapx][mapy];
        for(int i=0; i<mapx; ++i) {
            for(int j=0; j<mapy; ++j) {
                f[i][j] = (g[i][j] == -1) ? -1 : g[i][j] + manhattan(i,j,endx,endy);
            }
        }

        Coord[][] cameFrom = new Coord[mapx][mapy];

        cameFrom[startx][starty] = new Coord(-1,-1);

        //Nodes that have been expanded from and closed in Dijsktra's algorithm 
        boolean[][] closed = new boolean[mapx][mapy];
        for(int i=0; i<mapx; ++i) {
            for(int j=0; j<mapy; ++j) {
                closed[i][j] = false;
            }
        }

        Coord curr = new Coord(startx, starty);
        Coord from;

        while(curr.x!=endx || curr.y!=endy) {

            closed[curr.x][curr.y] = true; 

            Coord left = new Coord(curr.x, curr.y-1); 
            if(steppable(left) || (left.x == endx && left.y == endy) ) {
                if((g[curr.x][curr.y] + 1) < g[left.x][left.y] || (g[left.x][left.y] == -1)) {
                    g[left.x][left.y] = g[curr.x][curr.y] + 1;
                    f[left.x][left.y] = g[left.x][left.y] + manhattan(left.x, left.y, endx, endy);
                    cameFrom[left.x][left.y] = curr;
                }
            }

            Coord right = new Coord(curr.x, curr.y+1);
            if(steppable(right) || (right.x == endx && right.y == endy)) {
                if((g[curr.x][curr.y] + 1) < g[right.x][right.y] || (g[right.x][right.y] == -1)) {
                    g[right.x][right.y] = g[curr.x][curr.y] + 1;
                    f[right.x][right.y] = g[right.x][right.y] + manhattan(right.x, right.y, endx, endy);
                    cameFrom[right.x][right.y] = curr;
                }
            } 

            Coord up = new Coord(curr.x-1, curr.y);
            if(steppable(up) || (up.x == endx && up.y == endy)) {
                if((g[curr.x][curr.y] + 1) < g[up.x][up.y] || (g[up.x][up.y] == -1)) {
                    g[up.x][up.y] = g[curr.x][curr.y] + 1;
                    f[up.x][up.y] = g[up.x][up.y] + manhattan(up.x, up.y, endx, endy);
                    cameFrom[up.x][up.y] = curr;
                }
            }

            Coord down = new Coord(curr.x+1, curr.y);
            if(steppable(down) || (down.x == endx && down.y == endy)) {
                if((g[curr.x][curr.y] + 1) < g[down.x][down.y] || (g[down.x][down.y] == -1)) {
                    g[down.x][down.y] = g[curr.x][curr.y] + 1;
                    f[down.x][down.y] = g[down.x][down.y] + manhattan(down.x, down.y, endx, endy);
                    cameFrom[down.x][down.y] = curr;
                }
            }

            int minf = -1;
            for(int i=0; i<mapx; ++i) {
                for(int j=0; j<mapy; ++j) {
                    if( ((map[i][j]) || ((i==endx) && (j==endy))) && !(closed[i][j]) && (f[i][j] != -1)) {
                        if(minf == -1 || f[i][j] < minf) {
                            minf = f[i][j];
                            curr = new Coord(i,j); 
                        }
                    } 
                }
            } 

            if(minf == -1) {
                    break;
            }

        }

        if(cameFrom[endx][endy] == null) {
            return null;
        }

        List path = new ArrayList<String>();

        curr = new Coord(endx,endy);

        while(curr.x != startx || curr.y != starty) {
            from = cameFrom[curr.x][curr.y];

            //Left
            if(from.y - 1 == curr.y) {
                path.add(left);
            }

            //Right
            if(from.y + 1 == curr.y) {
                path.add(right);
            }

            //Up
            if(from.x - 1 == curr.x) {
                path.add(up);
            }

            //Down
            if(from.x + 1 == curr.x) {
                path.add(down);
            }

            curr = from;
        }

        Collections.reverse(path);

        System.out.println("[navigator] A* has been completed! From: ("+startx+","+starty+") To: ("+endx+","+endy+") I have calculated the following path: "+path);
        
        return path;
    }

    private int manhattan(int fromx, int fromy, int tox, int toy) {
        return Math.abs(tox-fromx + toy-fromy);
    }

    private boolean steppable(Coord c) {
        return (0<=c.x && c.x<mapx && 0<=c.y && c.y<mapy && map[c.x][c.y]);
    }

    @Override
    public void checkMail() {
        super.checkMail();

        Iterator im = getTS().getC().getMailBox().iterator();
        while (im.hasNext()) {
            Message m = (Message) im.next();
            if (m.getSender().equals("robot")) {
            
                String route = m.getPropCont().toString();

                Matcher mr = Pattern.compile("route([a-zA-Z]*)\\(([0-9]*),([0-9]*),([0-9]*),([0-9]*)\\)").matcher(route);
                mr.find();

                List<String> path = astar(Integer.parseInt(mr.group(2)),
                                            Integer.parseInt(mr.group(3)),
                                            Integer.parseInt(mr.group(4)),
                                            Integer.parseInt(mr.group(5)));
                String result = "route"+mr.group(1)+"(["+String.join(",", path)+"])";
                Message r = new Message(
                        "tell",
                        getAgName(),
                        m.getSender(),
                        result
                        );
                
                try {
                    sendMsg(r);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                im.remove();
            }
        }
    }
}
