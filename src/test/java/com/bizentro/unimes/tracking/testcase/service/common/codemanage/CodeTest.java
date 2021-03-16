package com.bizentro.unimes.tracking.testcase.service.common.codemanage;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.sql.DataSource;

import com.bizentro.unimes.common.message.MessageSet;
import com.bizentro.unimes.common.model.common.code.Code;
import com.bizentro.unimes.tracking.service.common.COMCodeManage;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/test/resources/spring/context-apiconfig.xml", "file:src/test/resources/spring/context-testapidstx.xml"})
public class CodeTest{
	
	@Autowired
	@Resource(name = "COMCodeManage")
	private COMCodeManage manage;
	
	 @Autowired
	private ApplicationContext context;
	
	// Instance Variable
	
	private String site = "BIZENTRO";
	private String name = "KEVIN1";
	private String updateUser = "Kevin,Park";
	private String id = "BizenCode";
	
	// Model : Code
	// 
	
	
	@Test
	public void testCreateCode() throws Exception{
		
		DataSource dataSource = (DataSource) context.getBean("dataSource");
        try {
           dataSource.getConnection();
        } catch (SQLException e) {
        }
		
		
		MessageSet msgSet = new MessageSet();
		
		msgSet.setUpdateUser(updateUser);
		
		Code testObj = new Code();
		
		testObj.setSite(site);
		testObj.setId(id);
		testObj.setName(name);
		
		msgSet.addMessage(testObj);
		
		msgSet = this.manage.createCode(msgSet);
		
	}

}
