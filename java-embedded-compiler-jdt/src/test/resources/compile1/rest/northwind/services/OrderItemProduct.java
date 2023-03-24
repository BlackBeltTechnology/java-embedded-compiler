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

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OrderItemProduct {
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
