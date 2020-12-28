package com.example.ossdemo.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.example.ossdemo.service.ExportWordService;

/**
 * 导出word
 * @author Administrator
 *
 */
@RequestMapping("/exportWord")
@RestController
public class ExportWordController {

	@Autowired
	private ExportWordService exportWordService;
	
	/**
	 * 进入导出页面
	 * @return
	 */
	@RequestMapping("/index")
	public ModelAndView toIndex(){
		return new ModelAndView("export/index");
	}
	/**
	 * 导出销售订单信息（导出word）
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/exportOrderContractInfo",method = RequestMethod.POST)
	public Map<String, Object> exportOrderContractInfo(HttpServletRequest request) throws Exception{
		return exportWordService.exportOrderContractInfo(request);
	}
}
