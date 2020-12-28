<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE html>
<html>
<head>
<!-- easyUI -->
<link rel="stylesheet" href="<%=basePath %>/js/jquery-easyui-1.7.0/themes/default/easyui.css">
<link rel="stylesheet" href="<%=basePath %>/js/jquery-easyui-1.7.0/themes/icon.css">
<link rel="stylesheet" href="<%=basePath %>/css/common.css">
<script type="text/javascript" src="<%=basePath %>/js/jquery-3.4.1.min.js"></script>
<script type="text/javascript" src="<%=basePath %>/js/jquery-easyui-1.7.0/jquery.easyui.min.js"></script>
<script type="text/javascript" src="<%=basePath %>/js/jquery-easyui-1.7.0/locale/easyui-lang-zh_CN.js"></script>
<!-- layer 弹出层 -->
<script type="text/javascript" src="<%=basePath %>/js/layer/layer.js"></script>
<link rel="stylesheet" href="<%=basePath %>/js/layui-v2.5.6/css/layui.css">
  <style>
    .m1{
      margin-left: 10px;                     
    }
    .s1{
      font-weight: bold;
      color:#1e9fff;
    }
    .m2{
      margin-bottom: 10px;
    }
  </style>
</head>
<body  class="easyui-layout">

    <h1 class="m1">OSS文件上传、下载：</h1>
    
    <div class="easyui-tabs" style="width:700px;height:250px;margin: 10px 0 0 10px;">
       <div title="图片上传" style="padding:10px">
            <div class="m2" style="margin-top: 30px;font-weight: bold;">图片上传</div>
            <div class="m2" style="margin-top: 30px;">
	            <img alt="" src="" id="img_url">
	            <input type="hidden" id="img_url2" value="">
            </div>
			<a href="#" class="easyui-linkbutton" id="imgButton">图片上传</a>
        </div>
        <div title="普通文件上传" style="padding:10px">
            <div class="m2" style="margin-top: 30px;font-weight: bold;">普通文件上传</div>
            <div class="m2" style="margin-top: 30px;">
                                                    文件地址：<input type="text" id="url" class="easyui-textbox" style="width:600px;" value="" readonly="readonly">
            </div>
			<a href="#" class="easyui-linkbutton"  id="fileButton">文件上传</a>
        </div>
        <div title="文件下载" style="padding:10px">
            <div class="m2" style="margin-top: 30px;font-weight: bold;">oss文件下载</div>
            <div class="m2" style="margin-top: 30px;">
            	文件地址：<input type="text" id="url3" class="easyui-textbox" style="width:600px;" value="http://xiaofang-001.oss-cn-shanghai.aliyuncs.com/file/20201216/093915477_23.docx" readonly="readonly">
            </div>
			<a href="#" class="easyui-linkbutton"  onclick="downloadFile()">文件下载</a>
        </div>
    </div>
    <script src="<%=basePath %>/js/layui-v2.5.6/layui.js" charset="utf-8"></script>
	<script type="text/javascript">
	 layui.use('upload', function(){
	   var $ = layui.jquery,upload = layui.upload;
	   var loading;
        //图片上传
		 upload.render({
		    elem: '#imgButton'
		    ,url: '/oss_file/upload_img' //改成您自己的上传接口
		    ,accept: 'file' //普通文件 //指定允许上传时校验的文件类型，可选值有：images（图片）、file（所有文件）、video（视频）、audio（音频）
		    ,acceptMime:'image/*'//规定打开文件选择框时，筛选出的文件类型，值为用逗号隔开的 MIME 类型列表。如：acceptMime: 'image/*'（只显示图片文件）acceptMime: 'image/jpg, image/png'（只显示 jpg 和 png 文件）
		    ,before: function(obj){
		    	loading = layer.msg("上传中", {
                    icon: 16,
                    time: 10000000,
                    shade: 0.3
                });
		    },
		    done: function(res){
		        if (res.code == "success"){
		            layer.close(loading);
                    layer.msg('上传成功');
                    console.log(res.url);
                    $("#img_url").attr("src",res.url)
                    $("#img_url2").val(res.url)
                }
		    },error: function(res){
		      //请求异常回调
		       console.log(res)
		    }
		 });
		 
		 //普通文件上传
		 upload.render({
		    elem: '#fileButton'
		    ,url: '/oss_file/upload_attachment' //改成您自己的上传接口
		    ,accept: 'file' //普通文件 //指定允许上传时校验的文件类型，可选值有：images（图片）、file（所有文件）、video（视频）、audio（音频）
		    //,exts: 'docx|doc' //只允许上传word文件
		    ,before: function(obj){
		    	loading = layer.msg("上传中", {
                    icon: 16,
                    time: 10000000,
                    shade: 0.3
                });
		    },
		    done: function(res){
		        if (res.code == "success"){
		            layer.close(loading);
                    layer.msg('上传成功');
                    console.log(res.url);
                    $("#url").val(res.url)
                }
		    }
		 });
     });
     
     function downloadFile(){
        window.location.href='/oss_file/downFile?oss_url='+$("#url3").val();
     }
	</script>
</body>
</html>



