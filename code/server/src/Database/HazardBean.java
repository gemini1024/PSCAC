package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import Vo.PSCACVo;

public class HazardBean {

	Connection conn = null;
	PreparedStatement pstmt = null;

	/* MySQL 연결정보 */
	String jdbc_driver = "com.mysql.jdbc.Driver";
	String jdbc_url = "jdbc:mysql://211.253.29.38:3306/odroid";

	// DB연결 메서드
	void connect() {
		try {
			Class.forName(jdbc_driver);

			conn = DriverManager.getConnection(jdbc_url, "root", "odroidroot!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void disconnect() {
		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	
	// 경보를 받았을 경우 위험 빈도 테이블에 삽입
		public boolean insertHazard(PSCACVo vo) {
			connect();

		String sql = "insert into hazardvo(id, status) values(?, ?)";

			try {
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, vo.getId());
				pstmt.setString(2, vo.getStatus());
				
				pstmt.executeUpdate();
				System.out.print(vo.getId());

			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			} finally {
				disconnect();
			}
			return true;
		}
		
		/*// 특정 id의 PSCACVo를 가져오는 메서드
		public PSCACVo getDBFId(String id) throws UnsupportedEncodingException {
			connect();

		String sql = "select * from vo where id=?";
		PSCACVo vo = new PSCACVo();

		try
		{
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			ResultSet rs = pstmt.executeQuery();

			// 데이터가 하나만 있으므로 rs.next()를 한번만 실행 한다. rs.next();
			if(rs.next()){
			vo.setId(rs.getString("id"));
			vo.setLatitude(rs.getString("latitude"));
			vo.setLongtitud(rs.getString("longtitud"));
			
			}

			rs.close();
		}catch(SQLException e)
		{
			e.printStackTrace();
		}finally
		{
			disconnect();
		}return vo;
		}*/
}
