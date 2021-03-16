package com.bizentro.unimes.tracking.service.base;

import java.util.List;

import javax.annotation.Resource;

import com.bizentro.unimes.common.message.Message;
import com.bizentro.unimes.common.message.MessageSet;
import com.bizentro.unimes.tracking.common.xml.XmlSchema;

public abstract class BaseApi {

	@Resource(name = "xmlSchema")
	protected XmlSchema xmlSchema;
	protected String apiName = getClass().getSimpleName();

	public abstract List<Message> execute(MessageSet paramMessageSet) throws Exception;
}
