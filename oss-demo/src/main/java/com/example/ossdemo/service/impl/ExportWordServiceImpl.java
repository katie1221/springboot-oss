package com.example.ossdemo.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.policy.HackLoopTableRenderPolicy;
import com.example.ossdemo.common.OSSConstant;
import com.example.ossdemo.service.ExportWordService;
import com.example.ossdemo.service.OssService;
import com.example.ossdemo.utils.MoneyUtils;

@Service("ExportWordService")
public class ExportWordServiceImpl implements ExportWordService{

	@Autowired
	private OssService ossService;
	@Autowired
	private OssServiceImpl ossServiceImpl;
	/**
	 * 导出订单合同word
	 */
	@Override
	public Map<String, Object> exportOrderContractInfo(HttpServletRequest request) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		//模拟数据请求,组装必要数据
		Map<String, Object> data = new HashMap<String, Object>();
		
		//导出word
		exportWordOrderInfo(data);
		
		result.put("code", "success");
		return result;
	}
	/**
	 * 生成订单合同word
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public void exportWordOrderInfo(Map<String,Object> data){
		Map<String, Object> params = new HashMap<>();

		DecimalFormat df = new DecimalFormat("######0.00");   
        Calendar now = Calendar.getInstance(); 
        double money = 0;//总金额
        //模拟数据请求，组装表格列表数据
        List<Map<String,Object>> detailList=new ArrayList<Map<String,Object>>();
        for (int i = 0; i < 6; i++) {
        	 Map<String,Object> detailMap = new HashMap<String, Object>();
        	 detailMap.put("index", i+1);//序号
        	 detailMap.put("title", "商品"+i);//商品名称
        	 detailMap.put("product_description", "套");//商品规格
        	 detailMap.put("buy_num", 3+i);//销售数量
        	 detailMap.put("saleprice", 100+i);//销售价格
        	
        	 double saleprice=Double.valueOf(String.valueOf(100+i));
             Integer buy_num=Integer.valueOf(String.valueOf(3+i));
             String buy_price=df.format(saleprice*buy_num);
             detailMap.put("buy_price", buy_price);//单个商品总价格
             money=money+Double.valueOf(buy_price);
             
             detailList.add(detailMap);
        }
        //总金额
        String order_money=String.valueOf(money);
        //金额中文大写
        String money_total = MoneyUtils.change(money);
	
        // 渲染文本{{}}
        
        //渲染表格
        HackLoopTableRenderPolicy  policy = new HackLoopTableRenderPolicy();
        Configure config = Configure.newBuilder().bind("detailList", policy).build();
        
        String basePath=ClassUtils.getDefaultClassLoader().getResource("").getPath()+"static/template/";//本地
      	String oss_url="http://xiaofang-001.oss-cn-shanghai.aliyuncs.com/file/20201217/130652163_419.docx";//模板地址(存储在oss上的模板文件地址)
      	//word模板地址
      	String resource=getResourceName(oss_url, basePath);//将oss上传文件地址 还原成 目标文件
      	
        XWPFTemplate template = XWPFTemplate.compile(resource, config).render(
		new HashMap<String, Object>() {{
				put("detailList", detailList);
		        put("order_number", "2356346346645");
		        put("y", now.get(Calendar.YEAR));//当前年
		        put("m", (now.get(Calendar.MONTH) + 1));//当前月
		        put("d", now.get(Calendar.DAY_OF_MONTH));//当前日
		        put("order_money",order_money);//总金额
		        put("money_total",money_total);//金额中文大写
		    }}
		);
        String newFilePath=basePath+getNewFilePath1(resource);
        try {
        	File newFile = new File(basePath, getNewFilePath1(resource));
            FileOutputStream out = new FileOutputStream(newFile);
            template.write(out);
            out.flush();
            out.close();
            template.close();
            String url = getOssFilePath(newFilePath,newFile);
            System.out.println(url);
            if(StringUtils.isNotEmpty(url)){
            	newFile.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * 将oss上传文件地址 还原成 目标文件
	 * @param oss_url:Word文档的oos url全地址
	 */
	public String getResourceName(String oss_url,String basePath){
		String resource="";
		//域名
		String oss_domain = OSSConstant.OSS_PIC_URL;
		String file_name = oss_url.replace(oss_domain, "");
		//获取oss文件byte[]
      	byte[] oss_byte = ossServiceImpl.getOssFileByteArray(file_name);
      	String targetPath=basePath+getNewFilePath1(oss_url);
      	//将字节数组还原为文件
      	ossServiceImpl.byteArrayToFile(oss_byte, targetPath);
      	resource=targetPath;
      	return  resource;
	}
	/**
	 * 新的文件名
	 * @param fileName
	 * @return
	 */
	public String getNewFilePath1(String fileName){
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
		 //生成新的文件名
		String newfilename = "";
		Date now = new Date();
		SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
		newfilename += date.format(now) + "";
		SimpleDateFormat time = new SimpleDateFormat("HHmmssSSS");
		newfilename += time.format(now);
		newfilename += "-" + new Random().nextInt(1000) + "." + fileExt;
		return newfilename;
	}
	
	
	/**
	 * 生成的Word上传oos
	 * @param fileName
	 * @param newFile
	 * @return
	 */
	public String getOssFilePath(String fileName,File newFile){
		String url = "";
		try {
			String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
			 //生成新的文件名
			 String newfilename = "file/";
			 Date now = new Date();
			 SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
			 newfilename += date.format(now) + "/";
			 SimpleDateFormat time = new SimpleDateFormat("HHmmssSSS");
			 newfilename += time.format(now);
			 newfilename += "_" + new Random().nextInt(1000) + "." + fileExt;
			 
			 FileInputStream fis = new FileInputStream(newFile);
			 //文件上传到oss
			 ossService.uploadFile(newfilename, fis);
			 url = OSSConstant.OSS_PIC_URL + newfilename;
			 
		}catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}

}
