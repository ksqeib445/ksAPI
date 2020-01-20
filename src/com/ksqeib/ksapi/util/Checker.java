package com.ksqeib.ksapi.util;

import org.bukkit.Location;

public class Checker {

    public static boolean isin(Location loc, Location firstLoc, Location secondLoc) {
        double fx = firstLoc.getX();
        double sx = secondLoc.getX();
        double lx = loc.getX();
        if (fx < sx) {
            if (lx > sx || lx < fx) {
                return false;
            }
        } else {
//            fx>sx
            if (lx > fx || lx < sx) {
                return false;
            }
        }
        double fy = firstLoc.getY();
        double sy = secondLoc.getY();
        double ly = loc.getY();
        if (fy < sy) {
            if (ly < fy || ly > sy) {
                return false;
            }
        } else {
            if (ly > fy || ly < sy) {
                return false;
            }
        }
        double fz = firstLoc.getZ();
        double sz = secondLoc.getZ();
        double lz = loc.getZ();
        if (fz < sz) {
            return !(lz < fz) && !(lz > sz);
        } else {
            return !(lz > fz) && !(lz < sz);
        }
    }
}
