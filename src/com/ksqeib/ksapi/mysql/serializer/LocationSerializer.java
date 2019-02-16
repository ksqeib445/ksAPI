/*******************************************************************************
 *     Copyright (C) 2017 wysohn
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.ksqeib.ksapi.mysql.serializer;

import java.lang.reflect.Type;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;


public class LocationSerializer implements Serializer<Location> {
    @Override
    public JsonElement serialize(Location arg0, Type arg1, JsonSerializationContext arg2) {
        JsonObject json = new JsonObject();

        // return empty if world does not exists
        if (arg0.getWorld() == null) {
            return json;
        }

        json.addProperty("world", arg0.getWorld().getName());
        json.addProperty("x", arg0.getX());
        json.addProperty("y", arg0.getY());
        json.addProperty("z", arg0.getZ());
        json.addProperty("pitch", arg0.getPitch());
        json.addProperty("yaw", arg0.getYaw());

        return json;
    }

    @Override
    public Location deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2)
            throws JsonParseException {
        JsonObject json = (JsonObject) arg0;

        JsonElement worldElem = json.get("world");
        if (worldElem == null)
            return null;

        String worldName = worldElem.getAsString();
        World world = Bukkit.getWorld(worldName);
        if (world == null)
            return null;

        double x = json.get("x").getAsDouble();
        double y = json.get("y").getAsDouble();
        double z = json.get("z").getAsDouble();
        float pitch = json.get("pitch") == null ? 0.0F : json.get("pitch").getAsFloat();
        float yaw = json.get("yaw") == null ? 0.0F : json.get("yaw").getAsFloat();

        return new Location(world, x, y, z, pitch, yaw);
    }

}