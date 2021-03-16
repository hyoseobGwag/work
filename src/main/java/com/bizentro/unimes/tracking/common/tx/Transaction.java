package com.bizentro.unimes.tracking.common.tx;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bizentro.unimes.common.dictionary.Column;
import com.bizentro.unimes.common.dictionary.Table;
import com.bizentro.unimes.common.message.Message;
import com.bizentro.unimes.common.model.common.state.State;
import com.bizentro.unimes.common.util.CommonUtil;
import com.bizentro.unimes.common.util.MesException;
import com.bizentro.unimes.tracking.common.dao.CommonDAO;
import com.bizentro.unimes.tracking.common.util.CONSTANT;

@Service("Transaction")
public class Transaction {
	@Resource(name = "CommonDAO")
	private CommonDAO commonDAO;
//	private static final String TABLETYPE_MAIN = "Main";
//	private static final String TABLETYPE_PROPERTY = "Property";
//	private static final String TABLETYPE_ATTRIBUTE = "Attribute";
//	private static final String CLASSTYPE_ATTRIBUTE = "Attributes";
//	private static final String TABLETYPE_INFACTORY = "Infactory";

	public void save(List<Message> list) throws Exception {
		if (list == null) {
			throw new MesException("NotFound", "Transaction List (txList)");
		}

		update(list);
	}

	public void save(Message message) throws Exception {
		if (message == null) {
			throw new MesException("NotFound", "Transaction Message");
		}

		update(message);
	}

	private void update(Message message) throws Exception {
		Integer txnCode = message.getTxnCode();

		String mainClassName = message.getClass().getName();

		//Table mainTable = this.commonDAO.getTable(mainClassName, "Main", true);
		Table mainTable = this.commonDAO.getTable(mainClassName, "Main");
		List<Column> mainColumnList = this.commonDAO.getColumnList(mainTable.getName());
		String mainIdColumnName = getIdColumnName(mainColumnList);

		Table attrTable = null;
		Table propTable = null;

		List<Column> attrColumnList = null;
		List<Column> propColumnList = null;

		if (CONSTANT.CREATE.equals(txnCode)) {
			if (message.getCreateTime() == null)
				message.setCreateTime(new Date());

			this.commonDAO.insert(message, mainColumnList);
		} else if (CONSTANT.MODIFY.equals(txnCode)) {
			this.commonDAO.update(message, mainColumnList);
		} else if (CONSTANT.REMOVE.equals(txnCode)) {
			boolean existAttrTable = false;
			boolean existPropTable = false;

			attrTable = this.commonDAO.getTable(mainClassName + "Attributes", "Attribute", false);

			if (attrTable != null) {
				attrColumnList = this.commonDAO.getColumnList(attrTable.getName());
				existAttrTable = true;
			}

			propTable = this.commonDAO.getTable(mainClassName, "Property", false);
			String propTableName = "";

			if (propTable != null) {
				propTableName = propTable.getName();
				propColumnList = this.commonDAO.getColumnList(propTableName);
				existPropTable = true;
			}

			this.commonDAO.delete(message, mainColumnList, existAttrTable, existPropTable, propTableName,
					mainIdColumnName);
		} else {
			throw new MesException("InvalidTxnCode");
		}

		if ((CONSTANT.CREATE.equals(txnCode)) || (CONSTANT.MODIFY.equals(txnCode))) {
			if (message.getAttributesCount() > 0) {
				attrTable = this.commonDAO.getTable(mainClassName + "Attributes", "Attribute", true);
				attrColumnList = this.commonDAO.getColumnList(attrTable.getName());

				this.commonDAO.setAttributes(message, mainIdColumnName, attrColumnList);
			}

			if (message.getPropertiesCount() > 0) {
				propTable = this.commonDAO.getTable(mainClassName, "Property", true);
				propColumnList = this.commonDAO.getColumnList(propTable.getName());

				this.commonDAO.setProperties(message, propColumnList);
			}

		}

		String infactoryTableName = mainTable.getCoreTableName();

		if (CommonUtil.isNotEmpty(infactoryTableName)) {
			List<Column> infactoryColumnList = this.commonDAO.getInfactoryColumnList(mainTable.getName(),
					propTable == null ? "" : propTable.getName());

			if (CommonUtil.isCollectionNotEmpty(infactoryColumnList)) {
				manageInfactory(message, infactoryTableName, infactoryColumnList, mainIdColumnName,
						this.commonDAO.getInfactoryDeleteStateList(message.getSite(),
								message.getClass().getSimpleName().toUpperCase()));
			}
		}
	}

