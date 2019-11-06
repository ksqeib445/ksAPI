package com.ksqeib.ksapi.depend;

import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.WorldBorder;
import org.bukkit.Location;

public class WorldBoardManager {
    public boolean checkInisdeBorder(Location loc){
        BorderData bd= WorldBorder.plugin.getWorldBorder(loc.getWorld().getName());
        return bd.insideBorder(loc);
    }
}
