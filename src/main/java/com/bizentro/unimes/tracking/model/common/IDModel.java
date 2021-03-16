package com.bizentro.unimes.tracking.model.common;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bizentro.unimes.common.dictionary.Column;
import com.bizentro.unimes.common.dictionary.Table;
import com.bizentro.unimes.common.message.Message;
import com.bizentro.unimes.common.model.common.id.IDPattern;
import com.bizentro.unimes.common.model.common.id.IDSerial;
import com.bizentro.unimes.common.util.CommonUtil;
import com.bizentro.unimes.common.util.MesException;
import com.bizentro.unimes.tracking.common.tx.Transaction;
import com.bizentro.unimes.tracking.model.Model;

@Service("id.IdModel")
public class IDModel extends Model{

	@Resource(name = "Transaction")
	private Transaction transaction;

	/**
	 * 
	 * API 내부에서 Message(Producible, Consumable, Durable... 등)에 대한 기본 세팅 값.
	 * 
	 * @param message
	 * @param apiName
	 * @throws Exception
	 */
	public void initializeMessage(Message message, String apiName) throws Exception {
		// site 유효성 체크
		@SuppressWarnings("unused")
		String Site = message.getSite();
//		this.existFacility(null, site);

		// Mandatory Check
		CommonUtil.checkMandatory(message.getUpdateUser(), "modifier");

		// Modify Time Setting
		if (message.getUpdateTime() == null)
			message.setUpdateTime(new Date());

		// activity,originalActivity 설정
		message.setExecuteService(CommonUtil.isEmpty(message.getExecuteService()) ? apiName : message.getExecuteService());
		message.setOriginalExecuteService(apiName);
	}
	
	/**
	 * 
	 * @param checkObject
	 * @throws Exception
	 */
	public void validateExceptionChars(IDPattern idPattern) throws Exception
	{
		if (!CommonUtil.isEmpty(idPattern.getExeceptionChars())) 
		{
			if (idPattern.getExeceptionChars().indexOf("I") != -1)
			{
				throw new MesException("InvalidCharacter", new String[]{"ExceptionChracter", "I"});
			}
			else if (idPattern.getExeceptionChars().indexOf("O") != -1)
			{
				throw new MesException("InvalidCharacter", new String[]{"ExceptionChracter", "O"});
			}
		}
	}
	
	/**
	 * 
	 * @param idPattern
	 * @return
	 */
	public String getFormat(IDPattern idPattern) throws Exception
	{
		boolean yyyy = false;
		boolean mm = false;
		boolean dd = false;
		boolean week = false;
		boolean serial = false;
		int currentSystemVariable = 0;

		List<String> list = idPattern.getConstants();
		String constants = "";

		for (int i = 0; i < list.size(); i++)
		{
			String constant = list.get(i);

			currentSystemVariable = checkValidConstant(constant);

			if (currentSystemVariable == 0)
			{
				throw new MesException("InvalidDataFormat", new String[] { idPattern.getName(), constant });
			}
			else
			{
				if (currentSystemVariable == 1)
				{
					if (!yyyy)
					{
						yyyy = true;
					}
					else
					{
						throw new MesException("ExistData", new String[] { idPattern.getName(), constant });
					}
				}
				else if (currentSystemVariable == 2)
				{
					if (!mm)
					{
						mm = true;
					}
					else
					{
						throw new MesException("ExistData", new String[] { idPattern.getName(), constant });
					}
				}
				else if (currentSystemVariable == 3)
				{
					if (!dd)
					{
						dd = true;
					}
					else
					{
						throw new MesException("ExistData", new String[] { idPattern.getName(), constant });
					}
				}
				else if (currentSystemVariable == 4)
				{
					if (!week)
					{
						week = true;
					}
					else
					{
						throw new MesException("ExistData", new String[] { idPattern.getName(), constant });
					}
				}
				else if (currentSystemVariable == 5)
				{
					if (!serial)
					{
						serial = true;
					}
					else
					{
						throw new MesException("ExistData", new String[] { idPattern.getName(), constant });
					}
				}
			}

			constants += "[" + constant + "]";
		}
		
		if (!serial)
		{
			throw new MesException("NotFound", "Serial Pattern");
		}

		return constants;
	}
	
