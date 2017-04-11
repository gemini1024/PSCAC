package Vo;

public class PSCACVo {

	private String Id;
	private String Gps;
	private String Longtitud;
	private String Latitude;
	private String Status;
	
	
	public String getLongtitud() {
		return Longtitud;
	}
	public void setLongtitud(String longtitud) {
		Longtitud = longtitud;
	}
	public String getLatitude() {
		return Latitude;
	}
	public void setLatitude(String latitude) {
		Latitude = latitude;
	}

	
	
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getGps() {
		return Gps;
	}
	public void setGps(String Gps) {
		Gps = Gps;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
}
