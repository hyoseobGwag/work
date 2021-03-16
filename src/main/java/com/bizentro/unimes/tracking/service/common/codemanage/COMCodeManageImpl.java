/*
 * FileName : COMCodeManage.java
 *
 * Version : 0.1
 * 
 * Author : kevin,Park
 *
 * Create Date : 2021. 2. 28.
 *
 */
package com.bizentro.unimes.tracking.service.common.codemanage;

import java.util.ArrayList;
import java.util.List;

//import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.bizentro.unimes.common.message.Message;
import com.bizentro.unimes.common.message.MessageSet;
import com.bizentro.unimes.common.util.MesException;
import com.bizentro.unimes.tracking.common.util.CONSTANT;
import com.bizentro.unimes.tracking.service.base.BaseService;
import com.bizentro.unimes.tracking.service.common.COMCodeManage;
import com.bizentro.unimes.tracking.service.common.codemanage.code.CreateCode;
import com.bizentro.unimes.tracking.service.common.codemanage.code.CreateCodeClass;
import com.bizentro.unimes.tracking.service.common.codemanage.code.ModifyCode;
import com.bizentro.unimes.tracking.service.common.codemanage.code.ModifyCodeClass;

@Service("COMCodeManage")
public class COMCodeManageImpl extends BaseService implements COMCodeManage{
	
	public COMCodeManageImpl() {
		super("COMCodeManage");
	}
	
	/**
	 * Code Management
	 * 
	 */
	public MessageSet manageCode(MessageSet set) throws Exception {
		MessageSet setClone = set.clone();

		List<Message> createList = new ArrayList<Message>();
		List<Message> modifyList = new ArrayList<Message>();

		for (Message message : setClone.getList()) {
			if (CONSTANT.CREATE.equals(message.getRequestType())) {
				createList.add(message);
			} else if (CONSTANT.MODIFY.equals(message.getRequestType())) {
				modifyList.add(message);
			}
		}

		if (createList.size() > 0) {
			setClone.setList(createList);
			createCode(setClone);
		}

		if (modifyList.size() > 0) {
			setClone.setList(modifyList);
			modifyCode(setClone);
		}

		return set;
	}

	/**
	 * Create Code
	 */
	public MessageSet createCode(MessageSet set) throws Exception {
		try {
			List<Message> list = ((CreateCode) ctx.getBean("COMCodeManage.CreateCode")).execute(set);
			tx.save(list);
			set.setList(list);
		}
		catch(MesException e) {
			set.setReplyStatus("FAIL");
			set.setErrCode(e.getMessageKey());
			set.setErrMsg(e.getMessage());
			e.printStackTrace();
		}
		catch (Exception e)
		{
			set.setReplyStatus("FAIL");
			set.setErrCode(e.getMessage());
			set.setErrMsg(e.toString());
			e.printStackTrace();
		}
		
		return set;
	}
	
	/**
	 * Modify Code
	 */
	public MessageSet modifyCode(MessageSet set) throws Exception {
		
		try {
			List<Message> list = ((ModifyCode) ctx.getBean("COMCodeManage.ModifyCode")).execute(set);
			tx.save(list);
			set.setList(list);
		}
		catch(MesException e) {
			set.setReplyStatus("FAIL");
			set.setErrCode(e.getMessageKey());
			set.setErrMsg(e.getMessage());
			e.printStackTrace();
		}
		catch (Exception e)
		{
			set.setReplyStatus("FAIL");
			set.setErrCode(e.getMessage());
			set.setErrMsg(e.toString());
			e.printStackTrace();
		}
		
		return set;
	}
	
	/**
	 * Create CodeClass
	 */
	public MessageSet manageCodeClass(MessageSet set) throws Exception {
		MessageSet setClone = set.clone();

		List<Message> createList = new ArrayList<Message>();
		List<Message> modifyList = new ArrayList<Message>();

		for (Message message : setClone.getList()) {
			if (CONSTANT.CREATE.equals(message.getRequestType())) {
				createList.add(message);
			} else if (CONSTANT.MODIFY.equals(message.getRequestType())) {
				modifyList.add(message);
			}
		}

		if (createList.size() > 0) {
			setClone.setList(createList);
			createCodeClass(setClone);
		}

		if (modifyList.size() > 0) {
			setClone.setList(modifyList);
			modifyCodeClass(setClone);
		}

		return set;
	}
	
	/**
	 * Modify CodeClass
	 */
	public MessageSet createCodeClass(MessageSet set) throws Exception {
		try
		{
			List<Message> list = ((CreateCodeClass) ctx.getBean("COMCodeManaget.CreateCodeClass")).execute(set);
			tx.save(list);
			set.setList(list);
		}
		catch (MesException e)
		{
			set.setReplyStatus("FAIL");
			set.setErrCode(e.getMessageKey());
			set.setErrMsg(e.getMessage());
			e.printStackTrace();
		}
		catch (Exception e)
		{
			set.setReplyStatus("FAIL");
			set.setErrCode(e.getMessage());
			set.setErrMsg(e.toString());
			e.printStackTrace();
		}
		
		return set;
	}
	
	public MessageSet modifyCodeClass(MessageSet set) throws Exception {
		try
		{
			List<Message> list = ((ModifyCodeClass) ctx.getBean("COMCodeManage.ModifyCodeClass")).execute(set);
			tx.save(list);
			set.setList(list);
		}
		catch (MesException e)
		{
			set.setReplyStatus("FAIL");
			set.setErrCode(e.getMessageKey());
			set.setErrMsg(e.getMessage());
			e.printStackTrace();
		}
		catch (Exception e)
		{
			set.setReplyStatus("FAIL");
			set.setErrCode(e.getMessage());
			set.setErrMsg(e.toString());
			e.printStackTrace();
		}
		
		return set;
	}
	


}