/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 6. 3
 */
package org.snu.ids.ha.ma;


import java.util.ArrayList;
import java.util.List;


/**
 * <pre>
 * ���� �¿� ���� ���е� token �� ��� class
 * list�� add, get, size �Լ����� �������� ��
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 3
 */
public class TokenList
{
	private ArrayList	list	= null;


	public TokenList(List list)
	{
		this.list = new ArrayList();
		this.list.addAll(list);
	}


	public int size()
	{
		return list.size();
	}


	public void add(Token token)
	{
		list.add(token);
	}


	public Token get(int i)
	{
		return (Token) list.get(i);
	}


	public void remove(int i)
	{
		list.remove(i);
	}
}
