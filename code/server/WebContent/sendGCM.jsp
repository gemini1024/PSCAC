<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="Controller.GCMController"%>

<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR" import="Vo.GCMVo"%>
<%
 
    //전달할 PUSH 내용
    String title = "gps 좌표 : 25.35.94";
    String msg = "-w-10-.-g-20-.-alt-default-.";
    
    // GCM 정보 셋팅
    GCMVo gcmVo = new GCMVo();
    gcmVo.setTitle(title);
    gcmVo.setMsg(msg);
    gcmVo.setTypeCode("");
    
    // GCM reg id 셋팅
    List<String> reslist = new ArrayList<String>();
    reslist.add("alert");
    
    GCMController gcmUtil = new GCMController(reslist, gcmVo);
    
   for(int i=0; i<gcmUtil.rtnList.size(); i++){
      GCMVo rtnGcmVo = gcmUtil.rtnList.get(i); 
 
      out.println("regId : " + rtnGcmVo.getRegId() +"\n");
      out.println("성공 여부 : " + rtnGcmVo.getPushSuccessOrFailure() +"\n");
      out.println("메시지ID : " + rtnGcmVo.getMsgId() +"\n");
      out.println("에러메시지 : " + rtnGcmVo.getErrorMsg() +"\n");
   } 
 %>


