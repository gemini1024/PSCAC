
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR" import="gcm.*"%>
<%
 
    //������ PUSH ����
    String title = "PUSH ���� �Դϴ�.";
    String msg = "PUSH �����Դϴ� ^^ !!!!";
    
    // GCM ���� ����
    GCMVo gcmVo = new GCMVo();
    gcmVo.setTitle(title);
    gcmVo.setMsg(msg);
    gcmVo.setTypeCode("");
    
    // GCM reg id ����
    List<String> reslist = new ArrayList<String>();
    reslist.add("�Է¹��� regId�� �Է��մϴ�.");
    
    GCMUtil gcmUtil = new GCMUtil(reslist, gcmVo);
    
   for(int i=0; i<gcmUtil.rtnList.size(); i++){
      GCMVo rtnGcmVo = gcmUtil.rtnList.get(i); 
 
      out.println("regId : " + rtnGcmVo.getRegId());
      out.println("���� ���� : " + rtnGcmVo.getPushSuccessOrFailure());
      out.println("�޽���ID : " + rtnGcmVo.getMsgId());
      out.println("�����޽��� : " + rtnGcmVo.getErrorMsg());
   } 
 %>


