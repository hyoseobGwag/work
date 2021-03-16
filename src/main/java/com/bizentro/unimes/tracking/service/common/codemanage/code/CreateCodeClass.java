package com.bizentro.unimes.tracking.service.common.codemanage.code;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bizentro.unimes.common.message.Message;
import com.bizentro.unimes.common.message.MessageSet;
import com.bizentro.unimes.common.model.common.code.CodeClass;
import com.bizentro.unimes.common.util.MesException;
import com.bizentro.unimes.tracking.common.util.TrackingUtil;
import com.bizentro.unimes.tracking.model.common.CodeModel;
import com.bizentro.unimes.tracking.service.base.BaseApi;

@Service("COMCodeManage.CreateCodeClass")
public class CreateCodeClass extends BaseApi{
	@Resource(name = "common.CodeModel")
	private CodeModel codeModel;

	public List<Message> execute(MessageSet set) throws Exception {
		this.xmlSchema.getMessageSet(this.apiName, set);

		this.codeModel.checkDuplicateMessage(set.getList());

		List<Message> txList = new ArrayList<Message>();

		for (Message message : set.getList()) {
			CodeClass msgCodeClass = (CodeClass) message;

			this.codeModel.initializeMessage(msgCodeClass, this.apiName);

			TrackingUtil.isCorrectIsValidText(msgCodeClass.getIsUsable());

			if (this.codeModel.get(msgCodeClass) != null) {
				throw new MesException("ExistData", new String[] { "CodeClass",
						"site : " + msgCodeClass.getSite() + ", name : " + msgCodeClass.getName() });
			}

			this.codeModel.create(msgCodeClass);

			txList.add(msgCodeClass);
		}

		return txList;
	}
}