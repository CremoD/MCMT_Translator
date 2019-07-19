package DataSchema;

import java.util.HashMap;

public class BulkUpdate {
	
	private ConjunctiveSelectQuery precondition;
	private HashMap<String, String> eevar_association;
	private HashMap<CaseVariable, String> set_table; 
	private String name;
	private String guard;
	private String local_update = "";
	private String local_static = "";
	
	public BulkUpdate(String name, ConjunctiveSelectQuery precondition) {
		this.precondition = precondition;
		this.eevar_association = precondition.getRef_manager();
		this.guard = precondition.getMCMT();
		this.set_table = new HashMap<CaseVariable, String>();
		this.name = name;
	}

}