	/**
	 * 
	 * @param constant
	 * @return
	 * @throws Exception
	 */
	public int checkValidConstant(String constant) throws Exception
	{
		if (CommonUtil.isEmpty(constant))
			return 0;

		if (constant.indexOf("[") != -1)
			return 0;
		if (constant.indexOf("]") != -1)
			return 0;

		if ("%".equals(constant.substring(0, 1)))
		{
			if (constant.indexOf("%") != constant.lastIndexOf("%"))
			{
				return 0;
			}
			else
			{
				return 6;
			}
		}

		if ("YY".equals(constant) || "YYYY".equals(constant) || "Y".equals(constant))
		{
			return 1;
		}

		if ("M".equals(constant) || "MM".equals(constant) || "MA".equals(constant) || "M1".equals(constant))
		{
			return 2;
		}

		if ("DD".equals(constant) || "D".equals(constant) || "D1".equals(constant) || "DDD".equals(constant))
		{
			return 3;
		}

		if ("WEEK".equals(constant))
		{
			return 4;
		}

		if (constant.indexOf("SERNUMALPH") != -1 || constant.indexOf("SERMIXED") != -1 || constant.indexOf("SERNUM") != -1 || constant.indexOf("SERALPH") != -1 || constant.indexOf("SERHEX") != -1)
		{
			if (constant.indexOf("{") == -1 || constant.indexOf("}") == -1)
			{
				return 0;
			}
			else
			{
				if (constant.indexOf("{") != constant.lastIndexOf("{") || constant.indexOf("}") != constant.lastIndexOf("}") || constant.indexOf("{") > constant.lastIndexOf("}"))
				{
					return 0;
				}
			}

			if (!CommonUtil.isNumeric(constant.substring(constant.indexOf("{") + 1, constant.length() - 1)))
			{
				throw new MesException("NotFoundData", new String[] { "Serial", "Length" });
			}

			return 5;
		}

		return 6;
	}
	
	/**
	 * 
	 * @param idPattern
	 */
	public void validateStartEndSerial(IDPattern idPattern) throws Exception
	{
		if (!CommonUtil.isEmpty(idPattern.getStartSerial()))
		{
			throw new MesException("InvalidValue", new String[]{"StartSerial", idPattern.getStartSerial().toString()});

		}

		if (!CommonUtil.isEmpty(idPattern.getEndSerial()))
		{
				throw new MesException("InvalidValue", new String[]{"EndSerial", idPattern.getEndSerial().toString()});
		}

		if (!CommonUtil.isEmpty(idPattern.getStartSerial()) && !CommonUtil.isEmpty(idPattern.getEndSerial()))
			{
				if (idPattern.getStartSerial()>idPattern.getEndSerial())
				{
					throw new MesException("InvalidStartEnd", new String[]{"StartSerial,", "EndSerial"});
				}
				
			}
	}
	
	/**
	 * 
	 * @param checkObject
	 * @throws Exception
	 */
	public void validateUniqueObject(String checkObject) throws Exception
	{
		if (CommonUtil.isEmpty(checkObject)) return;

		if (checkObject.indexOf(".") == -1)
		{
			throw new MesException("InvalidValue", "CheckObject");
		}
		
		String primaryKey = checkObject.substring(checkObject.indexOf(".") + 1).toUpperCase();
		String tableName = checkObject.substring(0, checkObject.indexOf(".")).toUpperCase();

		Table table = new Table();
		table.setName(tableName);
		
		if (super.get(" From Table Where name = ? ", table.getName())==null)
		{
			throw new MesException("NotFound", tableName);
		}

		Column column = new Column();
		column.setTableName(tableName);
		column.setName(primaryKey);
		
		if (super.get(" From Column Where tableName = ? and name = ? ", column.getTableName(), column.getName())==null)
		{
			throw new MesException("NotFound", primaryKey);
		}
	}

	/**
	 * 
	 * @param idSerial
	 * @param pattern
	 * @throws Exception
	 */
	public void create(IDSerial idSerial, IDPattern pattern) throws Exception
	{
		
		super.create(idSerial);
		
//		idSerial.setCheckObject(pattern.getCheckObject());
//		idSerial.setStartSerial(pattern.getStartSerial());
//		idSerial.setEndSerial(pattern.getEndSerial());
//		idSerial.setExceptionChars(getExceptionChars(pattern.getExeceptionChars()).trim());
//		idSerial.setLasteventtime(super.getCurrentTimeStampString());
//		idSerial.setPrevLasteventtime(idSerial.getLasteventtime());

		/**
		 * 생성자 정보 셋팅
		 */
		idSerial.setCreateUser(idSerial.getUpdateUser());
		idSerial.setCreateTime(idSerial.getUpdateTime());
		
		this.transaction.save(idSerial);
	}

