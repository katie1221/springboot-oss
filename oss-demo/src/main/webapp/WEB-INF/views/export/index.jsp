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

    <h1 class="m1">SpringBoot+oss实现根据word模板生成Word，文件地址保存在oss中：</h1>
    <div class="easyui-tabs" style="width:700px;height:350px;margin: 10px 0 0 10px;">
        <div title="导出销售订单" style="padding:10px">
            <div class="m2" style="margin-top: 30px;">
           	 	 需求：将word模板上传到oss中，然后根据返回的模板地址进行生成word，最后把生成的Word再上传到oss并返回oss文件地址。
           	</div>
           	
           	<div class="m2" style="margin-top: 30px;">
           	  word模板地址(oss)：<input type="text" id="url3" class="easyui-textbox" style="width:600px;" value="http://xiaofang-001.oss-cn-shanghai.aliyuncs.com/file/20201216/093915477_23.docx" readonly="readonly">
           	</div>
           	<a href="#" class="easyui-linkbutton" onclick="downloadTemplate();" >下载word模板地址</a>
            
            <div class="m2" style="margin-top: 30px;">
            	使用<span class="s1">POI-tl</span>根据word模板动态生成word（包含动态表格）
            </div>
			<a href="#" class="easyui-linkbutton" onclick="doExportWord3();" data-options="iconCls:'icon-save'">导出销售订单</a>
        </div>
    </div>
	<script type="text/javascript">
	
	  //下载word模板
	  function downloadTemplate(){
	    console.log($("#url3").val())
	    window.location.href=$("#url3").val();
	  }
	  //导出word（包含动态表格）
	  function doExportWord3(){
	    window.location.href="<%=basePath%>/exportWord/exportOrderContractInfo";
	  }
	</script>
</body>
</html>



