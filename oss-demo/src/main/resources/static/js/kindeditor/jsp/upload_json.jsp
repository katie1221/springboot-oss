<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*,java.io.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="org.apache.commons.fileupload.*" %>
<%@ page import="org.apache.commons.fileupload.disk.*" %>
<%@ page import="org.apache.commons.fileupload.servlet.*" %>
<%@ page import="org.json.simple.*" %>
<%@page import="com.core.common.App"%>
<%

/**
 * KindEditor JSP
 * 
 * 本JSP程序是演示程序，建议不要直接在实际项目中使用。
 * 如果您确定直接使用本程序，使用之前请仔细确认相关安全设置。
 * 
 */

//文件保存目录路径
// String savePath = pageContext.getServletContext().getRealPath("/") + "upload/";
//String userid=(null==request.getSession().getAttribute("SUSERID")?(request.getParameter("uid")==null?"":EncryptURL.decodePWS(request.getParameter("uid"))) :(String)request.getSession().getAttribute("SUSERID"));

//pub.upload.SetUploadDir SetUploadDir=new pub.upload.SetUploadDir();
//String pathz=SetUploadDir.setUserIdPath(userid);
String savePath =App.SAVE_BASE_PATH + "org\\" + "active\\";
File save = new File(savePath);//文件上传缓存目录
if(!save.exists()) 
{
	save.mkdirs();
}
File tempPathFile = new File(savePath);//文件上传缓存目录
if(!tempPathFile.exists()) 
{
   tempPathFile.mkdirs();
}


//文件保存目录URL
String saveUrl  = App.BASE_URL + App.BASE_FILE_NAME + "/org/active/";
System.out.println("saveUrl == "+saveUrl);

//定义允许上传的文件扩展名
HashMap<String, String> extMap = new HashMap<String, String>();
extMap.put("image", "gif,jpg,jpeg,png,bmp");
extMap.put("flash", "swf");
extMap.put("media", "swf");
extMap.put("file", "doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2,pdf");

//最大文件大小
long maxSize = 200000000;

response.setContentType("text/html; charset=UTF-8");

if(!ServletFileUpload.isMultipartContent(request)){
	out.println(getError("请选择文件。"));
	return;
}
//检查目录
File uploadDir = new File(savePath);
if(!uploadDir.isDirectory()){
	out.println(getError("上传目录不存在。"));
	return;
}
//检查目录写权限
if(!uploadDir.canWrite()){
	out.println(getError("上传目录没有写权限。"));
	return;
}

String dirName = request.getParameter("dir");
if (dirName == null) {
	dirName = "image";
}
if(!extMap.containsKey(dirName)){
	out.println(getError("目录名不正确。"));
	return;
}
//创建文件夹
//savePath += dirName + "/";
//saveUrl += dirName + "/";
File saveDirFile = new File(savePath);
if (!saveDirFile.exists()) {
	saveDirFile.mkdirs();
}
//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//String ymd = sdf.format(new Date());
//savePath += ymd + "/";
//saveUrl += ymd + "/";
//File dirFile = new File(savePath);
//if (!dirFile.exists()) {
//	dirFile.mkdirs();
//}

FileItemFactory factory = new DiskFileItemFactory();
ServletFileUpload upload = new ServletFileUpload(factory);
upload.setHeaderEncoding("UTF-8");
List items = upload.parseRequest(request);
Iterator itr = items.iterator();
while (itr.hasNext()) {
	FileItem item = (FileItem) itr.next();
	String fileName = item.getName();
	long fileSize = item.getSize();
	if (!item.isFormField()) {
		//检查文件大小
		if(item.getSize() > maxSize){
			out.println(getError("上传文件大小超过限制。"));
			return;
		}
		//检查扩展名
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
		if(!Arrays.<String>asList(extMap.get(dirName).split(",")).contains(fileExt)){
			out.println(getError("上传文件扩展名是不允许的扩展名。\n只允许" + extMap.get(dirName) + "格式。"));
			return;
		}
		//文件名
		String fileName1 = fileName.substring(fileName.lastIndexOf("\\") + 1,fileName.lastIndexOf("."));

		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String newFileName = df.format(new Date()) + "_" + new Random().nextInt(1000) + "." + fileExt;
		try{
			File uploadedFile = new File(savePath, newFileName);
			System.out.println("savePath============================="+savePath);
			System.out.println("newFileName============================="+newFileName);
			item.write(uploadedFile);
		}catch(Exception e){
			out.println(getError("上传文件失败。"));
			return;
		}

		String str = saveUrl + newFileName;
		
		JSONObject obj = new JSONObject();
		obj.put("error", 0);
		obj.put("url", str);
		System.out.println("str============================="+str);
		if(dirName!="image"){//不是图片，才显示名称
			obj.put("title", fileName1); 
		}
		out.println(obj.toJSONString());
	}
}
%>
<%!
private String getError(String message) {
	JSONObject obj = new JSONObject();
	obj.put("error", 1);
	obj.put("message", message);
	return obj.toJSONString();
}
%>