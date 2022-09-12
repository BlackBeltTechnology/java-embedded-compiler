package rest.northwind.services;

import org.osgi.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class CategoryInfo {
	// OSGI service reference, the class exported by another bundle
	LogService logService;
	Logger logger = LoggerFactory.getLogger(CategoryInfo.class);

	private java.lang.String categoryName;
	private List<rest.northwind.services.ProductInfo> products;

	// Getters
	public java.lang.String setCategoryName() {
		return this.categoryName;
	}
	public List<rest.northwind.services.ProductInfo> setProducts() {
		return this.products;
	}

	// Setters
	public void setCategoryName(java.lang.String categoryName) {
		this.categoryName = categoryName;
	}
	public void setProducts(List<rest.northwind.services.ProductInfo> products) {
		this.products = products;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap(); 
	    if (setCategoryName() != null) {
		    ret.put("categoryName", this.categoryName);
	    }
	    if (setProducts() != null) {
		    ret.put("products", setProducts().stream().map(i -> i.toMap()).collect(Collectors.toList()));
		}
		return ret;
	}

	public static CategoryInfo fromMap(Map<String, Object> map) {
		CategoryInfo ret = new CategoryInfo();
	    if (map.containsKey("categoryName")) {
			ret.setCategoryName((java.lang.String) map.get("categoryName"));
		}
	    if (map.containsKey("products")) {
		    ret.setProducts((List<rest.northwind.services.ProductInfo>) ((List) map.get("products")).stream().map(i -> rest.northwind.services.ProductInfo.fromMap((Map<String, Object>) i)).collect(Collectors.toList()));
	    }

	    return ret;		
	}

}