	/**
	 * 
	 * @param exceptionChars
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getExceptionChars(String exceptionChars)
	{

		char[] arrTemp = null;

		if (CommonUtil.isEmpty(exceptionChars))
		{
			exceptionChars = "IO";
		}
		else
		{
			exceptionChars = exceptionChars.replaceAll("[I|O]", "");
			exceptionChars = "IO" + exceptionChars;
		}

		arrTemp = new char[exceptionChars.length()];

		arrTemp[0] = 'I';
		arrTemp[1] = 'O';

		int j = 2;
		for (int i = 2; i < exceptionChars.length(); i++)
		{
			if (!isExceptionChars(exceptionChars.charAt(i), arrTemp))
			{
				arrTemp[j] = exceptionChars.charAt(i);
				j++;
			}
		}

		return new String(arrTemp);
	}

	/**
	 * 
	 * @param c
	 * @param arrTemp
	 * @return
	 */
	private boolean isExceptionChars(char c, char[] arrTemp)
	{
		for (int i = 0; i < arrTemp.length; i++)
		{
			if (c == arrTemp[i]) return true;
		}

		return false;
	}

	/**
	 * 
	 * @param idSerial
	 * @param primaryKeyValue
	 * @return
	 * @throws Exception
	 */
	private boolean existCheckObject(IDSerial idSerial, String primaryKeyValue) throws Exception
	{
//		if (CommonUtil.isEmpty(idSerial.getCheckObject())) return false;
//
//		String primaryKey = idSerial.getCheckObject().substring(idSerial.getCheckObject().indexOf(".") + 1);
//		String tableName = idSerial.getCheckObject().substring(0, idSerial.getCheckObject().indexOf("."));

//		if (CommonUtil.isCollectionEmpty(super.getNativeSql("SELECT * FROM "+tableName + " WHERE "+primaryKey+" = ?", primaryKeyValue)))
//		{
//			return false;
//		}
		return true;
	}

	private String toN(int N, int n)
	{
		StringBuffer sb = new StringBuffer();
		int mod = 0;

		if (n == 0) return "0";

		while (n != 0)
		{
			mod = n % N;
			n = n / N;

			if (mod > 9)
			{
				mod = 'A' - 10 + mod;
				sb.append((char) mod);
			}
			else
			{
				sb.append(mod);
			}
		}

		return sb.reverse().toString();
	}

	/**
	 * 
	 * @param endVal
	 * @param allowOverFlow
	 * @param curVal
	 * @return
	 * @throws Exception
	 */
	private int checkEndSerial(int endVal, boolean allowOverFlow, int curVal) throws Exception
	{

		if (curVal >= endVal)
		{
			if (allowOverFlow)
			{
				return 0;
			}

			throw new MesException("OverEndSerial");
		}

		return curVal;
	}

	/**
	 * 
	 * @param length
	 * @param curLength
	 * @throws Exception
	 */
//	private void checkLength(int length, int curLength) throws Exception
//	{
//		if (curLength > length)
//		{
//			throw new MesException("OverLengthSerial");
//		}
//	}

	/**
	 * 
	 * @param length
	 * @param value
	 * @return
	 */
	String paddingZero(int length, String value)
	{
		while (value.length() < length)
		{
			value = "0" + value;
		}

		return value;
	}

	/**
	 * 
	 * @param exceptionStr
	 * @param length
	 * @param value
	 * @return
	 * @throws Exception
	 */
	private String paddingA(String exceptionStr, int length, String value) throws Exception
	{
		while (value.length() < length)
		{
			value = "" + getAlpha(exceptionStr, 'A') + value;
		}

		return value;
	}

	/**
	 * 
	 * @param exceptionStr
	 * @param c
	 * @return
	 * @throws Exception
	 */
	private char getAlpha(String exceptionStr, char c) throws Exception
	{
		int k = (int) c;

		int j = (exceptionStr == null ? 0 : exceptionStr.length());

		for (int i = 0; i < j; i++)
		{
			int iC = (int) exceptionStr.charAt(i);

			if (iC <= k)
			{
				k++;
			}
		}

		if (k > 90)
		{
			throw new MesException("OverLengthSerial");
		}

		return (char) (k);
	}

