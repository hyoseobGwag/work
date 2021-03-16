package com.bizentro.unimes.tracking.common.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.bizentro.unimes.common.message.Message;
import com.bizentro.unimes.common.util.CommonUtil;
import com.bizentro.unimes.common.util.MesException;

public class TrackingUtil {
	public static long getTimeByUnit(long lTime, int arg, String unit) throws Exception {
		long ret = 0L;

		if (CommonUtil.isEmpty(unit)) {
			throw new MesException("NotFound", "unit (queueTimeUnit or periodUnit)");
		}

		Date today = new Date(lTime);
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);

		if (unit.equals("DAY")) {
			cal.add(5, arg);
		} else if (unit.equals("MONTH")) {
			cal.add(2, arg);
		} else if (unit.equals("YEAR")) {
			cal.add(1, arg);
		} else if (unit.equals("HOUR")) {
			cal.add(10, arg);
		} else if (unit.equals("MIN")) {
			cal.add(12, arg);
		} else if (unit.equals("SEC")) {
			cal.add(13, arg);
		}

		ret = cal.getTimeInMillis();

		return ret;
	}

	public static boolean isDefaultVersion(Integer version) {
		if ((version == null) || (version.intValue() == -1)) {
			return true;
		}

		return false;
	}

	public static boolean isNotDefaultVersion(Integer version) {
		return !isDefaultVersion(version);
	}

	public static boolean isNotDefaultRuleSequence(Integer ruleSequence) {
		return !isDefaultRuleSequence(ruleSequence);
	}

	public static boolean isDefaultRuleSequence(Integer ruleSequence) {
		if ((ruleSequence == null) || (ruleSequence.intValue() == -1)) {
			return true;
		}

		return false;
	}

	public static void isRelActive(Message msg, String str) throws Exception {
		if (!"Active".equals(msg.getIsUsable())) {
			throw new MesException("MustRelActive", str);
		}
	}

	public static void isCorrectIsValidTextWithCreateVersionFlag(String isUsable, boolean hasCreateVersion)
			throws Exception {
		if ((!"Frozen".equals(isUsable)) && (!"InActive".equals(isUsable)) && (!"Active".equals(isUsable))) {
			throw new MesException("InvalidValueWithValidValue",
					new String[] { "isUsable", isUsable, "Frozen or InActive or Active" });
		}

		if ((hasCreateVersion) && ("Active".equals(isUsable))) {
			throw new MesException("NotAcceptActive");
		}
	}

	public static void isCorrectIsValidText(String isUsable) throws Exception {
		isCorrectIsValidTextWithCreateVersionFlag(isUsable, false);
	}

	public static void isCorrectProcessNodeResourceTypeText(String resourceType) throws Exception {
		if ((!"PROCESS".equals(resourceType)) && (!"SEGMENT".equals(resourceType))) {
			throw new MesException("InvalidValueWithValidValue",
					new String[] { "resourceType(ProcessNode)", resourceType, "PROCESS or SEGMENT" });
		}
	}

	public static void isCorrectCancelAssembleTypeText(String cancelAssembleType) throws Exception {
		if ((!"RECLAIM".equals(cancelAssembleType)) && (!"RETURN".equals(cancelAssembleType))
				&& (!"SCRAP".equals(cancelAssembleType))) {
			throw new MesException("InvalidValueWithValidValue",
					new String[] { "cancelAssembleType", cancelAssembleType, "RECLAIM or RETURN or SCRAP" });
		}
	}

	public static void isCorrectCalcLossQtyTypeText(String calcLossQtyType) throws Exception {
		if ((!"ADD".equals(calcLossQtyType)) && (!"REPLACE".equals(calcLossQtyType))
				&& (!"REMAIN".equals(calcLossQtyType))) {
			throw new MesException("InvalidValueWithValidValue",
					new String[] { "calcLossQtyType", calcLossQtyType, "ADD or REPLACE or REMAIN" });
		}
	}

	public static void isCorrectDurablePositionText(String durablePosition) throws Exception {
		if ((!"DUMP".equals(durablePosition)) && (!"COLLAPSEUP".equals(durablePosition))
				&& (!"COLLAPSEDOWN".equals(durablePosition)) && (!"REVERSE".equals(durablePosition))
				&& (!"REVERSECOLLAPSEUP".equals(durablePosition)) && (!"REVERSECOLLAPSEDOWN".equals(durablePosition))) {
			throw new MesException("InvalidValueWithValidValue", new String[] { "durablePosition", durablePosition,
					"DUMP or COLLAPSEUP or COLLAPSEDOWN or REVERSE or REVERSECOLLAPSEUP or REVERSECOLLAPSEDOWN" });
		}
	}

	public static void isCorrectTimeUnit(String timeUnit) throws Exception {
		if ((!"YEAR".equals(timeUnit)) && (!"MONTH".equals(timeUnit)) && (!"DAY".equals(timeUnit))
				&& (!"HOUR".equals(timeUnit)) && (!"MIN".equals(timeUnit)) && (!"SEC".equals(timeUnit))) {
			throw new MesException("InvalidValueWithValidValue", new String[] {
					"timeUnit (queueTimeUnit or periodUnit)", timeUnit, "YEAR or MONTH or DAY or HOUR or MIN or SEC" });
		}
	}
}