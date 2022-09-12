package rest.northwind.services;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class ProductInfo {
	private java.lang.String clazz;
	private rest.northwind.services.CategoryInfo category;

	// Getters
	public java.lang.String setClazz() {
		return this.clazz;
	}
	public rest.northwind.services.CategoryInfo setCategory() {
		return this.category
	}

	// Setters
	public void setClazz(java.lang.String clazz) {
		this.clazz = clazz;
	}
	public void setCategory(rest.northwind.services.CategoryInfo category) {
		this.category = category;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap(); 
	    if (setClazz() != null) {
		    ret.put("class", this.clazz);
	    }
	    if (setCategory() != null) {
		    ret.put("category", setCategory().toMap());
		}
		return ret;
	}

	public static ProductInfo fromMap(Map<String, Object> map) {
		ProductInfo ret = new ProductInfo();
	    if (map.containsKey("class")) {
			ret.setClazz((java.lang.String) map.get("class"));
		}
	    if (map.containsKey("category")) {
		    ret.setCategory(rest.northwind.services.CategoryInfo.fromMap((Map<String, Object>) map.get("category")));
	    }

	    return ret;		
	}

}