	/**
	 * 
	 * @param curVal
	 * @param exceptionChar
	 * @param length
	 * @return
	 * @throws Exception
	 */
	private String getSERNUMALPH(int curVal, String exceptionChar, int length) throws Exception
	{
		String value = "" + curVal;

		if (value.length() > length)
		{
			int tempVal = curVal - (int) Math.pow(10, length);

			String temp = toN(26 - exceptionChar.length(), tempVal);

			char[] arrTemp = new char[temp.length()];

			int i = 0;
			for (i = 0; i < temp.length(); i++)
			{
				char cTemp = temp.charAt(i);
				char c = '0';

				if (CommonUtil.isNumeric(("" + cTemp)))
				{
					c = (char) (Integer.parseInt("" + cTemp) + 65);
				}
				else
				{
					c = (char) ((int) cTemp + 10);
				}

				c = getAlpha(exceptionChar, c);

				arrTemp[i] = c;

				value = new String(arrTemp);

			}

			if (i == 0) value = "A";
			value = paddingA(exceptionChar, length, value);
		}
		else
		{
			value = paddingZero(length, value);
		}
		return value;
	}

	/**
	 * 
	 * @param curVal
	 * @param exceptionChar
	 * @param length
	 * @return
	 * @throws Exception
	 */
	private String getSERMIXED(int curVal, String exceptionChar, int length) throws Exception
	{
		String value = null;

		String temp = toN(36 - exceptionChar.length(), curVal);

		char[] arrTemp = new char[temp.length()];

		for (int i = 0; i < temp.length(); i++)
		{
			char c = temp.charAt(i);

			if (!CommonUtil.isNumeric(("" + c)))
			{
				c = getAlpha(exceptionChar, c);
			}

			arrTemp[i] = c;

			value = new String(arrTemp);

		}

		value = paddingZero(length, value);

		return value;
	}

	/**
	 * 
	 * @param curVal
	 * @param length
	 * @return
	 * @throws Exception
	 */
	private String getSERNUM(int curVal, int length) throws Exception
	{
		String value = ""+curVal;

		value = paddingZero(length, value);

		return value;
	}

	/**
	 * 
	 * @param curVal
	 * @param exceptionChar
	 * @param length
	 * @return
	 * @throws Exception
	 */
	private String getSERALPH(int curVal, String exceptionChar, int length) throws Exception
	{
		String temp = toN(26 - exceptionChar.length(), curVal);

		char[] arrTemp = new char[temp.length()];

		String value = null;

		for (int i = 0; i < temp.length(); i++)
		{
			char cTemp = temp.charAt(i);
			char c = '0';

			if (CommonUtil.isNumeric(("" + cTemp)))
			{
				c = (char) (Integer.parseInt("" + cTemp) + 65);
			}
			else
			{
				c = (char) ((int) cTemp + 10);
			}

			c = getAlpha(exceptionChar, c);

			arrTemp[i] = c;

			value = new String(arrTemp);

		}

		value = paddingZero(length, value);

		return value;
	}

	/**
	 * 
	 * @param curVal
	 * @param exceptionChar
	 * @param length
	 * @return
	 * @throws Exception
	 */
	private String getSERHEX(int curVal, String exceptionChar, int length) throws Exception
	{
		String temp = toN(16, curVal);

		String value = null;

		char[] arrTemp = new char[temp.length()];

		for (int i = 0; i < temp.length(); i++)
		{
			char c = temp.charAt(i);

			if (!CommonUtil.isNumeric(("" + c)))
			{
				c = getAlpha(exceptionChar, c);
			}

			arrTemp[i] = c;

			value = new String(arrTemp);

		}

		value = paddingZero(length, value);

		return value;
	}

