package com.bizentro.unimes.tracking.common.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.bizentro.unimes.common.dictionary.Column;
import com.bizentro.unimes.common.dictionary.Table;
import com.bizentro.unimes.common.message.CustomedFields;
import com.bizentro.unimes.common.message.Message;
import com.bizentro.unimes.common.message.MessageAttributes;
//import com.bizentro.unimes.common.model.id.IDSerial;
import com.bizentro.unimes.common.model.common.state.State;
import com.bizentro.unimes.common.util.CommonUtil;
import com.bizentro.unimes.common.util.MesException;
import com.bizentro.unimes.tracking.common.util.TrackingUtil;

@Repository("CommonDAO")
public class CommonDAO extends HibernateDaoSupport {
	
	private String jdbcType = "MySQL";
	
	@Autowired
	public CommonDAO(SessionFactory sessionFactory) {
		
		this.setSessionFactory(sessionFactory);

	}

	/**
	 * 
	 * Message 객체를 가지고,<br>
	 * 해당 Id 멤버에 값이 있으면 Id로<br>
	 * Id 멤버에 값이 없으면 site, name, (version)으로 조회하여 Message 객체를 리턴한다.<br>
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public Message get(Message message) throws Exception {
		Message returnMsg = null;

		if (message == null)
			return null;

		if (CommonUtil.isNotEmpty(message.getId())) {
			// Id가 존재하는 경우
			returnMsg = (Message) this.getHibernateTemplate().get(message.getClass(), message.getId());
		} else {
			// Id가 존재하지 않는 경우
			// site, name, (version)을 가지고 HQL 쿼리 수행
			Query query = null;

			if (CommonUtil.getValue(message, "version") == null) {

				query = getSession().createQuery("FROM " + message.getClass().getName() + " WHERE Site = ? AND CodeName = ? ");
				query.setString(0, message.getSite());
				query.setString(1, message.getName());
			} else {
				String sql = "FROM " + message.getClass().getName() + " WHERE Site = ? AND CodeName = ? ";

				Integer version = message.getVersion();

				// default version(-1) 이면 version을 쿼리하지 않음
				if (TrackingUtil.isDefaultVersion(version)) {
					query = this.getSession().createQuery(sql);
					query.setString(0, message.getSite());
					query.setString(1, message.getName());
				} else {
					sql += " AND version = ? ";
					query = this.getSession().createQuery(sql);

					query.setString(0, message.getSite());
					query.setString(1, message.getName());
					query.setInteger(2, message.getVersion());
				}
			}

			returnMsg = (Message) query.uniqueResult();
		}

		return returnMsg;
	}

	/**
	 * 
	 * sql (from 절 포함) 과 parameter를 가지고 HQL을 수행하여<br>
	 * list(message list)를 리턴한다.<br>
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	
	@Autowired
	private ApplicationContext context;
	
public Message get(String CodeID, String Site, Object... params) throws Exception {
		
		DataSource dataSource = (DataSource) context.getBean("dataSource");
        try {
           dataSource.getConnection();
        } catch (SQLException e) {
        }
		
		Query query = this.getSession().createSQLQuery("FROM UMCODE WHERE Site = ? AND CodeID = ?");
	
		
				query.setString(0, CodeID);
				query.setString(1, Site);
		return (Message) query.uniqueResult();
	}

//	public Message get(String sql, Object... params) throws Exception {
//		
//		Query query = this.getSession().createSQLQuery(sql);
//		
//		if (params != null) {
//			int i = 0;
//		
//			for (Object obj : params) {
//				query.setParameter(i++, params);
//			}
//		}		
//		return (Message) query.uniqueResult();
//	}

	/**
	 * 
	 * sql (from 절 포함) 과 parameter를 가지고 HQL을 수행하여<br>
	 * list(message list)를 리턴한다.<br>
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List getSQLList(String sql, Object... params) throws Exception {
		Query query = this.getSession().createSQLQuery(sql);

		if (params != null) {
			int i = 0;

			for (Object obj : params) {
				query.setParameter(i++, obj);
			}
		}

		return query.list();
	}

	/**
	 * 
	 * sql (from 절 포함) 과 parameter를 가지고 HQL을 수행하여<br>
	 * list(message list)를 리턴한다.<br>
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<?> getList(String sql, Object... params) throws Exception {
		Query query = this.getSession().createQuery(sql);

		if (params != null) {
			int i = 0;

			for (Object obj : params) {
				query.setParameter(i++, obj);
			}
		}

		return query.list();
	}

	/**
	 * 
	 * Message 객체를 가지고,<br>
	 * 해당 Id 멤버에 값이 있으면 Id로<br>
	 * Id 멤버에 값이 없으면 site, name으로 조회하여 Message 객체를 리턴한다.<br>
	 * lock 을 하게 되는 api에서 사용한다.<br>
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public Message getWithLock(Message message) throws Exception {
		Message returnMsg = null;

		if (message == null)
			return null;

		if (CommonUtil.isNotEmpty(message.getId())) {
			// Id가 존재하는 경우
			returnMsg = (Message) this.getHibernateTemplate().get(message.getClass(), message.getId(),LockMode.UPGRADE);
		} else {
			// Id가 존재하지 않는 경우
			// site, name을 가지고 HQL 쿼리 수행 (with lock)
			Query query = this.getSession()
					.createQuery("From " + message.getClass().getName() + " Where Site = ? and CodeName = ? ");
			query.setString(0, message.getSite());
			query.setString(1, message.getName());
			returnMsg = (Message) query.uniqueResult();

			if (returnMsg != null) {
				returnMsg = (Message) this.getHibernateTemplate().get(message.getClass(), returnMsg.getId(),LockMode.UPGRADE);
			}
		}

		return returnMsg;
	}

	/**
	 * 
	 * sql (from 절 포함) 과 parameter를 가지고 NativeSql을 수행하여<br>
	 * message를 리턴한다.<br>
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Object getNativeSql(String sql, Object... params) throws Exception {
		SQLQuery query = this.getSession().createSQLQuery(sql);

		if (params != null) {
			int i = 0;

			for (Object param : params) {
				query.setParameter(i++, param);
			}
		}

		return (Object) query.uniqueResult();
	}

	/**
	 * 
	 * sql (from 절 포함) 과 parameter를 가지고 NativeSql을 수행하여<br>
	 * list(message list)를 리턴한다.<br>
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public List getNativeSqlList(String sql, Object... params) throws Exception {
		SQLQuery query = this.getSession().createSQLQuery(sql);

		if (params != null) {
			int i = 0;

			for (Object param : params) {
				query.setParameter(i++, param);
			}
		}

		return query.list();
	}

	/**
	 * 
	 * sql (from 절 포함) 과 parameter(Map(Key, Value)를 가지고 HQL을 수행하여<br>
	 * message를 리턴한다.<br>
	 * 
	 * @param sql
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public Message getWithMap(String sql, Map<String, Object> map) throws Exception {
		Query query = this.getSession().createQuery(sql);

		if (map != null) {
			Iterator<String> itor = map.keySet().iterator();
			while (itor.hasNext()) {
				String key = (String) itor.next();
				query.setParameter(key, map.get(key));
			}
		}

		return (Message) query.uniqueResult();
	}

	/**
	 * 
	 * sql (from 절 포함) 과 parameter(Map(Key, Value)를 가지고 HQL을 수행하여<br>
	 * list(message list)를 리턴한다.<br>
	 * 
	 * @param sql
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public List getWithMapList(String sql, Map<String, Object> map) throws Exception {
		Query query = this.getSession().createQuery(sql);

		if (map != null) {
			Iterator<String> itor = map.keySet().iterator();

			while (itor.hasNext()) {
				String key = (String) itor.next();
				query.setParameter(key, map.get(key));
			}
		}

		return query.list();
	}

	/**
	 * 
	 * 각 테이블에 들어갈 인공키를 생성하여 리턴한다.<br>
	 * 
	 * @param className
	 * @return
	 * @throws Exception
	 */
	public String getModelId(String className) throws Exception {
		
		String retValue = "";
		
		if (jdbcType == "MySQL"){					
			retValue =  CommonUtil.timestamp2String();			
		}
		else if (jdbcType == "ORACLE") {
			SQLQuery query = this.getSession().createSQLQuery(
					"SELECT PREFIXID || TO_CHAR(SYSTIMESTAMP,'YYYYMMDDHH24MISSff') FROM UMTABLE WHERE PREFIXID IS NOT NULL AND CLASSNAME = '"
							+ className + "'");
			
			retValue = (String) query.uniqueResult();
		}
		
		LoggerFactory.getLogger("API_TRACE").error("getModelId TimeStamp : " + retValue );
		return retValue;
	}

