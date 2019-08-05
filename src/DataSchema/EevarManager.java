package DataSchema;


import java.util.Collection;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import Exception.EevarOverflowException;

public class EevarManager {
	private static Multimap<Sort, String> eevar_list = HashMultimap.create();;
	
	
	public static String addEevar(Sort sort) throws EevarOverflowException  {
		// check the size of the eevar_list
		if (eevar_list.size() == 10) {
			throw new EevarOverflowException("Maximum number of eevar added, sorry"); // throw exception
		}
		else {
			eevar_list.put(sort,  "E" + (eevar_list.size() + 1));
			return "E" + (eevar_list.size());
		}	
	}
	
	public static Collection<String> getEevarWithSort(Sort sort) {
		return eevar_list.get(sort);
	}
	
	public static String printEevar() {
		String result = "";
		for (Map.Entry<Sort, String> entry : eevar_list.entries()) {
			result += ":eevar " + entry.getValue() + " " + entry.getKey().getName() + "\n";
		}
		return result;
	}
	
	public static Sort getSortByVariable(String name) {
		Multimap<String, Sort> inverse = Multimaps.invertFrom(eevar_list, 
			    ArrayListMultimap.<String,Sort>create());
		return inverse.get(name).iterator().next();
	}
	
	

}
