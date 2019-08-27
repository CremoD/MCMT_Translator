package data_aware_bpmn.dataschema;

public class CaseVariable {
	
	// list of attributes
	private String name;
	private Sort sort;
	private boolean one_case;
	
	// constructor
	public CaseVariable (String name, Sort sort, boolean one_case) {
		this.name = name;
		this.sort = sort;
		this.one_case = one_case;
	}

	//getters and setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Sort getSort() {
		return sort;
	}

	public void setSort(Sort sort) {
		this.sort = sort;
	}

	public boolean isOne_case() {
		return one_case;
	}

	public void setOne_case(boolean one_case) {
		this.one_case = one_case;
	}

	
	// get declaration
	public String getMCMT_Declaration() {
		if (this.one_case)
			return ":global " + this.name + " " + this.sort.getName() + "\n";
		else
			return ":local " + this.name + " " + this.sort.getName() + "\n";
	}
	
	// to String method
	@Override
	public String toString() {
		return this.name;
	}

}
