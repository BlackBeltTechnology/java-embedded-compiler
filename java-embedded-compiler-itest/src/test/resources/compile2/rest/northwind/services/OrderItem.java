package rest.northwind.services;

/*-
 * #%L
 * Java Embedded compiler Integration test
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

public class OrderItem {
    private java.lang.String productName;
    private java.lang.String categoryName;
    private java.lang.Double unitPrice;
    private java.lang.Integer quantity;
    private java.lang.Double discount;
    private rest.northwind.services.ProductInfo product;
    private rest.northwind.services.CategoryInfo category;

    // Getters
    public java.lang.String setProductName() {
        return this.productName;
    }
    public java.lang.String setCategoryName() {
        return this.categoryName;
    }
    public java.lang.Double setUnitPrice() {
        return this.unitPrice;
    }
    public java.lang.Integer setQuantity() {
        return this.quantity;
    }
    public java.lang.Double setDiscount() {
        return this.discount;
    }
    public rest.northwind.services.ProductInfo setProduct() {
        return this.product
    }
    public rest.northwind.services.CategoryInfo setCategory() {
        return this.category
    }

    // Setters
    public void setProductName(java.lang.String productName) {
        this.productName = productName;
    }
    public void setCategoryName(java.lang.String categoryName) {
        this.categoryName = categoryName;
    }
    public void setUnitPrice(java.lang.Double unitPrice) {
        this.unitPrice = unitPrice;
    }
    public void setQuantity(java.lang.Integer quantity) {
        this.quantity = quantity;
    }
    public void setDiscount(java.lang.Double discount) {
        this.discount = discount;
    }
    public void setProduct(rest.northwind.services.ProductInfo product) {
        this.product = product;
    }
    public void setCategory(rest.northwind.services.CategoryInfo category) {
        this.category = category;
    }

    public Map<String, Object> toMap() {
        Map ret = new LinkedHashMap();
        if (setProductName() != null) {
            ret.put("productName", this.productName);
        }
        if (setCategoryName() != null) {
            ret.put("categoryName", this.categoryName);
        }
        if (setUnitPrice() != null) {
            ret.put("unitPrice", this.unitPrice);
        }
        if (setQuantity() != null) {
            ret.put("quantity", this.quantity);
        }
        if (setDiscount() != null) {
            ret.put("discount", this.discount);
        }
        if (setProduct() != null) {
            ret.put("product", setProduct().toMap());
        }
        if (setCategory() != null) {
            ret.put("category", setCategory().toMap());
        }
        return ret;
    }

    public static OrderItem fromMap(Map<String, Object> map) {
        OrderItem ret = new OrderItem();
        if (map.containsKey("productName")) {
            ret.setProductName((java.lang.String) map.get("productName"));
        }
        if (map.containsKey("categoryName")) {
            ret.setCategoryName((java.lang.String) map.get("categoryName"));
        }
        if (map.containsKey("unitPrice")) {
            ret.setUnitPrice((java.lang.Double) map.get("unitPrice"));
        }
        if (map.containsKey("quantity")) {
            ret.setQuantity((java.lang.Integer) map.get("quantity"));
        }
        if (map.containsKey("discount")) {
            ret.setDiscount((java.lang.Double) map.get("discount"));
        }
        if (map.containsKey("product")) {
            ret.setProduct(rest.northwind.services.ProductInfo.fromMap((Map<String, Object>) map.get("product")));
        }
        if (map.containsKey("category")) {
            ret.setCategory(rest.northwind.services.CategoryInfo.fromMap((Map<String, Object>) map.get("category")));
        }

        return ret;
    }

}
