package com.example.ossdemo.service;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface OssService {
	
	/**
	 * 文件上传
	 * @param filepath
	 * @param inputstream
	 * @return
	 */
	public boolean uploadFile(String filepath, InputStream inputstream);
	
	/**
	 * 下载oss文件
	 * @param request
	 * @param response
	 */
	public void downFile(HttpServletRequest request,HttpServletResponse response);
}
