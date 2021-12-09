package com.inter6.mail.model.component.content;

import com.google.gson.*;

import java.lang.reflect.Type;

public class PartDataJsonSerializer implements JsonSerializer<PartData> {

    @Override
    public JsonElement serialize(PartData src, Type typeOfSrc, JsonSerializationContext context) {
        Gson gson = new GsonBuilder().registerTypeAdapter(PartData.class, new PartDataJsonSerializer()).create();
        return gson.toJsonTree(src, src.getContentType().getDataClass());
    }
}
