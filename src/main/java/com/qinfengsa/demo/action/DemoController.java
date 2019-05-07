package com.qinfengsa.demo.action;

import com.qinfengsa.demo.service.DemoService;
import com.qinfengsa.springframework.annotation.GPAutowired;
import com.qinfengsa.springframework.annotation.GPController;
import com.qinfengsa.springframework.annotation.GPRequestMapping;
import com.qinfengsa.springframework.annotation.GPRequestParam;

/**
 * 测试控制器
 * TestController
 * @author qinfengsa
 * @date 2019/4/1 13:06
 */
@GPController
@GPRequestMapping("/demo")
public class DemoController {

	@GPAutowired
    DemoService demoService;

	@GPRequestMapping("/test")
	public String test1() {
		return "test success!";
	}

	@GPRequestMapping("/add")
	public int add(@GPRequestParam("a") int a,@GPRequestParam("b") int b) {
		return demoService.add(a,b);
	}

	@GPRequestMapping("/sub")
	public int sub(@GPRequestParam("a") int a,@GPRequestParam("b") int b) {
		return demoService.sub(a,b);
	}

	@GPRequestMapping("/compare")
	public int compare(@GPRequestParam("a") int a,@GPRequestParam("b") int b) {
		return demoService.compare(a,b);
	}
}
