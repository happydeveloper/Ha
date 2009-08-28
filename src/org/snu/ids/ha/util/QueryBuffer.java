/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 7. 23
 */
package org.snu.ids.ha.util;


/**
 * <pre>
 *
 * </pre>
 * @author 	therocks
 * @since	2007. 7. 23
 */
public class QueryBuffer
{
	StringBuffer	sb	= null;


	public QueryBuffer()
	{
		sb = new StringBuffer();
	}


	public QueryBuffer(int length)
	{
		sb = new StringBuffer(length);
	}


	public QueryBuffer(String str)
	{
		sb = new StringBuffer(str);
	}


	public void append(boolean val)
	{
		sb.append(val);
	}


	public void append(char val)
	{
		sb.append(val);
	}


	public void append(char[] val)
	{
		sb.append(val);
	}


	public void append(char[] val, int offset, int len)
	{
		sb.append(val, offset, len);
	}


	public void append(double val)
	{
		sb.append(val);
	}


	public void append(float val)
	{
		sb.append(val);
	}


	public void append(int val)
	{
		sb.append(val);
	}


	public void append(long val)
	{
		sb.append(val);
	}


	public void append(Object val)
	{
		sb.append(val);
	}


	public void append(String val)
	{
		sb.append(val);
	}


	public void appendLn(String val)
	{
		sb.append(val);
		sb.append(Util.LINE_SEPARATOR);
	}


	public void append(StringBuffer val)
	{
		sb.append(val);
	}


	public void append(QueryBuffer qb)
	{
		sb.append(qb.sb);
	}


	public int capacity()
	{
		return sb.capacity();
	}


	public String substring(int start)
	{
		return sb.substring(start);
	}


	public String substring(int start, int end)
	{
		return sb.substring(start, end);
	}


	public String toString()
	{
		return sb.toString();
	}
}
