package com.bizentro.unimes.tracking.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
//import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.LoggerFactory;

import com.bizentro.unimes.common.message.CustomedFields;
import com.bizentro.unimes.common.message.Message;
//import com.bizentro.unimes.common.message.MessageAttributes;
import com.bizentro.unimes.common.util.CommonUtil;
import com.bizentro.unimes.common.util.MesException;
import com.bizentro.unimes.tracking.common.dao.CommonDAO;
import com.bizentro.unimes.tracking.common.util.CONSTANT;
import com.bizentro.unimes.tracking.common.util.TrackingUtil;

public class Model {
	@Resource(name = "CommonDAO")
	private CommonDAO commonDAO;

	/**
	 * 
	 * message를 가지고 commonDAO의 get(message)을 호출한다.<br>
	 * 복사본을 만들어 리턴한다.<br>
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public Message find(Message message) throws Exception {
		Object targetObj = null;
		Object sourceObj = this.commonDAO.get(message);


		if (sourceObj != null) {
			targetObj = Class.forName(message.getClass().getName()).newInstance();
			CommonUtil.copyValue(targetObj, sourceObj);
		}

		return (Message) targetObj;
	}

	public void checkDuplicateMessage(List<Message> list) throws Exception {
		checkDuplicateMessage(list, new String[] { "site", "name" });
	}

	public void checkDuplicateMessage(List<Message> list, String[] params) throws Exception {
		
		// list 자체가 null 이면 에러
		if (list == null) {
			throw new MesException("NotFoundMessage", "Message");
		}

		if (list.size() <= 1)
			return;

		String className = ((Message) list.get(0)).getClass().getSimpleName();

		Map<String, Object> distinctMap = new HashMap<String, Object>();

		// loop를 돌면서 key 값을 map에 담음
		for (Message obj : list) {
			String key = "";
			for (String param : params) {
				key += CommonUtil.getValue(obj, param);
			}

			if (distinctMap.get(key) != null) {
				// MessageSet 내부의 Message ([{0}]) List 에 중복된 키가 존재합니다.
				throw new MesException("DuplicateMessage", new String[] { className });
			} else {
				distinctMap.put(key, "");
			}
		}
	}

	public Message findWithLock(Message message) throws Exception {
		return this.commonDAO.getWithLock(message);
	}

	/**
	 * 
	 * sql 및 parameter 값을 가지고, commonDAO의 get(sql, params)를 호출한다. 복사본을 만들어 리턴한다.<br>
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Message find(String sql, Object... params) throws Exception {
		
		Object targetObj = null;
		Object sourceObj = this.commonDAO.get(sql, params);

		if (sourceObj != null) {
			targetObj = Class.forName(sourceObj.getClass().getName()).newInstance();
			CommonUtil.copyValue(targetObj, sourceObj);
		}

		return (Message) targetObj;
	}

//	public Message findWithOutVersion(Message message) throws Exception {
//		String sql = "FROM " + message.getClass().getSimpleName() + " WHERE site = ? AND name = ? AND isUsable = ? ";
//		Object targetObj = null;
//		Object sourceObj = this.commonDAO.get(sql,
//				new Object[] { message.getSite(), message.getName(), message.getIsValid() });
//
//		if (sourceObj != null) {
//			targetObj = Class.forName(sourceObj.getClass().getName()).newInstance();
//			CommonUtil.copyValue(targetObj, sourceObj);
//		}
//
//		return (Message) targetObj;
//	}

	public List<?> getSQLList(String sql, Object[] params) throws Exception {
		return this.commonDAO.getSQLList(sql, params);
	}

	/**
	 * 
	 * sql 및 parameter 값을 가지고, commonDAO의 getList(sql, params)를 호출한다. 복사본을 만들어 리턴한다.<br>
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List findList(String sql, Object... params) throws Exception {
		List returnList = new ArrayList();

		List sourceList = this.commonDAO.getList(sql, params);

		for (Object sourceObj : sourceList) {
			Object targetObj = null;

			targetObj = Class.forName(sourceObj.getClass().getName()).newInstance();
			CommonUtil.copyValue(targetObj, sourceObj);
			//returnList.add(targetObj);
			returnList.add(targetObj);
		}

		return returnList;
	}

	/**
	 * 
	 * sql 및 parameter 값을 가지고, commonDAO의 getWithMapList(sql, params)를 호출한다. 복사본을 만들어 리턴한다.<br>
	 * 
	 * @param sql
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Message> findList(String sql, Map<String, Object> param) throws Exception {
		List<Message> returnList = new ArrayList<Message>();

		List<?> sourceList = this.commonDAO.getWithMapList(sql, param);

		for (Object sourceObj : sourceList) {
			Object targetObj = Class.forName(sourceObj.getClass().getName()).newInstance();
			CommonUtil.copyValue(targetObj, sourceObj);
			returnList.add((Message) targetObj);
		}

		return returnList;
	}

	/**
	 * 
	 * sql 및 parameter 값을 가지고, commonDAO의 getNativeSql(sql, params)를 호출한다. 복사본을 만들어 리턴한다.<br>
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Message findNativeSql(String sql, Object... params) throws Exception {
		Object targetObj = null;
		Object sourceObj = this.commonDAO.getNativeSql(sql, params);

		if (sourceObj != null) {
			targetObj = Class.forName(sourceObj.getClass().getName()).newInstance();
			CommonUtil.copyValue(targetObj, sourceObj);
		}

		return (Message) targetObj;
	}

	/**
	 * 
	 * sql 및 parameter 값을 가지고, commonDAO의 getNativeSqlList(sql, params)를 호출한다. 복사본을 만들어 리턴한다.<br>
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List findNativeSqlList(String sql, Object... params) throws Exception {
		List returnList = new ArrayList();

		List sourceList = this.commonDAO.getNativeSqlList(sql, params);

		for (Object sourceObj : sourceList) {
			if (sourceObj != null) {
				Object targetObj = Class.forName(sourceObj.getClass().getName()).newInstance();
				CommonUtil.copyValue(targetObj, sourceObj);
				returnList.add(targetObj);
			}
		}

		return returnList;
	}

	/**
	 * 
	 * message를 가지고 commonDAO의 get을 호출한다.<br>
	 * 복사본을 만들지 않는다.<br>
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public Message get(Message message) throws Exception
	{
		return this.commonDAO.get(message);
	}

	/**
	 * 
	 * sql 및 parameter 값을 가지고, commonDAO의 get(sql, params)를 호출한다. 복사본을 만들지 않는다.<br>
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Message get(String sql, Object... params) throws Exception
	{
		return this.commonDAO.get(sql, params);
	}

	/**
	 * 
	 * sql 및 parameter 값을 가지고, commonDAO의 getList(sql, params)를 호출한다. 복사본을 만들지 않는다.<br>
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<?> getList(String sql, Object... params) throws Exception
	{
		return this.commonDAO.getList(sql, params);
	}

	/**
	 * 
	 * sql 및 parameter 값을 가지고, commonDAO의 getNativeSql(sql, params)를 호출한다. 복사본을 만들지 않는다.<br>
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Object getNativeSql(String sql, Object... params) throws Exception
	{
		return this.commonDAO.getNativeSql(sql, params);
	}

	/**
	 * 
	 * sql 및 parameter 값을 가지고, commonDAO의 getNativeSql(sql, params)를 호출한다. 복사본을 만들지 않는다.<br>
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<?> getNativeSqlList(String sql, Object... params) throws Exception
	{
		return this.commonDAO.getNativeSqlList(sql, params);
	}

	public void existSite(String site) throws Exception {
		String sql = "FROM Facility WHERE site = ? AND name = ? AND isUsable = 'Usable'";

		if (this.commonDAO.get(sql, new Object[] { site, site }) == null) {
			throw new MesException("NotFoundMasterDataWithUsable",
					new String[] { "site : " + site + ", name : " + site, "Facility (site)" });
		}
	}

	public void getProperties(Message message) throws Exception {
		this.commonDAO.getProperties(message);
	}

	/**
	 * @param message
	 * @throws Exception
	 */
	public void create(Message message) throws Exception
	{
		message.setTxnCode(CONSTANT.CREATE);
		generateModelId(message);
	}
	
//	public void create(Message message) throws Exception {
//		message.setTxnCode(CONSTANT.CREATE);
//		generateModelId(message);
//
//		message.setCreateTime(message.getUpdateTime());
//		message.setCreateUser(message.getUpdateUser());
//
//		message.setLastEventTime(getCurrentTimeStampString());
//	}

