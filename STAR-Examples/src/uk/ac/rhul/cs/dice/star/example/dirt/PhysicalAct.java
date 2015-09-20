package uk.ac.rhul.cs.dice.star.example.dirt;

import org.javatuples.Tuple;

import uk.ac.rhul.cs.dice.star.action.Action;
import uk.ac.rhul.cs.dice.star.example.dirt.sweep.CleanActionType;


public class PhysicalAct extends Action {

	public PhysicalAct(String recipient, Tuple payload) {
		super(CleanActionType.PHYSICAL_ACT.toString(), recipient, payload);
	}

}