/*
 * Copyright 2017 Alex Thomson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mojang.authlib.properties;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PropertyMap extends ForwardingMultimap<String, Property> {
	
	private final Multimap<String, Property> properties;
	
	public PropertyMap() {
		this.properties = LinkedHashMultimap.create();
	}
	
	protected Multimap<String, Property> delegate() {
		return this.properties;
	}
	
	public static class Serializer implements JsonSerializer<PropertyMap>, JsonDeserializer<PropertyMap> {
		public PropertyMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			PropertyMap result = new PropertyMap();
			Iterator<Entry<String, JsonElement>> i$;
			Map.Entry<String, JsonElement> entry;
			if (json instanceof JsonObject) {
				JsonObject object = (JsonObject) json;
				for (i$ = object.entrySet().iterator(); i$.hasNext();) {
					entry = (Map.Entry<String, JsonElement>) i$.next();
					if (entry.getValue() instanceof JsonArray) {
						for (JsonElement element : (JsonArray)entry.getValue()) {
							result.put(entry.getKey(), new Property((String) entry.getKey(), element.getAsString()));
						}
					}
				}
			} else if (json instanceof JsonArray) {
				for (JsonElement element : (JsonArray) json) {
					if (element instanceof JsonObject) {
						JsonObject object = (JsonObject) element;
						String name = object.getAsJsonPrimitive("name").getAsString();
						String value = object.getAsJsonPrimitive("value").getAsString();
						if (object.has("signature")) {
							result.put(name, new Property(name, value, object.getAsJsonPrimitive("signature").getAsString()));
						} else {
							result.put(name, new Property(name, value));
						}
					}
				}
			}
			return result;
		}
		
		public JsonElement serialize(PropertyMap src, Type typeOfSrc, JsonSerializationContext context) {
			JsonArray result = new JsonArray();
			for (Property property : src.values()) {
				JsonObject object = new JsonObject();
				object.addProperty("name", property.getName());
				object.addProperty("value", property.getValue());
				if (property.hasSignature()) {
					object.addProperty("signature", property.getSignature());
				}
				result.add(object);
			}
			return result;
		}
	}
}
