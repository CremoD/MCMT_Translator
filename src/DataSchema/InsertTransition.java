package DataSchema;

import java.util.HashMap;

public class InsertTransition {
	
	private ConjunctiveSelectQuery precondition;
	private HashMap<String, Sort> eevar_list;
	private String guard;
	
	public InsertTransition(ConjunctiveSelectQuery precondition) {
		this.precondition = precondition;
		this.eevar_list = precondition.getEevar_List();
		this.guard = precondition.getMCMT();
	}
	
	public void insert(RepositoryRelation r, String...variables) {
		// first control that the attributes inserted matches the relation's arity
		if (r.arity() != variables.length) {
			System.out.println("No matching between arity of the relation and number of inserted values");
			return;
		}
		
		for (int i = 0; i < r.arity(); i++) {
			// case in which inserted attribute is not in the eevar
			if (!eevar_list.containsKey(variables[i])) {
				System.out.println("Errorrrrrr");
				return;
			}
			// control of the sorts to see if they match
			if (!r.getAttribute(i).getSort().getName().equals(eevar_list.get(variables[i]).getName())) {
				System.out.println("No matching between sorts");
				return;
			}
			
			if (!precondition.isIndex_present())
				guard += " (= " + r.getName() + (i+1)+ "[x] null)";
			else 
				guard += " (= " + r.getName() + (i+1)+ "[y] null)";
		}	
	}

	
	// getters and setters
	public ConjunctiveSelectQuery getPrecondition() {
		return precondition;
	}

	public void setPrecondition(ConjunctiveSelectQuery precondition) {
		this.precondition = precondition;
	}

	public HashMap<String, Sort> getEevar_List() {
		return eevar_list;
	}

	public void setEevar_list(HashMap<String, Sort> eevar) {
		this.eevar_list = eevar;
	}

	public String getGuard() {
		return guard;
	}

	public void setGuard(String guard) {
		this.guard = guard;
	}

}