	public String getModelId() throws Exception {
		
		String retValue = "";
		
		if (jdbcType == "MySQL"){			
			retValue =  CommonUtil.timestamp2String();	
		}
		else if (jdbcType == "ORACLE") {
			SQLQuery query = getSession().createSQLQuery(
				"SELECT 'SYS' || TO_CHAR(SYSTIMESTAMP,'YYYYMMDDHH24MISSff') || LPAD(SMIDSEQ.NEXTVAL, 3, '0') FROM DUAL ");

			retValue = (String) query.uniqueResult();
		}
		
		LoggerFactory.getLogger("API_TRACE").error("getModelId TimeStamp : " + retValue );
		return retValue;
	}

	/**
	 * 
	 * 현재 timestamp 20자리를 리턴한다.<br>
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getCurrentTimeStampString() throws Exception {
		
		String retValue = "";
		
		if (jdbcType == "MySQL"){			
			retValue =  CommonUtil.timestamp2String();	
		}		
		else if (jdbcType == "ORACLE") {
			SQLQuery query = this.getSession()
					.createSQLQuery("SELECT TO_CHAR(SYSTIMESTAMP,'YYYYMMDDHH24MISSff') FROM DUAL");
			retValue = (String) query.uniqueResult();
		}
		
		LoggerFactory.getLogger("API_TRACE").error("getModelId TimeStamp : " + retValue );
		return retValue;
	}

	/**
	 * 
	 * insert (history 포함)<br>
	 * 
	 * @param obj
	 * @throws Exception
	 */
	public void insert(Object obj) throws Exception {
		getHibernateTemplate().save(obj);
		getSessionFactory().getCurrentSession().flush();
		getSessionFactory().getCurrentSession().clear();

		insertHistory(obj);
	}

	public void insert(Object obj, List<Column> ColumnList) throws Exception {
		getHibernateTemplate().save(obj);
		getSessionFactory().getCurrentSession().flush();
		getSessionFactory().getCurrentSession().clear();

		insertHistory(obj, ColumnList);
	}

