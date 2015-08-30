package uk.ac.rhul.cs.dice.star.example.dirt;

import uk.ac.rhul.cs.dice.star.action.AbstractSensor;
import uk.ac.rhul.cs.dice.star.action.Action;

public class DirtSensor extends AbstractSensor {

	public DirtSensor() {
		super();
		addType(Action.ActionType.HTTP.toString());
	}
}
