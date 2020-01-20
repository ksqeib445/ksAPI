package com.ksqeib.ksapi.mysql.serializer;

import com.google.gson.*;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;

/**
 * 物品序列化专家(雾)
 */
public class ItemStackSerializer implements Serializer<ItemStack> {

    @Override
    public JsonElement serialize(ItemStack arg0, Type arg1, JsonSerializationContext arg2) {
        JsonObject obj = new JsonObject();

        String ser = null;

        try {
            FileConfiguration fc = new Utf8YamlConfiguration();
            fc.set("ItemStack", arg0);
            ser = fc.saveToString();
        } catch (Exception e) {

        } finally {
            if (ser == null)
                ser = "";
        }

        obj.addProperty("IS", ser);

        return obj;
    }

    @Override
    public ItemStack deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2)
            throws JsonParseException {
        JsonObject obj = (JsonObject) arg0;
        FileConfiguration fc = new Utf8YamlConfiguration();
        try {
            fc.loadFromString(obj.get("IS").getAsString());
            return fc.getItemStack("ItemStack", new ItemStack(Material.AIR));
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }

}