	/**
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public int executeNativeSql(String sql, List<Object[]> params) throws Exception {
		Query query = this.getSession().createSQLQuery(sql);

		int i = 0;

		if (params != null) {
			for (Object[] obj : params) {
				if (obj[0].equals(java.lang.String.class)) {
					query.setString(i, (String) obj[1]);
				} else if (obj[0].equals(java.lang.Float.class)) {
					query.setString(i, CommonUtil.float2String((Float) obj[1]));
				} else if (obj[0].equals(java.lang.Integer.class)) {
					query.setString(i, CommonUtil.integer2String((Integer) obj[1]));
				} else if (obj[0].equals(java.lang.Long.class)) {
					query.setString(i, CommonUtil.long2String((Long) obj[1]));
				} else if (obj[0].equals(java.util.Date.class)) {
					query.setTime(i, (Date) obj[1]);
				} else if (obj[0].equals(java.lang.Boolean.class)) {
					query.setString(i, CommonUtil.boolean2String((Boolean) obj[1]));
				}
				i++;
			}
		}

		int cnt = query.executeUpdate();

		this.getSessionFactory().getCurrentSession().clear();

		return cnt;
	}

	/**
	 * @param obj
	 * @throws Exception
	 */
	protected void insertHistory(Object obj) throws Exception {
		List<Column> columns = null;
		String tableName = null;
		List<Object[]> params = null;

		if (obj instanceof Message) {
			if (!((Message) obj).getHistFlag())
				return;
			columns = getColumn(obj.getClass().getName(), "Main");
			params = getParameters(columns, (Message) obj);
		} else if (obj instanceof MessageAttributes) {
			if (!((MessageAttributes) obj).getHistFlag())
				return;
			columns = getColumn(obj.getClass().getName(), "Attribute");
			params = getParameters(columns, (MessageAttributes) obj);
		}

		if (CommonUtil.isCollectionEmpty(columns)) {
			return;
		} else {
			tableName = columns.get(0).getTableName() + "HIST";
		}

		String sql = makeInsertSql(tableName, columns);

		executeNativeSql(sql, params);
	}

	private void insertHistory(Object obj, List<Column> ColumnList) throws Exception {
		String tableName = ((Column) ColumnList.get(0)).getTableName() + "HIST";

		List<Object[]> params = null;

		if ((obj instanceof Message)) {
			if (!CommonUtil.null2Boolean(((Message) obj).getHistFlag(), Boolean.valueOf(false)).booleanValue())
				return;

			params = getParameters(ColumnList, (Message) obj);
		} else if ((obj instanceof MessageAttributes)) {
			if (!CommonUtil.null2Boolean(((MessageAttributes) obj).getHistFlag(), Boolean.valueOf(false))
					.booleanValue())
				return;

			params = getParameters(ColumnList, (MessageAttributes) obj);
		}

		String sql = makeInsertSql(tableName, ColumnList);

		executeNativeSql(sql, params);
	}

	public List<Object[]> getParameters(List<Column> columns, Object message) throws Exception {
		List<Object[]> params = new ArrayList<Object[]>();

		for (Column column : columns) {
			Object[] param = new Object[2];

			Object obj = null;

			param[0] = Class.forName(column.getClassName());

			if (CommonUtil.fieldExist(message, column.getPropertyName())) {
				obj = CommonUtil.getValue(message, column.getPropertyName());
				param[1] = obj;
			} else {
				param[1] = null;
			}

			params.add(param);
		}

		return params;
	}

	/**
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public boolean update(Object obj) throws Exception {
		List<Column> cols = getColumn(obj.getClass().getName(), "Main");

		if (CommonUtil.isCollectionEmpty(cols)) {
			throw new MesException("NotFound", "Main tableType of " + obj.getClass().getName());
		}

		List<Object[]> params = new ArrayList<Object[]>();

		String sql = getUpdateSql(cols, params, obj);
		int count = executeNativeSql(sql, params);
		if (count == 0)
			throw new MesException("UpdateFailByDiffTimeStamp");

		insertHistory(obj);

		return true;
	}

	public boolean update(Object obj, List<Column> ColumnList) throws Exception {
		List<Object[]> params = new ArrayList<Object[]>();

		String sql = getUpdateSql(ColumnList, params, obj);

		int count = executeNativeSql(sql, params);

		if (count == 0) {
			throw new MesException("UpdateFailByDiffTimeStamp");
		}

		insertHistory(obj, ColumnList);

		return true;
	}

	/**
	 * @param column
	 * @param params
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	private String getUpdateSql(List<Column> column, List<Object[]> params, Object obj) throws Exception {
		String setStr = "Update " + column.get(0).getTableName() + " SET ";

		int i = 0;
		String primaryKey = "";

		for (Column col : column) {
			if ("id".equalsIgnoreCase(col.getPropertyName())) {
				primaryKey = col.getName();
				continue;
			}

			if (i > 0)
				setStr += ", ";

			setStr += " " + col.getName() + "= ? ";

			params.add(new Object[] { Class.forName(col.getClassName()),
					CommonUtil.getValue(obj, col.getPropertyName()) });

			i++;
		}

		setStr += " Where " + primaryKey + " = ? AND lastEventTime = ? ";
		params.add(new Object[] { java.lang.String.class, CommonUtil.getValue(obj, "id") });
		params.add(new Object[] { java.lang.String.class, CommonUtil.getValue(obj, "prevLastEventTime") });
		return setStr;
	}

	/**
	 * @param obj
	 * @throws Exception
	 */
	public void delete(Object obj) throws Exception {
		List<Column> cols = getColumn(obj.getClass().getName(), "Main");

		if (CommonUtil.isCollectionEmpty(cols)) {
			throw new MesException("NotFound", "Main tableType of " + obj.getClass().getSimpleName());
		}

		List<Object[]> parameters = new ArrayList<Object[]>();

		String sql = getDeleteSql(cols, parameters, obj);
		int count = executeNativeSql(sql, parameters);
		if (count == 0)
			throw new MesException("DeleteFailByDiffTimeStamp");

		insertHistory(obj);
	}

