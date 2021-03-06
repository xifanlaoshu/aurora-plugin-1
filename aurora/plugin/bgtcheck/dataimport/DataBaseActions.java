package aurora.plugin.bgtcheck.dataimport;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.datatype.IntegerType;
import uncertain.datatype.StringType;
import uncertain.logging.ILogger;
import uncertain.ocm.IObjectRegistry;
import aurora.plugin.bgtcheck.DatabaseTool;
import aurora.plugin.bgtcheck.PrepareParameter;

public class DataBaseActions extends DatabaseTool{

	public DataBaseActions(IObjectRegistry registry, ILogger logger) {
		super(registry, logger);
	}
	
	

	public int generateBatchId(String batchCode, String batchName, String batchFullName) throws SQLException {
		int batch_id = generateBatchId();
		StringBuffer prepareSQL = new StringBuffer();
		prepareSQL.append("insert into td_batchs(");
		prepareSQL.append("   batch_id,");
		prepareSQL.append("   batch_code,");
		prepareSQL.append("   batch_name,");
		prepareSQL.append("   batch_full_name,");
		prepareSQL.append("   creation_date,");
		prepareSQL.append("   created_by,");
		prepareSQL.append("   last_update_date,");
		prepareSQL.append("   last_updated_by");
		prepareSQL.append(")values(?,?,?,?,sysdate,1,sysdate,1)");
		
		PrepareParameter[] paras = new PrepareParameter[4];
		int i=0;
		paras[i++] = new PrepareParameter(new IntegerType(), batch_id);
		paras[i++] = new PrepareParameter(new StringType(), batchCode);
		paras[i++] = new PrepareParameter(new StringType(), batchName);
		paras[i++] = new PrepareParameter(new StringType(), batchFullName);
		try{
			sqlExecuteWithParas(mRegistry, prepareSQL.toString(), paras);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "execute sql:" + prepareSQL + " failed.", e);
			throw new SQLException(e);
		}
		return batch_id;
	}
	
	private int generateBatchId() throws SQLException {
		int batch_id = -1;
		String prepareSQL = "select td_batchs_s.nextval batch_id from dual";
		try {
			CompositeMap result = sqlQueryWithParas(mRegistry, prepareSQL, null);
			if (result != null) {
				List<CompositeMap> childList = result.getChilds();
				if (childList != null) {
					for (CompositeMap record : childList) {
						batch_id = record.getInt("batch_id");
					}
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "execute sql:" + prepareSQL + " failed.", e);
			throw new SQLException(e);
		}
		return batch_id;
	}
	
	public void insert_td_batch_tables(int batch_id,String table_name, String data_desc, String data_full_name) throws SQLException {
		StringBuffer prepareSQL = new StringBuffer();
		prepareSQL.append("insert into td_batch_tables(");
		prepareSQL.append("   batch_id,");
		prepareSQL.append("   table_name,");
		prepareSQL.append("   data_desc,");
		prepareSQL.append("   data_full_name,");
		prepareSQL.append("   creation_date,");
		prepareSQL.append("   created_by,");
		prepareSQL.append("   last_update_date,");
		prepareSQL.append("   last_updated_by");
		prepareSQL.append(")values(?,?,?,?,sysdate,1,sysdate,1)");
		
		PrepareParameter[] paras = new PrepareParameter[4];
		int i=0;
		paras[i++] = new PrepareParameter(new IntegerType(), batch_id);
		paras[i++] = new PrepareParameter(new StringType(), table_name);
		paras[i++] = new PrepareParameter(new StringType(), data_desc);
		paras[i++] = new PrepareParameter(new StringType(), data_full_name);
		try{
			sqlExecuteWithParas(mRegistry, prepareSQL.toString(), paras);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "execute sql:" + prepareSQL + " failed.", e);
			throw new SQLException(e);
		}
	}
	
	public void deleteTable(String tableName) throws SQLException {
		String prepareSQL = "delete from "+tableName;
		try {
			sqlExecuteWithParas(mRegistry, prepareSQL, null);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "execute sql:" + prepareSQL + " failed.", e);
			throw new SQLException(e);
		}
	}
	
	public void copyDataToTdTable(int batch_id,String tableName) throws SQLException {
		String td_table_name = queryTdTable(tableName);
		if(td_table_name == null || "".equals(td_table_name))
			throw new IllegalArgumentException("Can not find "+tableName+"'s td_table_name.");
		String prepareSQL = "insert into "+td_table_name+" select "+batch_id+",t.* from "+tableName+" t";
		try {
			sqlExecuteWithParas(mRegistry, prepareSQL, null);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "execute sql:" + prepareSQL + " failed.", e);
			throw new SQLException(e);
		}
	}
	private String queryTdTable(String tableName) throws SQLException {
		String td_table_name = null;
		String prepareSQL = "select td_table_name from td_tables t where t.table_name=?";
		try {
			PrepareParameter[] paras = new PrepareParameter[1];
			int i=0;
			paras[i++] = new PrepareParameter(new StringType(), tableName.toUpperCase());
			CompositeMap result = sqlQueryWithParas(mRegistry, prepareSQL, paras);
			if (result != null) {
				List<CompositeMap> childList = result.getChilds();
				if (childList != null) {
					for (CompositeMap record : childList) {
						td_table_name = record.getString("td_table_name");
					}
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "execute sql:" + prepareSQL + " failed.", e);
			throw new SQLException(e);
		}
		return td_table_name;
	}
	
	public void insert_td_batch_hierarchy(int batch_id, int parent_batch_id) throws SQLException {
		StringBuffer prepareSQL = new StringBuffer();
		prepareSQL.append("insert into td_batch_hierarchy(");
		prepareSQL.append("   hierarchy_id,");
		prepareSQL.append("   batch_id,");
		prepareSQL.append("   parent_batch_id,");
		prepareSQL.append("   creation_date,");
		prepareSQL.append("   created_by,");
		prepareSQL.append("   last_update_date,");
		prepareSQL.append("   last_updated_by");
		prepareSQL.append(")values(td_batch_hierarchy_s.nextval,?,?,sysdate,1,sysdate,1)");
		
		PrepareParameter[] paras = new PrepareParameter[2];
		int i=0;
		paras[i++] = new PrepareParameter(new IntegerType(), batch_id);
		paras[i++] = new PrepareParameter(new IntegerType(), parent_batch_id);
		try{
			sqlExecuteWithParas(mRegistry, prepareSQL.toString(), paras);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "execute sql:" + prepareSQL + " failed.", e);
			throw new SQLException(e);
		}
	}
}
