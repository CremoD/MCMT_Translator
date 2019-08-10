package ControlFlow;

import DataSchema.CaseVariableFactory;
import DataSchema.ConjunctiveSelectQuery;
import DataSchema.InsertTransition;
import DataSchema.SortFactory;
import Exception.EevarOverflowException;
import Exception.InvalidInputException;
import Exception.UnmatchingSortException;

public class ErrorBlock extends Block{

	private ConjunctiveSelectQuery cond;
	
	public ErrorBlock (String name) {
		this.name = name;
		this.sub_blocks = new Block[1];
		this.life_cycle = CaseVariableFactory.getInstance().getCaseVariable("lifecycle" + name, SortFactory.getInstance().getSort("String_sort"), true);
	}
	
	public ErrorBlock (String name, ConjunctiveSelectQuery cond) {
		this.name = name;
		this.cond = cond;
		this.sub_blocks = new Block[2];
		this.life_cycle = CaseVariableFactory.getInstance().getCaseVariable("lifecycle" + name, SortFactory.getInstance().getSort("String_sort"), true);
	}
	
	public void addB1 (Block b1) {
		this.sub_blocks[0] = b1;
	}
	
	public void addCond (ConjunctiveSelectQuery cond1) {
		this.cond = cond1;
	}
		
	@Override
	public String mcmt_translation() throws InvalidInputException, UnmatchingSortException, EevarOverflowException {
		// TODO Auto-generated method stub
		String result = "";

		// first part: itself ENABLED --> B1 ENABLED and itself ACTIVE
		ConjunctiveSelectQuery firstG = new ConjunctiveSelectQuery();
		firstG.addBinaryCondition(true, this.life_cycle, "Enabled");
		InsertTransition firstU = new InsertTransition(this.name + " first translation", firstG);
		firstU.set(this.life_cycle, "Active");
		firstU.set(this.sub_blocks[0].life_cycle, "Enabled");

		// second part: B1 completed and cond TRUE --> B1 IDLE and B2 ENABLED 
		ConjunctiveSelectQuery secondG = new ConjunctiveSelectQuery();
		secondG.addBinaryCondition(true, this.sub_blocks[0].life_cycle, "Completed");
		InsertTransition secondU = new InsertTransition(this.name + " second translation", secondG);
		secondU.addTaskGuard(cond.getMCMT());
		secondU.set(this.sub_blocks[1].life_cycle, "Enabled");
		secondU.set(this.sub_blocks[0].life_cycle, "Idle");
		

		// third part: B2 completed --> B1 ENABLED and B2 IDLE
		ConjunctiveSelectQuery thirdG = new ConjunctiveSelectQuery();
		thirdG.addBinaryCondition(true, this.sub_blocks[1].life_cycle, "Completed");
		InsertTransition thirdU = new InsertTransition(this.name + " third translation", thirdG);
		thirdU.set(this.sub_blocks[1].life_cycle, "Idle");
		thirdU.set(this.sub_blocks[0].life_cycle, "Enabled");
		
		// fourth part:  B1 completed and cond FALSE --> B1 IDLE and itself COMPLETED
		ConjunctiveSelectQuery fourthG = new ConjunctiveSelectQuery();
		fourthG.addBinaryCondition(true, this.sub_blocks[0].life_cycle, "Completed");
		InsertTransition fourthU = new InsertTransition(this.name + " fourth translation", fourthG);
		fourthU.addTaskGuard(cond.getNegated_mcmt());
		fourthU.set(this.sub_blocks[0].life_cycle, "Idle");
		fourthU.set(this.life_cycle, "Completed");
		
		

		// generate MCMT translation
		result += firstU.generateMCMT() + "\n" + secondU.generateMCMT() + "\n" + thirdU.generateMCMT() + "\n"+ fourthU.generateMCMT() + "\n";

		return result;
		
	}
}