	private void update(List<Message> list) throws Exception {
		Map<String, Table> distinctTableMap;
		Map<String, List<Column>> distinctTableColumnListMap;
		Map<String, List<State>> distinctInfactoryDeleteStateMap;
		if (list.size() == 1) {
			update((Message) list.get(0));
		} else {
			CommonUtil.sort(list, "getId");

			distinctTableMap = new HashMap<String, Table>();
			distinctTableColumnListMap = new HashMap<String, List<Column>>();

			distinctInfactoryDeleteStateMap = null;

			for (Message message : list) {
				Integer txnCode = message.getTxnCode();

				String mainClassName = message.getClass().getName();

				Table mainTable = getDistinctTable(mainClassName, "Main", distinctTableMap, true);

				List<Column> mainColumnList = getDistinctTableColumnList(mainTable.getName(), distinctTableColumnListMap);

				String mainIdColumnName = getIdColumnName(mainColumnList);

				Table attrTable = null;
				Table propTable = null;

				List<Column> attrColumnList = null;
				List<Column> propColumnList = null;

				if (CONSTANT.CREATE.equals(txnCode)) {
					if (message.getCreateTime() == null)
						message.setCreateTime(new Date());

					this.commonDAO.insert(message, mainColumnList);
				} else if (CONSTANT.MODIFY.equals(txnCode)) {
					this.commonDAO.update(message, mainColumnList);
				} else if (CONSTANT.REMOVE.equals(txnCode)) {
					boolean existAttrTable = false;
					boolean existPropTable = false;

					attrTable = getDistinctTable(mainClassName + "Attributes", "Attribute", distinctTableMap, false);

					if (attrTable != null) {
						attrColumnList = getDistinctTableColumnList(attrTable.getName(), distinctTableColumnListMap);
						existAttrTable = true;
					}

					propTable = getDistinctTable(mainClassName, "Property", distinctTableMap, false);
					String propTableName = "";

					if (propTable != null) {
						propTableName = propTable.getName();
						propColumnList = getDistinctTableColumnList(propTableName, distinctTableColumnListMap);
						existPropTable = true;
					}

					this.commonDAO.delete(message, mainColumnList, existAttrTable, existPropTable, propTableName,
							mainIdColumnName);
				} else {
					throw new MesException("InvalidTxnCode");
				}

				if ((CONSTANT.CREATE.equals(txnCode)) || (CONSTANT.MODIFY.equals(txnCode))) {
					if (message.getAttributesCount() > 0) {
						attrTable = getDistinctTable(mainClassName + "Attributes", "Attribute", distinctTableMap, true);
						attrColumnList = getDistinctTableColumnList(attrTable.getName(), distinctTableColumnListMap);

						this.commonDAO.setAttributes(message, mainIdColumnName, attrColumnList);
					}

					if (message.getPropertiesCount() > 0) {
						propTable = getDistinctTable(mainClassName, "Property", distinctTableMap, true);
						propColumnList = getDistinctTableColumnList(propTable.getName(), distinctTableColumnListMap);

						this.commonDAO.setProperties(message, propColumnList);
					}

				}

				String infactoryTableName = mainTable.getCoreTableName();

				if (CommonUtil.isNotEmpty(infactoryTableName)) {
					List<Column> infactoryColumnList = getDistinctTableInfactoryColumnList(mainTable.getName(),
							propTable == null ? "" : propTable.getName(), distinctTableColumnListMap);

					if (CommonUtil.isCollectionNotEmpty(infactoryColumnList)) {
						if (distinctInfactoryDeleteStateMap == null) {
							distinctInfactoryDeleteStateMap = new HashMap<String, List<State>>();
						}

						List<State> stateList = getDistinctInfactoryDeleteStateList(message.getSite(),
								message.getClass().getSimpleName().toUpperCase(), distinctInfactoryDeleteStateMap);

						manageInfactory(message, infactoryTableName, infactoryColumnList, mainIdColumnName, stateList);
					}
				}
			}
		}
	}

