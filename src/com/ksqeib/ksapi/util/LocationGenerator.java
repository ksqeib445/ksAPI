package com.ksqeib.ksapi.util;

import com.ksqeib.ksapi.KsAPI;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Random;

public class LocationGenerator {
    Random rm = new Random();
    UtilManager um;

    protected LocationGenerator(UtilManager um) {
        this.um = um;
    }

    public Location getLoc(World w, int f, boolean nowater) {
        Location loc = genLoc(w, f);
        int time = 0;
        while (!canUse(loc, nowater)) {
            loc = genLoc(w, f);
            time++;
            if (time > 50) {
                break;
            }
        }
        return loc;
    }

    public Location genLoc(World w, int f) {
        return genPos(new Location(w, 0, 0, 0), f);
    }


    public Location poser(Location pos, int range) {
        Location bpos = genPos(pos, range);
        int time = 0;
        while (!canUse(bpos, true)) {
            bpos = genPos(pos, range);
            time++;
            if (time > 50) return bpos;
        }
        return bpos;
    }

    public boolean canUse(Location loc, boolean nowater) {

        if (!isInsideBorder(loc)) {
            return false;
        }
//        水
        if (!nowater) return true;
        return !loc.getBlock().getType().name().contains("water");
    }

    public Location genPos(Location pos, int range) {
        //生成随机坐标
        double x = pos.getX();
        double z = pos.getZ();
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
        double y = world.getHighestBlockYAt((int) x, (int) z);
        return new Location(world, x, y, z);

    }

    public boolean isInsideBorder(Location loc) {
        return loc.getWorld().getWorldBorder().isInside(loc) && KsAPI.getDependManager().isInsideBoard(loc);
    }

    public Location randomInSurface(Location l1, Location l2) {
        return new Location(l1.getWorld(),
                um.getIo().randInt(l1.getBlockX(), l2.getBlockX()), um.getIo().randInt(l1.getBlockY(), l2.getBlockY()),
                um.getIo().randInt(l1.getBlockZ(), l2.getBlockZ()
                ));
    }
}
