package rest.northwind.services;

/*-
 * #%L
 * Java Embedded compiler JDT
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

import javax.ws.rs.Path;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

@Path("test")
public class CategoryInfo {
    Logger log = LoggerFactory.getLogger(CategoryInfo.class);

    private Yaml yaml;

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
