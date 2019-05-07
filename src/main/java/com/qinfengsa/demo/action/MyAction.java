package com.qinfengsa.demo.action;

import com.qinfengsa.demo.service.IModifyService;
import com.qinfengsa.demo.service.IQueryService;
import com.qinfengsa.springframework.annotation.GPAutowired;
import com.qinfengsa.springframework.annotation.GPController;
import com.qinfengsa.springframework.annotation.GPRequestMapping;
import com.qinfengsa.springframework.annotation.GPRequestParam;
import com.qinfengsa.springframework.web.servlet.GPModelAndView;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 控制器
 * @author qinfengsa
 * @date 2019/4/27 23:00
 */
@GPController
@GPRequestMapping("/web")
@Slf4j
public class MyAction {

	@GPAutowired
	IQueryService queryService;
	@GPAutowired
	IModifyService modifyService;


	@GPRequestMapping("/view.json")
	public GPModelAndView testView() {
		Map<String,Object> model = new HashMap<>();
		model.put("teacher","tom");
		model.put("data","abc");
		model.put("token", "123456");
		return new GPModelAndView("first",model);
	}




	@GPRequestMapping("/query.json")
	public GPModelAndView query(HttpServletRequest request, HttpServletResponse response,
								@GPRequestParam("name") String name){
		String result = queryService.query(name);
		return out(response,result);
	}
	
	@GPRequestMapping("/add*.json")
	public GPModelAndView add(HttpServletRequest request, HttpServletResponse response,
                              @GPRequestParam("name") String name, @GPRequestParam("addr") String addr){
		String result = null;
		try {
			result = modifyService.add(name,addr);
			return out(response,result);
		} catch (Exception e) {
			Map<String,Object> model = new HashMap<String,Object>();
			model.put("detail",e.getCause().getMessage());
			model.put("stackTrace", Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]",""));
			return new GPModelAndView("500",model);
		}

	}
	
	@GPRequestMapping("/remove.json")
	public GPModelAndView remove(HttpServletRequest request, HttpServletResponse response,
                                 @GPRequestParam("id") Integer id){
		String result = modifyService.remove(id);
		return out(response,result);
	}
	
	@GPRequestMapping("/edit.json")
	public GPModelAndView edit(HttpServletRequest request, HttpServletResponse response,
                               @GPRequestParam("id") Integer id,
                               @GPRequestParam("name") String name){
		String result = modifyService.edit(id,name);
		return out(response,result);
	}
	
	
	
	private GPModelAndView out(HttpServletResponse resp, String str){
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
