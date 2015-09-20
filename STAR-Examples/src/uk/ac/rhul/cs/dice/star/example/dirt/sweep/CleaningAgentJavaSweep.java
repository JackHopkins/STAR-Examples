package uk.ac.rhul.cs.dice.star.example.dirt.sweep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import org.javatuples.Pair;

import exceptions.NoPhysicsException;
import uk.ac.rhul.cs.dice.star.action.Action;
import uk.ac.rhul.cs.dice.star.action.HttpAction;
import uk.ac.rhul.cs.dice.star.action.Percept;
import uk.ac.rhul.cs.dice.star.agent.AbstractAgentMind;
import uk.ac.rhul.cs.dice.star.example.dirt.Directions;
import uk.ac.rhul.cs.dice.star.example.dirt.Location;
import uk.ac.rhul.cs.dice.star.example.dirt.PhysicalAct;
import uk.ac.rhul.cs.dice.star.http.HttpRequest;
import uk.ac.rhul.cs.dice.star.http.HttpResponse;
import uk.ac.rhul.cs.dice.star.example.dirt.CleanPhysics;

public class CleaningAgentJavaSweep extends AbstractAgentMind {
	private static final String environmentAgent = "environment-1";
	private static final long serialVersionUID = -565563161972920536L;

	private boolean initialised = false;

	private Integer x ;
	private Integer y;
	private String direction;

	private Stack<String> goals;
	private String currentGoal;

	private Vector<Location> dirtObserved;

	private HashSet<String> checkedPlaces = new HashSet<String>(); 
	private String lastLocation = "";
	public CleaningAgentJavaSweep() {
		super();
		initialised = false;
	}
	/*public CleaningAgentJavaSweep(AgentBrain brain, int x, int y, String direction) {
		super(brain);
        this.x = x;
        this.y = y;
        this.direction = direction;

		initialised = false;
	}*/

	@SuppressWarnings("unchecked")
	@Override
	public List<Action> executeStep() throws Exception {
		List<Action> actions = new ArrayList<>();

		// iterate over received messages
		List<Percept> percepts = getBrain().getAllPerceptions();
		for (Percept percept : percepts) {
			Action action = percept.getPerceptContent();
			Pair<String, String> payload = (Pair<String, String>) action.getPayload();

			// process observation
			if (action.getActionType().equals(CleanActionType.SENSING_ACT.toString())) {
				String area = payload.getValue1();

				// get location
				String obs[] = area.split("#");
				String loc[] = obs[0].substring(4, obs[0].length() - 1) .split(",");
				lastLocation = "{"+x+","+y+"}";
				x = Integer.parseInt(loc[1]);
				y = Integer.parseInt(loc[2]);
				
				System.out.println("Finding location");
				
				direction = loc[3];

				// get dirt
				dirtObserved = new Vector<Location>();
				for (int i = 1; i < obs.length; i++) {
					if (obs[i].startsWith("dirt")) {
						String dirtLoc[] = obs[i].substring(5, obs[i].length() - 1) .split(",");
						int dirtX = Integer.parseInt(dirtLoc[0]);
						int dirtY = Integer.parseInt(dirtLoc[1]);

						dirtObserved.add(new Location("", "", "", dirtX, dirtY));
					}
				}
			} else if (action instanceof HttpAction) {
				HttpAction outboundHttp = respondHttp((HttpAction) action);
				if (outboundHttp != null)
					actions.add(outboundHttp);
			}
		}

		if (!initialised) return actions;

		// clean if there is dirt on the way
		if (dirt()) {
			goals.push(currentGoal);
			currentGoal = "";
			goals.push("CLEAN");
		}

		// get current goal
		if (currentGoal.equals(""))
			currentGoal = goals.pop();

		System.out.println("Goal: " + getBrain().getAgentId() + "," + currentGoal);

		// find 0,0 on the grid
		if (currentGoal.equals("FIND_START"))
			findStart(actions);

		// sweep
		if (currentGoal.equals("SWEEP"))
			sweep(actions);

		// clean
		if (currentGoal.equals("CLEAN")) {
			clean(actions);
			currentGoal = "";
		}

		return actions;
	}

	private boolean dirt() {
		for (int i = 0; i < dirtObserved.size(); i++)
			if (dirtObserved.get(i).getX() == x && dirtObserved.get(i).getY() == y)
				return true;

		return false;
	}

	private void findStart(List<Action> actions) {
		// reached start, begin sweeping
		if (x == 0 && y == 0) {
			currentGoal = "";
			goals.push("SWEEP");
		}

		// go west
		if (x != 0) {
			// face west
			if (!direction.equals(Directions.WEST.toString()))
				turnToFace(actions, Directions.WEST);
			else
				move(actions);    		
		}
		// go north
		else if (y != 0) {
			// face north
			if (!direction.equals(Directions.NORTH.toString()))
				turnToFace(actions, Directions.NORTH);
			else
				move(actions);
		}
	}

