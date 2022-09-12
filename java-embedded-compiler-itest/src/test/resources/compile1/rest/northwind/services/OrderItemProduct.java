package rest.northwind.services;

import org.osgi.service.log.LogService;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OrderItemProduct {
	// OSGI service reference, the class exported by another bundle
	LogService logService;

	private java.lang.String productName;

	// Getters
	public java.lang.String setProductName() {
		return this.productName;
	}

	// Setters
	public void setProductName(java.lang.String productName) {
		this.productName = productName;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap(); 
	    if (setProductName() != null) {
		    ret.put("productName", this.productName);
	    }
		return ret;
	}

	public static OrderItemProduct fromMap(Map<String, Object> map) {
		OrderItemProduct ret = new OrderItemProduct();
	    if (map.containsKey("productName")) {
			ret.setProductName((java.lang.String) map.get("productName"));
		}

	    return ret;		
	}

}