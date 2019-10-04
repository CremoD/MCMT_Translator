package data_aware_bpmn.dataschema;

/**
 * Class responsible for representing the case variables of the data schema
 * @author DavideCremonini
 *
 */
public class CaseVariable {
	
	// list of attributes
	private String name;
	private Sort sort;
	private boolean one_case;
	// 0 stands for normal case variable, 1 for lifecycle variable, 2 for lifecycle of the root process block.
	// this distinction is necessary for initialization
	private int lifecycle; 
	
	/**
	 * Constructor of the class. To instantiate a case variable, it is necessary to indicate its name, sort and boolean
	 * variable indicating if we are in one case or multiple case dimension
	 * @param name of the case variable
	 * @param sort of the case variable
	 * @param one_case indicating if we are in one case or multiple case dimension
	 */
	public CaseVariable (String name, Sort sort, boolean one_case) {
		this.name = name;
		this.sort = sort;
		this.one_case = one_case;
		this.lifecycle = 0;
	}

	/**
	 * Method for getting the name of the case variable
	 * @return name of the case variable
	 */
	public String getName() {
		return name;
	}

	/**
	 * Method for setting the name of the case variable
	 * @param name of the case variable to be set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Method for getting the sort of the case variable
	 * @return sort of the case variable
	 */
	public Sort getSort() {
		return sort;
	}
	
	/**
	 * Method for setting the sort of the case variable
	 * @param sort of the case variable to be set
	 */
	public void setSort(Sort sort) {
		this.sort = sort;
	}

	/**
	 * Method for getting the boolean variable indicating one case or multiple case dimension
	 * @return value of the boolean variable indicating one case or multiple case dimension
	 */
	public boolean isOne_case() {
		return one_case;
	}

	/**
	 * Method for setting the value of the boolean variable indicating one case or multiple case dimension
	 * @param one_case of the boolean variable indicating one case or multiple case dimension to be set
	 */
	public void setOne_case(boolean one_case) {
		this.one_case = one_case;
	}

	/**
	 * Method for getting the boolean variable indicating whether the case variable is a normal case variable (0),
	 * a control case variable (1) or the control case variable of the root process (2)
	 * @return boolean variable indicating whether the case variable is a normal case variable (0),
	 * a control case variable (1) or the control case variable of the root process (2)
	 */
	public int getLifeCycle() {
		return this.lifecycle;
	}
	
	/**
	 * Method for setting the boolean variable indicating whether the case variable is a normal case variable (0),
	 * a control case variable (1) or the control case variable of the root process (2)
	 * @param value of the boolean variable to be set
	 */
	public void setLifeCycle (int value) {
		this.lifecycle = value;
	}

	
	/**
	 * Method for getting the String representing the MCMT declaration of case variable
	 * @return String representing the MCMT declaration of case variable
	 */
	public String getMCMT_Declaration() {
		if (this.one_case)
			return ":global " + this.name + " " + this.sort.getName() + "\n";
		else
			return ":local " + this.name + " " + this.sort.getName() + "\n";
	}
	
	/**
	 * To string method of the case variable
	 * @return name of the case variable
	 */
	@Override
	public String toString() {
		return this.name;
	}

}
