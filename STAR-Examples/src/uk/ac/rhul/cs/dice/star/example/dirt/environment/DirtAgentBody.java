package uk.ac.rhul.cs.dice.star.example.dirt.environment;

import java.util.concurrent.ConcurrentHashMap;

import uk.ac.rhul.cs.dice.star.action.Action;
import uk.ac.rhul.cs.dice.star.action.Effector;
import uk.ac.rhul.cs.dice.star.action.Sensor;
import uk.ac.rhul.cs.dice.star.agent.AbstractAgentBody;
import uk.ac.rhul.cs.dice.star.agent.AgentBrain;
import uk.ac.rhul.cs.dice.star.container.Environment;

public class DirtAgentBody extends AbstractAgentBody {

    private String id;
    private Environment environment;
    private final ConcurrentHashMap<String, Sensor> sensors;
    private final ConcurrentHashMap<String, Effector> effectors;
    private AgentBrain brain;

    public DirtAgentBody() {
    	super();
    	sensors = new ConcurrentHashMap<>();
    	effectors = new ConcurrentHashMap<>();
    }
    public DirtAgentBody(String agentId) {
    	this();
       setAgentId(agentId.toLowerCase());
    }

    @Override
    public boolean act(Action action) {
        String actionType = action.getActionType();
        for (Effector effector : effectors.values()) {
            if (effector.handlesType(actionType)) {
                effector.act(action);
                return true;
            }
        }

        return false;
    }

    @Override
    public ConcurrentHashMap<String, Effector> getAllEffectors() {
        return effectors;
    }

    @Override
    public ConcurrentHashMap<String, Sensor> getAllSensors() {
        return sensors;
    }

    @Override
    public AgentBrain getBrain() {
        return brain;
    }

    @Override
    public Effector getEffector(String effectorId) {
        return effectors.get(effectorId);
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Sensor getSensor(String sensorId) {
        return sensors.get(sensorId);
    }

    @Override
    public boolean perceive(Action action) {
        String actionType = action.getActionType();

        for (Sensor sensor : sensors.values()) {
            if (sensor.handlesType(actionType)) {
                sensor.sense(action);
                return true;
            }
        }

        return false;
    }

    @Override
    public void registerEffector(Effector effector) {
        if (!effectors.containsKey(effector.getId())) {
            effectors.put(effector.getId(), effector);
        }
    }

    @Override
    public void registerSensor(Sensor sensor, boolean subscribeToBroadcasts) {
        if (!sensors.containsKey(sensor.getId())) {
            if (brain != null)
                sensor.setSensorHasPerceptListener(brain);
            sensors.put(sensor.getId(), sensor);
        }
        if (subscribeToBroadcasts) {
            getEnvironment().subscribe(this, sensor);
        }
    }

    /**
     * Set the id for this agent.
     * 
     * @param id
     *            the id to set for this agent
     */
    public void setAgentId(String id) {
        this.id = id;
    }

    @Override
    public void setBrain(AgentBrain brain) {
        this.brain = brain;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }


	
}
