package com.bizentro.unimes.tracking.common.aspect;

import org.aspectj.lang.JoinPoint;
import org.slf4j.LoggerFactory;

public class ModuleLoggingAspect {
	public void moduleBeforeLogger(JoinPoint thisJoinPoint) throws Exception {
		moduleLogger(thisJoinPoint, "[MODULE-START]");
	}

	public void moduleAfterLogger(JoinPoint thisJoinPoint) throws Exception {
		moduleLogger(thisJoinPoint, "[MODULE-END]");
	}

	public void moduleLogger(JoinPoint thisJoinPoint, String logType) throws Exception {
		LoggerFactory.getLogger("API_TRACE").info(thisJoinPoint.getTarget().getClass().getName() + "\t" + logType);
	}
}