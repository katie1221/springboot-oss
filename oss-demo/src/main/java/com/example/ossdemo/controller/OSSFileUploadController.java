package com.example.ossdemo.controller;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.example.ossdemo.common.OSSConstant;
import com.example.ossdemo.service.OssService;

/**
 * oss文件上传、下载
 * 
 * @author Administrator
 *
 */
@RequestMapping("/oss_file")
@RestController
public class OSSFileUploadController {

	@Autowired
	private OssService ossService;
	
	
    //定义允许上传的文件扩展名
    private static HashMap<String, String> extMap = new HashMap<String, String>();

    static {
        extMap.put("image", "gif,jpg,jpeg,png,bmp");
        extMap.put("flash", "swf,flv");
        extMap.put("media", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
        extMap.put("file", "pdf,doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2,java,php,exe,gif,jpg,jpeg,png,bmp");
    }
    
	 //最大文件大小 100M
    long maxSize = 104857600;
    //最大文件大小 10M
    long maxImages = 10485760;
    
	/**
	 * 进入oss文件上传、下载页面
	 * @return
	 */
	@RequestMapping("/index")
	public ModelAndView toIndex(){
		return new ModelAndView("oss/index");
	}
	
	/**
	 * 图片上传
	 * @param file
	 * @return
	 */
	@RequestMapping(value = "/upload_img", method = RequestMethod.POST)
	public Map<String,Object> uploadImg(@RequestParam("file") MultipartFile file) {
		Map<String,Object> result = new HashedMap<String, Object>();
		//允许上传的文件扩展名
		String fileTypes = "gif,jpg,jpeg,png,bmp";
		
		String fileName = file.getOriginalFilename();
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
		long fileSize = file.getSize();
		try {
			if (fileSize > maxImages) {
				result.put("code", "fail");
				result.put("msg", "上传图片大小超过限制" + (maxImages / 1024) + "KB");
				result.put("error", 1);// 富文本框图片上传，失败返回值
			} else if (!Arrays.<String>asList(fileTypes.split(",")).contains(fileExt)) {
				result.put("code", "fail");
				result.put("msg", "图片格式有误，\n只允许" + fileTypes + "格式。");
				result.put("error", 1);// 富文本框图片上传，失败返回值

			} else {
				// 生成新的文件名
				String newfilename = "image/";
				Date now = new Date();
				SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
				newfilename += date.format(now) + "/";
				SimpleDateFormat time = new SimpleDateFormat("HHmmssSSS");
				newfilename += time.format(now);
				newfilename += "_" + new Random().nextInt(1000) + "." + fileExt;
				ossService.uploadFile(newfilename, file.getInputStream());
				//返回文件访问路径
				String url = OSSConstant.OSS_PIC_URL + newfilename;
				result.put("code", "success");
				result.put("url", url);
				result.put("error", 0);// 富文本框图片上传，成功返回值
			}
		} catch (Exception e) {
			result.put("code", "fail");
			result.put("msg", "系统异常");
			e.printStackTrace();
		}
		return result;
	}
	
	/**
     * 普通文件上传
     * @param file
     * @return
     */
    @RequestMapping(value = "/upload_attachment", method = RequestMethod.POST)
    public Map<String,Object> uploadAttachment(@RequestParam("file") MultipartFile file,HttpServletRequest request) {
    	Map<String,Object> result = new HashedMap<String, Object>();
        //允许上传的文件扩展名
        String fileTypes = "pdf,doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2,java,php,exe,gif,jpg,jpeg,png,bmp";
        
        String fileName = file.getOriginalFilename();
        String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        long fileSize = file.getSize();
       
        try {
            if (fileSize > maxImages) {
                result.put("code", "fail");
                result.put("msg", "上传文件大小超过限制" + (maxImages / 1024) + "KB");
            } else if (!Arrays.<String>asList(fileTypes.split(","))
                    .contains(fileExt)) {
                result.put("code", "fail");
                result.put("msg", "文件格式有误，\n只允许" + fileTypes + "格式。");
                
            } else {
                //生成新的文件名
                String newfilename = "file/";
                Date now = new Date();
                SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
                newfilename += date.format(now) + "/";
                SimpleDateFormat time = new SimpleDateFormat("HHmmssSSS");
                newfilename += time.format(now);
                newfilename += "_" + new Random().nextInt(1000) + "." + fileExt;
                ossService.uploadFile(newfilename, file.getInputStream());
                //返回文件访问路径
                String url = OSSConstant.OSS_PIC_URL + newfilename;
                result.put("code", "success");
                result.put("url", url);
            }
        } catch (Exception e) {
            result.put("code", "fail");
            result.put("msg", "系统异常");
            e.printStackTrace();
        }
        return result;
    }  
    
	/**
	 * oss文件下载
	 */
	@RequestMapping(value = "/downFile", method = {RequestMethod.GET})
	public void downFile(HttpServletRequest request,HttpServletResponse response){
		ossService.downFile(request, response);
    }
}
