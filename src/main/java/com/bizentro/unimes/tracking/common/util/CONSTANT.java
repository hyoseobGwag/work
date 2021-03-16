package com.bizentro.unimes.tracking.common.util;

public class CONSTANT {
	public static final String TOKEN = ",";
	public static final Integer CREATE = Integer.valueOf(0);
	public static final Integer MODIFY = Integer.valueOf(1);
	public static final Integer REMOVE = Integer.valueOf(2);
	public static final Integer CANCEL = Integer.valueOf(3);
	public static final Integer IGNORE = Integer.valueOf(4);
	public static final String PAYLOAD_EMPTY = "Empty";
	public static final String PAYLOAD_FULL = "Full";
	public static final String PAYLOAD_PARTIAL = "Partial";
	public static final String MATERIAL_CONSUMABLE = "Consumable";
	public static final String MATERIAL_PRODUCIBLE = "Producible";
	public static final String ACTIVE = "Active";
	public static final String INACTIVE = "InActive";
	public static final String FROZEN = "Frozen";
	public static final String DATE_HOUR = "HOUR";
	public static final String DATE_MIN = "MIN";
	public static final String DATE_SEC = "SEC";
	public static final String DATE_YEAR = "YEAR";
	public static final String DATE_MONTH = "MONTH";
	public static final String DATE_DAY = "DAY";
	public static final int DEFAULT_VERSION = -1;
	public static final int DEFAULT_RULESEQUENCE = -1;
	public static final String DEFAULT_PROCESSPATH = "PASS";
	public static final String DEFAULT_UNIT = "EA";
	public static final String NEXT_RULE = "NEXTRULE";
	public static final String PREV_RULE = "PREVRULE";
	public static final String DURABLE_DUMP = "DUMP";
	public static final String DURABLE_COLLAPSEUP = "COLLAPSEUP";
	public static final String DURABLE_COLLAPSEDOWN = "COLLAPSEDOWN";
	public static final String DURABLE_REVERSE = "REVERSE";
	public static final String DURABLE_REVERSECOLLAPSEUP = "REVERSECOLLAPSEUP";
	public static final String DURABLE_REVERSECOLLAPSEDOWN = "REVERSECOLLAPSEDOWN";
	public static final String ASSEMBLEHISTORY_ASSEMBLE = "ASSEMBLE";
	public static final String ASSEMBLEHISTORY_RECLAIM = "RECLAIM";
	public static final String ASSEMBLEHISTORY_RETURN = "RETURN";
	public static final String ASSEMBLEHISTORY_SCRAP = "SCRAP";
	public static final String PROCESSNODE_RESOURCETYPE_SEGMENT = "SEGMENT";
	public static final String PROCESSNODE_RESOURCETYPE_PROCESS = "PROCESS";
	public static final String PRODUCIBLETYPE_MAIN = "MAIN";
	public static final String PRODUCIBLETYPE_SUB = "SUB";
	public static final String CALCLOSSQTYTYPE_ADD = "ADD";
	public static final String CALCLOSSQTYTYPE_REPLACE = "REPLACE";
	public static final String CALCLOSSQTYTYPE_REMAIN = "REMAIN";
	public static final String IDLE = "Idle";
	public static final String RUN = "Run";
	public static final String PM = "Pm";
	public static final String DOWN = "Down";
	public static final String HOLD = "Hold";
	public static final String OCCUPIED = "Occupied";
	public static final String EMPTY = "Empty";
	public static final String CLEANING = "Cleaning";
	public static final String PROCESSING = "Processing";
	public static final String REPAIR = "Repair";
	public static final String TERMINATED = "Terminated";
	public static final String DEFECTIVE = "Defective";
	public static final String INSPECTED = "Inspected";
	public static final String CREATED = "Created";
	public static final String INVENTORY = "Inventory";
	public static final String FINISHED = "Finished";
	public static final String ARCHIVABLE = "Archivable";
	public static final String SHIPPED = "Shipped";
	public static final String SCRAPPED = "Scrapped";
	public static final String CONVERTED = "Converted";
	public static final String PROCESSEDSEGMENT = "ProcessedSegment";
	public static final String WAITFORFUNCTION = "WaitForFunction";
	public static final String ACKNOWLEDGE = "Acknowledge";
	public static final String CLOSED = "Closed";
	public static final String FAIL = "Fail";
	public static final String REFUSED = "Refused";
	public static final String SUCCESS = "Success";
	public static final String Processing = "Processing";
	public static final String APPROVED = "Approved";
	public static final String COMPLETE = "Complete";
	public static final String ALARM_STATE = "ALARM";
	public static final String ALARMACTION_STATE = "ALARMACTION";
	public static final String BATCH_STATE = "BATCH";
	public static final String CONSUMABLE_STATE = "CONSUMABLE";
	public static final String DURABLE_STATE = "DURABLE";
	public static final String EQUIPMENT_STATE = "EQUIPMENT";
	public static final String SUBPRODUCIBLE_STATE = "SUBPRODUCIBLE";
	public static final String PRODUCIBLE_STATE = "PRODUCIBLE";
	public static final String PRODUCTIONWORKORDER_STATE = "PRODUCTIONWORKORDER";
	public static final String FUTUREACTION_CompleteReworkProducible = "CompleteReworkProducible";
	public static final String FUTUREACTION_HoldProducible = "HoldProducible";
	public static final String FUTUREACTION_RepositionSegmentProducible = "RepositionSegmentProducible";
	public static final String FUTUREACTION_ReassignProducible = "ReassignProducible";
	public static final String Consumable_CreateConsumable = "CreateConsumable";
	public static final String Durable_CancelAssignDurableConsumable = "CancelAssignDurableConsumable";
	public static final String Durable_MoveDurable = "MoveDurable";
	public static final String Durable_AssignDurableProducible = "AssignDurableProducible";
	public static final String Durable_CancelAssignDurableProducible = "CancelAssignDurableProducible";
}