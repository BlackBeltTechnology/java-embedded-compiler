package rest.northwind.services;

import org.osgi.service.log.LogService;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class Comment {
	// OSGI service reference, the class exported by another bundle
	LogService logService;

	private java.lang.String note;
	private java.lang.String author;
	private java.time.ZonedDateTime timestamp;

	// Getters
	public java.lang.String setNote() {
		return this.note;
	}
	public java.lang.String setAuthor() {
		return this.author;
	}
	public java.time.ZonedDateTime setTimestamp() {
		return this.timestamp;
	}

	// Setters
	public void setNote(java.lang.String note) {
		this.note = note;
	}
	public void setAuthor(java.lang.String author) {
		this.author = author;
	}
	public void setTimestamp(java.time.ZonedDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap(); 
	    if (setNote() != null) {
		    ret.put("note", this.note);
	    }
	    if (setAuthor() != null) {
		    ret.put("author", this.author);
	    }
	    if (setTimestamp() != null) {
		    ret.put("timestamp", this.timestamp);
	    }
		return ret;
	}

	public static Comment fromMap(Map<String, Object> map) {
		Comment ret = new Comment();
	    if (map.containsKey("note")) {
			ret.setNote((java.lang.String) map.get("note"));
		}
	    if (map.containsKey("author")) {
			ret.setAuthor((java.lang.String) map.get("author"));
		}
	    if (map.containsKey("timestamp")) {
			ret.setTimestamp((java.time.ZonedDateTime) map.get("timestamp"));
		}

	    return ret;		
	}

}