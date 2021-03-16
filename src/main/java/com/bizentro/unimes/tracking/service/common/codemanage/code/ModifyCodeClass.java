package com.bizentro.unimes.tracking.service.common.codemanage.code;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bizentro.unimes.common.message.Message;
import com.bizentro.unimes.common.message.MessageSet;
import com.bizentro.unimes.common.model.common.code.CodeClass;
import com.bizentro.unimes.common.util.CommonUtil;
import com.bizentro.unimes.common.util.MesException;
import com.bizentro.unimes.tracking.model.common.CodeModel;
import com.bizentro.unimes.tracking.service.base.BaseApi;


@Service("COMCodeManage.ModifyCodeClass")
public class ModifyCodeClass  extends BaseApi{

	@Resource(name = "common.CodeModel")
	private CodeModel codeModel;

	public List<Message> execute(MessageSet set) throws Exception {
		this.xmlSchema.getMessageSet(this.apiName, set);

		this.codeModel.checkDuplicateMessage(set.getList());

		List<Message> txList = new ArrayList<Message>();

		for (Message message : set.getList()) {
			CodeClass msgCodeClass = (CodeClass) message;

			this.codeModel.initializeMessage(msgCodeClass, this.apiName);

			CodeClass fetchCodeClass = (CodeClass) this.codeModel.find(msgCodeClass);

			if (fetchCodeClass == null) {
				throw new MesException("NotFoundData", new String[] { "CodeClass",
						"site : " + msgCodeClass.getSite() + ", name : " + msgCodeClass.getName() });
			}

			this.codeModel.finalizeMessageFromModify(fetchCodeClass, msgCodeClass);

			fetchCodeClass.setOverrides(msgCodeClass.getOverrides());
			this.codeModel.setOverrideRemoveFields(fetchCodeClass);
			CommonUtil.setOverrideMessage(fetchCodeClass);

			txList.add(fetchCodeClass);
		}

		return txList;
	}
}