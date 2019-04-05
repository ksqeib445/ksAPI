package com.ksqeib.ksapi.tester.client.config;
import com.ksqeib.ksapi.tester.net.SrvConnect;

import java.util.Hashtable;


public class UpdateConfig {
	public static String host;
	public static int port;

	public static boolean srvConfig(String host,int port) {
			//对参数进行srv解析
			String query = "_mcupdate._tcp." + host;
			Hashtable<String, String> ret = SrvConnect.resoveSrv(query);
			if(ret!=null) {
			if (ret.size() != 0) {
				// 参数重设
				
				UpdateConfig.host = ret.get("host0");
				System.out.println(host);
				UpdateConfig.port = Integer.parseInt(ret.get("port0"));
				System.out.println(port);
				
				System.out.println("src解析成功");
				
				return true;

			}
		}
		System.out.println("src解析失败");
		return false;
		
	}
}
