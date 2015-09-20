package uk.ac.rhul.cs.dice.star.example.dirt;

import org.javatuples.Pair;

import uk.ac.rhul.cs.dice.star.action.Event;
import uk.ac.rhul.cs.dice.star.action.HttpAction;
import uk.ac.rhul.cs.dice.star.example.dirt.sweep.CleanActionType;
import uk.ac.rhul.cs.dice.star.physics.AbstractPhysics;

public class CleanPhysics extends AbstractPhysics {

	private static final int PERCEPTION_RANGE = 1;
	
	private int width;
	private int height;
	private Location[][] grid;
	
	private Location userLoc;
	
	public void createGrid(int width, int height) {
		this.width = width;
		this.height = height;
		
		// create empty grid
		grid = new Location[width][height];
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				grid[i][j] = new Location("", "", "", i, j);
		
		// create human user
		userLoc = new Location("", "", "", 0, 0);
		grid[0][0].setUser("user");
	}
	
	public void removeAgent(int x, int y) {
		grid[x][y].setAgent("");
		grid[x][y].setAgentDirection("");
	}
	
	public boolean addAgent(String agent, String direction, int x, int y) {
		if (grid[x][y].getAgent().equals("")) {
			grid[x][y].setAgent(agent);
			grid[x][y].setAgentDirection(direction);
			
			return true;
		}
		
		return false;
	}
	
	public void updateAgentDirection(int x, int y, String direction) {
		grid[x][y].setAgentDirection(direction);
	}
	
	public String removeDirt(int x, int y) {
		String dirt = grid[x][y].getDirt();
		grid[x][y].setDirt("");

		return dirt;
	}
	
	public void addDirt(String dirt, int x, int y) {
		if (grid[x][y].getDirt().equals(""))
			grid[x][y].setDirt(dirt);
	}

	public void moveUser(int x, int y) {
		// remove from old location
		grid[userLoc.getX()][userLoc.getY()].setUser("");
		
		// create new location
		userLoc = new Location("", "", "", x, y);
		grid[x][y].setUser("user");
	}
	
	private Location getAgentLocation(String agent) {
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				if (grid[i][j].getAgent().equals(agent)) {
					Location loc = new Location("", grid[i][j].getAgentDirection(), "", i, j);
					return loc;
				}
		
		return new Location("", "", "", -1, -1);
	}
	
	public String observeArea(String agent) {
		Location loc = getAgentLocation(agent);
		String area = "loc(" + agent + "," + loc.getX() + "," + loc.getY() + "," + loc.getAgentDirection() + ")";
		
		if (loc.getAgentDirection().equals(Directions.EAST.toString()))
			for (int i = loc.getX(); i <= loc.getX() + PERCEPTION_RANGE; i++)
				for (int j = loc.getY() - PERCEPTION_RANGE; j <= loc.getY() + PERCEPTION_RANGE; j++)
					area += getArea(agent, i, j);
		else if (loc.getAgentDirection().equals(Directions.WEST.toString()))
			for (int i = loc.getX() - PERCEPTION_RANGE; i <= loc.getX(); i++)
				for (int j = loc.getY() - PERCEPTION_RANGE; j <= loc.getY() + PERCEPTION_RANGE; j++)
					area += getArea(agent, i, j);
		else if (loc.getAgentDirection().equals(Directions.NORTH.toString()))
			for (int i = loc.getX() - PERCEPTION_RANGE; i <= loc.getX() + PERCEPTION_RANGE; i++)
				for (int j = loc.getY() - PERCEPTION_RANGE; j <= loc.getY(); j++)
					area += getArea(agent, i, j);
		else if (loc.getAgentDirection().equals(Directions.SOUTH.toString()))
			for (int i = loc.getX() - PERCEPTION_RANGE; i <= loc.getX() + PERCEPTION_RANGE; i++)
				for (int j = loc.getY(); j <= loc.getY() + PERCEPTION_RANGE; j++)
					area += getArea(agent, i, j);
		
		return area;
	}
	
	private String getArea(String agent, int x, int y){
		return getUser(x, y) + getAgent(agent, x, y) + getDirt(x, y);
	}
	
	private String getUser(int x, int y) {
		String user = "";
		
		if (x >= 0 && x < width && y >= 0 && y < height)
			if (!grid[x][y].getUser().equals(""))
				user = "#user(" + x + "," + y + ")";
			
		return user;
	}

	private String getDirt(int x, int y) {
		String dirt = "";
		
		if (x >= 0 && x < width && y >= 0 && y < height)
			if (!grid[x][y].getDirt().equals(""))
				dirt = "#dirt(" + x + "," + y + ")";
			
		return dirt;
	}
	
