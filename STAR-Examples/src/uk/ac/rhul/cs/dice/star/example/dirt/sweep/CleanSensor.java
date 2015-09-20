package uk.ac.rhul.cs.dice.star.example.dirt.sweep;

import uk.ac.rhul.cs.dice.star.action.AbstractSensor;
import uk.ac.rhul.cs.dice.star.action.Action;

public class CleanSensor extends AbstractSensor {

	public CleanSensor() {
		super();

		addType(CleanActionType.SPEECH_ACT.toString());
		addType(CleanActionType.SENSING_ACT.toString());
		addType(CleanActionType.PHYSICAL_ACT.toString());
		addType(Action.ActionType.HTTP.toString());
	}

}