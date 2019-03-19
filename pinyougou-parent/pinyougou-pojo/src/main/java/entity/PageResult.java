package entity;

import java.io.Serializable;
/**
 * 分页结果类
 * @author 57473
 *
 */
import java.util.List;

import javax.sql.RowSet;
public class PageResult implements Serializable{
	private long total;//总记录数
	private List rows;//当前记录
	/**
	 * @param total
	 * @param rows
	 */
	public PageResult(long total, List rows) {
		super();
		this.total = total;
		this.rows = rows;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public List getRows() {
		return rows;
	}
	public void setRows(List rows) {
		this.rows = rows;
	}
	
}
