/*
 * Copyright (c) 2018-2020 ksqeib. All rights reserved.
 * @author ksqeib <ksqeib@dalao.ink> <https://github.com/ksqeib445>
 * @create 2020/08/10 09:59:15
 *
 * ksAPI/ksAPI/KsPlugin.java
 */

package com.ksqeib.ksapi.develop;

import com.ksqeib.ksapi.util.UtilManager;
import org.bukkit.plugin.java.JavaPlugin;

public class KsPlugin extends JavaPlugin {
    protected UtilManager utilManager;

    public KsPlugin(){
        utilManager=new UtilManager(this);
    }

    public UtilManager getUtilManager() {
        return utilManager;
    }

    public void reload(){

    }
}