	public void modify(Message message) throws Exception {
		message.setTxnCode(CONSTANT.MODIFY);
	}

	public void remove(Message message) throws Exception {
		message.setTxnCode(CONSTANT.REMOVE);
	}

	public void generateModelId(Message message) throws Exception {
		message.setId(this.commonDAO.getModelId());
	}

	/**
	 * 
	 * 현재의 TimeStamp(20자리)를 가져온다.
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getCurrentTimeStampString() throws Exception
	{
		return this.commonDAO.getCurrentTimeStampString();
	}

	/**
	 * 
	 * API 내부에서 Message(Producible, Consumable, Durable... 등)에 대한 기본 세팅 값.
	 * 
	 * @param message
	 * @param apiName
	 * @throws Exception
	 */
	public void initializeMessage(Message message, String apiName) throws Exception
	{
		String site = message.getSite();

		// Mandatory Check
		CommonUtil.checkMandatory(site, "Site");
		//CommonUtil.checkMandatory(message.getName(), "name");
		CommonUtil.checkMandatory(message.getUpdateUser(), "updateUser");

		// site 유효성 체크
		//this.existFacility(null, site);		
		
		// Modify Time Setting
		if (message.getUpdateTime()==null) message.setUpdateTime(new Date());
		
		// activity,originalActivity 설정
		message.setExecuteService(CommonUtil.isEmpty(message.getCustomEventName()) ? apiName : message.getCustomEventName());
		message.setOriginalExecuteService(apiName);

		// histFlag의 default는 true
		message.setHistFlag(CommonUtil.null2Boolean(message.getHistFlag(), new Boolean(true)));
	}
	
