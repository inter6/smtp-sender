package com.inter6.mail.model.component.content;

import com.google.gson.*;
import com.inter6.mail.model.ContentType;

import java.lang.reflect.Type;

public class PartDataJsonDeserializer implements JsonDeserializer<PartData> {

    @Override
    public PartData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String contentTypeStr = json.getAsJsonObject().get("contentType").getAsString();
        ContentType contentType = ContentType.valueOf(contentTypeStr);
        Gson gson = new GsonBuilder().registerTypeAdapter(PartData.class, new PartDataJsonDeserializer()).create();
        return gson.fromJson(json, contentType.getDataClass());
    }
}
