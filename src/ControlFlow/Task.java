package ControlFlow;

import DataSchema.CaseVariableFactory;
import DataSchema.SortFactory;
import DataSchema.Transition;
import Exception.InvalidInputException;
import Exception.UnmatchingSortException;

public class Task extends Block{
	private Transition effect;
	
	public Task (String name) {
		this.sub_blocks = new Block[0];
		this.life_cycle = CaseVariableFactory.getInstance().getCaseVariable("lifecycle" + name, SortFactory.getInstance().getSort("String_sort"), true);
	}
	
	public Task (String name, Transition eff) {
		this.effect = eff;
		this.sub_blocks = new Block[0];
		this.life_cycle = CaseVariableFactory.getInstance().getCaseVariable("lifecycle" + name, SortFactory.getInstance().getSort("String_sort"), true);
	}
	
	public void addTransition (Transition eff) {
		this.effect = eff;
	}
	
	

	@Override
	public String mcmt_translation() throws InvalidInputException, UnmatchingSortException {
		String result = "";
		
		this.effect.addTaskGuard("(= " + this.life_cycle.getName() + " Enabled)");
		this.effect.set(this.life_cycle, "Completed");
		result += this.effect.generateMCMT();
		
		return result;
	}

}
