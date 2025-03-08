package com.github.tommyettinger.cg;

import com.github.tommyettinger.digital.BitConversion;
import com.github.tommyettinger.ds.*;
import com.github.yellowstonegames.grid.IntPointHash;
import com.github.yellowstonegames.grid.NoiseWrapper;
import com.github.yellowstonegames.grid.PerlueNoise;

import java.util.List;

import static com.github.tommyettinger.cg.ColorGuardData.Terrain.*;

public class ColorGuardData {
    public enum Terrain {
        Coast, Desert, Forest, Ice, Jungle, Mountains,
        Ocean, Plains, River, Rocky, Ruins, Volcano;

        public static final Terrain[] ALL = values();
    }

    public static class Unit {
        public String name;
        public String primary;
        public String secondary;
        public int primaryRange, primaryStrength;
        public int secondaryRange, secondaryStrength;
        public EnumSet places;

        public Unit(String name, String placement){
            this(name, null, 0, 0, null, 0, 0, placement);
        }
        public Unit(String name, String primary, int primaryRange, int primaryStrength, String placement){
            this(name, primary, primaryRange, primaryStrength, null, 0, 0, placement);
        }
        public Unit(String name, String primary, int primaryRange, int primaryStrength, String secondary, int secondaryRange, int secondaryStrength, String placement){
            this.name = name;
            this.primary = primary;
            this.secondary = secondary;
            this.primaryRange = primaryRange;
            this.secondaryRange = secondaryRange;
            this.primaryStrength = primaryStrength;
            this.secondaryStrength = secondaryStrength;
            String[] placementNames = placement.split(" ");
            places = new EnumSet(Terrain.ALL, true);
            for(String p : placementNames){
                places.add(Terrain.valueOf(p));
            }
        }

        public boolean hasWeapon(String type){
            return type != null && (type.equals(primary) || type.equals(secondary));
        }
    }

    public static List<Unit> units = ObjectList.with(
            new Unit("Infantry", "Machine_Gun", 1, 1, "Coast Desert Forest Ice Jungle Mountains Plains River Rocky Ruins"),
            new Unit("Bazooka", "Handgun", 1, 1, "Forward_Missile", 1, 3, "Coast Desert Forest Ice Jungle Mountains Plains River Rocky Ruins"),
            new Unit("Bike", "Machine_Gun", 1, 2, "Coast Desert Forest Ice Jungle Plains Ruins"),
            new Unit("Rifle_Sniper", "Handgun", 1, 1, "Handgun", 2, 2, "Coast Desert Forest Ice Jungle Mountains Plains River Rocky Ruins"),
            new Unit("Mortar_Sniper", "Arc_Cannon", 3, 1, "Coast Desert Forest Ice Jungle Mountains Plains River Rocky Ruins"),
            new Unit("Missile_Sniper", "Arc_Missile", 2, 1, "Coast Desert Forest Ice Jungle Mountains Plains River Rocky Ruins"),
            new Unit("Light_Tank", "Forward_Cannon", 1, 1, "Machine_Gun", 1, 1, "Coast Desert Forest Ice Jungle Plains Rocky Ruins"),
            new Unit("War_Tank", "Forward_Cannon", 1, 2, "Machine_Gun", 1, 1, "Coast Desert Forest Ice Jungle Plains Rocky Ruins"),
            new Unit("Scout_Tank", "Forward_Cannon", 1, 1, "Handgun", 2, 2, "Coast Desert Forest Ice Jungle Plains River Rocky Ruins"),
            new Unit("Heavy_Cannon", "Forward_Cannon", 2, 2, "Coast Desert Forest Ice Jungle Plains Rocky Ruins"),
            new Unit("Recon", "Machine_Gun", 1, 1, "Coast Desert Forest Ice Jungle Plains Ruins"),
            new Unit("AA_Gun", "Machine_Gun", 1, 2, "Coast Desert Forest Ice Jungle Plains Rocky Ruins"),
            new Unit("Flamethrower", "Flame_Wave", 1, 1, "Coast Desert Forest Ice Jungle Plains Rocky Ruins"),
            new Unit("Light_Artillery", "Arc_Cannon", 2, 1, "Coast Desert Forest Ice Jungle Plains Rocky Ruins"),
            new Unit("Rocket_Artillery", "Arc_Missile", 3, 8, "Coast Desert Forest Ice Jungle Plains Rocky Ruins"),
            new Unit("AA_Artillery", "Arc_Missile", 4, 1, "Coast Desert Forest Ice Jungle Plains Ruins"),
            new Unit("Supply_Truck", "Coast Desert Forest Ice Jungle Plains Ruins"),
            new Unit("Amphi_Transport", "Coast Desert Forest Ice Jungle Ocean Plains River Rocky Ruins"),
            new Unit("Build_Rig", "Coast Desert Forest Ice Jungle Plains Rocky Ruins"),
            new Unit("Jammer", "Hack", 2, 2, "Coast Desert Forest Ice Jungle Plains Ruins"),
            new Unit("Comm_Copter", "Hack", 3, 2, "Coast Desert Forest Ice Jungle Mountains Ocean Plains River Rocky Ruins"),
            new Unit("Jetpack", "Machine_Gun", 1, 2, "Coast Desert Forest Ice Jungle Mountains Ocean Plains River Rocky Ruins"),
            new Unit("Transport_Copter", "Coast Desert Forest Ice Jungle Mountains Ocean Plains River Rocky Ruins"),
            new Unit("Blitz_Copter", "Machine_Gun", 1, 2, "Coast Desert Forest Ice Jungle Mountains Ocean Plains River Rocky Ruins"),
            new Unit("Gunship_Copter", "Machine_Gun", 1, 2, "Forward_Missile", 1, 2, "Coast Desert Forest Ice Jungle Mountains Ocean Plains River Rocky Ruins"),
            new Unit("Patrol_Boat", "Machine_Gun", 2, 2, "Ocean River"),
            new Unit("Battleship", "Arc_Cannon", 3, 4, "Ocean River"),
            new Unit("Cruiser", "Arc_Missile", 2, 4, "Torpedo", 1, 1, "Ocean River"),
            new Unit("Submarine", "Arc_Missile", 3, 1, "Torpedo", 1, 2, "Ocean River"),
            new Unit("Legacy_Plane", "Machine_Gun", 1, 2, "Coast Desert Forest Ice Jungle Mountains Ocean Plains River Rocky Ruins"),
            new Unit("Fighter_Jet", "Forward_Missile", 1, 1, "Coast Desert Forest Ice Jungle Mountains Ocean Plains River Rocky Ruins Volcano"),
            new Unit("Stealth_Jet", "Forward_Missile", 1, 2, "Coast Desert Forest Ice Jungle Mountains Ocean Plains River Rocky Ruins Volcano"),
            new Unit("Heavy_Bomber", "Bomb_Drop", 1, 3, "Coast Desert Forest Ice Jungle Mountains Ocean Plains River Rocky Ruins Volcano"),
            new Unit("City", "Coast Desert Forest Ice Jungle Plains"),
            new Unit("Mansion", "Coast Desert Forest Ice Jungle Plains"),
            new Unit("Fort", "Coast Desert Forest Ice Jungle Plains"),
            new Unit("Factory", "Coast Desert Forest Ice Jungle Plains"),
            new Unit("Airport", "Coast Desert Forest Ice Jungle Plains"),
            new Unit("Dock", "Coast"),
            new Unit("Farm", "Forest Jungle Plains"),
            new Unit("Mining_Outpost", "Coast Desert Forest Ice Jungle Plains"),
            new Unit("Oil_Well", "Coast Desert Forest Ice Jungle Plains"),
            new Unit("Laboratory", "Coast Desert Forest Ice Jungle Plains"),
            new Unit("Hospital", "Coast Desert Forest Ice Jungle Plains")
//            new Unit("Terrain"),
//            new Unit("Road_Straight"),
//            new Unit("Road_Center")
            );

