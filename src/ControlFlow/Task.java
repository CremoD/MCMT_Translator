package ControlFlow;

import DataSchema.ConjunctiveSelectQuery;

public class Task extends Block{
	private String name;
	
	public Task (String name) {
		this.name = name;
		this.sub_blocks = new Block[0];
		
	}
	
	

	@Override
	public String mcmt_translation() {
		return "";
	}

}
