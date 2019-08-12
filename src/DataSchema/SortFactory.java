package DataSchema;

import java.util.HashMap;

public class SortFactory {
	// create an object of Relation Factory (singleton pattern)
	private static SortFactory sort_factory = new SortFactory ();
	public static HashMap<String, Sort> sorts;
	
	// make the constructor private so that this class cannot be instantiated
	private SortFactory() {
		sorts = new HashMap<String, Sort>();
		getSort("bool");
		getSort("int");
	}
	
	// get the unique object
	public static SortFactory getInstance() {
		return sort_factory;
	}
	
	// method for creating a sort if it doesn't exist, or return it if it exists
	public Sort getSort(String sort_name) {
		if (sorts.containsKey(sort_name))
			return sorts.get(sort_name);
		else {
			Sort sort = new Sort(sort_name);
			sorts.put(sort_name, sort);
			return sort;
		}
			
	}
	
	// method for getting the Strings representing the sorts
	public String printSort() {
		String result = "";
		for (Sort value : sorts.values()) {
		    result += value; 
		}
		return result;
	}
	
	// method for printing sort list
	public String printSortList() {
		String result = ":db_sorts ";
		for (Sort value : sorts.values()) {
		    result += value.getName() + " "; 
		}
		return result;
	}
	
	
}

