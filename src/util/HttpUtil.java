package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtil {
	public static void sendHttpRequest(final String address, final HttpCallbackListener listener){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in = connection.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line = null;
					while ((line = br.readLine()) != null) {
						response.append(line);
					}
					if(listener != null){
						// 回调 onFinish方法
						listener.onFinish(response.toString());
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
//					e.printStackTrace();
					if(listener != null){
						listener.onError(e);
					}
				} finally{
					if(connection != null){
						connection.disconnect();
					}
				}
			}
		}).start();
	}
	
	public interface HttpCallbackListener{
		void onFinish(String response);
		void onError(Exception e);
	}
}
