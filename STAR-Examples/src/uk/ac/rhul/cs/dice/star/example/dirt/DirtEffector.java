package uk.ac.rhul.cs.dice.star.example.dirt;

import uk.ac.rhul.cs.dice.star.action.AbstractEffector;
import uk.ac.rhul.cs.dice.star.action.Action;

public class DirtEffector extends AbstractEffector {

	public DirtEffector() {
		super();
		addType(Action.ActionType.HTTP.toString());
	}

}
