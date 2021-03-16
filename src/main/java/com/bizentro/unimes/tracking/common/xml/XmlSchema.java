package com.bizentro.unimes.tracking.common.xml;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.BeansDtdResolver;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.bizentro.unimes.common.message.CustomedFields;
import com.bizentro.unimes.common.message.Message;
import com.bizentro.unimes.common.message.MessageSet;
import com.bizentro.unimes.common.util.CommonUtil;
import com.bizentro.unimes.common.util.ExtensionFileFilter;
import com.bizentro.unimes.common.util.MesException;
import com.bizentro.unimes.common.xml.XmlDefine;
import com.bizentro.unimes.common.xml.XmlEntry;

@Service("xmlSchema")
public class XmlSchema{
	private List<String> xmlFiles;
	private Map<String, XmlDefine> configuration = new HashMap<String, XmlDefine>();

	public void setXmlFiles(List<String> xmlFiles) throws Exception {
		this.xmlFiles = xmlFiles;
		createConfiguration();
	}

	private DefaultConfigurationBuilder getBuilder() throws ParserConfigurationException, SAXException {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		saxParserFactory.setValidating(false);
		SAXParser saxParser = saxParserFactory.newSAXParser();
		XMLReader parser = saxParser.getXMLReader();
		parser.setEntityResolver(new BeansDtdResolver());
		return new DefaultConfigurationBuilder(parser);
	}

	private void parseChildren(XmlDefine definition, Configuration[] children) throws Exception {
		
		XmlEntry[] xmlChildren = new XmlEntry[children.length];

		for (int i = 0; i < children.length; i++) {
			xmlChildren[i] = new XmlEntry(children[i]);
			xmlChildren[i] = addConfiguration(children[i], false);
			xmlChildren[i].setId(xmlChildren[i].toString());
		}

		if (xmlChildren != null)
			definition.setChildren(xmlChildren);
	}

	private XmlDefine createXmlDefine(Configuration config) throws Exception {
		
		
		XmlDefine definition = new XmlDefine();

		XmlEntry parent = new XmlEntry(config);
		parent.setId(parent.toString());

		definition.setName(parent.getName());
		definition.setParent(parent);
		definition.setId(parent.getId());

		if (config.getChildren() != null) {
			parseChildren(definition, config.getChildren());
		}

		return definition;
	}

	@SuppressWarnings("unused")
	private void buildMap(DefaultConfigurationBuilder builder, String filename) throws Exception {

		
		Configuration config = builder.build(filename);

		Configuration[] list = config.getChildren();

		for (Configuration apiName : list) {
			addConfiguration(apiName, true);
		}
	}

	private XmlEntry addConfiguration(Configuration config, boolean apiName) throws Exception {
		
		
		XmlDefine definition = createXmlDefine(config);

		if (apiName) {
			this.configuration.put(definition.getParent().getName(), definition);
		} else {
			this.configuration.put(definition.getParent().getId(), definition);
		}

		return definition.getParent();
	}

	private void createConfiguration() throws Exception {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		
		@SuppressWarnings("unused")
		URL fileURL = null;

		@SuppressWarnings("unused")
		DefaultConfigurationBuilder builder = getBuilder();

		for (String path : this.xmlFiles) {
			while ("*.".equals(path.substring(path.length() - 1))) {
				path = path.substring(0, path.length() - 1);
			}

			try {
				
				fileURL = classloader.getResource(path.substring("classpath:".length()));

				
			} catch (Exception e) {
				LoggerFactory.getLogger("API_TRACE").error("XML File Loading Problem File Path : " + path);
				continue;
			}
			
			
			File newFile = new File(fileURL.getFile());

			List<String> files = new ArrayList<String>();
			

			if (newFile.isDirectory())
			{
				files = getFiles(fileURL.getFile(), "xml");
			}
			else
			{
				files.add(fileURL.getFile());
			}

			for (String file : files)
			{
				
				buildMap(builder, file);
			}
			
			
			continue;
		}
	}	


	@SuppressWarnings("unused")
	private List<String> getFiles(String dir, String extName) throws Exception {
		ArrayList<File> dirList = new ArrayList<File>();
		dirList = getDirs(dirList, dir);
		ExtensionFileFilter filter = new ExtensionFileFilter(extName);
		ArrayList<String> fileList = new ArrayList<String>();
		for (int i = 0; i < dirList.size(); i++) {
			File currentDir = (File) dirList.get(i);
			File[] files = currentDir.listFiles(filter);
			if (files != null) {
				for (int j = 0; j < files.length; j++) {
					fileList.add(currentDir.toURI() + files[j].getName());
				}
			}
		}
		return fileList;
	}

	private ArrayList<File> getDirs(ArrayList<File> dirList, String dir) throws Exception {
		File parentDir = new File(dir);
		dirList.add(parentDir);
		String[] directoryList = parentDir.list();
		for (int i = 0; i < directoryList.length; i++) {
			String sub = directoryList[i];
			File subDir = new File(dir + File.separator + sub);
			if (subDir.isDirectory())
				dirList = getDirs(dirList, dir + File.separator + sub);
		}

		return dirList;
	}

