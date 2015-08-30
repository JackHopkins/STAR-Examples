package uk.ac.rhul.cs.dice.star.example.helloworld;

import java.util.ArrayList;
import java.util.List;

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

public class HelloWorldAgentMind extends AbstractAgentMind {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean initialised = false;
	public HelloWorldAgentMind() {
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
			String cssLocation = renderer.getResourceUrl("uk.ac.rhul.cs.dice.star.example.helloworld.star");
			
			return new HttpAction(new Pair(action.getPayload().getValue(0), new HttpResponse(renderer.
					//Wrap the content in some agent-specific boilerplate
					setViewTemplate(true).
					
					//Set the view to render
					setView("uk.ac.rhul.cs.dice.star.example.helloworld.view").
					
					addParam("star", cssLocation).

					//Add a parameter to pass into the view
					//addParam("title", "Applications").
					//Add a parameter which is the URL of a resource
					//addParam("js", renderer.getResourceUrl("gumby.js")).
					//Render the HTML 
					render())), false);
		} catch (ViewNotFoundException e) {
			return new HttpAction(new Pair(action.getPayload().getValue(0), new HttpResponse(ErrorCode.VIEWNOTFOUND_VIEWNOTFOUND)), false);

		} catch (ResourceNotFoundException e) {
			return new HttpAction(new Pair(action.getPayload().getValue(0), new HttpResponse(ErrorCode.RESOURCENOTFOUND_RESOURCENOTFOUND)), false);
		}
	}
}
