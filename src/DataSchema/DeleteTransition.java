package DataSchema;

import java.util.HashMap;

import Exception.InvalidInputException;
import Exception.UnmatchingSortException;

public class DeleteTransition {

	private ConjunctiveSelectQuery precondition;
	private HashMap<String, String> eevar_association;
	private HashMap<CaseVariable, String> set_table;
	private String name;
	private String guard;
	private String local_update = "";
	private String local_static = "";

	public DeleteTransition(String name, ConjunctiveSelectQuery precondition) {
		this.precondition = precondition;
		this.eevar_association = precondition.getRef_manager();
		this.guard = precondition.getMCMT();
		this.set_table = new HashMap<CaseVariable, String>();
		this.name = name;
	}

	// insert method (insert tuple <variables> into relation r)
	public void delete(RepositoryRelation r, String... variables) throws InvalidInputException, UnmatchingSortException {
		// first control that the attributes inserted matches the relation's arity
		if (r.arity() != variables.length) {
			throw new InvalidInputException("No matching between arity of the relation and number of deleted values. The number of inserted values should be " + r.arity());
		}

		// update part
		for (RepositoryRelation rep : RelationFactory.repository.values()) {
			if (rep.equals(r)) {
				for (int i = 0; i < r.arity(); i++) {
					// control if it is eevar or constant
					boolean eevar = isEevar(variables[i]);
					
					// control of the sorts to see if they match, case eevar
					checkSorts(eevar, r.getAttribute(i).getSort(), variables[i]);
					
					if (!precondition.isIndex_present()) {
						if (eevar)
							guard += "(= " + r.getName() + (i + 1) + "[x] " + eevar_association.get(variables[i])
									+ ") ";
						else
							guard += "(= " + r.getName() + (i + 1) + "[x] " + variables[i] + ") ";
					} else {
						if (eevar)
							guard += "(= " + r.getName() + (i + 1) + "[y] " + eevar_association.get(variables[i])
									+ ") ";
						else
							guard += "(= " + r.getName() + (i + 1) + "[y] " + variables[i] + ") ";
					}

					local_update += ":val null" + "\n";
					local_static += ":val " + rep.getName() + (i + 1) + "[j]\n";
				}
			} else {
				for (int i = 0; i < rep.arity(); i++) {
					local_update += ":val " + rep.getName() + (i + 1) + "[j]\n";
					local_static += ":val " + rep.getName() + (i + 1) + "[j]\n";
				}
			}
		}

	}

	// set method
	public void set(CaseVariable cv, String new_value) throws UnmatchingSortException, InvalidInputException {
		// first step: control that the casevariable is in the collection of changes
		// insert it if it is not present
		if (set_table.containsKey(cv)) {
			System.out.println("Variable already set");
			return;
		}

		boolean eevar = isEevar(new_value);
		checkSorts(eevar, cv.getSort(), new_value);
		
		if (eevar) 
			set_table.put(cv, eevar_association.get(new_value));		
		else 
			set_table.put(cv, new_value);
		
	}

	// generate global updates
	public String generateGlobalMCMT() {
		String result = "";

		for (CaseVariable cv : CaseVariableFactory.casevariable_list.values()) {
			// control if the user set a new value for the current case variable
			if (set_table.containsKey(cv)) {
				result += ":val " + set_table.get(cv) + "\n";
			} else
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
		String final_mcmt = ":comment " + this.name + "\n:transition\n:var j\n";
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

	// prova, check for matching method
	public void checkSorts(boolean eevar, Sort first, String second) throws UnmatchingSortException {
		if (eevar && !first.getName()
				.equals(EevarManager.getSortByVariable(eevar_association.get(second)).getName())) {
			throw new UnmatchingSortException("No matching between sorts: " + first.getName() + " VS " + EevarManager.getSortByVariable(eevar_association.get(second)).getName());
		}
		if (!eevar && !first.getName().equals(ConstantFactory.constant_list.get(second).getSort().getName())) {
			throw new UnmatchingSortException("No matching between sorts: " + first.getName() + " VS " + ConstantFactory.constant_list.get(second).getSort().getName());
		}
	}
		
	public boolean isEevar (String value) throws InvalidInputException {
		if (!eevar_association.containsKey(value)) {
			if (!ConstantFactory.constant_list.containsKey(value)) {
				throw new InvalidInputException( value + " is not an answer of the precondition or a constant");
			}
			return false;
		}
		return true;
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
