package data_aware_bpmn.dataschema;

import java.util.HashMap;

/**
 * Class responsible for managing the usage and creation of case variables within the program. Its singleton and factory pattern design
 * ensures consistency and robustness, since it ensures the uniqueness of names identifying case variables
 * @author DavideCremonini
 *
 */
public class CaseVariableFactory {

	// create an object of Case Variable Factory (singleton pattern)
	private static CaseVariableFactory cv_factory = new CaseVariableFactory ();
	public static HashMap<String, CaseVariable> casevariable_list = new HashMap<String, CaseVariable>();
		
	/**
	 * Constructor of the class. Make the constructor private so that this class cannot be instantiated
	 */
	private CaseVariableFactory() {
	}

	/**
	 * Method for getting the unique instance of the class (Singleton pattern)
	 * @return unique instance of the class
	 */
	public static CaseVariableFactory getInstance() {
		return cv_factory;
	}

	/**
	 * method for creating a case variable if it doesn't exist, or return it if it exists
	 * @param cv_name of the case variable to check if it is already created or not
	 * @param sort of the case variable in case of new instantiation
	 * @param type of the one/multi case dimension of case variable
	 * @return case variable already created if it exists, or the new created one if it doesn't exist
	 */
	public CaseVariable getCaseVariable(String cv_name, Sort sort, boolean type) {
		if (casevariable_list.containsKey(cv_name))
			return casevariable_list.get(cv_name);
		else {
			CaseVariable cv = new CaseVariable(cv_name, sort, type);
			casevariable_list.put(cv_name, cv);
			return cv;
		}

	}

	/**
	 * Method for getting the String representing the case variable in MCMT specification file
	 * @return String representing the case variable in MCMT specification file
	 */
	public String printCaseVariables() {
		String result = "";
		for (CaseVariable value : casevariable_list.values()) {
			result += value.getMCMT_Declaration();
		}
		return result;
	}
	
	/**
	 * Method for building the MCMT declaration of initialization of the case variables. Normal case variable
	 * are initialized with undef, control case variables to Idle (except from control case variable of root process, 
	 * which is initialized with Enabled).
	 * @return the String representing the MCMT declaration of initialization of the case variables
	 */
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
