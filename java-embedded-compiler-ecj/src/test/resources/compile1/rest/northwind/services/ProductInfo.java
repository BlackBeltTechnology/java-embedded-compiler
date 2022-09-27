package rest.northwind.services;

/*-
 * #%L
 * Java Embedded compiler ECJ
 * %%
 * Copyright (C) 2018 - 2022 BlackBelt Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
		return this.category;
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
