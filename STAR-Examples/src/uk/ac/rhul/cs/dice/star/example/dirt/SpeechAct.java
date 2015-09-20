package uk.ac.rhul.cs.dice.star.example.dirt;

import org.javatuples.Tuple;

import uk.ac.rhul.cs.dice.star.action.Action;
import uk.ac.rhul.cs.dice.star.example.dirt.sweep.CleanActionType;

public class SpeechAct extends Action {

	public SpeechAct(String recipient, Tuple payload) {
		super(CleanActionType.SPEECH_ACT.toString(), recipient, payload);
	}

}