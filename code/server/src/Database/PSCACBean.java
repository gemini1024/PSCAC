package Database;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Vo.PSCACVo;

public class PSCACBean {

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

	// id값에 일치하는 내용 수정하는 메서드
	public boolean updateDB(PSCACVo vo) {
		connect();

		String sql = "update vo set id=?, gps=?, status=? where id=?";

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, vo.getId());
			pstmt.setString(2, vo.getGps());
			pstmt.setString(3, vo.getStatus());

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

	// 특정 주소록 게시글 삭제 메서드
	public boolean deleteDB(int id) {
		connect();

		String sql = "delete from vo where id=?";

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			disconnect();
		}
		return true;
	}

	// 신규 gps 추가 메서드
	public boolean insertDB(String gps) {
		connect();
		// sql 문자열 , id는 자동 등록 되므로 입력하지 않는다.

		String sql = "insert into vo(gps) values(?)";

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, gps);

			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			disconnect();
		}
		return true;
	}

	// 특정 gps의 PSCACVo를 가져오는 메서드
	public PSCACVo getDBFgps(String gps) throws UnsupportedEncodingException {
		connect();

	String sql = "select * from vo where gps=?";
	PSCACVo vo = new PSCACVo();

	try
	{
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, gps);
		ResultSet rs = pstmt.executeQuery();

		// 데이터가 하나만 있으므로 rs.next()를 한번만 실행 한다. rs.next();
		vo.setId(rs.getString("id"));
		vo.setGps(rs.getString("gps"));
		vo.setStatus(rs.getString("status"));

		rs.close();
	}catch(SQLException e)
	{
		e.printStackTrace();
	}finally
	{
		disconnect();
	}return vo;
	}

	
	// 특정 id의 PSCACVo를 가져오는 메서드
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
		//vo.setGps(rs.getString("gps"));
		vo.setStatus(rs.getString("status"));
		}

		rs.close();
	}catch(SQLException e)
	{
		e.printStackTrace();
	}finally
	{
		disconnect();
	}return vo;
	}

	// 전체 주소록 목록을 가져오는 메서드
	public ArrayList<PSCACVo> getDBList() throws UnsupportedEncodingException {
		connect();
		ArrayList<PSCACVo> datas = new ArrayList<PSCACVo>();

		String sql = "select * from vo order by id desc";
		try {
			pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				PSCACVo vo = new PSCACVo();

				vo.setId(rs.getString("id"));
				vo.setGps(rs.getString("gps"));
				vo.setStatus(rs.getString("status"));
				datas.add(vo);
			}
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
		return datas;
	}

}
