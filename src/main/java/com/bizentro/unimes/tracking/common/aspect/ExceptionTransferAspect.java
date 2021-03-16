package com.bizentro.unimes.tracking.common.aspect;

import java.sql.SQLException;
import java.util.Locale;

import org.aspectj.lang.JoinPoint;
import org.hibernate.exception.GenericJDBCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

import com.bizentro.unimes.common.util.CommonUtil;
import com.bizentro.unimes.common.util.MesException;


public class ExceptionTransferAspect implements MessageSourceAware
{
	private Logger logger = LoggerFactory.getLogger("API_TRACE");
	private MessageSource messageSource;

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void transfer(JoinPoint thisJoinPoint, Exception ex) throws Exception {
		if ((ex instanceof MesException)) {
			MesException uniMesEx = (MesException) ex;

			String messageKey = uniMesEx.getMessageKey();
			String[] messageParameters = (String[]) uniMesEx.getMessageParameters();
			String MesExceptionMessage = uniMesEx.getMesExceptionMessage();

			if (CommonUtil.isEmpty(MesExceptionMessage)) {
				try {
					MesExceptionMessage = this.messageSource.getMessage(messageKey, messageParameters,
							Locale.getDefault());
				} catch (Exception e) {
					MesExceptionMessage = "NOT FOUND ERROR MESSAGE. CHECK MESSAGE RESOURCE FILE : [MESSAGEKEY : "
							+ messageKey + "]";
				}

				this.logger.error(MesExceptionMessage);
			}

			uniMesEx.setMessageKey(messageKey);
			uniMesEx.setMesExceptionMessage(MesExceptionMessage);
			throw uniMesEx;
		}

		if ((ex instanceof GenericJDBCException)) {
			SQLException sqlEx = ((GenericJDBCException) ex).getSQLException();
			this.logger.error(sqlEx.getMessage(), sqlEx);

			throw sqlEx;
		}

		this.logger.error(ex.getMessage(), ex);

		throw ex;
	}
}