	private void sweep(List<Action> actions) {
		String currentLocation = "{"+x+","+y+"}";
		System.out.println(lastLocation+"   "+currentLocation);
		if (lastLocation.equals(currentLocation)) {
		
			turnRight(actions);
			move(actions);
			return;
		}
		checkedPlaces.add(currentLocation);
		HashMap<Directions, String> neighbouringLocations = new HashMap<Directions, String>();
		neighbouringLocations.put(Directions.WEST, "{"+(x-1)+","+y+"}");
		neighbouringLocations.put(Directions.EAST, "{"+(x+1)+","+y+"}");
		neighbouringLocations.put(Directions.NORTH,"{"+(x)+","+(y-1)+"}");
		neighbouringLocations.put(Directions.SOUTH,"{"+(x)+","+(y+1)+"}");

		HashMap<Directions, String> possibleNeighbours = new HashMap<Directions, String>();

		for (Directions neighbour : neighbouringLocations.keySet()) {
			if (!checkedPlaces.contains(neighbouringLocations.get(neighbour))) {
				possibleNeighbours.put(neighbour, neighbouringLocations.get(neighbour));
			}
		}
		if (possibleNeighbours.containsKey(Directions.valueOf(direction))) {
			move(actions);
			return;
		}
		
		//turnToFace(actions, Directions.);
	//	move(actions);
	}

	private void turnToFace(List<Action> actions, Directions desiredDirection) {
		Directions current = Directions.valueOf(direction);
		if (current.equals(desiredDirection)) return;

		if (current.equals(Directions.EAST)) {
			if (desiredDirection.equals(Directions.NORTH)) {
				turnLeft(actions);
			}else if (desiredDirection.equals(Directions.SOUTH)) {
				turnRight(actions);
			}else if (desiredDirection.equals(Directions.WEST)) {
				turnRight(actions);
				turnRight(actions);
			}
		}else if (current.equals(Directions.WEST)) {
			if (desiredDirection.equals(Directions.NORTH)) {
				turnRight(actions);
			}else if (desiredDirection.equals(Directions.SOUTH)) {
				turnLeft(actions);
			}else if (desiredDirection.equals(Directions.EAST)) {
				turnRight(actions);
				turnRight(actions);
			}
		}else if (current.equals(Directions.NORTH)) {
			if (desiredDirection.equals(Directions.EAST)) {
				turnRight(actions);
			}else if (desiredDirection.equals(Directions.SOUTH)) {
				turnRight(actions);
				turnRight(actions);
			}else if (desiredDirection.equals(Directions.WEST)) {
				turnLeft(actions);
			}
		}else if (current.equals(Directions.SOUTH)) {
			if (desiredDirection.equals(Directions.WEST)) {
				turnRight(actions);
			}else if (desiredDirection.equals(Directions.NORTH)) {
				turnRight(actions);
				turnRight(actions);
			}else if (desiredDirection.equals(Directions.EAST)) {
				turnLeft(actions);
			}
		}
	}
	private void turnLeft(List<Action> actions) {
		Action action = new PhysicalAct(environmentAgent, new Pair<String, String>(getBrain().getAgentId(), "TURN_LEFT"));
		actions.add(action);
	}

	private void turnRight(List<Action> actions) {
		Action action = new PhysicalAct(environmentAgent, new Pair<String, String>(getBrain().getAgentId(), "TURN_RIGHT"));
		actions.add(action);
	}

	private void move(List<Action> actions) {
		System.out.println(this.getBrain().getAgentId()+" is moving.");
		Action action = new PhysicalAct(environmentAgent, new Pair<String, String>(getBrain().getAgentId(), "MOVE"));
		actions.add(action);
	}

	private void clean(List<Action> actions) {
		Action action = new PhysicalAct(environmentAgent, new Pair<String, String>(getBrain().getAgentId(), "CLEAN"));
		actions.add(action);
	}

	private HttpAction respondHttp(HttpAction action) throws NoPhysicsException {
		HttpRequest request = (HttpRequest) action.getPayload().getValue(1);
		Map<String, String[]> httpParameters = request.getParameters();

		if (httpParameters.containsKey("x")) {
			try {
				x = Integer.parseInt(httpParameters.get("x")[0]);
			}catch(NumberFormatException e) {

			}
		}
		if (httpParameters.containsKey("y")) {
			try {
				y = Integer.parseInt(httpParameters.get("y")[0]);
			}catch(NumberFormatException e) {

			}
		}
		if (httpParameters.containsKey("direction")) {

			direction = httpParameters.get("direction")[0];

		}
		if (direction != null && x != null && y != null) {
			initialised = true;

			System.out.println("Starting Java agent " + getBrain().getAgentId() + " at location (" + x + "," + y + ")");
			((CleanPhysics)getBrain().getBody().getPhysics()).addAgent(getBrain().getAgentId(), direction, x, y) ;

			goals = new Stack<String>();
			goals.push("FIND_START");
			currentGoal = "";
			dirtObserved = new Vector<Location>();
		}

		return new HttpAction(new Pair<Object, HttpResponse>(action.getPayload().getValue(0), new HttpResponse("Initialised agent.")), false);
	}

}