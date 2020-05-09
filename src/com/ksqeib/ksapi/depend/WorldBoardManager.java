package com.ksqeib.ksapi.depend;

import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.WorldBorder;
import org.bukkit.Location;

public class WorldBoardManager {
    protected boolean checkInisdeBorder(Location loc) {
        BorderData bd = WorldBorder.plugin.getWorldBorder(loc.getWorld().getName());
        if (bd == null) return true;
        return bd.insideBorder(loc);
    }
}
