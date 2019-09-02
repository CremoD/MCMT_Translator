package data_aware_bpmn.dataschema;

import java.util.HashMap;

public class CaseVariableFactory {

	// create an object of Case Variable Factory (singleton pattern)
	private static CaseVariableFactory cv_factory = new CaseVariableFactory ();
	public static HashMap<String, CaseVariable> casevariable_list = new HashMap<String, CaseVariable>();
		
	// make the constructor private so that this class cannot be instantiated
	private CaseVariableFactory() {
	}

	// get the unique object
	public static CaseVariableFactory getInstance() {
		return cv_factory;
	}

	// method for creating a casevariable if it doesn't exist, or return it if it
	// exists
	public CaseVariable getCaseVariable(String cv_name, Sort sort, boolean type) {
		if (casevariable_list.containsKey(cv_name))
			return casevariable_list.get(cv_name);
		else {
			CaseVariable cv = new CaseVariable(cv_name, sort, type);
			casevariable_list.put(cv_name, cv);
			return cv;
		}

	}

	// method for getting the Strings representing the sorts
	public String printCaseVariables() {
		String result = "";
		for (CaseVariable value : casevariable_list.values()) {
			result += value.getMCMT_Declaration();
		}
		return result;
	}
	
	public String initialize() {
		String result = "";
		for (CaseVariable value : casevariable_list.values()) {
			if(value.getLifeCycle() == 2)
				result += "(= " + value.getName() + " Enabled) ";
			else if (value.getLifeCycle() == 1)
				result += "(= " + value.getName() + " Idle) ";
			else {
				if (value.getSort().getName().equals("bool"))
					result += "(= " + value.getName() + " false) ";
				else
					result += "(= " + value.getName() + " NULL_" + value.getSort().getName()+") ";
			}
		}
		return result;
	}
}