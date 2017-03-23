package Vo;

public class FCMVo {
	// android 로 보낼 정보
	private String title = "제목입니다.";
	private String msg = "내용입니다.";
	
	private String latitude = "위도";
	private String longitude = "경도";
	private String alarm; // 알람
	
	private String ImgUrl;
	private String Link;

	
	public String getAlarm() {
		return alarm;
	}

	public void setAlarm(String alarm) {
		this.alarm = alarm;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}


	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getImgUrl() {
		return ImgUrl;
	}

	public void setImgUrl(String imgeUrl) {
		ImgUrl = imgeUrl;
	}

	public String getLink() {
		return Link;
	}

	public void setLink(String link) {
		Link = link;
	}


}
