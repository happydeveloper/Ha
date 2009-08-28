/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 6. 3
 */
package org.snu.ids.ha.ma;


import java.util.ArrayList;

import org.snu.ids.ha.constants.HgEncoded;


/**
 * <pre>
 * ���¼� ������ �����ϴ� Class
 * List�� ���� ����
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 3
 */
public class MorphemeList
{
	Morpheme	firstMorpheme	= null;	// �м����� ù��° ���¼�
	Morpheme	lastMorpheme	= null;	// �м����� ������ ���¼�
	ArrayList	list			= null; // ���¼� �м� ����


	/**
	 * <pre>
	 * deault constructor
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 */
	public MorphemeList()
	{
		list = new ArrayList();
	}


	/**
	 * <pre>
	 * ũ�� ���� ��ȯ
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @return
	 */
	public int size()
	{
		return list.size();
	}


	/**
	 * <pre>
	 * ���¼� ���� ����
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param mp
	 */
	public void add(Morpheme mp)
	{
		// � ��̰� ������ �����ش�.
		if(lastMorpheme != null
				&& lastMorpheme.isSufficientByAnd(HgEncoded.EM)
				&& mp.isSufficientByAnd(HgEncoded.EM) )
		{
			Morpheme temp = lastMorpheme.copy();
			temp.string += mp.string;
			// ������ ��쿡�� ���� ������ �ǵ��� ����
			if( mp.isSufficientByAnd(HgEncoded.EM_ED_HR) ) {
				// ������ ����� ������ ���������� ��ȯ�Ѵ�.
				if( lastMorpheme.isSufficientByAnd(HgEncoded.EM_CN_SU) ) {
					temp.hgEncoded = HgEncoded.EM_ED_NM;
				}
				// ���, �ƿ� �� ���������� ó��
				else if( lastMorpheme.isSufficientByAnd(HgEncoded.EM_CN_DP)
						&& (lastMorpheme.string.equals("��")
								|| lastMorpheme.string.equals("��")
								|| lastMorpheme.string.equals("��")
								|| lastMorpheme.string.equals("��")
								|| lastMorpheme.string.endsWith("��")
								|| lastMorpheme.string.endsWith("��")) )
				{
					temp.hgEncoded = HgEncoded.EM_ED_NM;
				}
			} else {
				temp.hgEncoded = mp.hgEncoded;
			}
			if( firstMorpheme == lastMorpheme) {
				firstMorpheme = temp;
			}
			list.remove(size() - 1);
			mp = temp;
		}
		list.add(mp);
		if( firstMorpheme == null ) firstMorpheme = mp;
		lastMorpheme = mp;
	}


	/**
	 * <pre>
	 * ���¼� ���� ��ȯ
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param i
	 * @return
	 */
	public Morpheme get(int i)
	{
		return (Morpheme) list.get(i);
	}


	/**
	 * <pre>
	 * i��° ���¼Ҹ� �����Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param i
	 */
	public void remove(int i)
	{
		list.remove(i);
	}


	/**
	 * <pre>
	 * ���¼� �м� ����� ���ϼ��� Ȯ���Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param ml
	 * @return
	 */
	public boolean equals(MorphemeList ml)
	{
		return getEncodedString().equals(ml.getEncodedString());
	}


	/**
	 * <pre>
	 * �м��� ���¼� ����� ����Ѵ�.
	 * ���¼�+���¼�+���¼� �� ���� ���·� ����Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @return
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		for( int i = 0, stop = size(); i < stop; i++ ) {
			if( i > 0 ) sb.append("+");
			sb.append(get(i));
		}
		return sb.toString();
	}


	/**
	 * <pre>
	 * �м��� ���¼� ����� ����Ѵ�.
	 * encoding�� ���·� ����Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 20
	 * @return
	 */
	String getEncodedString()
	{
		StringBuffer sb = new StringBuffer();
		for( int i = 0, stop = size(); i < stop; i++ ) {
			if( i > 0 ) sb.append("+");
			sb.append(get(i).getEncodedString());
		}
		return sb.toString();
	}


	/**
	 * <pre>
	 * �ռ�� �� �� �ִ� �͵��� �ռ���� ����� ���� Encoded String�� ������.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 27
	 * @return
	 */
	String getMergedEncodedString()
	{
		return getMerged().getEncodedString();
	}


	/**
	 * <pre>
	 * �ռ�� �� �� �ִ� �͵��� ���ļ� ��ȯ�Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 27
	 * @return
	 */
	MorphemeList getMerged()
	{
		MorphemeList ml = new MorphemeList();
		Morpheme preMp = null, curMp = null;
		for( int i = 0, stop = size(); i < stop; i++ ) {
			curMp = get(i).copy();
			if( preMp != null && preMp.appendable(curMp) ) {
				preMp.append(curMp);
				continue;
			}
			ml.add(curMp);
			preMp = curMp;
		}
		return ml;
	}
}
