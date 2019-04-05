package com.ksqeib.ksapi.tester.net;

import java.util.Hashtable;

import org.xbill.DNS.*;

public class SrvConnect {

    public static Hashtable<String, String> resoveSrv(String query) {

//   	   String s = "ramuh.example.com";  // the inputted string, I need to obtain the Port to be added to this

	    Hashtable<String, String> ret = new Hashtable<String, String>();

//  	    String query = "_rdp._tcp." + s;

	    try{

	        Record[] records = new Lookup(query,Type.SRV).run();  // returning null

	        if(records != null && records.length > 0) {

	            for(int i=0;i<records.length;i++) {
	            	Record r=records[i];
	                SRVRecord srv = (SRVRecord)r;

	                String hostname = srv.getTarget().toString().replaceFirst("\\.$", "");

	                String port = srv.getPort()+"";

	                ret.put("host"+i, hostname);
	                ret.put("port"+i, port);

	            }

	            return ret;

	        }

	        else{

	           return null;

	        }

	    } catch (TextParseException e) {

	        return null;

	    }

    }
    public static int resoveSrvport(String query) {
    	 try{

 	        Record[] records = new Lookup(query,Type.SRV).run();  // returning null

 	        if(records != null && records.length > 0) {

 	            for(Record r : records) {

 	                SRVRecord srv = (SRVRecord)r;

 	                String hostname = srv.getTarget().toString().replaceFirst("\\.$", "");

 	                int port = srv.getPort();
 	               return port;

 	            }


 	        }

 	        else{

 	           return 0;

 	        }

 	    } catch (TextParseException e) {

 	        return 0;

 	    }
		return 0;
    	
    }
    public static int resoveSrvhost(String query) {
		return 0;
    	
    }

}
