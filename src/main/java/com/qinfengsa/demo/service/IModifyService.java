package com.qinfengsa.demo.service;

/**
 * 增删改业务
 * @author Tom
 *
 */
public interface IModifyService {

	/**
	 * 增加
	 * @param name
	 * @param addr
	 * @return
	 * @throws Exception
	 */
	String add(String name, String addr) throws Exception;
	
	/**
	 * 修改
	 * @param id
	 * @param name
	 * @return
	 */
	String edit(Integer id, String name);
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	String remove(Integer id);
	
}
