package ControlFlow;

import DataSchema.CaseVariableFactory;
import DataSchema.ConjunctiveSelectQuery;
import DataSchema.InsertTransition;
import DataSchema.SortFactory;
import Exception.EevarOverflowException;
import Exception.InvalidInputException;
import Exception.UnmatchingSortException;

public class SequenceBlock extends Block{
	
	public SequenceBlock (String name) {
		this.name = name;
		this.sub_blocks = new Block[2];
		this.life_cycle = CaseVariableFactory.getInstance().getCaseVariable("lifecycle" + name, SortFactory.getInstance().getSort("String_sort"), true);
	}
	
	public void addB1 (Block b1) {
		this.sub_blocks[0] = b1;
	}
	
	public void addB2 (Block b2) {
		this.sub_blocks[1] = b2;
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

		// second part: B1 COMPLETED --> B1 IDLE and B2 ENABLED
		ConjunctiveSelectQuery secondG = new ConjunctiveSelectQuery();
		secondG.addBinaryCondition(true, this.sub_blocks[0].life_cycle, "Completed");
		InsertTransition secondU = new InsertTransition(this.name + " second translation", secondG);
		secondU.set(this.sub_blocks[0].life_cycle, "Idle");
		secondU.set(this.sub_blocks[1].life_cycle, "Enabled");

		// third part: B2 COMPLETED --> B2 IDLE and itself COMPLETED
		ConjunctiveSelectQuery thirdG = new ConjunctiveSelectQuery();
		thirdG.addBinaryCondition(true, this.sub_blocks[1].life_cycle, "Completed");
		InsertTransition thirdU = new InsertTransition(this.name + " third translation", thirdG);
		thirdU.set(this.life_cycle, "Completed");
		thirdU.set(this.sub_blocks[1].life_cycle, "Idle");
		
		// generate MCMT translation
		result += firstU.generateMCMT() + "\n" + secondU.generateMCMT() + "\n" + thirdU.generateMCMT() + "\n";

		return result;
		
	}

}
