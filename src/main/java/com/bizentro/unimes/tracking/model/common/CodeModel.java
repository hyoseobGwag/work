/**
 * CodeModel : Code 
 */

package com.bizentro.unimes.tracking.model.common;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bizentro.unimes.common.model.common.code.Code;
import com.bizentro.unimes.common.model.common.code.CodeClass;
import com.bizentro.unimes.common.model.common.code.CodeMap;
import com.bizentro.unimes.common.util.CommonUtil;
import com.bizentro.unimes.common.util.MesException;
import com.bizentro.unimes.tracking.model.Model;


@Service("common.CodeModel")
public class CodeModel extends Model{
	private String SQL_CODE = "FROM UMCODE WHERE Site = ? AND CodeID = ?";
	private String SQL_CODECLASS = "FROM UMCODEClass WHERE Site = ? AND CodeID = ?";
	private String SQL_CODEMAP = "FROM UMCODEMap WHERE Site = ? AND CodeID = ?";
	
	private String SQL_USABLE = " AND IsUsable = 'Usable";
	
	/*
	 * Code 찾기
	 */
	public Code findCode(Code code) throws Exception {
		String sql = SQL_CODE;
		
		return (Code)super.find(sql, new Object[] {code.getSite(), code.getId()});
	}
	
	public Code findCodeUsable(Code code, boolean bUsable) throws Exception {
		String sql = SQL_CODE;
		
		if (bUsable)
			sql += SQL_USABLE;
		
		return (Code)super.find(sql, new Object[] {code.getSite(), code.getId()});
	}
	
	/*
	 * CodeClass 찾기
	 */
	
	public CodeClass findCodeClass(CodeClass codeClass) throws Exception {
		String sql = SQL_CODECLASS;
		
		return (CodeClass)super.find(sql, new Object[] {codeClass.getSite(), codeClass.getCodeClassID()});
	}
	
	public CodeClass getCodeClass(String site, String codeClassId, boolean bUsable) throws Exception {
		String sql = SQL_CODECLASS;

		if (bUsable)
			sql += SQL_USABLE;

		CodeClass codeClass = (CodeClass) super.get(sql, new Object[] { site, codeClassId });

		if (codeClass == null) {
			if (bUsable)
				throw new MesException("NotFoundMasterData(Usable)",
						new String[] { "Site : " + site + ", CodeID : " + codeClassId, "CodeClass" });

			throw new MesException("NotFoundData",
					new String[] { "CodeClass", "Site : " + site + ", CodeClassID : " + codeClassId });
		}

		return codeClass;
	}
	
	public CodeClass findCodeClassUsable(CodeClass codeClass, boolean bUsable) throws Exception {
		String sql = SQL_CODECLASS;
		
		if (bUsable)
			sql += SQL_USABLE;
		
		return (CodeClass)super.find(sql, new Object[] {codeClass.getSite(), codeClass.getCodeClassID()});
	}
	
	public CodeMap findCodeMap(CodeMap codeMap) throws Exception {
		String sql = SQL_CODEMAP;
		
		return (CodeMap)super.find(sql, new Object[] {codeMap.getSite(), codeMap.getCodeID()});
	}
	
	public CodeMap findCodeClassUsable(CodeMap codeMap, boolean bUsable) throws Exception {
		String sql = SQL_CODECLASS;
		
		if (bUsable)
			sql += SQL_USABLE;
		
		return (CodeMap)super.find(sql, new Object[] {codeMap.getSite(), codeMap.getCodeID()});
	}
	
	public void initializeMessage(Code Code, String apiName) throws Exception {

		CommonUtil.checkMandatory(Code.getId(), "CodeID");

		super.initializeMessage(Code, apiName);
	}

}
