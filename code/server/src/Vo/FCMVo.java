package Vo;

public class FCMVo {
	// android 로 보낼 정보
	private String title = "제목입니다.";
	private String msg = "내용입니다.";
	private String ImgUrl;
	private String Link;

	
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
