package rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;


// 把需要重复多次的代码 同一整理起来 避免每次都需重复
public class RpcHelper {
	// Writes a JSONArray to http response.
	public static void writeJsonArray(HttpServletResponse response, JSONArray array) throws IOException{
		response.setContentType("application/json");
	// “*” 代表所有网站可访问（白名单）， 如果多个 用“|”分隔开； 第一个参数告诉浏览器返回的是json格式，而非html格式
		response.setHeader("Access-Control-Allow-Origin", "*");
		// 为啥要加这句话？？
		PrintWriter out = response.getWriter();
		out.print(array);
		out.close();
	}
	
	// Writes a JSONObject to http response.
	public static void writeJsonObject(HttpServletResponse response, JSONObject obj) throws IOException {
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
			PrintWriter out = response.getWriter();	
			out.print(obj);
			out.close();
	}
	
	// 把httpservlet request 格式转化成json object
	public static JSONObject readJSONObject(HttpServletRequest request) {
  	   StringBuilder sBuilder = new StringBuilder();
  	   try (BufferedReader reader = request.getReader()) {
  		 String line = null;
  		 while((line = reader.readLine()) != null) {
  			 sBuilder.append(line);
  		 }
  		 return new JSONObject(sBuilder.toString());
  		
  	   } catch (Exception e) {
  		 e.printStackTrace();
  	   }
  	
  	  return new JSONObject();
            }

}