	private void manageInfactory(Message message, String infactoryTableName, List<Column> infactoryColumnList,
			String mainIdColumnName, List<State> deleteStateList) throws Exception {
		List<Object[]> params = getInfactoryMessage(infactoryColumnList, message);

		boolean isDelete = false;

		if (CONSTANT.REMOVE.equals(message.getTxnCode())) {
			isDelete = true;
		} else if (CommonUtil.isCollectionEmpty(deleteStateList)) {
			isDelete = false;
		} else {
			for (State state : deleteStateList) {
				if (state.getName().equals(message.getState())) {
					isDelete = true;
					break;
				}

			}

		}

		boolean isExistInfactory = true;

		if (this.commonDAO.getNativeSql(
				"SELECT " + mainIdColumnName + " FROM  " + infactoryTableName + " WHERE " + mainIdColumnName + " = ? ",
				new Object[] { message.getId() }) == null) {
			isExistInfactory = false;
		}

		if (isDelete) {
			if (isExistInfactory) {
				params.clear();
				params.add(new Object[] { String.class, message.getId() });
				this.commonDAO.executeNativeSql("DELETE " + infactoryTableName + " WHERE " + mainIdColumnName + " = ? ",
						params);
			}

		} else if (isExistInfactory) {
			params.add(new Object[] { String.class, message.getId() });
			this.commonDAO.executeNativeSql(
					this.commonDAO.makeUpdateSql(infactoryTableName, infactoryColumnList, mainIdColumnName), params);
		} else {
			this.commonDAO.executeNativeSql(this.commonDAO.makeInsertSql(infactoryTableName, infactoryColumnList),
					params);
		}
	}

	private List<Object[]> getInfactoryMessage(List<Column> columns, Message message) throws Exception {
		List<Object[]> params = new ArrayList<Object[]>();

		for (Column column : columns) {
			Object[] param = new Object[2];

			Object obj = null;

			if (CommonUtil.fieldExist(message, column.getPropertyName())) {
				obj = CommonUtil.getValue(message, column.getPropertyName());
				param[0] = Class.forName(column.getClassName());
				param[1] = obj;
			} else {
				obj = message.getProperty(column.getName());
				param[0] = String.class;
				param[1] = obj;
			}

			params.add(param);
		}

		return params;
	}

	private Table getDistinctTable(String className, String postFix, Map<String, Table> distinctTableMap,
			boolean exceptionFlag) throws Exception {
		String distinctTableKey = className + "\t" + postFix;

		Table table = (Table) distinctTableMap.get(distinctTableKey);

		if (table == null) {
			table = this.commonDAO.getTable(className, postFix, exceptionFlag);
			distinctTableMap.put(distinctTableKey, table);
		}

		return table;
	}

	private List<Column> getDistinctTableColumnList(String tableName,
			Map<String, List<Column>> distinctTableColumnListMap) throws Exception {
		List<Column> columnList = (List<Column>) distinctTableColumnListMap.get(tableName);

		if (columnList == null) {
			columnList = this.commonDAO.getColumnList(tableName);
			distinctTableColumnListMap.put(tableName, columnList);
		}

		return columnList;
	}

	private List<Column> getDistinctTableInfactoryColumnList(String mainTableName, String propTableName,
			Map<String, List<Column>> distinctTableColumnListMap) throws Exception {
		String distinctInfactoryColumnKey = mainTableName + "Infactory";

		List<Column> columnList = (List<Column>) distinctTableColumnListMap.get(distinctInfactoryColumnKey);

		if (CommonUtil.isCollectionEmpty(columnList)) {
			columnList = this.commonDAO.getInfactoryColumnList(mainTableName, propTableName);
			distinctTableColumnListMap.put(distinctInfactoryColumnKey, columnList);
		}

		return columnList;
	}

	private List<State> getDistinctInfactoryDeleteStateList(String site, String stateModelName,
			Map<String, List<State>> distinctInfactoryDeleteStateMap) throws Exception {
		List<State> infactoryDeleteStateList = null;

		String deleteInfactoryStateKey = site + "\t" + stateModelName;

		if (distinctInfactoryDeleteStateMap.containsKey(deleteInfactoryStateKey)) {
			infactoryDeleteStateList = (List<State>) distinctInfactoryDeleteStateMap.get(deleteInfactoryStateKey);
		} else {
			infactoryDeleteStateList = commonDAO.getInfactoryDeleteStateList(site, stateModelName);
			distinctInfactoryDeleteStateMap.put(site + stateModelName, infactoryDeleteStateList);
		}

		return infactoryDeleteStateList;
	}

	private String getIdColumnName(List<Column> columnList) throws Exception {
		String idColumnName = "";

		for (Column column : columnList) {
			if ("ID".equalsIgnoreCase(column.getPropertyName())) {
				idColumnName = column.getName();
				break;
			}
		}

		if (CommonUtil.isEmpty(idColumnName)) {
			throw new MesException("NotFoundData", new String[] { "property 'id' ",
					"tableName : " + ((Column) columnList.get(0)).getTableName() + " in SMColumn" });
		}

		return idColumnName;
	}
}