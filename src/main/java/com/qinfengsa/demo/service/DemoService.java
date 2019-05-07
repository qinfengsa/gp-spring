package com.qinfengsa.demo.service;

/**
 *
 * DemoService
 * @author qinfengsa
 * @date 2019/4/4 15:12
 */
public interface DemoService {

	/**
	 * 加法
	 * @param x
	 * @param y
	 * @return
	 */
	int add(int x, int y);

	/**
	 * 减法
	 * @param x
	 * @param y
	 * @return
	 */
	int sub(int x, int y);

	/**
	 * 比较
	 * @param x
	 * @param y
	 * @return
	 */
	int compare(int x, int y);
}
