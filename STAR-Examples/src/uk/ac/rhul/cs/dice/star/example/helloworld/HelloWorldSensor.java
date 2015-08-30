package uk.ac.rhul.cs.dice.star.example.helloworld;

import uk.ac.rhul.cs.dice.star.action.AbstractSensor;
import uk.ac.rhul.cs.dice.star.action.Action;

public class HelloWorldSensor extends AbstractSensor {

	public HelloWorldSensor() {
		super();
		addType(Action.ActionType.HTTP.toString());
	}
}
