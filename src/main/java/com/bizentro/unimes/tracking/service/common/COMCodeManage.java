package com.bizentro.unimes.tracking.service.common;

import com.bizentro.unimes.common.message.MessageSet;

public abstract interface COMCodeManage {
	public abstract MessageSet manageCode(MessageSet paramMessageSet) throws Exception;
	
	public abstract MessageSet createCode(MessageSet paramMessageSet) throws Exception;
	
	public abstract MessageSet modifyCode(MessageSet paramMessageSet) throws Exception;

	public abstract MessageSet manageCodeClass(MessageSet paramMessageSet) throws Exception;
	
	public abstract MessageSet createCodeClass(MessageSet paramMessageSet) throws Exception;
	
	public abstract MessageSet modifyCodeClass(MessageSet paramMessageSet) throws Exception;
}