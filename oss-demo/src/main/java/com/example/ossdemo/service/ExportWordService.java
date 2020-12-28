package com.example.ossdemo.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface ExportWordService {

	/**
	 * 导出订单合同信息
	 * @param request
	 */
	public Map<String,Object> exportOrderContractInfo(HttpServletRequest request) throws Exception;
}
