package rest.northwind.services;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class CategoryLink {

	// Getters

	// Setters

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap(); 
		return ret;
	}

	public static CategoryLink fromMap(Map<String, Object> map) {
		CategoryLink ret = new CategoryLink();

	    return ret;		
	}

}