	/**
	 * 
	 * @param targetList
	 * @param sourceList
	 */
	public void addList2List(List<Message> targetList, List<Message> sourceList)
	{
		for (Message message :sourceList)
		{
			targetList.add(message);
		}
	}

	public void initializeMessage(Message message, String apiName, Map<String, String> distinctSiteExistMap)
			throws Exception {
		String site = message.getSite();

		CommonUtil.checkMandatory(site, "site");
		CommonUtil.checkMandatory(message.getName(), "name");

		if (distinctSiteExistMap == null) {
			existSite(site);
		} else if (!distinctSiteExistMap.containsKey(site)) {
			existSite(site);
			distinctSiteExistMap.put(site, "EXISTS");
		}

		initializeMessageWithCommonValue(message, apiName, true);
	}

	public void initializeMessageWithVersion(Message message, String apiName) throws Exception {
		if (TrackingUtil.isDefaultVersion(message.getVersion())) {
			throw new MesException("EmptyMandatory", "version");
		}

		initializeMessage(message, apiName);
	}

	protected void initializeMessageWithCommonValue(Message message, String apiName, boolean hasHistory)
			throws Exception {
		message.setTxnCode(CONSTANT.MODIFY);

		CommonUtil.checkMandatory(message.getUpdateUser(), "modifier");

		if (message.getUpdateTime() == null)
			message.setUpdateTime(new Date());

		message.setExecuteService(CommonUtil.isEmpty(message.getCustomEventName()) ? apiName : message.getCustomEventName());
		message.setOriginalExecuteService(apiName);

		message.setHistFlag(CommonUtil.null2Boolean(message.getHistFlag(),
				CommonUtil.null2Boolean(Boolean.valueOf(hasHistory), Boolean.valueOf(true))));
	}

