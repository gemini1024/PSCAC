<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="Controller.GCMController"%>

<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR" import="Vo.GCMVo"%>
<%
 
    //������ PUSH ����
    String title = "gps ��ǥ : 25.35.94";
    String msg = "-w-10-.-g-20-.-alt-default-.";
    
    // GCM ���� ����
    GCMVo gcmVo = new GCMVo();
    gcmVo.setTitle(title);
    gcmVo.setMsg(msg);
    gcmVo.setTypeCode("");
    
    // GCM reg id ����
    List<String> reslist = new ArrayList<String>();
    reslist.add("alert");
    
    GCMController gcmUtil = new GCMController(reslist, gcmVo);
    
   for(int i=0; i<gcmUtil.rtnList.size(); i++){
      GCMVo rtnGcmVo = gcmUtil.rtnList.get(i); 
 
      out.println("regId : " + rtnGcmVo.getRegId() +"\n");
      out.println("���� ���� : " + rtnGcmVo.getPushSuccessOrFailure() +"\n");
      out.println("�޽���ID : " + rtnGcmVo.getMsgId() +"\n");
      out.println("�����޽��� : " + rtnGcmVo.getErrorMsg() +"\n");
   } 
 %>


