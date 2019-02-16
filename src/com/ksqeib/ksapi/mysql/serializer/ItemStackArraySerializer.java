package com.ksqeib.ksapi.mysql.serializer;

import com.google.gson.*;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;

public class ItemStackArraySerializer implements Serializer<ItemStack[]> {

    @Override
    public JsonElement serialize(ItemStack[] src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray arr = new JsonArray();
        for (ItemStack IS : src) {
            arr.add(context.serialize(IS, ItemStack.class));
        }
        return arr;
    }

    @Override
    public ItemStack[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonArray arr = (JsonArray) json;
        ItemStack[] src = new ItemStack[arr.size()];
        for (int i = 0; i < src.length; i++) {
            src[i] = context.deserialize(arr.get(i), ItemStack.class);
        }

        return src;
    }

}
