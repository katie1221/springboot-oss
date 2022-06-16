package com.example.ossdemo.controller;

import com.example.ossdemo.common.OSSConstant;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 普通文件下载
 * 
 * @author Administrator
 *
 */
@RequestMapping("/file")
@RestController
public class FileDownController {

	/**
	 * 批量下载文件（打包下载）
	 */
	@RequestMapping(value = "/download",method = RequestMethod.GET )
	public void download(HttpServletResponse response, HttpServletRequest request,Integer orderDetailId){
		//模拟数据库获取oss图片列表
		List<String> imageList = new ArrayList<>();
		imageList.add("https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png");
		imageList.add("https://zhengxin-pub.cdn.bcebos.com/brandpic/77506441df6a4dcaeabaf7a69dc01c11_fullsize.jpg");
		imageList.add("https://pics4.baidu.com/feed/35a85edf8db1cb1320fa053476d2294493584b92.jpeg");
		try {
			//压缩包名称  自行定义
			String fileName = "qr_code.zip";
			// 创建临时文件
			File  zipFile = File.createTempFile("qr_code", ".zip");
			FileOutputStream f = new FileOutputStream(zipFile);
			/**
			 * 作用是为任何OutputStream产生校验和
			 * 第一个参数是制定产生校验和的输出流，第二个参数是指定Checksum的类型 （Adler32（较快）和CRC32两种）
			 */
			CheckedOutputStream csum = new CheckedOutputStream(f, new Adler32());
			// 用于将数据压缩成Zip文件格式
			ZipOutputStream zos = new ZipOutputStream(csum);
			//域名
			String oss_domain = OSSConstant.OSS_PIC_URL;
			for (String url:imageList){

				URL imageURL = new URL(url);
				File file = new File(url);
				// 读去Object内容  返回
				InputStream inputStream = imageURL.openStream();
				// 对于每一个要被存放到压缩包的文件，都必须调用ZipOutputStream对象的putNextEntry()方法，确保压缩包里面文件不同名
				zos.putNextEntry(new ZipEntry(file.getName()));
				int bytesRead = 0;
				// 向压缩文件中输出数据
				while((bytesRead=inputStream.read())!=-1){
					zos.write(bytesRead);
				}
				inputStream.close();
				zos.closeEntry(); // 当前文件写完，定位为写入下一条项目
			}
			zos.close();
			String header = request.getHeader("User-Agent").toUpperCase();
			if (header.contains("MSIE") || header.contains("TRIDENT") || header.contains("EDGE")) {
				fileName = URLEncoder.encode(fileName, "utf-8");
				fileName = fileName.replace("+", "%20");    //IE下载文件名空格变+号问题
			} else {
				fileName = new String(fileName.getBytes(), "ISO8859-1");
			}
			response.reset();
			response.setContentType("text/plain");
			response.setContentType("application/octet-stream; charset=utf-8");
			response.setHeader("Location", fileName);
			response.setHeader("Cache-Control", "max-age=0");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

			FileInputStream fis = new FileInputStream(zipFile);
			BufferedInputStream buff = new BufferedInputStream(fis);
			BufferedOutputStream out=new BufferedOutputStream(response.getOutputStream());
			byte[] car=new byte[1024];
			int l=0;
			while (l < zipFile.length()) {
				int j = buff.read(car, 0, 1024);
				l += j;
				out.write(car, 0, j);
			}
			// 关闭流
			fis.close();
			buff.close();
			out.close();
			// 删除临时文件
			zipFile.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