	private String getAgent(String ag, int x, int y) {
		String agent = "";
		
		if (x >= 0 && x < width && y >= 0 && y < height)
			if (!(grid[x][y].getAgent().equals("") || grid[x][y].getAgent().equals(ag)))
				agent = "#agent(" + grid[x][y].getAgent() + "," + x + "," + y + "," + grid[x][y].getAgentDirection() + ")";
			
		return agent;
	}
	
	//@SuppressWarnings("unchecked")
	@Override
	public boolean isPossible(Event event) {
		//Allow all HTTP Actions
		if (event.getAction() instanceof HttpAction) return true;
		
		Pair<String, String> payload = (Pair<String, String>) event.getAction().getPayload();
		
		String type = event.getActionType();
		String agent = payload.getValue0();
		String action = payload.getValue1();
		
		if (type.equals(CleanActionType.PHYSICAL_ACT.toString())) {
			if (action.equals("TURN_LEFT"))
				return turnLeft(agent);
			else if (action.equals("TURN_RIGHT"))
				return turnRight(agent);
			else if (action.equals("MOVE"))
				return move(agent);
			else if (action.equals("CLEAN"))
				return clean(agent);
		}
		
    	System.out.println(agent + ": " + action);
		return true;
	}

	private boolean turnLeft(String agent) {
		Location loc = getAgentLocation(agent);
		String direction = "";
		if (loc.getAgentDirection().equals(Directions.EAST.toString()))
			direction = Directions.NORTH.toString();
		else if (loc.getAgentDirection().equals(Directions.NORTH.toString()))
			direction = Directions.WEST.toString();
		else if (loc.getAgentDirection().equals(Directions.WEST.toString()))
			direction = Directions.SOUTH.toString();
		else if (loc.getAgentDirection().equals(Directions.SOUTH.toString()))
			direction = Directions.EAST.toString();
		
		updateAgentDirection(loc.getX(), loc.getY(), direction);
    	System.out.println(agent + ": TURN LEFT");		

    	return true;
	}

	private boolean turnRight(String agent) {
		Location loc = getAgentLocation(agent);
		String direction = "";
		if (loc.getAgentDirection().equals(Directions.EAST.toString()))
			direction = Directions.SOUTH.toString();
		else if (loc.getAgentDirection().equals(Directions.SOUTH.toString()))
			direction = Directions.WEST.toString();
		else if (loc.getAgentDirection().equals(Directions.WEST.toString()))
			direction = Directions.NORTH.toString();
		else if (loc.getAgentDirection().equals(Directions.NORTH.toString()))
			direction = Directions.EAST.toString();
		
		updateAgentDirection(loc.getX(), loc.getY(), direction);
    	System.out.println(agent + ": TURN RIGHT");

    	return true;
	}
	
	private boolean move(String agent) {
		Location loc = getAgentLocation(agent);
		
		// out of bounds
		if ((loc.getX() == 0 && loc.getAgentDirection().equals(Directions.WEST.toString())) ||
			(loc.getX() == width - 1 && loc.getAgentDirection().equals(Directions.EAST.toString())) ||
			(loc.getY() == 0 && loc.getAgentDirection().equals(Directions.NORTH.toString())) ||
			(loc.getY() == height - 1 && loc.getAgentDirection().equals(Directions.SOUTH.toString()))
			) {
	    	System.out.println(agent + ": MOVE NOT POSSIBLE (OUT OF BOUNDS)");
	    	return false;
		}		
		
		// move to location
		int x = loc.getX();
		int y = loc.getY();
		if (loc.getAgentDirection().equals(Directions.EAST.toString()))
			x++;
		else if (loc.getAgentDirection().equals(Directions.WEST.toString()))
			x--;
		else if (loc.getAgentDirection().equals(Directions.SOUTH.toString()))
			y++;
		else if (loc.getAgentDirection().equals(Directions.NORTH.toString()))
			y--;

		// occupied by another agent
		if (!grid[x][y].getAgent().equals("")) {
	    	System.out.println(agent + ": MOVE NOT POSSIBLE (OCCUPIED BY AGENT)");
	    	return false;
		}
		
		// occupied by user
		if (!grid[x][y].getUser().equals("")) {
	    	System.out.println(agent + ": MOVE NOT POSSIBLE (OCCUPIED BY USER)");
	    	return false;
		}
		
		removeAgent(loc.getX(), loc.getY());
		addAgent(agent, loc.getAgentDirection(), x, y);
    	System.out.println(agent + ": MOVE");

		return true;
	}
	
	private boolean clean(String agent) {
		Location loc = getAgentLocation(agent);
		if (grid[loc.getX()][loc.getY()].getDirt().equals("")) {
	    	System.out.println(agent + ": CLEAN NOT POSSIBLE");
	    	return false;
		}
		
		removeDirt(loc.getX(), loc.getY());
    	System.out.println(agent + ": CLEAN");
    	
    	return true;
	}
	
}