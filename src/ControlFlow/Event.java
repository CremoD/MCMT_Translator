package ControlFlow;

import DataSchema.CaseVariableFactory;
import DataSchema.InsertTransition;
import DataSchema.SortFactory;
import Exception.InvalidInputException;
import Exception.UnmatchingSortException;

public class Event extends Block{
	private InsertTransition effect = null;
	
	public Event (String name) {
		this.name = name;
		this.sub_blocks = new Block[0];
		this.life_cycle = CaseVariableFactory.getInstance().getCaseVariable("lifecycle_" + name, SortFactory.getInstance().getSort("String_sort"), true);
	}
	
	public Event (String name, InsertTransition eff) {
		this.name = name;
		this.effect = eff;
		this.sub_blocks = new Block[0];
		this.life_cycle = CaseVariableFactory.getInstance().getCaseVariable("lifecycle_" + name, SortFactory.getInstance().getSort("String_sort"), true);
	}
	
	public void addTransition (InsertTransition eff) {
		this.effect = eff;
	}
	
	public InsertTransition getTransition() {
		return this.effect;
	}
	
	public boolean hasEffect() {
		if (this.effect != null)
			return true;
		return false;
	}
	
	

	@Override
	public String mcmt_translation() throws InvalidInputException, UnmatchingSortException {
		String result = "";
		
		// control if this event has an effect
		if (this.effect != null)
			result += this.effect.generateMCMT();
	
		
		return result;
	}

}