	@SuppressWarnings("unchecked")
	public void delete(Message message, List<Column> mainColumnList, boolean existAttrColumn,
			boolean existPropColumn, String propTableName, String mainIdColumnName) throws Exception {
		List<Object[]> parameters = new ArrayList<Object[]>();

		String sql = getDeleteSql(message, mainColumnList, parameters);

		int count = executeNativeSql(sql, parameters);

		if (count == 0) {
			throw new MesException("DeleteFailByDiffTimeStamp");
		}

		insertHistory(message, mainColumnList);

		if (existAttrColumn) {
			String attrSql = " From " + message.getClass().getName() + "Attributes Where "
					+ message.getClass().getSimpleName().toUpperCase() + "ID = ? ";

			List<MessageAttributes> list = (List<MessageAttributes>) getList(attrSql, new Object[] { CommonUtil.getValue(message, "id") });
			List<MessageAttributes> rows = list;

			for (MessageAttributes deleteAttr : rows) {
				getHibernateTemplate().delete(deleteAttr);
				getSessionFactory().getCurrentSession().flush();
				getSessionFactory().getCurrentSession().clear();
			}

		}

		if (existPropColumn) {
			String deleteStmt = "DELETE " + propTableName + " WHERE " + mainIdColumnName + " = ? ";
			@SuppressWarnings("rawtypes")
			List extDeleteParameters = new ArrayList();
			extDeleteParameters.add(new Object[] { String.class, CommonUtil.getValue(message, "id") });

			executeNativeSql(deleteStmt, extDeleteParameters);
		}
	}

	/**
	 * @param cols
	 * @param parameters
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	private String getDeleteSql(List<Column> cols, List<Object[]> parameters, Object obj) throws Exception {
		String setStr = "Delete From " + cols.get(0).getTableName() + " Where ";

		for (Column col : cols) {
			if ("id".equalsIgnoreCase(col.getPropertyName())) {
				setStr += " " + col.getName() + "= ? ";
				break;
			}
		}

		setStr += " AND lastEventTime = ? ";
		parameters.add(new Object[] { java.lang.String.class, CommonUtil.getValue(obj, "id") });
		parameters.add(new Object[] { java.lang.String.class, CommonUtil.getValue(obj, "prevLastEventTime") });
		return setStr;
	}

	private String getDeleteSql(Message message, List<Column> cols, List<Object[]> parameters) throws Exception {
		String deleteSql = "DELETE " + ((Column) cols.get(0)).getTableName() + " WHERE ";

		for (Column col : cols) {
			if ("id".equalsIgnoreCase(col.getPropertyName())) {
				deleteSql = deleteSql + " " + col.getName() + "= ? ";
				break;
			}
		}

		deleteSql = deleteSql + " AND lastEventTime = ? ";

		parameters.add(new Object[] { String.class, CommonUtil.getValue(message, "id") });
		parameters.add(new Object[] { String.class, CommonUtil.getValue(message, "prevLastEventTime") });

		return deleteSql;
	}

	/**
	 * @param className
	 * @param tableType
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Column> getColumn(String className, String tableType) throws Exception {
		Table table = getTable(className, tableType);

		if (table == null)
			return null;

		String sql = "From " + Column.class.getName() + " Where tableName = ? order By seq";

		CommonUtil.checkMandatory(table.getName(), "tableName");

		List<Column> columnList = (List<Column>) getList(sql, table.getName());

		// if (CommonUtil.isCollectionEmpty(columnList))
		// {
		// throw new MesException("NotFound", tableType + " tableType of " +
		// className);
		// }

		return columnList;
	}

	@SuppressWarnings("unchecked")
	public List<Column> getColumnList(String tableName) throws Exception {
		String sql = "FROM Column WHERE tableName = ? ORDER BY SEQ";

		List<?> ColumnList = getList(sql, new Object[] { tableName });

		if (CommonUtil.isCollectionEmpty(ColumnList)) {
			throw new MesException("NotFoundData", new String[] { "SMColumn", "tableName : " + tableName });
		}

		return (List<Column>) ColumnList;
	}

	/**
	 * @param list
	 * @throws Exception
	 */
	public void getProperties(Message message) throws Exception {
		String sql = null;

		if (message == null) {
			return;
		}

		List<Column> columnList = getColumn(message.getClass().getName(), "Property");

		if (CommonUtil.isCollectionEmpty(columnList)) {
			return;
		}

		sql = getSelectSql(columnList) + " From " + columnList.get(0).getTableName() + " Where "
				+ columnList.get(0).getName() + " = ? ";

		@SuppressWarnings("rawtypes")
		List row = getSQLList(sql, message.getId());

		Object[] cols = (Object[]) row.get(0);

		for (int i = 0; i < cols.length; i++) {
			if (i == 0) {
				message.setId((String) cols[i]);
			} else if ("CreateUser".equalsIgnoreCase(columnList.get(i).getName())) {
				message.setCreateUser((String) cols[i]);
			} else if ("CreationTime".equalsIgnoreCase(columnList.get(i).getName())) {
				message.setCreateTime((Date) cols[i]);
			} else if ("UpdateUser".equalsIgnoreCase(columnList.get(i).getName())) {
				message.setUpdateUser((String) cols[i]);
			} else if ("UpdateTime".equalsIgnoreCase(columnList.get(i).getName())) {
				message.setUpdateTime((Date) cols[i]);
			} else if ("LastEventTime".equalsIgnoreCase(columnList.get(i).getName())) {
				message.setLastEventTime((String) cols[i]);
			} else {
				if (cols[i] != null)
					message.setProperty(columnList.get(i).getName(),
							columnList.get(i).getClassName().indexOf("Date") != -1
									? CommonUtil.date2String((Date) cols[i], "yyyyMMddHHmmss")
									: "" + cols[i]);
			}
		}
	}