	/**
	 * 
	 * update 정보 설정
	 * 
	 * @param sourceMessage
	 * @param destMessage
	 * @throws Exception
	 */
	public void finalizeMessage(Message targetMessage, Message sourceMessage) throws Exception {
		if (CommonUtil.isCollectionNotEmpty(targetMessage.getPrevLastEventTime()))
			return;

		targetMessage.setPrevLastEventTime(targetMessage.getLastEventTime());
		targetMessage.setLastEventTime(getCurrentTimeStampString());
		targetMessage.setUpdateTime(sourceMessage.getUpdateTime());
		targetMessage.setUpdateUser(sourceMessage.getUpdateUser());
		targetMessage.setExecuteService(sourceMessage.getExecuteService());
		targetMessage.setOriginalExecuteService(sourceMessage.getOriginalExecuteService());
		targetMessage.setHistFlag(sourceMessage.getHistFlag());

		targetMessage.setTxnCode(sourceMessage.getTxnCode());

		if (sourceMessage.getActionCode() != null)
			targetMessage.setActionCode(sourceMessage.getActionCode());
		if (sourceMessage.getComments() != null)
			targetMessage.setComments(sourceMessage.getComments());
		if (sourceMessage.getDescription() != null)
			targetMessage.setDescription(sourceMessage.getDescription());
		if (sourceMessage.getDetailDescription() != null)
			targetMessage.setDetailDescription(sourceMessage.getDetailDescription());

		targetMessage.setAttributes(sourceMessage.getAttributes());
		targetMessage.setProperties(sourceMessage.getProperties());
	}

	public void finalizeMessageOnlyModifyInfo(Message targetMessage, Message sourceMessage) throws Exception {
		if (CommonUtil.isCollectionNotEmpty(targetMessage.getPrevLastEventTime()))
			return;

		targetMessage.setPrevLastEventTime(targetMessage.getLastEventTime());
		targetMessage.setLastEventTime(getCurrentTimeStampString());
		targetMessage.setUpdateTime(sourceMessage.getUpdateTime());
		targetMessage.setUpdateUser(sourceMessage.getUpdateUser());

		targetMessage.setExecuteService(sourceMessage.getExecuteService());
		targetMessage.setOriginalExecuteService(sourceMessage.getOriginalExecuteService());
		targetMessage.setHistFlag(sourceMessage.getHistFlag());
		targetMessage.setTxnCode(sourceMessage.getTxnCode());
	}

	public void finalizeMessageFromModify(Message targetMessage, Message sourceMessage) throws Exception {
		if (CommonUtil.isCollectionNotEmpty(targetMessage.getPrevLastEventTime()))
			return;

		targetMessage.setPrevLastEventTime(targetMessage.getLastEventTime());
		targetMessage.setLastEventTime(getCurrentTimeStampString());

		targetMessage.setUpdateTime(new Date());

		targetMessage.setUpdateUser(sourceMessage.getUpdateUser());
		targetMessage.setExecuteService(sourceMessage.getExecuteService());
		targetMessage.setOriginalExecuteService(sourceMessage.getOriginalExecuteService());
		targetMessage.setHistFlag(sourceMessage.getHistFlag());

		targetMessage.setTxnCode(sourceMessage.getTxnCode());

		targetMessage.setAttributes(sourceMessage.getAttributes());
		targetMessage.setProperties(sourceMessage.getProperties());
	}

	public void setOverrideRemoveFields(Message message) throws Exception {
		CustomedFields overrides = message.getOverrides();

		if (CommonUtil.getValue(message, "version") != null) {
			overrides.remove("isUsable");
		}

		overrides.remove("createUser");
		overrides.remove("createTime");
		overrides.remove("originalExecuteService");
		overrides.remove("histFlag");
		overrides.remove("txnCode");
		overrides.remove("prevLastEventTime");
		overrides.remove("site");
		overrides.remove("name");
		overrides.remove("version");
		overrides.remove("id");
	}

	public void setOverrideRemoveFields(Message message, String[] fields) throws Exception {
		CustomedFields overrides = message.getOverrides();

		for (String field : fields) {
			overrides.remove(field);
		}

		setOverrideRemoveFields(message);
	}

	public void existObjectWithSiteNId(Message message, String site, String id) throws Exception {
		String hvoName = message.getClass().getSimpleName();

		existObjectWithSiteNId(hvoName, site, id);
	}

	public void existObjectWithSiteNId(String hvoName, String site, String id) throws Exception {
		String sql = "FROM " + hvoName + " WHERE Site = ? AND id = ?";

		if (this.commonDAO.get(sql, new Object[] { site, id }) == null) {
			throw new MesException("NotFoundData", new String[] { hvoName,
					"site : " + site + ", id : " + id + ", (SM" + hvoName.toUpperCase() + ")" });
		}
	}
	
}