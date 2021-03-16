package com.bizentro.unimes.tracking.service.common.codemanage.code;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bizentro.unimes.common.message.Message;
import com.bizentro.unimes.common.message.MessageSet;
import com.bizentro.unimes.common.model.common.code.Code;
import com.bizentro.unimes.common.util.MesException;
import com.bizentro.unimes.tracking.common.util.TrackingUtil;
import com.bizentro.unimes.tracking.model.common.CodeModel;
import com.bizentro.unimes.tracking.service.base.BaseApi;

@Service("COMCodeManage.CreateCode")
public class CreateCode extends BaseApi{
	@Resource(name = "common.CodeModel")
	private CodeModel codeModel;

	public List<Message> execute(MessageSet set) throws Exception {
		this.xmlSchema.getMessageSet(this.apiName, set);

		this.codeModel.checkDuplicateMessage(set.getList(), new String[] { "site", "name", "codeClassName" });

		List<Message> txList = new ArrayList<Message>();

		for (Message message : set.getList()) {
			Code msgCode = (Code) message;

			this.codeModel.initializeMessage(msgCode, this.apiName);

			TrackingUtil.isCorrectIsValidText(msgCode.getIsUsable());

//			this.codeModel.getCodeClassWithActiveFlag(msgCode.getSite(), msgCode.getCodeClassID(), true);

			if (this.codeModel.findCode(msgCode) != null) {
				throw new MesException("ExistData", new String[] { "Code", "site : " + msgCode.getSite() + ", name : "
						+ msgCode.getName() + ", codeClassName : " + msgCode.getCodeClassID() });
			}

			this.codeModel.create(msgCode);

			txList.add(msgCode);
		}

		return txList;
	}
}