	/**
	 * @param list
	 * @throws Exception
	 */
	public void getProperties(List<Message> list) throws Exception {
		String sql = null;

		if (CommonUtil.isCollectionEmpty(list)) {
			return;
		}

		List<Column> columnList = getColumn(list.get(0).getClass().getName(), "Property");

		if (CommonUtil.isCollectionEmpty(columnList)) {
			return;
		}

		sql = getSelectSql(columnList) + " From " + columnList.get(0).getTableName() + " Where "
				+ columnList.get(0).getName() + " = ? ";

		for (Message msg : list) {
			if (msg == null)
				continue;

			@SuppressWarnings("rawtypes")
			List row = getSQLList(sql, msg.getId());

			if (row.size() != 1)
				continue;

			Object[] cols = (Object[]) row.get(0);

			for (int i = 0; i < cols.length; i++) {
				if (i == 0) {
					msg.setId((String) cols[i]);
				} else if ("CreateUser".equalsIgnoreCase(columnList.get(i).getName())) {
					msg.setCreateUser((String) cols[i]);
				} else if ("CreationTime".equalsIgnoreCase(columnList.get(i).getName())) {
					msg.setCreateTime((Date) cols[i]);
				} else if ("UpdateUser".equalsIgnoreCase(columnList.get(i).getName())) {
					msg.setUpdateUser((String) cols[i]);
				} else if ("UpdateTime".equalsIgnoreCase(columnList.get(i).getName())) {
					msg.setUpdateTime((Date) cols[i]);
				} else if ("LastEventTime".equalsIgnoreCase(columnList.get(i).getName())) {
					msg.setLastEventTime((String) cols[i]);
				} else {
					if (cols[i] != null)
						msg.setProperty(columnList.get(i).getName(),
								columnList.get(i).getClassName().indexOf("Date") != -1
										? CommonUtil.date2String((Date) cols[i], "yyyyMMddHHmmss")
										: "" + cols[i]);
				}
			}
		}
	}

