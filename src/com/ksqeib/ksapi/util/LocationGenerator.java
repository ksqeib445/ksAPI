package com.ksqeib.ksapi.util;

import com.ksqeib.ksapi.KsAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Random;

public class LocationGenerator {
    Random rm = new Random();
    protected LocationGenerator(){

    }

    public Location getLoc(World w, int f, boolean nowater) {
        double x = rm.nextDouble() * f;
        double z = rm.nextDouble() * f;
        Double y = (double) w.getHighestBlockYAt((int) x, (int) z);
        Location loc = new Location(w, x, y, z);
        Block block = w.getHighestBlockAt(loc);
//        边界
        if (!isInsideBorder(loc)) return getLoc(w, f, nowater);
//        水
        if (!nowater) return loc;
        if ((block.getType() != Material.STATIONARY_WATER) &&
                (block.getType() != Material.WATER) && (block.getType() != Material.WATER_LILY) &&
                (block.getType() != Material.WATER_BUCKET)) {
            return loc;
        }
        return getLoc(w, f, true);
    }


    public Location poser(Location pos,int range) {
        //生成随机坐标
        Double x = pos.getX();
        Double z = pos.getZ();
        World world = pos.getWorld();
        int key = rm.nextInt(8);
        switch (key) {
            default:
            case 1:
                x += (range + rm.nextInt(range * 2));
                z += rm.nextInt(range * 2);
                break;
            case 2:
                x -= (range + rm.nextInt(range * 2));
                z -= rm.nextInt(range * 2);
                break;
            case 3:
                z += (range + rm.nextInt(range * 2));
                x -= rm.nextInt(range * 2);
                break;
            case 4:
                z -= (range + rm.nextInt(range * 2));
                x += rm.nextInt(range * 2);
                break;
            case 5:
                z += (range * 2 + rm.nextInt(range * 2));
                x += (range * 2 + rm.nextInt(range * 2));
                break;
            case 6:
                z += (range * 2 + rm.nextInt(range * 2));
                x -= (range * 2 + rm.nextInt(range * 2));
                break;
            case 7:
                z -= (range * 2 + rm.nextInt(range * 2));
                x += (range * 2 + rm.nextInt(range * 2));
                break;
            case 8:
                z -= (range * 2 + rm.nextInt(range * 2));
                x -= (range * 2 + rm.nextInt(range * 2));
                break;
        }
        Double y = (double) world.getHighestBlockYAt(x.intValue(), z.intValue());
        Location bpos = new Location(world, x, y, z);
        Location blpos = new Location(world, x, y - 1, z);
        if (isInsideBorder(bpos) && (blpos.getBlock().getType() != Material.STATIONARY_WATER) &&
                (blpos.getBlock().getType() != Material.WATER) && (blpos.getBlock().getType() != Material.WATER_LILY) &&
                (blpos.getBlock().getType() != Material.WATER_BUCKET)) {
            return bpos;
        } else {
            return poser(pos,range);
        }
    }
    public boolean isInsideBorder(Location loc) {
        return loc.getWorld().getWorldBorder().isInside(loc) && KsAPI.getDependManager().isInsideBoard(loc);
    }
}