    public static final NoiseWrapper BASE_NOISE = new NoiseWrapper(new PerlueNoise(123), 0x6.23p-7f, NoiseWrapper.FBM, 4);
    public static final NoiseWrapper RIDGE_NOISE = new NoiseWrapper(new PerlueNoise(123 ^ 0xC965815B), 0x7.09p-6f, NoiseWrapper.RIDGED_MULTI, 3);
    public static final NoiseWrapper HEAT_NOISE = new NoiseWrapper(new PerlueNoise(123 ^ 0xDE916ABC), 0x6.13p-5f, NoiseWrapper.FBM, 3);
    public static final NoiseWrapper WET_NOISE = new NoiseWrapper(new PerlueNoise(123 ^ -1), 0x3.13p-6f, NoiseWrapper.DOMAIN_WARP, 2);

    public static Terrain queryTerrain(float x, float y, int seed){
        int r = IntPointHash.hashAll(BitConversion.floatToIntBits(x),
                BitConversion.floatToIntBits(y), seed);
        BASE_NOISE.setSeed(seed);
        float high = BASE_NOISE.getNoise(x, y);
        RIDGE_NOISE.setSeed(seed ^ 0xC965815B);
        high = high * 0.5f + RIDGE_NOISE.getNoise(x, y) * 0.5f;
        high = high / (((0.4f - 1f) * (1f - Math.abs(high))) + 1.0000001f);
        HEAT_NOISE.setSeed(seed ^ 0xDE916ABC);
        float hot = HEAT_NOISE.getNoise(x, y);
        hot = hot / (((0.333f - 1f) * (1f - Math.abs(hot))) + 1.0000001f);
        WET_NOISE.setSeed(~seed);
        float wet = WET_NOISE.getNoise(x, y);
        if(hot < -0.8) return Ice;
        if(high < -0.04) return Ocean;
        if(high < 0.06) return River;
        if(high < 0.1) return hot < -0.4f ? Rocky : Coast;
        if(r > 0x7D000000) return Ruins;
        if(high > 0.82) return Volcano;
        if(high > 0.72) return Mountains;
        if(high > 0.55) return Rocky;
        if(hot > 0.4 && wet < 0.5) return wet < 0.0 ? Desert : Plains;
        if(wet > 0.15) return hot < 0.3 ? Forest : Jungle;
        return Plains;
    }

    public static final ObjectObjectOrderedMap<Terrain, IntList> placeable =
            new ObjectObjectOrderedMap<>(Terrain.ALL,
                new IntList[]{
                            new IntList(), new IntList(), new IntList(),
                            new IntList(), new IntList(), new IntList(),
                            new IntList(), new IntList(), new IntList(),
                            new IntList(), new IntList(), new IntList()});

    static {
        for (Terrain t : Terrain.ALL) {
            IntList us = placeable.get(t);
            int ui = 0;
            for (Unit u : units) {
                if (u.places.contains(t))
                    us.add(ui);
                ui++;
            }
        }
    }
}
