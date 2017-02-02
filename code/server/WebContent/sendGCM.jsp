
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR" import="gcm.*"%>
<%
 
    //전달할 PUSH 내용
    String title = "PUSH 제목 입니다.";
    String msg = "PUSH 내용입니다 ^^ !!!!";
    
    // GCM 정보 셋팅
    GCMVo gcmVo = new GCMVo();
    gcmVo.setTitle(title);
    gcmVo.setMsg(msg);
    gcmVo.setTypeCode("");
    
    // GCM reg id 셋팅
    List<String> reslist = new ArrayList<String>();
    reslist.add("입력받은 regId를 입력합니다.");
    
    GCMUtil gcmUtil = new GCMUtil(reslist, gcmVo);
    
   for(int i=0; i<gcmUtil.rtnList.size(); i++){
      GCMVo rtnGcmVo = gcmUtil.rtnList.get(i); 
 
      out.println("regId : " + rtnGcmVo.getRegId());
      out.println("성공 여부 : " + rtnGcmVo.getPushSuccessOrFailure());
      out.println("메시지ID : " + rtnGcmVo.getMsgId());
      out.println("에러메시지 : " + rtnGcmVo.getErrorMsg());
   } 
 %>