	/**
	 * 
	 * @param startSerial
	 * @param EndSerial
	 * @param serial
	 * @param exceptionChar
	 * @param allowOverFlow
	 * @param currentSerial
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private String[] getNextSerial(String startSerial, String EndSerial, String serial, String exceptionChar, boolean allowOverFlow, String currentSerial)
			throws Exception
	{
		String[] str = new String[2];
		String value = "";
		int fromIdx = serial.indexOf("{");
		int toIdx = serial.indexOf("}");
		int length = Integer.parseInt(serial.substring(fromIdx + 1, toIdx));
		int curVal = 0;
		int startVal = 0;
		int endVal = 0;

		if (!CommonUtil.isEmpty(startSerial))
		{
			startVal = Integer.parseInt(startSerial);
		}

		if (!CommonUtil.isEmpty(EndSerial))
		{
			endVal = Integer.parseInt(EndSerial);
		}

		if (!CommonUtil.isEmpty(currentSerial) && !"0".equals(currentSerial))
		{
			curVal = Integer.parseInt(currentSerial);
		}
		else
		{
			curVal = startVal;
		}

		if (endVal > 0)
		{
			curVal = checkEndSerial(endVal, allowOverFlow, curVal);
		}

		if (serial.indexOf("SERNUMALPH") != -1)
		{
			value = getSERNUMALPH(curVal, exceptionChar, length);

			if (value.length() > length)
			{
				if (allowOverFlow)
				{
					curVal = startVal;
					value = getSERNUMALPH(curVal, exceptionChar, length);
				}
				else
				{
					throw new MesException("OverEndSerial");
				}
			}

		}
		else if (serial.indexOf("SERMIXED") != -1)
		{

			value = getSERMIXED(curVal, exceptionChar, length);

			if (value.length() > length)
			{
				if (allowOverFlow)
				{
					curVal = startVal;
					value = getSERMIXED(curVal, exceptionChar, length);
				}
				else
				{
					throw new MesException("OverEndSerial");
				}
			}

		}
		else if (serial.indexOf("SERNUM") != -1)
		{

			value = getSERNUM(curVal, length);

			if (value.length() > length)
			{
				if (allowOverFlow)
				{
					curVal = startVal;
					value = getSERNUM(curVal, length);
				}
				else
				{
					throw new MesException("OverEndSerial");
				}
			}

		}
		else if (serial.indexOf("SERALPH") != -1)
		{
			value = getSERALPH(curVal, exceptionChar, length);

			if (value.length() > length)
			{
				if (allowOverFlow)
				{
					curVal = startVal;
					value = getSERALPH(curVal, exceptionChar, length);
				}
				else
				{
					throw new MesException("OverEndSerial");
				}
			}
		}
		else if (serial.indexOf("SERHEX") != -1)
		{
			value = getSERHEX(curVal, exceptionChar, length);

			if (value.length() > length)
			{
				if (allowOverFlow)
				{
					curVal = startVal;
					value = getSERHEX(curVal, exceptionChar, length);
				}
				else
				{
					throw new MesException("OverEndSerial");
				}
			}
		}

		curVal++;

		str[0] = ("" + curVal);
		str[1] = value;
		return str;
	}

	/**
	 * 
	 * @param format
	 * @return
	 * @throws Exception
	 */
	private int getSerialStartIndex(String format) throws Exception
	{
		if (format.indexOf("SERNUMALPH") != -1)
		{
			return format.indexOf("SERNUMALPH");
		}
		else if (format.indexOf("SERMIXED") != -1)
		{
			return format.indexOf("SERMIXED");
		}
		else if (format.indexOf("SERNUM") != -1)
		{
			return format.indexOf("SERNUM");
		}
		else if (format.indexOf("SERALPH") != -1)
		{
			return format.indexOf("SERALPH");
		}
		else if (format.indexOf("SERHEX") != -1)
		{
			return format.indexOf("SERHEX");
		}
		else
		{
			throw new MesException("NotFoundPattern", "Serial");
		}

	}

