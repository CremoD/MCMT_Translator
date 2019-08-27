package data_aware_bpmn.dataschema;

public class Constant {
	// list of attributes
	private String name;
	private Sort sort;

	// constructor
	public Constant(String name, Sort sort) {
		this.name = name;
		this.sort = sort;
	}

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

	// to String method
	@Override
	public String toString() {
		return this.name;
	}
	
	// method that return mcmt declaration
	public String mcmtDeclaration() {
		return ":smt (define " + this.name + " ::" + this.sort.getName() + ")\n";
	}

}
