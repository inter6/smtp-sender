package com.inter6.mail.model.component.content;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.inter6.mail.model.ContentType;

public class PartDataJsonDeserializer implements JsonDeserializer<PartData> {

	@Override
	public PartData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		String contentTypeStr = json.getAsJsonObject().get("contentType").getAsString();
		ContentType contentType = ContentType.valueOf(contentTypeStr);
		return new Gson().fromJson(json, contentType.getDataClass());
	}
}
