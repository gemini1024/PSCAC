<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/json; charset=EUC-KR"
    pageEncoding="EUC-KR" import="gcm.*"%>
<%
 
    System.out.println("*********************************************************");
    System.out.println("REG ȹ��");
    System.out.println("KEY : " + request.getParameter("KEY"));
    System.out.println("REG : " + request.getParameter("REG"));
    System.out.println("*********************************************************");
    
    Map rtnMap = new HashMap();
    
    rtnMap.put("DATA", URLEncoder.encode("��ϵǾ����ϴ�.", "UTF-8"));
    rtnMap.put("DATA1", "END1");
    out.print(rtnMap.toString());
 
%>