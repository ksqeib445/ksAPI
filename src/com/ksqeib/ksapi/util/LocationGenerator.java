package com.ksqeib.ksapi.util;

import com.ksqeib.ksapi.KsAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Random;

public class LocationGenerator {
    Random rm = new Random();

    protected LocationGenerator() {

    }

    public Location getLoc(World w, int f, boolean nowater) {
        Location loc = genLoc(w, f);
        int time=0;
        while (!canUse(loc,nowater)) {
            loc = genLoc(w, f);
            time++;
            if(time>50){
                return null;
            }
        }
        return loc;
    }

    public Location genLoc(World w, int f) {
        double x = rm.nextDouble() * f;
        double z = rm.nextDouble() * f;
        Double y = (double) w.getHighestBlockYAt((int) x, (int) z);
        return new Location(w, x, y, z);
    }


    public Location poser(Location pos, int range) {
        Location bpos=genPos(pos,range);
        int time=0;
        while (!canUse(bpos,true)){
            bpos=genPos(pos,range);
            time++;
            if(time>50)return null;
        }
        return bpos;
    }

    public boolean canUse(Location loc,boolean nowater){

        if (!isInsideBorder(loc)) {
            return false;
        }
        Block block=loc.getBlock();
//        水
        if (!nowater) return true;
        if ((block.getType() != Material.STATIONARY_WATER) &&
                (block.getType() != Material.WATER) && (block.getType() != Material.WATER_LILY) &&
                (block.getType() != Material.WATER_BUCKET)) {
            return true;
        }
        return false;
    }

    public Location genPos(Location pos, int range){
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
        return new Location(world, x, y, z);

    }

    public boolean isInsideBorder(Location loc) {
        return loc.getWorld().getWorldBorder().isInside(loc) && KsAPI.getDependManager().isInsideBoard(loc);
    }
}
