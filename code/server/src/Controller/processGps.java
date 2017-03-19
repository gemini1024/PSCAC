package Controller;

import java.io.UnsupportedEncodingException;

import Database.PSCACBean;
import Vo.FCMVo;
import Vo.PSCACVo;

public class processGps {
	
	void receiveGPS(String gps) throws UnsupportedEncodingException{
		PSCACBean database = new PSCACBean();
		PSCACVo pscac = new PSCACVo();
		
		pscac = database.getDBFgps(gps);		
		if(pscac.getId() == null)
			database.insertDB(gps);
	}
	
	void receiveAlarm(PSCACVo vo) throws Exception{
		PSCACBean database = new PSCACBean();
		PSCACVo pscac = new PSCACVo();
		
		FCMVo fcmvo = new FCMVo();
		SendPushServer send = new SendPushServer();
		
		String gps;
		gps = database.getDBFId(vo.getId()).getGps();
		vo.setGps(gps);
		
		//vo.setGps(database.getDBFId(vo.getId()).getGps());
		
		fcmvo.setMsg("\"content\":" + vo.getGps());
		fcmvo.setTitle("Alarm");
		
		send.pushFCMNotification(fcmvo);
		
	}
}
