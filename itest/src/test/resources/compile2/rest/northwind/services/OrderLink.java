package rest.northwind.services;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OrderLink {

	// Getters

	// Setters

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap(); 
		return ret;
	}

	public static OrderLink fromMap(Map<String, Object> map) {
		OrderLink ret = new OrderLink();

	    return ret;		
	}

}