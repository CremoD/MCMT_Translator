package DataSchema;

import java.util.HashMap;

public class ConstantFactory {

	// create an object of Case Variable Factory (singleton pattern)
	private static ConstantFactory constant_factory = new ConstantFactory();
	public static HashMap<String, Constant> constant_list;

	// make the constructor private so that this class cannot be instantiated
	private ConstantFactory() {
		constant_list = new HashMap<String, Constant>();
		SortFactory sort_factory = SortFactory.getInstance();
		Sort string_sort = sort_factory.getSort("String_sort");
		Sort bool = sort_factory.getSort("bool");

		getConstant("Idle", string_sort);
		getConstant("Enabled", string_sort);
		getConstant("Active", string_sort);
		getConstant("Completed", string_sort);
		getConstant("Active1", string_sort);
		getConstant("Active2", string_sort);
		getConstant("True", bool);
		getConstant("False", bool);
		getConstant("Undef", string_sort);



	}

	// get the unique object
	public static ConstantFactory getInstance() {
		return constant_factory;
	}

	// method for creating a constant if it doesn't exist, or return it if it
	// exists
	public Constant getConstant(String c_name, Sort sort) {
		if (constant_list.containsKey(c_name))
			return constant_list.get(c_name);
		else {
			Constant c = new Constant(c_name, sort);
			constant_list.put(c_name, c);
			return c;
		}

	}
	
	// method for getting the Strings representing the constant
	public String printConstants() {
		String result = "";
		for (Constant value : constant_list.values()) {
			result += value.mcmtDeclaration();
		}
		return result;
	}

	// method for printing sort list
	public String printConstantList() {
		String result = ":db_constants ";
		for (Constant value : constant_list.values()) {
			result += value + " ";
		}
		return result + "\n";
	}
	

}
