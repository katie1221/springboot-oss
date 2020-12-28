	/**
	 * 导出list到Excel
	 * @return {[type]} [description]
	   var list = [{ }]格式
	   var mapperObj={id:'id',username:'username'} 映射数据
	   var thObj={id:"ID",username:"用户名"} 表头
	   filename:导出的文件名称 带后缀xlsx
	 */
	function exportListToExcel(list,mapperObj,thObj,filename) {
	
		layui.use(['excel'], function() {
			var excel = layui.excel;
			 // 重点！！！如果后端给的数据顺序和映射关系不对，请执行梳理函数后导出
			list = excel.filterExportData(list,mapperObj);
			// 重点2！！！一般都需要加一个表头，表头的键名顺序需要与最终导出的数据一致
			list.unshift(thObj);
			excel.exportExcel({
				sheet1: list
			}, filename, 'xlsx');
		});
	}

