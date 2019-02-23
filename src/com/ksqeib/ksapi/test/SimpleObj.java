package com.ksqeib.ksapi.test;

import java.util.UUID;

public class SimpleObj {
    public UUID uuid;
    protected final String name;
    public SimpleObj(String name) {
        super();
//        this.uuid = UUID.randomUUID();
        this.name=name;
    }
    public static SimpleObj fromkeyserizable(String  o){

        return new SimpleObj(o);
    }

}
