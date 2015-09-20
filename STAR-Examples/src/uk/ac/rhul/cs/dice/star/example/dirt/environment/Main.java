package uk.ac.rhul.cs.dice.star.example.dirt;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//import models.GolemPlatform;
import uk.ac.rhul.cs.dice.star.agent.AbstractAgentBody;
import uk.ac.rhul.cs.dice.star.agent.AgentFactory;
import uk.ac.rhul.cs.dice.star.agent.DefaultAgentBrain;
import uk.ac.rhul.cs.dice.star.container.AbstractContainer;
import uk.ac.rhul.cs.dice.star.entity.View;
import uk.ac.rhul.cs.dice.star.persistence.Resource;
import uk.ac.rhul.cs.dice.star.sdk.DummyPlatform;
import uk.ac.rhul.cs.dice.star.sdk.DummyResourceFinder;
import uk.ac.rhul.cs.dice.star.sdk.DummyServer;
import uk.ac.rhul.cs.dice.star.sdk.DummyViewFinder;

public class Main {

	public static void main(String[] args) {
		String container = "welcome";
		String agent = "tutorial-agent";

		File parentDirectory = new File(ClassLoader.getSystemResource("uk/ac/rhul/cs/dice/star/example/dirt").getFile());
		DummyServer dummyServer = new DummyServer(parentDirectory);
		
		Map<String, Resource> resources = DummyResourceFinder.findResources(parentDirectory, "uk.ac.rhul.cs.dice.star.example.dirt");//new HashMap<String, Resource>();
		Map<String, View> views = DummyViewFinder.findViews(parentDirectory, "uk.ac.rhul.cs.dice.star.example.dirt");
		
		System.out.println("Resources #: "+resources.size()+", Views #: "+views.size());
		System.out.println("Resources: "+resources.keySet().toString());
		System.out.println("Views: "+views.keySet().toString());
		
		DummyPlatform platform = DummyPlatform.getInstance();
		
		AbstractContainer containerObj = platform.createContainer(container);
		platform.registerContainer(container, containerObj);
		
		try {
		Set<Class<?>> effectors = new HashSet<Class<?>>();
		effectors.add(DirtEffector.class);
		Set<Class<?>> sensors = new HashSet<Class<?>>();
		sensors.add(DirtSensor.class);
		
		AbstractAgentBody body = AgentFactory.initAgent(
				agent,
				DefaultAgentBrain.class,
				DirtAgentMind.class,
				DirtAgentBody.class,
				effectors,
				sensors,
				containerObj);
		body.setRenderer(views, resources);
		
		containerObj.makePresent(body);
		System.out.println("Added agent to \""+container+"\"");
		
		dummyServer.start();
		
		}catch(Exception e) {
			e.printStackTrace();								
		}
	}

}
