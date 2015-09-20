package uk.ac.rhul.cs.dice.star.example.dirt.sweep;

import uk.ac.rhul.cs.dice.star.action.AbstractEffector;
import uk.ac.rhul.cs.dice.star.action.Action;

public class CleanEffector extends AbstractEffector {

	public CleanEffector() {
		super();

		addType(CleanActionType.SPEECH_ACT.toString());
		addType(CleanActionType.SENSING_ACT.toString());
		addType(CleanActionType.PHYSICAL_ACT.toString());
		addType(Action.ActionType.HTTP.toString());
	}

}