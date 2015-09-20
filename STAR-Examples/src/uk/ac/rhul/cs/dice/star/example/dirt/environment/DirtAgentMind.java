package uk.ac.rhul.cs.dice.star.example.dirt.environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import play.Logger;
import exceptions.NoPhysicsException;
import exceptions.ResourceNotFoundException;
import exceptions.ViewNotFoundException;
import uk.ac.rhul.cs.dice.star.action.Action;
import uk.ac.rhul.cs.dice.star.action.Event;
import uk.ac.rhul.cs.dice.star.action.HttpAction;
import uk.ac.rhul.cs.dice.star.action.Percept;
import uk.ac.rhul.cs.dice.star.agent.AbstractAgentMind;
import uk.ac.rhul.cs.dice.star.agent.Renderer;
import uk.ac.rhul.cs.dice.star.container.ContainerHistory;
import uk.ac.rhul.cs.dice.star.container.DefaultContainer;
import uk.ac.rhul.cs.dice.star.example.dirt.CleanPhysics;
import uk.ac.rhul.cs.dice.star.example.dirt.Directions;
import uk.ac.rhul.cs.dice.star.example.dirt.Location;
import uk.ac.rhul.cs.dice.star.example.dirt.SensingAct;
import uk.ac.rhul.cs.dice.star.example.dirt.sweep.CleanActionType;
import uk.ac.rhul.cs.dice.star.http.ErrorCode;
import uk.ac.rhul.cs.dice.star.http.HttpRequest;
import uk.ac.rhul.cs.dice.star.http.HttpResponse;

public class DirtAgentMind extends AbstractAgentMind {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean initialised = false;
	private long lastEventRequest;
	public DirtAgentMind() {
		super();
	}
	public List<Action> initialise() {
		return new ArrayList<Action>();
	}
	@Override
	public List<Action> executeStep() throws Exception {

		List<Action> actions = new ArrayList<Action>();

		if (!initialised ) {
			List<Action> initActions = initialise();
			if (initActions != null)
				actions.addAll(initActions);
			initialised = true;			
		} else {
			
				// iterate over received messages
			    List<Percept> percepts = getBrain().getAllPerceptions();
		        for (Percept percept : percepts) {
		            Action action = percept.getPerceptContent();
		            Pair<String, String> payload = (Pair<String, String>) action.getPayload();
		            
		            // update agent after a physical action
		            if (action.getActionType().equals(CleanActionType.PHYSICAL_ACT.toString())) {
		            	System.out.println("Received physical act");
		            	String area = getPhysics().observeArea(payload.getValue0());
		        		Action act = new SensingAct(payload.getValue0(), new Pair<String, String>(getBrain().getAgentId(), area));
		        		actions.add(act);            	
		            }else if (action instanceof HttpAction) {
						HttpAction outboundHttp = respondHttp((HttpAction) action);
						if (outboundHttp != null)
							actions.add(outboundHttp);
					}
		        }
			
		}
		
		
		return actions;
	}

	public void createEnvironment(int width, int height, Location[] agentLoc, Location[] dirtLoc) throws NoPhysicsException {

		// create grid
		getPhysics().createGrid(width, height);

		// create agents
		for (int i = 0; i < agentLoc.length; i++)
			addAgent(agentLoc[i].getAgent(), agentLoc[i].getAgentDirection(), agentLoc[i].getX(), agentLoc[i].getY());

		// create dirt
		for (int i = 0; i < dirtLoc.length; i++)
			addDirt(dirtLoc[i].getDirt(), dirtLoc[i].getX(), dirtLoc[i].getY());
	}

