package Controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import Vo.FCMVo;
import Vo.Message;

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

		Map<String, Object> testVO = new HashMap<String, Object>();

		Message message = new Message();		
		message.setTitle(fcmvo.getTitle());
		//message.setContent(fcmvo.getMsg());
		
		message.setLatitude(fcmvo.getLatitude());
		message.setLongitude(fcmvo.getLongitude());
		message.setAlarm("default");
		
		String to = "/topics/alert";

		Gson gson = new Gson();

		//testVO.put("message", message);

		Map<String, Object> data = new HashMap<>();
		data.put("data", message);
		data.put("to", to);
		String jsonStr = gson.toJson(data, HashMap.class);

		System.out.println(jsonStr);


		try (OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream())) {
			// 혹시나 한글 깨짐이 발생하면
			// try(OutputStreamWriter wr = new
			// OutputStreamWriter(conn.getOutputStream(), "UTF-8")){ 인코딩을 변경해준다.

			wr.write(jsonStr.toString());
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
		FCMVo fcmvo = new FCMVo();
		
		//E동
		fcmvo.setLatitude("37.3396026");
		fcmvo.setLongitude("126.7347525");
		
		//tip
		//fcmvo.setLatitude("37.3410697");
		//fcmvo.setLongitude("126.7331218");
		
		fcmvo.setAlarm("default");
		fcmvo.setTitle("alert");
		
		try {
			send.pushFCMNotification(fcmvo);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
