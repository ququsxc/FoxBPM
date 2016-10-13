/**
 * 
 */
package org.foxbpm.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author wangzhiwei
 *
 */
public class JavaRESTClient {

	/**
	 * 
	 */
	private JavaRESTClient() {
		// TODO Auto-generated constructor stub
	}

	public static String requestURL(String targetURL, String method,
			String params) {
		StringBuffer buffer = new StringBuffer();
		
		try {
			URL restServiceURL = new URL(targetURL);

			HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
			httpConnection.setDoOutput(true);
			httpConnection.setRequestMethod("POST");	//	POST|GET
			httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			// 入参
			OutputStream outputStream = httpConnection.getOutputStream();
            outputStream.write(params.getBytes());
            outputStream.flush();
			
			if (httpConnection.getResponseCode() != 200) {
				throw new RuntimeException(
						"HTTP GET Request Failed with Error code : " + httpConnection.getResponseCode());
			}

            // 出参
			BufferedReader responseBuffer = new BufferedReader(
					new InputStreamReader((httpConnection.getInputStream())));
			
			String output = null;
			System.out.println("Output from Server:");

			while ((output = responseBuffer.readLine()) != null) {
				System.out.println(output);
				buffer.append(output);
			}

			httpConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return buffer.toString();
	}

	public static void main(String[] args) {
		String expenseId = null, accont = null, params = null;
		
		for (int temperature = 0; temperature < 100; temperature++) {
			if (temperature == 40) {
				expenseId = "BXD-20161011055100008";
				accont = "1";
			}
			if (temperature == 50) {
				expenseId = "BXD-20161011055100009";
				accont = "2";
			}
			if (temperature == 60) {
				expenseId = "BXD-20161011055100010";
				accont = "3";
			}
			
			if(temperature == 40 || temperature == 50 || temperature == 60) {
				params = "userName=admin&password=1"
						+ "&expenseId=" + expenseId +"&createTime=2016-09-11&ownerName=超级管理员&owner=admin&dept=200011&account=" + accont + "&invoiceType=1&reason=不喜欢你"
						+ "&flowCommandInfo={\"processDefinitionKey\":\"tt_1\",\"processInstanceId\":\"\",\"taskId\":\"\","
						+ "\"commandId\":\"HandleCommand_2\",\"commandType\":\"startandsubmit\",\"bizKey\":\"" + expenseId + "\",\"taskComment\":\"\"}";
				
				requestURL("http://localhost:8080/foxbpm-webapps-common/expenses.action", 
						"POST", params);
			}
			
			try {
				Thread.sleep(100);
				System.out.println("========" + temperature);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
