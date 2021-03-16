package com.bizentro.unimes.tracking.service.base;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.bizentro.unimes.common.message.Message;
import com.bizentro.unimes.common.message.MessageSet;
import com.bizentro.unimes.tracking.common.tx.Transaction;

public abstract class BaseService implements ApplicationContextAware
{
	protected String m_seriveceName;
	protected ApplicationContext ctx;

	@Resource(name = "Transaction")
	protected Transaction tx;

	public BaseService(String seriveceName) {
		this.m_seriveceName = seriveceName;
	}

	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.ctx = ctx;
	}

	protected void execute(String serviceBeanName, MessageSet set) throws Exception {
		serviceBeanName = this.m_seriveceName + "." + serviceBeanName;

		List<Message> msgList = ((BaseApi) this.ctx.getBean(serviceBeanName)).execute(set);

		this.tx.save(msgList);
	}
}