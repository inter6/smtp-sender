package com.inter6.mail.model.component.content;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PartDataJsonSerializer implements JsonSerializer<PartData> {

	@Override
	public JsonElement serialize(PartData src, Type typeOfSrc, JsonSerializationContext context) {
		Gson gson = new GsonBuilder().registerTypeAdapter(PartData.class, new PartDataJsonSerializer()).create();
		return gson.toJsonTree(src, src.getContentType().getDataClass());
	}
}