	public String getRecentEvents() {

		ContainerHistory history = ((DefaultContainer)this.getBrain().getBody().getEnvironment()).getHistory();
		int size = history.size();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < size; i++) {
			Event evt = history.get(i);
			Action act = evt.getAction();
			Pair<String, String> payload = (Pair<String, String>) act.getPayload();

			if (act.getActionType().equals(CleanActionType.PHYSICAL_ACT.toString()))
				builder.append(evt.getTimestamp() + "," + payload.getValue0() + "," + payload.getValue1() + "\n");
		}
		history.clear();
		return builder.toString();
	}

	public boolean addAgent(String agent, String direction, int x, int y) throws NoPhysicsException {
		System.out.println("Adding agent");
		 return getPhysics().addAgent(agent, direction, x, y);
	}

	public void addDirt(String dirt, int x, int y) throws NoPhysicsException {
		getPhysics().addDirt(dirt, x, y);

		System.out.println("Dirt " + dirt + " at location (" + x + "," + y + ")");
	}

	public void removeDirt(int x, int y) throws NoPhysicsException {
		String dirt = getPhysics().removeDirt(x, y);

		System.out.println("Dirt " + dirt + " removed from location (" + x + "," + y + ")");		
	}

	public void moveUser(int x, int y) throws NoPhysicsException {
		getPhysics().moveUser(x, y);

		System.out.println("User moved to location (" + x + "," + y + ")");		
	}
	private CleanPhysics getPhysics() throws NoPhysicsException {
		try {
			CleanPhysics physics = (CleanPhysics) getBrain().getBody().getPhysics();
			System.out.println("Physics is "+physics);
			return physics;
		}catch (ClassCastException e) {
			throw new NoPhysicsException(e);
		}
	}

	private void start(int width, int height, String agentStr, String dirtStr) throws NoPhysicsException {
		System.out.println("Cleaning robot starting...");


		/*String agentArray[] = agentStr.split("-");
		int length = agentArray.length;
		if (agentStr.equals("")) length = 0;
		
		Location agents[] = new Location[length];
		for (int i=0; i < agents.length; i++) {
			String loc[] = agentArray[i].split(",");
			//if (loc.length > 2)
			agents[i] = new Location(loc[0], Directions.EAST.toString(), "", Integer.parseInt(loc[1]), Integer.parseInt(loc[2]));
		}

		String dirtArray[] = dirtStr.split("-");
		length = dirtArray.length;
		if (dirtStr.equals("")) length = 0;
		
		Location dirt[] = new Location[dirtArray.length];
		for (int i=0; i < dirt.length; i++) {
			String loc[] = dirtArray[i].split(",");
			if (loc.length == 1) continue;
			//if (loc.length > 2)
			dirt[i] = new Location("", "", loc[0], Integer.parseInt(loc[1]), Integer.parseInt(loc[2]));
		}*/

		createEnvironment(width, height, new Location[]{},  new Location[]{});
	}
	private void addEvent(String asText) {
		// TODO Auto-generated method stub

	}
	private HttpResponse respondToAjaxViewRequests(Map<String, String[]> queryParameters) throws NumberFormatException, NoPhysicsException {
		if (queryParameters.get("task") != null) {
			if (queryParameters.get("task")[0].equals("start")) {
				String width = "0";
				String height = "0";
				String agents = "";
				String dirt  = "";
				if (queryParameters.containsKey("agents") && queryParameters.containsKey("dirt")) {	
					agents = queryParameters.get("agents")[0];
					dirt = queryParameters.get("dirt")[0];

					if (queryParameters.containsKey("width")) width = queryParameters.get("width")[0];
					if (queryParameters.containsKey("height")) height = queryParameters.get("height")[0];

					start(Integer.parseInt(width), Integer.parseInt(height), agents, dirt);
					return new HttpResponse("");
				}
			}
			else if (queryParameters.get("task")[0].equals("getevents")) {
				return new HttpResponse(getRecentEvents());
			}
			else if (queryParameters.get("task")[0].equals("adddirt")) {
				if (queryParameters.containsKey("id") && queryParameters.containsKey("x") && queryParameters.containsKey("y")) {	
					addDirt(queryParameters.get("id")[0], Integer.parseInt(queryParameters.get("x")[0]), Integer.parseInt(queryParameters.get("y")[0]));
					return new HttpResponse("");
				}
				return new HttpResponse(ErrorCode.AGENT_BADREQUEST);
			}
			else if (queryParameters.get("task")[0].equals("addagent")) {
				if (queryParameters.containsKey("id") && queryParameters.containsKey("direction") &&  queryParameters.containsKey("x") && queryParameters.containsKey("y")) {	
					boolean success = addAgent(queryParameters.get("id")[0], queryParameters.get("direction")[0], Integer.parseInt(queryParameters.get("x")[0]), Integer.parseInt(queryParameters.get("y")[0]));
					if (success)
					return new HttpResponse("");
				}
				return new HttpResponse(ErrorCode.AGENT_BADREQUEST);
			}
			else if (queryParameters.get("task")[0].equals("moveuser")) {
				if (queryParameters.containsKey("x") && queryParameters.containsKey("y")) {	
					moveUser(Integer.parseInt(queryParameters.get("x")[0]), Integer.parseInt(queryParameters.get("y")[0]));
					return new HttpResponse("");
				}
				return new HttpResponse(ErrorCode.AGENT_BADREQUEST);
			}
			else if (queryParameters.get("task")[0].equals("addevent"))
				addEvent(queryParameters.get("event")[0]);

			return new HttpResponse("");
		}
		return new HttpResponse(ErrorCode.AGENT_BADREQUEST);
	}
	@SuppressWarnings("unchecked")
	private HttpAction respondHttp(HttpAction action) {


		HttpRequest request = (HttpRequest) action.getPayload().getValue(1);

		Map<String, String[]> httpParameters = request.getParameters();

		if (!httpParameters.isEmpty()) {	
			try {
				HttpResponse response = respondToAjaxViewRequests(httpParameters);
				return new HttpAction(new Pair<Long, HttpResponse>((Long) action.getPayload().getValue(0), response), false);
			} catch (NumberFormatException | NoPhysicsException e) {
				e.printStackTrace();
				return new HttpAction(new Pair<Long, HttpResponse>((Long) action.getPayload().getValue(0), new HttpResponse(ErrorCode.CONTAINER_BADREQUEST)), false);
			}
		}
		Renderer renderer = getBrain().getBody().getRenderer();
		Logger.debug("Views: "+renderer.getViews().keySet().toString());
		Logger.debug("Resources: "+renderer.getResources().keySet().toString());
		String url = "/v1/"+this.getBrain().getBody().getEnvironment().getId()+"/"+this.getBrain().getAgentId();
		try {

			Map<String, String> resources = new HashMap<String, String>();

			resources.put("bootstrapCss", renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.environment.bootstrap"));
			resources.put("stylesCss", renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.environment.styles"));
			resources.put("dashboardCss", renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.environment.dashboard"));

			resources.put("globePng", renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.environment.globe"));
			resources.put("userPng", renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.environment.user"));
			resources.put("kettlePng", renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.environment.kettle"));
			resources.put("robot0Png", renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.environment.robot0"));
			resources.put("robot1Png", renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.environment.robot1"));
			resources.put("robot2Png", renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.environment.robot2"));
			resources.put("robot3Png", renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.environment.robot3"));
			resources.put("robotIconPng", renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.environment.robotIcon"));
			resources.put("dirt2Png", renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.environment.dirt2"));
			resources.put("agentPng", renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.environment.agent"));
			resources.put("user1Png", renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.environment.user1"));

			resources.put("scriptsJs", renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.environment.scripts"));
			resources.put("utilJs", renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.environment.util"));
			resources.put("clientGolemSupportJs", renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.environment.client-golem-support"));
			resources.put("layoutScriptsJs", renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.environment.layout-scripts"));
			resources.put("jqueryJs", renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.environment.jquery"));

			return new HttpAction(new Pair<Object, HttpResponse>(action.getPayload().getValue(0), new HttpResponse(renderer.
					//Wrap the content in some agent-specific boilerplate
					setViewTemplate(true).

					//Set the view to render
					setView("uk.ac.rhul.cs.dice.star.example.dirt.environment.view").

					addParam("resources", resources).

					addParam("url", url).
					//Add a parameter to pass into the view
					//addParam("title", "Applications").
					//Add a parameter which is the URL of a resource
					//addParam("js", renderer.getResourceUrl("gumby.js")).
					//Render the HTML 
					render())), false);
		} catch (ViewNotFoundException e) {
			return new HttpAction(new Pair<Object, HttpResponse>(action.getPayload().getValue(0), new HttpResponse(ErrorCode.VIEWNOTFOUND_VIEWNOTFOUND)), false);

		} catch (ResourceNotFoundException e) {
			return new HttpAction(new Pair<Object, HttpResponse>(action.getPayload().getValue(0), new HttpResponse(e.getMessage())), false);
		}
	}
}
