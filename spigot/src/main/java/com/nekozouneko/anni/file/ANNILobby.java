package com.nekozouneko.anni.file;

import com.nekozouneko.anni.util.WorldLocation;
import org.bukkit.Location;

public class ANNILobby {

    private WorldLocation loc;

    public ANNILobby(Location loc) {
        this.loc = new WorldLocation(loc);
    }

    public ANNILobby(WorldLocation wl) {
        this.loc = wl;
    }

    public WorldLocation getLocation() {
        return loc;
    }

    public void setLocation(WorldLocation loc) {
        this.loc = loc;
    }

}