	public List<String> getXmlFiles() {
		return this.xmlFiles;
	}

	public Map<String, XmlDefine> getConfiguration() {
		return this.configuration;
	}

	public void setConfiguration(Map<String, XmlDefine> configuration) {
		this.configuration = configuration;
	}

	public XmlDefine getXmlDefine(String name) {
		XmlDefine definition = (XmlDefine) this.configuration.get(name);

		return definition;
	}

	public void getMessageSet(String apiName, MessageSet messageSet) throws Exception {
		
		
		if (messageSet == null) {
			throw new MesException("EmptyMandatory", "MessageSet");
		}

		
		XmlDefine rootDefinition = getXmlDefine(apiName);
		
		
		if (rootDefinition == null) {
			throw new MesException("NotFoundApiSchema", apiName);
		}

		XmlEntry root = rootDefinition.getParent();

		if (root == null) {
			throw new MesException("InvalidApiSchema", apiName);
		}

		if (!messageSet.getClass().getName().equals(root.getClassName())) {
			throw new MesException("InvalidSchemaClass",
					new String[] { root.getClass().getSimpleName(), messageSet.getClass().getSimpleName() });
		}

		for (Message msg : messageSet.getList()) {
			if (msg.getAttributesCount() <= 0)
				msg.setAttributes(messageSet.getAttributes());
			if (msg.getPropertiesCount() <= 0)
				msg.setProperties(messageSet.getProperties());
			if (msg.getOverridesCount() <= 0)
				msg.setOverrides(messageSet.getOverrides());

		}

		checkMember(messageSet, messageSet, rootDefinition.getChildren());
	}

	private Object checkDefault(Object parent, Object obj, XmlEntry entry) throws Exception {
		Object value = entry.getValue();

		if ("Date".equalsIgnoreCase(entry.getValueType())) {
			if ("current".equalsIgnoreCase((String) value)) {
				value = new Date();
			}
		}

		return value;
	}


	@SuppressWarnings("unchecked")
		List<Message> list = null;	private void checkList(Object parent, Object obj, XmlEntry child) throws Exception {

		try {
			Method getterMethod = obj.getClass().getMethod(CommonUtil.getMethodName(child.getName(), "get"),
					new Class[0]);

			list = (List<Message>) getterMethod.invoke(obj, new Object[0]);
		} catch (Exception e) {
			throw new MesException("NotFound", child.getName());
		}

		checkChild(parent, list, child);
	}

	private void checkChild(Object parent, List<Message> list, XmlEntry child) throws Exception {
		XmlDefine listDef = getXmlDefine(child.getId());
		if ((listDef != null) && (list != null) && (list.size() > 0)) {
			for (Message msg : list) {
				for (Field field : parent.getClass().getDeclaredFields()) {
					String fieldTypeClass = field.getGenericType().toString();

					if ((fieldTypeClass.indexOf("CustomedFields") == -1) && (fieldTypeClass.indexOf("Map") == -1)) {
						if ((CommonUtil.fieldExist(msg, field.getName()))
								&& (CommonUtil.getValue(msg, field.getName()) == null)) {
							CommonUtil.setValue(msg, field.getName(), CommonUtil.getValue(parent, field.getName()));
						}
					}
				}

				checkMember(parent, msg, listDef.getChildren());
			}
		}
	}

	private void checkMember(Object parent, Object obj, XmlEntry[] children) throws Exception {
		if (children == null)
			return;

		for (int i = 0; i < children.length; i++) {
			if ("list".equalsIgnoreCase(children[i].getPropertyName())) {
				if ("header".equalsIgnoreCase(children[i].getType())) {
					checkList(parent, obj, children[i]);
				}
			} else if ("property".equalsIgnoreCase(children[i].getPropertyName())) {
				parseProperty(parent, obj, children[i]);
			}
		}
	}

	private void parseProperty(Object parent, Object obj, XmlEntry child) throws Exception {
		if ("value".equalsIgnoreCase(child.getType())) {
			Object value = checkDefault(parent, obj, child);

			if ((!(value instanceof CustomedFields)) && (!(value instanceof Map)) && (!(value instanceof HashMap))) {
				if ((value != null) && (CommonUtil.getValue(obj, child.getName()) == null)) {
					CommonUtil.setValue(obj, child.getName(), value);
				}
			}
		} else if (!"header".equalsIgnoreCase(child.getType())) {
			if (!"parent".equalsIgnoreCase(child.getType())) {
				throw new MesException("NotFoundData", new String[] { "parseProperty", "Type" });
			}
		}

		if (child.getMandatory()) {
			if (CommonUtil.getValue(obj, child.getName()) == null) {
				throw new MesException("EmptyMandatory", child.getName());
			}
		}
	}
}