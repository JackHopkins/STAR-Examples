package uk.ac.rhul.cs.dice.star.example.helloworld;

import uk.ac.rhul.cs.dice.star.action.AbstractEffector;
import uk.ac.rhul.cs.dice.star.action.Action;

public class HelloWorldEffector extends AbstractEffector {

	public HelloWorldEffector() {
		super();
		addType(Action.ActionType.HTTP.toString());
	}

}
