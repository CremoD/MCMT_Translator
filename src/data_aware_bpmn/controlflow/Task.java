package data_aware_bpmn.controlflow;

import data_aware_bpmn.dataschema.CaseVariableFactory;
import data_aware_bpmn.dataschema.SortFactory;
import data_aware_bpmn.dataschema.Transition;
import data_aware_bpmn.exception.InvalidInputException;
import data_aware_bpmn.exception.UnmatchingSortException;

public class Task extends Block{
	private Transition effect;
	
	public Task (String name) {
		this.name = name;
		this.sub_blocks = new Block[0];
		this.life_cycle = CaseVariableFactory.getInstance().getCaseVariable("lifecycle_" + name, SortFactory.getInstance().getSort("String_sort"), true);
		this.life_cycle.setLifeCycle(1);
	}
	
	public Task (String name, Transition eff) {
		this.name = name;
		this.effect = eff;
		this.sub_blocks = new Block[0];
		this.life_cycle = CaseVariableFactory.getInstance().getCaseVariable("lifecycle_" + name, SortFactory.getInstance().getSort("String_sort"), true);
		this.life_cycle.setLifeCycle(1);
	}
	
	public void addTransition (Transition eff) {
		this.effect = eff;
	}
	
	public Transition getTransition() {
		return this.effect;
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
