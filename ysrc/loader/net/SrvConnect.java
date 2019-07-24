package com.ksqeib.ksapi.loader.net;

import org.xbill.DNS.*;

import java.util.Hashtable;

public class SrvConnect {

    public static Hashtable<String, String> resoveSrv(String query) {
        Hashtable<String, String> ret = new Hashtable<String, String>();
        try {

            Record[] records = new Lookup(query, Type.SRV).run();  // returning null

            if (records != null && records.length > 0) {

                for (int i = 0; i < records.length; i++) {
                    Record r = records[i];
                    SRVRecord srv = (SRVRecord) r;

                    String hostname = srv.getTarget().toString().replaceFirst("\\.$", "");

                    String port = srv.getPort() + "";

                    ret.put("host" + i, hostname);
                    ret.put("port" + i, port);

                }

                return ret;

            } else {

                return null;

            }

        } catch (TextParseException e) {

            return null;

        }

    }
}
