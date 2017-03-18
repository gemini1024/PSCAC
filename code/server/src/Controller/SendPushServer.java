package Controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONObject;

import Vo.FCMVo;

public class SendPushServer {

	// Method to send Notifications from server to client end.

	public final static String AUTH_KEY_FCM = "AIzaSyDjdwnZEpzbO4JDtNy7tCDQE83r61VIGQY";
	public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

	// userDeviceIdKey is the device id you will query from your database

	void pushFCMNotification(FCMVo fcmvo) throws Exception {

		String authKey = AUTH_KEY_FCM; // You FCM AUTH key
		String FMCurl = API_URL_FCM;

		URL url = new URL(FMCurl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization", "key=" + authKey);
		conn.setRequestProperty("Content-Type", "application/json");

		// String str = "\"content\":\".-w-10-.-g-20-.-alt-default-.\"";

		JSONObject notification = new JSONObject();
		JSONObject root = new JSONObject();

		notification.put("body", fcmvo.getMsg());
		notification.put("title", fcmvo.getTitle());
		root.put("notification", notification);
		root.put("to", "/topics/alert"); // deviceID

		try (OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream())) {
			// 혹시나 한글 깨짐이 발생하면
			// try(OutputStreamWriter wr = new
			// OutputStreamWriter(conn.getOutputStream(), "UTF-8")){ 인코딩을 변경해준다.

			wr.write(root.toString());
			wr.flush();
		} catch (Exception e) {
		}

		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

		String output;
		System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}

		conn.disconnect();
	}

	public static void main(String a[]) throws Exception {
		SendPushServer send = new SendPushServer();
		try {
			send.pushFCMNotification(null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
