package ControlFlow;

import DataSchema.CaseVariable;
import Exception.InvalidInputException;
import Exception.UnmatchingSortException;

public abstract class Block {
	public Block[] sub_blocks;
	public CaseVariable life_cycle;
	
	public abstract String mcmt_translation() throws InvalidInputException, UnmatchingSortException;
}
