import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import Vo.Message;

public class FCMVtest {
	
	public static void main(String[] args) {
	
		Map<String, Object> testVO=new HashMap<String, Object>();
		
		Message message=new Message();
		message.setTitle("alert");
		message.setContent(".-w-36-.-g-127-.-alt-default-.");
		message.setImgUrl("");
		message.setLink("");
		
		String to="/topics/alert";
		
		Gson gson = new Gson();
		
		testVO.put("message", message);
		
		
		Map<String, Object> data=new HashMap<>();
		data.put("data", testVO);
		data.put("to", to);
		String jsonStr=gson.toJson(data, HashMap.class);
		
		System.out.println(jsonStr);
		
		
	}
}
