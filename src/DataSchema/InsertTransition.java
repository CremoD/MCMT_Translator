package DataSchema;

import java.util.HashMap;

public class InsertTransition {
	
	private ConjunctiveSelectQuery precondition;
	private HashMap<String, String> eevar_association;
	private HashMap<CaseVariable, String> set_table; 
	private String name;
	private String guard;
	private String local_update = "";
	private String local_static = "";
	
	public InsertTransition(String name, ConjunctiveSelectQuery precondition) {
		this.precondition = precondition;
		this.eevar_association = precondition.getRef_manager();
		this.guard = precondition.getMCMT();
		this.set_table = new HashMap<CaseVariable, String>();
		this.name = name;
	}
	
	// insert method (insert tuple <variables> into relation r)
	public void insert(RepositoryRelation r, String...variables) {
		// first control that the attributes inserted matches the relation's arity
		if (r.arity() != variables.length) {
			System.out.println("No matching between arity of the relation and number of inserted values");
			return;
		}
		
		// update part
		for (RepositoryRelation rep : RelationFactory.repository.values()) {
			if (rep.equals(r)) {
				for (int i = 0; i < r.arity(); i++) {
					// case in which inserted attribute is not in the eevar
					if (!eevar_association.containsKey(variables[i])) {
						System.out.println("Inserted attribute is not an answer of the precondition");
						return;
					}
					// control of the sorts to see if they match
					if (!r.getAttribute(i).getSort().getName().equals(EevarManager.getSortByVariable(eevar_association.get(variables[i])).getName())) {
						System.out.println("No matching between sorts");
						return;
					}
					
					if (!precondition.isIndex_present())
						guard += "(= " + r.getName() + (i+1)+ "[x] null) ";
					else 
						guard += "(= " + r.getName() + (i+1)+ "[y] null) ";
					
					local_update += ":val " + eevar_association.get(variables[i]) + "\n";
					local_static += ":val " + rep.getName() + (i+1) + "[j]\n";
				}
			}
			else {
				for (int i = 0; i < rep.arity(); i++) {
					local_update += ":val " + rep.getName() + (i+1) + "[j]\n";
					local_static += ":val " + rep.getName() + (i+1) + "[j]\n";
				}
			}
		}
		
	}

	
	// set method 
	public void set(CaseVariable cv, String new_value) {
		// first step: control that the casevariable is in the collection of changes
		// insert it if it is not present
		if (set_table.containsKey(cv)) {
			System.out.println("Variable already set");
			return;
		}
		
		set_table.put(cv, eevar_association.get(new_value));
	}
	
	// generate global updates
	public String generateGlobalMCMT() {
		String result = "";
		
		for (CaseVariable cv : CaseVariableFactory.casevariable_list.values()) {
			// control if the user set a new value for the current case variable
			if (set_table.containsKey(cv)) {
				result += ":val " + set_table.get(cv) + "\n";
			}
			else
				result += ":val " + cv.getName() + "\n";
				
		}
		
		return result;
		
	}

	// generate MCMT in the special case in which there is no insert
	public String generateOneCaseMCMT() {
		String result = ":numcases 1\n:case\n";
		
		for (RepositoryRelation rep : RelationFactory.repository.values()) {
			for (int i = 0; i < rep.arity(); i++) {
				result += ":val " + rep.getName() + (i + 1) + "[j]\n";
			}
		}
		
		result += "\n";
		result += this.generateGlobalMCMT();
		return result;

	}
	
	// method for checking if it is one case
	public boolean is_oneCase() {
		if (this.local_update.equals(""))
			return true;
		else
			return false;
	}
	
	// method to print the complete insert transition
	public String generateMCMT() {
		String final_mcmt = ":comment "+ this.name + "\n:transition\n:var j\n";
		// control of the indexes
		if (this.precondition.isIndex_present()) {
			final_mcmt += ":var x\n";
			if (!this.is_oneCase())
				final_mcmt += ":var y\n";
		} else if (!this.is_oneCase())
			final_mcmt += ":var x\n";

		final_mcmt += ":guard " + this.guard + "\n";

		// one case
		if (this.is_oneCase())
			final_mcmt += this.generateOneCaseMCMT();
		// 2 cases
		else {
			if (this.precondition.isIndex_present())
				final_mcmt += ":numcases 2\n:case (= j y)\n";
			else
				final_mcmt += ":numcases 2\n:case (= j x)\n";

			final_mcmt += this.local_update + "\n";
			final_mcmt += this.generateGlobalMCMT();
			final_mcmt += "\n:case\n";
			final_mcmt += local_static + "\n";
			final_mcmt += this.generateGlobalMCMT();
		}

		return final_mcmt;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////
	// getters and setters
	public ConjunctiveSelectQuery getPrecondition() {
		return precondition;
	}
	public void setPrecondition(ConjunctiveSelectQuery precondition) {
		this.precondition = precondition;
	}
	public HashMap<String, String> getEevar_List() {
		return eevar_association;
	}
	public void setEevar_list(HashMap<String, String> eevar) {
		this.eevar_association = eevar;
	}
	public String getGuard() {
		return guard;
	}
	public void setGuard(String guard) {
		this.guard = guard;
	}
	public String getLocal_update() {
		return local_update;
	}
	public void setLocal_update(String local_update) {
		this.local_update = local_update;
	}
	public String getLocal_static() {
		return local_static;
	}
	public void setLocal_static(String local_static) {
		this.local_static = local_static;
	}
	public HashMap<CaseVariable, String> getSet_table() {
		return set_table;
	}
	public void setSet_table(HashMap<CaseVariable, String> set_table) {
		this.set_table = set_table;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	

}
