package gcm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Message.Builder;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

/**
 * GCM UTIL
 * 
 * gcm-server.jar, json-simple-1.1.1.jar 필요
 * 
 * @author
 *
 */
public class GCMUtil {
	static final String API_KEY = "AIzaSyArA50RlBZfVEdYS1CG8V5WC44QNZlYI-E"; // server api key
	private static final int MAX_SEND_CNT = 999; // 1회 최대 전송 가능 수

	// android 에서 받을 extra key (android app 과 동일해야 함)
	static final String TITLE_EXTRA_KEY = "TITLE";
	static final String MSG_EXTRA_KEY = "MSG";
	static final String TYPE_EXTRA_CODE = "TYPE_CODE";
	// android 에서 받을 extras key

	List<String> resList = null;
	private Sender sender;
	private Message message;

	public ArrayList<GCMVo> rtnList;

	/**
	 * GCM Util 생성자 RegistrationId 셋팅, sender 셋팅, message 셋팅
	 * 
	 * @param reslist
	 *            : RegistrationId
	 * @param gcmVo
	 *            : msg 정보
	 */
	public GCMUtil(List<String> reslist, GCMVo gcmVo) {
		sender = new Sender(API_KEY);
		this.resList = reslist;
		setMessage(gcmVo);
		rtnList = new ArrayList<GCMVo>();
		sendGCM();
	}

	/**
	 * 메시지 셋팅
	 * 
	 * @param gcmVo
	 */
	private void setMessage(GCMVo gcmVo) {
		Builder builder = new Message.Builder();
		builder.addData(TITLE_EXTRA_KEY, gcmVo.getTitle());
		builder.addData(MSG_EXTRA_KEY, gcmVo.getMsg());
		builder.addData(TYPE_EXTRA_CODE, gcmVo.getTypeCode());
		message = builder.build();
	}

	/**
	 * 메시지 전송
	 */
	private void sendGCM() {
		if (resList.size() > 0) {
			if (resList.size() <= MAX_SEND_CNT) { // 한번에 1000건만 보낼 수 있음
				sendMultivastResult(resList);
			} else {
				List<String> resListTemp = new ArrayList<String>();
				for (int i = 0; i < resList.size(); i++) {
					if ((i + 1) % MAX_SEND_CNT == 0) {
						sendMultivastResult(resListTemp);
						resListTemp.clear();
					}
					resListTemp.add(resList.get(i));
				}

				// 1000건씩 보내고 남은 것 보내기
				if (resListTemp.size() != 0) {
					sendMultivastResult(resListTemp);
				}
			}
		}

	}

	/**
	 * 실제 멀티 메시지 전송
	 * 
	 * @param list
	 */
	private void sendMultivastResult(List<String> list) {
		try {

			MulticastResult multiResult = sender.send(message, list, 5); 
			// 발송할  메시지, 발송할 타깃(RegistrationId), Retry 횟수
			List<Result> resultList = multiResult.getResults();

			for (int i = 0; i < resultList.size(); i++) {
				Result result = resultList.get(i);

				// 결과 셋팅
				GCMVo rtnGcmVo = new GCMVo();
				rtnGcmVo.setRegId(list.get(i));
				rtnGcmVo.setMsgId(result.getMessageId());
				rtnGcmVo.setErrorMsg(result.getErrorCodeName());

				if (result.getMessageId() != null) { // 전송 성공
					rtnGcmVo.setPushSuccessOrFailure(true);
				} else { // 전송 실패
					rtnGcmVo.setPushSuccessOrFailure(false);
				}

				rtnList.add(rtnGcmVo);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