	/**
	 * 
	 * @param var
	 * @return
	 * @throws Exception
	 */
	private String getVarToReal(String var) throws Exception
	{
		String value = "";

		value = var;

		if ("YY".equals(var))
		{
			value = CommonUtil.date2String(new Date(), "yyyy").substring(2);
		}

		if ("YYYY".equals(var))
		{
			value = CommonUtil.date2String(new Date(), "yyyy");
		}

		if ("Y".equals(var))
		{
			value = CommonUtil.date2String(new Date(), "yyyy").substring(3);
		}

		if ("M".equals(var))
		{
			value = ""
					+ CommonUtil.string2Integer(CommonUtil.date2String(new Date(), "MM"));
		}

		if ("MM".equals(var))
		{
			value = CommonUtil.date2String(new Date(), "MM");

			if (value.length() < 2)
			{
				value = "0" + value;
			}
		}

		if ("MA".equals(var))
		{
			int i = CommonUtil.string2Integer(CommonUtil.date2String(new Date(), "MM"));
			char c = (char) (i + 64);
			value = new String("" + c);
		}

		if ("M1".equals(var))
		{
			int i = CommonUtil.string2Integer(CommonUtil.date2String(new Date(), "MM"));

			if (i == 10)
			{
				value = "A";
			}
			else if (i == 11)
			{
				value = "B";
			}
			else if (i == 12)
			{
				value = "C";
			}
			else
			{
				value = "" + i;
			}

		}

		if ("DD".equals(var))
		{
			value = CommonUtil.date2String(new Date(), "dd");

			if (value.length() < 2)
			{
				value = "0" + value;
			}
		}

		if ("D".equals(var))
		{
			value = ""
					+ CommonUtil
							.string2Integer((CommonUtil.date2String(new Date(), "dd")));
		}

		if ("D1".equals(var))
		{
			int i = CommonUtil.string2Integer(CommonUtil.date2String(new Date(), "dd"));

			if (i >= 10)
			{
				char c = (char) (i - 9 + 64);
				value = new String("" + c);
			}
			else
			{
				value = "" + i;
			}
		}

		if ("DDD".equals(var))
		{
			value = CommonUtil.date2String(new Date(), "DDD");

			while (value.length() < 3)
			{
				value = "0" + value;
			}
		}

		if ("WEEK".equals(var))
		{
			value = CommonUtil.date2String(new Date(), "w");

			while (value.length() < 2)
			{
				value = "0" + value;
			}
		}

		return value;
	}

	/**
	 * 
	 * @param pattern
	 * @param serial
	 * @return
	 * @throws Exception
	 */
	public String getSerialKey(IDPattern pattern, IDSerial serial) throws Exception
	{
		
		String value = "";

		int fromIdx = -1;
		int toIdx = 0;

		String source = pattern.getIdFormat();

		while (fromIdx < source.length())
		{
			fromIdx = source.indexOf("[", fromIdx + 1);
			toIdx = source.indexOf("]", fromIdx + 1);

			if (fromIdx == -1) break;

			String var = source.substring(fromIdx + 1, toIdx);

			if (var.indexOf("%") != -1)
			{
//				value += serial.getUserConstant(var.substring(var.lastIndexOf("%") + 1));
			}
			else if (var.indexOf("SER") == -1)
			{
				value += getVarToReal(var);
			}
			else
			{
				value += var;
			}
		}
		return value;
		
	}

	/**
	 * 
	 * @param idSerial
	 * @throws Exception
	 */
	public void settingIdCount(IDSerial idSerial) throws Exception
	{
		if (idSerial.getCurrentSerial() != null)
		{
			if (idSerial.getCurrentSerial()<0)
			{
				throw new MesException("InvalidValue", new String[]{"IdCount",CommonUtil.integer2String(idSerial.getCurrentSerial())});
			}
		}
		else
		{
//			idSerial.setIDCount(1);
			idSerial.setCurrentSerial(1);
		}
		
	}
	
	/**
	 * 
	 * @param idSerial
	 * @throws Exception
	 */
	public void generateIdList(IDSerial idSerial, IDPattern pattern) throws Exception
	{
		String[] str = new String[2];

		List<String> list = new ArrayList<String>();
		idSerial.setIdGen(list);

		@SuppressWarnings("unused")
		String curVal = "0";
		curVal = CommonUtil.integer2String(idSerial.getCurrentSerial());

		int fromIdx = getSerialStartIndex(idSerial.getName());
		int toIdx = idSerial.getName().indexOf("}");
		
		String prefix = idSerial.getName().substring(0, fromIdx);
		String postfix = idSerial.getName().substring(toIdx+1);
		
		for (int i = 0; i < idSerial.getCurrentSerial(); i++)
		{
			do
			{
//				str = getNextSerial(idSerial.getStartSerial(), idSerial.getEndSerial(), pattern.getFormat(), idSerial.getExeceptionChars(), idSerial.getAllowOverFlow(), curVal);
				curVal = str[0];
			}
			while (existCheckObject(idSerial, prefix + str[1]+postfix));

			list.add(prefix + str[1]+postfix);
		}
		
//		idSerial.setCurrentSerial(str[0]);
	}
	
	
}
