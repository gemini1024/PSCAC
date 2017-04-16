package Controller;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import Vo.PSCACVo;

/*처음 5001포트로 연결 한다.
 *  1)	odroid 부팅 gps를 송신할때 SendGps를 송신후 gps값 송신 
 *  2)  위험상황 송신시 SendAlarm를 송신후 위헝상화값 송신*/
public class ReceiveServer {
	private DatagramSocket dsock = null;

	String gps = ".-w-10-.-g-20-.-alt-default-.";

	public ReceiveServer() {
		PSCACVo pscacvo = new PSCACVo();
		processGps pgps = new processGps();


		try {
			dsock = new DatagramSocket(5001); // 포트생성
			System.out.println("Ready to receiving...");

			while (true) {
				byte[] buffer = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);

				// 클라이언트로부터 DatagramPacket을 전송 받기 위해서 DatagramPacket 객체 하나를 생성하고
				// 패킷을 전송할 때까지 대기
				dsock.receive(receivePacket);

				// 전송받은 데이터를 String 객체에 지정하고 출력
				String msg = new String(receivePacket.getData(), 0, receivePacket.getLength());

				System.out.println("Receiving : " + msg);
				/*
				 * //odroid 첫 부팅시 gps 수신 if(msg.equals("SendGps")){
				 * pgps.receiveGPS(gps); System.out.println(gps); }
				 */

				// odrodi에서 위험상황 판단시
				// if(msg.equals("SendAlarm")){
				
				PSCACVo vo = new PSCACVo();

				String str = msg; // 예시 2, dangerous
				String[] values = str.split(",");
				vo.setId(values[0]);
				vo.setStatus(values[1]);				
				
				pgps.receiveAlarm(vo);
				
				System.out.println("Alarm Text save in pscacvo");
								

			}
		} catch (Exception e) {
			System.out.println("Fail to make socket..");
			e.printStackTrace();
		}
		try {

			dsock.close();
		} catch (Exception e) {
			System.out.println("마지막 예외");
		}
	}

	public static void main(String[] args) {
		ReceiveServer ss = new ReceiveServer(); // 생성자 호출

	}
}