	/**
	 * @param message
	 * @throws Exception
	 */
	public void setProperties(Message message) throws Exception {
		if (message == null)
			return;
		else if (message.getPropertiesCount() < 1)
			return;

		// EXT 테이블 컬럼정보 가져오기
		List<Column> columnList = getColumn(message.getClass().getName(), "Property");

		if (CommonUtil.isCollectionEmpty(columnList)) {
			return;
		}

		String tableName = columnList.get(0).getTableName();
		String idName = columnList.get(0).getName();

		CustomedFields prop = message.getProperties();
		Iterator<String> list = prop.keySet().iterator();

		// EXT 테이블 정보 조회
		String sql = getSelectSql(columnList) + " From " + tableName + " Where " + idName + " = ? ";

		@SuppressWarnings("rawtypes")
		List prpList = getSQLList(sql, message.getId());

		Object[] prpValueList = null;

		String updateStmt = "";
		String insertStmt = "";
		String updateSubStmt = "";
		String inserSubStmt = "";
		boolean updateFlag = false;

		// SQL Parameter list 생성
		List<Object[]> updateSet = new ArrayList<Object[]>();
		List<Object[]> insertSet = new ArrayList<Object[]>();

		// UserId, UpdateTime 셋팅.
		updateSet.add(new Object[] { java.lang.String.class,
				CommonUtil.isEmpty(prop.getUpdateUser()) ? message.getUpdateUser() : prop.getUpdateUser() });
		updateSet.add(new Object[] { java.util.Date.class,
				prop.getUpdateTime() == null ? message.getUpdateTime() : prop.getUpdateTime() });
		insertSet.add(new Object[] { java.lang.String.class,
				CommonUtil.isEmpty(prop.getUpdateUser()) ? message.getUpdateUser() : prop.getUpdateUser() });
		insertSet.add(new Object[] { java.util.Date.class,
				prop.getUpdateTime() == null ? message.getUpdateTime() : prop.getUpdateTime() });

		// Update/Insert 여부 확인.
		if (prpList.size() > 0) {
			updateFlag = true;
			prpValueList = (Object[]) prpList.get(0);
		}

		// Update Sql
		updateStmt = "Update " + tableName + " Set ";
		updateSubStmt = "modifier=?, modifyTime=?, lastEventTime=? ";
		// Insert Sql
		insertStmt = "Insert into " + tableName + "(modifier, modifyTime, creator, creationTime, " + idName
				+ ", lastEventTime ";
		inserSubStmt = "?,?,?,?,?,?";

		// Insert params
		insertSet.add(new Object[] { java.lang.String.class,
				CommonUtil.isEmpty(prop.getCreateUser()) ? message.getUpdateUser() : prop.getCreateUser() });
		insertSet.add(new Object[] { java.util.Date.class,
				prop.getCreateTime() == null ? message.getCreateTime() : prop.getCreateTime() });
		insertSet.add(new Object[] { java.lang.String.class, message.getId() });

		// LastEventTime 셋팅.
		updateSet.add(new Object[] { java.lang.String.class, message.getLastEventTime() });
		insertSet.add(new Object[] { java.lang.String.class, message.getLastEventTime() });

		// 1) 유효성 check
		// 2) SQL 생성
		int i = 0;
		boolean isUpdated = false;
		while (list.hasNext()) {
			String name = list.next();
			boolean currentFieldUpdateFlag = validateProperties(name, message.getProperty(name), columnList,
					prpValueList, updateSet, insertSet);

			if (currentFieldUpdateFlag) {
				// Update Sql
				updateSubStmt += "," + name + " =?";

				// Insert Sql
				insertStmt += ", " + name;
				inserSubStmt += ",?";
			}

			// Value 변경 여부 확인
			if (!isUpdated) {
				isUpdated = currentFieldUpdateFlag;
			}

//			i++;
			i=i+1;
		}

		// Update Sql
		updateStmt = updateStmt + updateSubStmt + " Where " + idName + " = ? ";
		updateSet.add(new Object[] { java.lang.String.class, message.getId() });

		// Insert Sql
		insertStmt = insertStmt + ") " + " Values (" + inserSubStmt + ") ";

		// Update case 인 경우에는 갱신된 value가 있는 경우에만 실행.
		// Insert case 인 경우에는 무조건 실행.
		if (isUpdated) {
			if (updateFlag) {
				executeNativeSql(updateStmt, updateSet);

			} else if (!updateFlag) {
				executeNativeSql(insertStmt, insertSet);
			}

			if (message.getHistFlag()) {
				// Table table = getTable(message.getClass().getName(), "PropertyHistory");

				int idx = insertStmt.indexOf(tableName);
				insertStmt = insertStmt.substring(0, idx) + tableName + "HIST"
						+ insertStmt.substring(idx + tableName.length());
				executeNativeSql(insertStmt, insertSet);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void setProperties(Message message, List<Column> ColumnList) throws Exception {
		String extTableName = ((Column) ColumnList.get(0)).getTableName();
		String extIdColumnName = ((Column) ColumnList.get(0)).getName();

		String sql = getSelectSql(ColumnList) + " From " + extTableName + " Where " + extIdColumnName
				+ " = ? ";

		@SuppressWarnings("rawtypes")
		List extList = getSQLList(sql, new Object[] { message.getId() });

		Object[] parameterList = null;

		String updateSql = "";
		String insertSql = "";
		String updateParameterSql = "";
		String insertParameterSql = "";

		@SuppressWarnings("rawtypes")
		List updateParameterValueList = new ArrayList();
		@SuppressWarnings("rawtypes")
		List insertParameterValueList = new ArrayList();

		CustomedFields properties = message.getProperties();

		updateParameterValueList.add(
				new Object[] { String.class, CommonUtil.isEmpty(properties.getUpdateUser()) ? message.getUpdateUser()
						: properties.getUpdateUser() });

		updateParameterValueList.add(new Object[] { Date.class,
				properties.getUpdateTime() == null ? message.getUpdateTime() : properties.getUpdateTime() });

		insertParameterValueList.add(
				new Object[] { String.class, CommonUtil.isEmpty(properties.getUpdateUser()) ? message.getUpdateUser()
						: properties.getUpdateUser() });

		insertParameterValueList.add(new Object[] { Date.class,
				properties.getUpdateTime() == null ? message.getUpdateTime() : properties.getUpdateTime() });

		boolean updateFlag = false;

		if (extList.size() > 0) {
			updateFlag = true;
			parameterList = (Object[]) extList.get(0);
		}

		updateSql = "Update " + extTableName + " Set ";
		updateParameterSql = "modifier=?, modifyTime=?, lastEventTime=? ";

		insertSql = "Insert into " + extTableName + "(modifier, modifyTime, creator, creationTime, "
				+ extIdColumnName + ", lastEventTime ";
		insertParameterSql = "?,?,?,?,?,?";

		insertParameterValueList.add(
				new Object[] { String.class, CommonUtil.isEmpty(properties.getCreateUser()) ? message.getUpdateUser()
						: properties.getCreateUser() });

		insertParameterValueList.add(new Object[] { Date.class,
				properties.getCreateTime() == null ? message.getUpdateTime() : properties.getCreateTime() });

		insertParameterValueList.add(new Object[] { String.class, message.getId() });

		updateParameterValueList.add(new Object[] { String.class, message.getLastEventTime() });
		insertParameterValueList.add(new Object[] { String.class, message.getLastEventTime() });

		int i = 0;
		boolean isUpdated = false;
		Iterator<String> list = properties.keySet().iterator();

		while (list.hasNext()) {
			String name = (String) list.next();
			boolean currentFieldUpdateFlag = validateProperties(name, message.getProperty(name), ColumnList,
					parameterList, updateParameterValueList, insertParameterValueList);

			if (currentFieldUpdateFlag) {
				updateParameterSql = updateParameterSql + "," + name + " =?";

				insertSql = insertSql + ", " + name;
				insertParameterSql = insertParameterSql + ",?";
			}

			if (!isUpdated) {
				isUpdated = currentFieldUpdateFlag;
			}

			//i++;
			i=i+1;
		}

		updateSql = updateSql + updateParameterSql + " Where " + extIdColumnName + " = ? ";
		updateParameterValueList.add(new Object[] { String.class, message.getId() });

		insertSql = insertSql + ") " + " Values (" + insertParameterSql + ") ";

		if (isUpdated) {
			if (updateFlag) {
				executeNativeSql(updateSql, updateParameterValueList);
			} else if (!updateFlag) {
				executeNativeSql(insertSql, insertParameterValueList);
			}

			boolean histFlag = CommonUtil.null2Boolean(message.getHistFlag(), Boolean.valueOf(false)).booleanValue();

			if (histFlag) {
				int idx = insertSql.indexOf(extTableName);
				insertSql = insertSql.substring(0, idx) + extTableName + "HIST"
						+ insertSql.substring(idx + extTableName.length());

				int j = 3;
				for (int k = updateParameterValueList.size() - 1; j < k; j++) {
					insertParameterValueList.add(updateParameterValueList.get(j));
				}

				executeNativeSql(insertSql, insertParameterValueList);
			}
		}
	}

	/**
	 * @param name
	 * @param value
	 * @param list
	 * @param prpValueList
	 * @param updateList
	 * @param insertList
	 * @return
	 * @throws Exception
	 */
	private boolean validateProperties(String name, String value, List<Column> list, Object[] prpValueList,
			List<Object[]> updateList, List<Object[]> insertList) throws Exception {
		int i = 0;
		for (Column column : list) {
			if (name.equalsIgnoreCase(column.getName())) {
				Class<?> clazz = Class.forName(column.getClassName());
				Object obj = null;

				if (clazz.equals(java.lang.String.class)) {
					obj = value;
				} else if (clazz.equals(java.lang.Float.class)) {
					if (CommonUtil.isNotEmpty(value))
						obj = new Float(value);
				} else if (clazz.equals(java.lang.Integer.class)) {
					if (CommonUtil.isNotEmpty(value))
						obj = new Integer(value);
				} else if (clazz.equals(java.lang.Long.class)) {
					if (CommonUtil.isNotEmpty(value))
						obj = new Long(value);
				} else if (clazz.equals(java.util.Date.class)) {
					if (CommonUtil.isNotEmpty(value))
						obj = CommonUtil.string2Date(value, "yyyyMMddHHmmss");
				} else if (clazz.equals(java.lang.Boolean.class)) {
					if (CommonUtil.isNotEmpty(value))
						obj = new Boolean(value);
				}

				// Update case 인 경우에는 값이 변경되 었을 경우에는 true 리턴
				if (prpValueList != null) {
					if ((obj != null && !obj.equals(prpValueList[i])) || (obj == null && prpValueList[i] != null)) {
						updateList.add(new Object[] { clazz, obj });
						return true;
					} else {
						return false;
					}
				} else {
					insertList.add(new Object[] { clazz, obj });
					return true;
				}
			}
			i++;
		}

		return false;
		// 유효하지 않는 columnName인 경우에는 raise Error.
		// throw new MesException("InvalidValue", new String[] { name,
		// list.get(0).getTableName() });
	}

	/**
	 * 
	 * @param list
	 * @return
	 */
	private String getSelectSql(List<Column> list) {
		String sqlSelect = "Select ";

		int i = 0;
		for (Column column : list) {
			if (i >= 1) {
				sqlSelect += ", ";
			}

			sqlSelect += " " + column.getName();
			i++;
		}
		sqlSelect += " ";

		return sqlSelect;
	}

	/**
	 * @param className
	 * @param tableType
	 * @return
	 * @throws Exception
	 */
	public Table getTable(String className, String tableType) throws Exception {
		CommonUtil.checkMandatory(tableType, "tableType");

		String sql = " From " + Table.class.getName() + " Where className = ? And tableType= ?";

		return (Table) get(sql, className, tableType);
	}

	public Table getTable(String className, String tableType, boolean excetionFlag) throws Exception {
		String sql = " FROM Table WHERE className = ? AND tableType= ? ";

		Table table = (Table) get(sql, new Object[] { className, tableType });

		if ((excetionFlag) && (table == null)) {
			throw new MesException("NotFoundData", new String[] { "UMTABLE", "className : " + className });
		}

		return table;
	}

	/**
	 * @param list
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void getAttributes(List<Message> list) throws Exception {
		String sql = null;

		if (CommonUtil.isCollectionEmpty(list)) {
			return;
		}

		List<Column> columnList = getColumn(list.get(0).getClass().getName() + "Attributes", "Attribute");

		if (CommonUtil.isCollectionEmpty(columnList)) {
			throw new MesException("NotFound", "Attribute tableType of " + list.get(0).getClass().getSimpleName());
		}

		sql = " From " + list.get(0).getClass().getName() + "Attributes " + " Where " + columnList.get(0).getName()
				+ " = ? ";

		for (Message msg : list) {
			if (msg == null)
				continue;

			List<MessageAttributes> row = (List<MessageAttributes>) getList(sql, msg.getId());

			int i = 0;
			for (i = 0; i < row.size(); i++) {
				msg.setAttribute(row.get(i).getName(), row.get(i).getValue());
				msg.setUpdateUser(row.get(i).getUpdateUser());
				msg.setUpdateTime(row.get(i).getUpdateTime());
				msg.setCreateUser(row.get(i).getCreateUser());
				msg.setCreateTime(row.get(i).getCreateTime());
				msg.setLastEventTime(row.get(i).getLastEventTime());
			}
		}
	}

	/**
	 * @param vo
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void setAttributes(Message vo) throws Exception {
		if (vo == null)
			return;

		if (vo.getAttributesCount() < 1)
			return;

		CustomedFields attr = vo.getAttributes();

		String sql = null;

		List<Column> columnList = getColumn(vo.getClass().getName(), "Main");

		if (CommonUtil.isCollectionEmpty(columnList)) {
			throw new MesException("NotFound", "Main tableType of " + vo.getClass().getSimpleName());
		}

		sql = " From " + vo.getClass().getName() + "Attributes Where " + columnList.get(0).getName() + " = ? ";

		List<MessageAttributes> rows = (List<MessageAttributes>) this.getList(sql, vo.getId());

		Iterator<String> list = attr.keySet().iterator();

		while (list.hasNext()) {

			String name = list.next();
			MessageAttributes row = nameExist(name, rows);

			// Update case
			if (row != null) {
				// 갱신된 value가 있는 경우에만 update 실행.
				if (!CommonUtil.null2Empty(row.getValue()).equals(vo.getAttribute(name))) {
					row.setCreateTime(row.getCreateTime());
					row.setCreateUser(row.getCreateUser());
					row.setUpdateTime(attr.getUpdateTime() == null ? vo.getUpdateTime() : attr.getUpdateTime());
					row.setUpdateUser(
							CommonUtil.isEmpty(attr.getUpdateUser()) ? vo.getUpdateUser() : attr.getUpdateUser());
					row.setValue(vo.getAttribute(row.getName()));
					row.setLastEventTime(vo.getLastEventTime());
					row.setHistFlag(vo.getHistFlag());

					update(row);
				}
			}
			// Insert Case
			else {
				// Create new Attribute VO
				row = (MessageAttributes) Class.forName(vo.getClass().getName() + "Attributes").newInstance();
				// Setting Id
				row.setId(getModelId(vo.getClass().getName() + "Attributes"));
				// Setting referenced field(Base field)
//				MessageUtil.update(row, columnList.get(0).getName(), vo.getId());
				// Setting name
				row.setName(name);
				// Setting value
				row.setValue(vo.getAttribute(row.getName()));
				row.setCreateTime(attr.getCreateTime() == null ? vo.getCreateTime() : attr.getCreateTime());
				row.setCreateUser(CommonUtil.isEmpty(attr.getCreateUser()) ? vo.getCreateUser() : attr.getCreateUser());
				row.setUpdateTime(attr.getUpdateTime() == null ? vo.getUpdateTime() : attr.getUpdateTime());
				row.setUpdateUser(CommonUtil.isEmpty(attr.getUpdateUser()) ? vo.getUpdateUser() : attr.getUpdateUser());
				// Base 테이블의 이력시간과 동일하게 한다.
				row.setLastEventTime(vo.getLastEventTime());
				row.setHistFlag(vo.getHistFlag());

				this.insert(row);
			}
		}
	}

	/**
	 * @param name
	 * @param rows
	 * @return
	 */
	private MessageAttributes nameExist(String name, List<?> rows) {

		for (Object vo : rows) {
			if (name.equalsIgnoreCase(((Message) vo).getName())) {
				return (MessageAttributes) vo;
			}
		}

		return null;
	}

	public void setAttributes(Message message, String mainIdColumnName, List<Column> attrColumnList)
			throws Exception {
		String sql = "FROM " + message.getClass().getName() + "Attributes WHERE " + mainIdColumnName + " = ? ";

		List<?> rows = getList(sql, new Object[] { message.getId() });

		CustomedFields attr = message.getAttributes();

		Iterator<String> list = attr.keySet().iterator();

		while (list.hasNext()) {
			String name = (String) list.next();
			MessageAttributes row = nameExist(name, rows);

			if (row != null) {
				if (!CommonUtil.null2Empty(row.getValue()).equals(message.getAttribute(name))) {
					row.setCreateTime(row.getCreateTime());
					row.setCreateUser(row.getCreateUser());
					row.setUpdateTime(attr.getUpdateTime() == null ? message.getUpdateTime() : attr.getUpdateTime());
					row.setUpdateUser(
							CommonUtil.isEmpty(attr.getUpdateUser()) ? message.getUpdateUser() : attr.getUpdateUser());
					row.setValue(message.getAttribute(row.getName()));
					row.setLastEventTime(message.getLastEventTime());
					row.setHistFlag(message.getHistFlag());

					getHibernateTemplate().update(row);
					getSessionFactory().getCurrentSession().flush();
					getSessionFactory().getCurrentSession().clear();
					if (message.getHistFlag().booleanValue())
						insertHistory(row, attrColumnList);

				}

			} else {
				row = (MessageAttributes) Class.forName(message.getClass().getName() + "Attributes").newInstance();

				row.setId(getModelId());

				CommonUtil.update(row, mainIdColumnName, message.getId());

				row.setName(name);

				row.setValue(message.getAttribute(row.getName()));
				row.setCreateTime(attr.getCreateTime() == null ? message.getCreateTime() : attr.getCreateTime());
				row.setCreateUser(
						CommonUtil.isEmpty(attr.getCreateUser()) ? message.getCreateUser() : attr.getCreateUser());
				row.setUpdateTime(attr.getUpdateTime() == null ? message.getUpdateTime() : attr.getUpdateTime());
				row.setUpdateUser(
						CommonUtil.isEmpty(attr.getUpdateUser()) ? message.getUpdateUser() : attr.getUpdateUser());

				row.setLastEventTime(message.getLastEventTime());
				row.setHistFlag(message.getHistFlag());

				insert(row, attrColumnList);
			}
		}
	}

	public String getIdColumnName(Message message) throws Exception {
		Table table = getTable(message.getClass().getName(), "Main");

		String sql = " From Column Where tableName = ? and propertyName = 'id' ";

		String keyName = ((Column) this.get(sql, table.getName())).getName();

		if (keyName == null)
			throw new MesException("NotFound", "id");
		else
			return keyName;
	}

	public String makeSelectSql(String tableName, String keyName) throws Exception {
		String sql = "Select * From  " + tableName + " Where " + keyName + " = ? ";
		return sql;
	}

	public String makeUpdateSql(String tableName, List<Column> params, String keyName) throws Exception {
		String sql = "Update " + tableName + " Set ";

		int i = 0;
		for (Column column : params) {
			if (i > 0) {
				sql += ", " + column.getName() + " = ? ";
			} else {
				sql += column.getName() + " = ? ";
			}
			i++;
		}

		sql += " Where " + keyName + " = ? ";

		return sql;
	}

	public String makeDeleteSql(String tableName, String keyName) throws Exception {
		String sql = "Delete From  " + tableName + " Where " + keyName + " = ? ";

		return sql;
	}

	public String makeInsertSql(String tableName, List<Column> params) {
		String sql = "Insert into " + tableName + " ( ";
		String values = " Values (";

		int i = 0;
		for (Column column : params) {
			if (i > 0) {
				sql += ", " + column.getName();
				values += ", ? ";
			} else {
				sql += column.getName();
				values += " ? ";
			}
			i++;
		}

		sql += ")" + values + " )";

		return sql;
	}

	@SuppressWarnings("unchecked")
	public List<Column> getInfactoryColumnList(String maintableName, String propTableName)
			throws Exception {
		String sql = "FROM Column WHERE tableName IN (?, ?) AND UPPER(isInfactory) = 'T' ORDER BY tableName, seq";
		return (List<Column>) getList(sql, new Object[] { maintableName, propTableName });
	}

	@SuppressWarnings("unchecked")
	public List<State> getInfactoryDeleteStateList(String site, String stateModel) throws Exception {
		String sql = "FROM State WHERE Site = ? AND stateModelName = ? AND (UPPER(isDeleteInfactory) = 'T' OR  UPPER(isDeleteInfactory) = 'TRUE') ";
		return (List<State>) getList(sql, new Object[] { site, stateModel });
	}

}
