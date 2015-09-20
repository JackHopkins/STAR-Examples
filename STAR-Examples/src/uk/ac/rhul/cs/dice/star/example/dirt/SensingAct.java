package uk.ac.rhul.cs.dice.star.example.dirt;

import org.javatuples.Tuple;

import uk.ac.rhul.cs.dice.star.action.Action;
import uk.ac.rhul.cs.dice.star.example.dirt.sweep.CleanActionType;


public class SensingAct extends Action {

	public SensingAct(String recipient, Tuple payload) {
		super(CleanActionType.SENSING_ACT.toString(), recipient, payload);
	}

}