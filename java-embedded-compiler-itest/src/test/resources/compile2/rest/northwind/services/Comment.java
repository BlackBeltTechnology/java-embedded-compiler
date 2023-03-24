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

public class Comment {
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
            ret.put("author", this.author)
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
