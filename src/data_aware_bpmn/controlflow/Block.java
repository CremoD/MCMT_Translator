package data_aware_bpmn.controlflow;

import data_aware_bpmn.dataschema.CaseVariable;
import data_aware_bpmn.exception.EevarOverflowException;
import data_aware_bpmn.exception.InvalidInputException;
import data_aware_bpmn.exception.UnmatchingSortException;

public abstract class Block {
	public Block[] sub_blocks;
	public String name;
	public CaseVariable life_cycle;
	
	public abstract String mcmt_translation() throws InvalidInputException, UnmatchingSortException, EevarOverflowException;
}
