package com.ksqeib.ksapi.mysql.serializer;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class UUIDListSerializer implements Serializer<List<UUID>> {

    @Override
    public JsonElement serialize(List<UUID> obj, Type type, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        for (UUID uuid : obj) array.add(context.serialize(uuid));
        return array;
    }

    @Override
    public List<UUID> deserialize(JsonElement obj, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        List<UUID> uuids = new ArrayList<UUID>();
        JsonArray array = (JsonArray) obj;
        Iterator<JsonElement> iter = array.iterator();
        while (iter.hasNext()) {
            UUID uuid = context.deserialize(iter.next(), UUID.class);
            uuids.add(uuid);
        }
        return uuids;
    }

}
