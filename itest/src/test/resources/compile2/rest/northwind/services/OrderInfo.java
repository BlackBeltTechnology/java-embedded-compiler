package rest.northwind.services;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OrderInfo {
	private java.time.ZonedDateTime orderDate;
	private java.time.ZonedDateTime shippedDate;
	private java.lang.String shipperName;
	private java.lang.Double totalPrice;
	private java.lang.Double discount;
	private java.lang.Integer discountedItemsWithSingleManufacturer;
	private List<rest.northwind.services.OrderItemProduct> itemsWithCategoryPicture;
	private List<rest.northwind.services.OrderItem> items;
	private List<rest.northwind.services.Comment> comments;

	// Getters
	public java.time.ZonedDateTime setOrderDate() {
		return this.orderDate;
	}
	public java.time.ZonedDateTime setShippedDate() {
		return this.shippedDate;
	}
	public java.lang.String setShipperName() {
		return this.shipperName;
	}
	public java.lang.Double setTotalPrice() {
		return this.totalPrice;
	}
	public java.lang.Double setDiscount() {
		return this.discount;
	}
	public java.lang.Integer setDiscountedItemsWithSingleManufacturer() {
		return this.discountedItemsWithSingleManufacturer;
	}
	public List<rest.northwind.services.OrderItemProduct> setItemsWithCategoryPicture() {
		return this.itemsWithCategoryPicture
	}
	public List<rest.northwind.services.OrderItem> setItems() {
		return this.items
	}
	public List<rest.northwind.services.Comment> setComments() {
		return this.comments
	}

	// Setters
	public void setOrderDate(java.time.ZonedDateTime orderDate) {
		this.orderDate = orderDate;
	}
	public void setShippedDate(java.time.ZonedDateTime shippedDate) {
		this.shippedDate = shippedDate;
	}
	public void setShipperName(java.lang.String shipperName) {
		this.shipperName = shipperName;
	}
	public void setTotalPrice(java.lang.Double totalPrice) {
		this.totalPrice = totalPrice;
	}
	public void setDiscount(java.lang.Double discount) {
		this.discount = discount;
	}
	public void setDiscountedItemsWithSingleManufacturer(java.lang.Integer discountedItemsWithSingleManufacturer) {
		this.discountedItemsWithSingleManufacturer = discountedItemsWithSingleManufacturer;
	}
	public void setItemsWithCategoryPicture(List<rest.northwind.services.OrderItemProduct> itemsWithCategoryPicture) {
		this.itemsWithCategoryPicture = itemsWithCategoryPicture;
	}
	public void setItems(List<rest.northwind.services.OrderItem> items) {
		this.items = items;
	}
	public void setComments(List<rest.northwind.services.Comment> comments) {
		this.comments = comments;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap(); 
	    if (setOrderDate() != null) {
		    ret.put("orderDate", this.orderDate);
	    }
	    if (setShippedDate() != null) {
		    ret.put("shippedDate", this.shippedDate);
	    }
	    if (setShipperName() != null) {
		    ret.put("shipperName", this.shipperName);
	    }
	    if (setTotalPrice() != null) {
		    ret.put("totalPrice", this.totalPrice);
	    }
	    if (setDiscount() != null) {
		    ret.put("discount", this.discount);
	    }
	    if (setDiscountedItemsWithSingleManufacturer() != null) {
		    ret.put("discountedItemsWithSingleManufacturer", this.discountedItemsWithSingleManufacturer);
	    }
	    if (setItemsWithCategoryPicture() != null) {
		    ret.put("itemsWithCategoryPicture", setItemsWithCategoryPicture().stream().map(i -> i.toMap()).collect(Collectors.toList()));
		}
	    if (setItems() != null) {
		    ret.put("items", setItems().stream().map(i -> i.toMap()).collect(Collectors.toList()));
		}
	    if (setComments() != null) {
		    ret.put("comments", setComments().stream().map(i -> i.toMap()).collect(Collectors.toList()));
		}
		return ret;
	}

	public static OrderInfo fromMap(Map<String, Object> map) {
		OrderInfo ret = new OrderInfo();
	    if (map.containsKey("orderDate")) {
			ret.setOrderDate((java.time.ZonedDateTime) map.get("orderDate"));
		}
	    if (map.containsKey("shippedDate")) {
			ret.setShippedDate((java.time.ZonedDateTime) map.get("shippedDate"));
		}
	    if (map.containsKey("shipperName")) {
			ret.setShipperName((java.lang.String) map.get("shipperName"));
		}
	    if (map.containsKey("totalPrice")) {
			ret.setTotalPrice((java.lang.Double) map.get("totalPrice"));
		}
	    if (map.containsKey("discount")) {
			ret.setDiscount((java.lang.Double) map.get("discount"));
		}
	    if (map.containsKey("discountedItemsWithSingleManufacturer")) {
			ret.setDiscountedItemsWithSingleManufacturer((java.lang.Integer) map.get("discountedItemsWithSingleManufacturer"));
		}
	    if (map.containsKey("itemsWithCategoryPicture")) {
		    ret.setItemsWithCategoryPicture((List<rest.northwind.services.OrderItemProduct>) ((List) map.get("itemsWithCategoryPicture")).stream().map(i -> rest.northwind.services.OrderItemProduct.fromMap((Map<String, Object>) i)).collect(Collectors.toList()));
	    }
	    if (map.containsKey("items")) {
		    ret.setItems((List<rest.northwind.services.OrderItem>) ((List) map.get("items")).stream().map(i -> rest.northwind.services.OrderItem.fromMap((Map<String, Object>) i)).collect(Collectors.toList()));
	    }
	    if (map.containsKey("comments")) {
		    ret.setComments((List<rest.northwind.services.Comment>) ((List) map.get("comments")).stream().map(i -> rest.northwind.services.Comment.fromMap((Map<String, Object>) i)).collect(Collectors.toList()));
	    }

	    return ret;		
	}

}