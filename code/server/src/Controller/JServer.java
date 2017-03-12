package Controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class JServer {
	private ServerSocket server = null;
    BufferedWriter bw = null;
    BufferedReader br = null;
    BufferedReader br1 = null;
    /**
     * @param args
     */
    // 생성자
    public JServer(){
        try{
            InetAddress ia = InetAddress.getLocalHost();    // 자신의 호스트 주소
            System.out.println("IP : " + ia.getHostAddress() + "에서 방이 개설되었습니다.");  
        }catch(Exception e){
 
        }
 
        try{
            server = new ServerSocket(5001);    // 포트생성
            System.out.println("접속 대기중...");
            Socket client = server.accept();    // 클라이언트에 대한 소켓대기
 
            InputStream is = client.getInputStream();   // 입력 스트림 연결
            OutputStream os = client.getOutputStream(); // 출력 스트림 연결
            System.out.println(client.getInetAddress() + "님이 접속하셨습니다.");
 
            while(true){
                String msg = "";
                String msg1 = "";
 
                br = new BufferedReader(new InputStreamReader(is)); // Client의 입력스트림 읽기
                br1 = new BufferedReader(new InputStreamReader(System.in)); // 서버가 입력한 값
                bw = new BufferedWriter(new OutputStreamWriter(os));    // Server에서 쓸 내용
                if(msg.equals("exit")){  //client가 exit 해서 나갔다면..ee
                     
                    System.out.println(client.getInetAddress()+" 가 방을 나갔습니다.");
                }
                else{
                    msg = br.readLine();
                    System.out.println(client.getInetAddress()+" 님의 말 : "+ msg +"\n");
                    System.out.print("서버측 메세지 :");
                    msg1 = br1.readLine();
                    bw.write(msg1+"\n");  // client의 msg1이 받는다.
                    bw.flush();
                }
            }
        }catch(Exception e){System.out.println("소켓생성 실패..");}
        try{
            bw.close();
            br.close();
            server.close();
        }catch(Exception e){ System.out.println("마지막 예외");}
    }
 
    public static void main(String[] args) {
    	JServer ss = new JServer();  //생성자 호출
 
    }
}





    

	


