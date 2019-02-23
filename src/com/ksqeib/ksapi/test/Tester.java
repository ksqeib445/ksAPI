package com.ksqeib.ksapi.test;

import com.ksqeib.ksapi.mysql.KDatabase;
import com.ksqeib.ksapi.mysql.Sqlitedatabase;

import java.io.File;
import java.util.UUID;

public class Tester {
    public static void main(String [] args){
        KDatabase<SimpleObj> kd=new Sqlitedatabase<>(new File("F:/tester/test.db"),SimpleObj.class,"simple");
        kd.save("fuck",new SimpleObj("fuck"));

        System.out.println(kd.keyload("fuck",null).uuid.toString());
    }
}
