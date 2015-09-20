package uk.ac.rhul.cs.dice.star.example.dirt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.javatuples.Tuple;

import play.Logger;
import exceptions.ResourceNotFoundException;
import exceptions.ViewNotFoundException;
import uk.ac.rhul.cs.dice.star.action.Action;
import uk.ac.rhul.cs.dice.star.action.HttpAction;
import uk.ac.rhul.cs.dice.star.action.Percept;
import uk.ac.rhul.cs.dice.star.agent.AbstractAgentMind;
import uk.ac.rhul.cs.dice.star.agent.Renderer;
import uk.ac.rhul.cs.dice.star.http.ErrorCode;
import uk.ac.rhul.cs.dice.star.http.HttpResponse;

public class DirtAgentMind extends AbstractAgentMind {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean initialised = false;
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
		}
		List<Percept> percepts = getBrain().getAllPerceptions();

		for (Percept percept : percepts) {
			Action action = percept.getPerceptContent();
			//Tuple payload = action.getPayload();
			
			if (action instanceof HttpAction) {
				HttpAction outboundHttp = respondHttp((HttpAction) action);
				if (outboundHttp != null)
				actions.add(outboundHttp);
			}
		}
		return actions;
	}
	private HttpAction respondHttp(HttpAction action) {
		
		Renderer renderer = getBrain().getBody().getRenderer();
		Logger.debug("Views: "+renderer.getViews().keySet().toString());
		Logger.debug("Resources: "+renderer.getResources().keySet().toString());
		
		try {
			
			//Styles
			String bootstrapCss = renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.bootstrap");
			String stylesCss = renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.styles");
			String dashboardCss = renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.dashboard");
			
			//Images
			String globePng = renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.globe");
			String userPng = renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.user");
			String kettlePng = renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.kettle");
			String robot0Png = renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.robot0");
			String robot1Png = renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.robot1");
			String robot2Png = renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.robot2");
			String robot3Png = renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.robot3");
			String robotIconPng = renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.robotIcon");
			String dirt2Png = renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.dirt2");
			String agentPng = renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.agent");
			String user1Png = renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.user1");
			
			String test = renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.test");
			
			//Javascipts
			String scriptsJs = renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.scripts");
			String utilJs = renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.util");
			String clientGolemSupportJs = renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.client-golem-support");
			String layoutScriptsJs = renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.layout-scripts");
			String jqueryJs = renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.dirt.jquery");
			
			Map<String, String> resources = new HashMap<String, String>();
			
			resources.put("bootstrapCss", bootstrapCss);
			resources.put("stylesCss", stylesCss);
			resources.put("dashboardCss", dashboardCss);
			
			resources.put("globePng", globePng);
			resources.put("userPng", userPng);
			resources.put("kettlePng", kettlePng);
			resources.put("robot0Png", robot0Png);
			resources.put("robot1Png", robot1Png);
			resources.put("robot2Png", robot2Png);
			resources.put("robot3Png", robot3Png);
			resources.put("robotIconPng", robotIconPng);
			resources.put("dirt2Png", dirt2Png);
			resources.put("agentPng", agentPng);
			resources.put("user1Png", user1Png);
			
			resources.put("scriptsJs", scriptsJs);
			resources.put("utilJs", utilJs);
			resources.put("clientGolemSupportJs", clientGolemSupportJs);
			resources.put("layoutScriptsJs", layoutScriptsJs);
			resources.put("jqueryJs", jqueryJs);
			
			return new HttpAction(new Pair(action.getPayload().getValue(0), new HttpResponse(renderer.
					//Wrap the content in some agent-specific boilerplate
					setViewTemplate(true).
					
					//Set the view to render
					setView("uk.ac.rhul.cs.dice.star.example.dirt.view").
					
					addParam("resources", resources).

					//Add a parameter to pass into the view
					//addParam("title", "Applications").
					//Add a parameter which is the URL of a resource
					//addParam("js", renderer.getResourceUrl("gumby.js")).
					//Render the HTML 
					render())), false);
		} catch (ViewNotFoundException e) {
			return new HttpAction(new Pair(action.getPayload().getValue(0), new HttpResponse(ErrorCode.VIEWNOTFOUND_VIEWNOTFOUND)), false);

		} catch (ResourceNotFoundException e) {
			return new HttpAction(new Pair(action.getPayload().getValue(0), new HttpResponse(e.getMessage())), false);
		}
	}
}
