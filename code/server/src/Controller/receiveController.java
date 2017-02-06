package Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Database.PSCACBean;
import Vo.PSCACVo;


/**
 * Servlet implementation class receiveController
 */
@WebServlet("/receiveController")
public class receiveController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public receiveController() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// requset : 사용자 정보들 처리, response : 응답 처리

		response.setContentType("text/html; charset=UTF-8");

		request.setCharacterEncoding("UTF-8");

		String id;

		PSCACBean pb = new PSCACBean();
		PSCACVo vo = new PSCACVo();

		String action = request.getParameter("action");

		PrintWriter out = response.getWriter();

		// 파라미터에 따른 요청 처리
		// 주소록 목록 요청인 경우

		if (action.equals("list")) {
			ArrayList<PSCACVo> datas = pb.getDBList();
			request.setAttribute("datas", datas);
			//RequestDispatcher pageContext = request.getRequestDispatcher("addrbook_list.jsp");
			//pageContext.forward(request, response);
		}
		// 주소록 등록 요청인 경우
		else if (action.equals("insert")) {

			vo.setId(request.getParameter("id"));
			vo.setGps(request.getParameter("gps"));
			vo.setStatus(request.getParameter("status"));
			

			if (pb.insertDB(vo)) {
			//	response.sendRedirect("AddrbookController?action=list");
			} else
				try {
					throw new Exception("DB 입력오류");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	/*	// 주소록 수정 페이지 요청인 경우
		else if (action.equals("edit")) {
			id = request.getParameter("id");
			vo.setId(id);

			vo = pb.getDB(vo.getId());

			if (!request.getParameter("upasswd").equals("1234")) {
				out.println("<script>alert('비밀번호가 틀렸습니다.!!');" + "history.go(-1);</script>");
			} else {
				request.setAttribute("ab", vo);
				RequestDispatcher pageContext = request.getRequestDispatcher("addrbook_edit_form.jsp");
				pageContext.forward(request, response);
			}
		}*/
		// 주소록 수정 등록 요청인 경우
		else if (action.equals("update")) {

			id = request.getParameter("id");
			vo.setId(id);

			vo = pb.getDB(vo.getId());

			vo.setId(request.getParameter("id"));
			vo.setGps(request.getParameter("gps"));
			vo.setStatus(request.getParameter("status"));
			
			if (pb.updateDB(vo)) {
				//response.sendRedirect("AddrbookController?action=list");
			} else
				try {
					throw new Exception("DB 갱신오류");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		// 주소록 삭제 요청인 경우
		else if (action.equals("delete")) {
			id = request.getParameter("id");
			vo.setId(id);

			vo = pb.getDB(vo.getId());

			vo.setId(request.getParameter("id"));
			vo.setGps(request.getParameter("gps"));
			vo.setStatus(request.getParameter("status"));
			
			if (pb.updateDB(vo)) {
			//	response.sendRedirect("AddrbookController?action=list");
			} else
				try {
					throw new Exception("DB 삭제 오류");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		} else {

			out.println("<script>alert('action 파라미터를 확인해 주세요!!!')</script>");
		}

	}

}
