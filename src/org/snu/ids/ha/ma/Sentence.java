/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 6. 26
 */
package org.snu.ids.ha.ma;


import java.util.ArrayList;

import org.snu.ids.ha.util.Util;


/**
 * <pre>
 * �ѹ����� �̷�� �������� ����Ʈ�� ������.
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 26
 */
public class Sentence
{
	ArrayList	eojeolList	= null;


	public Sentence()
	{
		eojeolList = new ArrayList();
	}


	public Eojeol get(int idx)
	{
		return (Eojeol) eojeolList.get(idx);
	}


	public void add(Eojeol e)
	{
		// ������ �� �λ�� �ٿ��� ���� ���� ó�����ش�.
		if( e.isAdverbCombined() || e.isPrenounCombined() ) {
			Eojeol adEojeol = e.removeCombinedFirst();
			eojeolList.add(adEojeol);
		}
		eojeolList.add(e);
	}


	public int size()
	{
		return eojeolList.size();
	}


	public void remove(int i)
	{
		eojeolList.remove(i);
	}


	/**
	 * <pre>
	 * ������ ���Ⱑ ������ ���� ��ȯ���ش�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 26
	 * @return
	 */
	public String getSentence()
	{
		StringBuffer sb = new StringBuffer();
		Eojeol eojeol = null;
		String temp = null;
		for( int i = 0, stop = size(); i < stop; i++ ) {
			eojeol = get(i);
			temp = eojeol.exp;
			if( i > 0 ) sb.append(" ");
			sb.append(temp);
		}
		return sb.toString();
	}


	/**
	 * <pre>
	 *
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 12
	 * @return
	 */
	public String getMAString()
	{
		StringBuffer sb = new StringBuffer();
		Eojeol eojeol = null;
		sb.append("[" + getSentence() + "]" + Util.LINE_SEPARATOR);
		for( int i = 0, stop = size(); i < stop; i++ ) {
			eojeol = get(i);
			if( i > 0 ) sb.append(Util.LINE_SEPARATOR);
			sb.append("\t" + eojeol);
		}
		return sb.toString();
	}
}
