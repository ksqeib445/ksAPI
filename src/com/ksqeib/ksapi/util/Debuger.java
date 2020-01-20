package com.ksqeib.ksapi.util;

import java.io.File;

public class Debuger {
    public boolean delconfigs;
    UtilManager um;

    protected Debuger(UtilManager um) {
        this.um = um;
    }

    public void disable() {
        if (delconfigs) {
            um.getIo().configs.forEach((a, b) -> {
                File mF = new File(um.jp.getDataFolder(), a);
                mF.deleteOnExit();
            });
            if (um.getTip() != null) {
                File mF = new File(um.getTip().messagefile.getCurrentPath());
                mF.deleteOnExit();
            }
        }
    }
}
