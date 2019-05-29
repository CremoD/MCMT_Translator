package DataSchema;

import java.util.HashMap;

public class InsertTransition {
	
	private ConjunctiveSelectQuery precondition;
	private HashMap<String, Sort> eevar_list;
	private String guard;
	private String local_update = "";
	private String local_static = "";
	
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
		
		// update part
		for (RepositoryRelation rep : RelationFactory.repository.values()) {
			if (rep.equals(r)) {
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
					
					local_update += ":val " + variables[i] + "\n";
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
	public String getLocal_update() {
		return local_update;
	}

	public void setLocal_update(String local_update) {
		this.local_update = local_update;
	}

	// method to print the complete insert transition
	public String generateMCMT() {
		String final_mcmt = ":transition\n:var j\n:var x\n"; // to do, check for y
		final_mcmt +=":guard " + this.guard + "\n";
		final_mcmt += ":numcases 2\n:case (= j x)\n";
		final_mcmt += this.local_update + "\n";
		final_mcmt += ":case\n";
		final_mcmt += local_static;
		
		
		
		
		
		
		
		
		return final_mcmt;
	}

}
