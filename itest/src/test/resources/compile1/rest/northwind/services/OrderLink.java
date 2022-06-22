package rest.northwind.services;

import org.osgi.service.log.LogService;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OrderLink {
	// OSGI service reference, the class exported by another bundle
	LogService logService;

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