/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 6. 26
 */
package org.snu.ids.ha.ma;

import org.snu.ids.ha.constants.HgEncoded;
import org.snu.ids.ha.util.Util;


/**
 * <pre>
 *
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 26
 */
public class Eojeol
	extends MorphemeList
{
	String	exp	= null;


	private Eojeol()
	{
		// DO NOTHING
	}


	Eojeol(String exp, MCandidate mc)
	{
		this.exp = exp;
		for( int i = 0, stop = mc.size(); i < stop; i++ ) {
			list.add(mc.get(i));
		}
	}


	Eojeol(MExpression me)
	{
		this(me.getExp(), me.get(0));
	}


	/**
	 * <pre>
	 * ������ ������ ��Ÿ���� �������� Ȯ���Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 26
	 * @return
	 */
	public boolean isEnding()
	{
		return get(size() - 1).isSufficientByAnd(HgEncoded.EM_ED);
	}


	/**
	 * <pre>
	 *
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 19
	 * @return
	 */
	public boolean isDummySymbol()
	{
		if( size() == 1 ) {
			Morpheme mp = get(0);
			if( mp.isSufficientByAnd(HgEncoded.SY) ) {
				// ��ȣ�� �ƴϸ� ���ʿ��� Symbol�̶�� �Ǵ��Ѵ�.
				if( HgEncoded.PARENTHESIS_SET.contains(mp.string)
						|| mp.string.startsWith(",")
						|| mp.string.startsWith(".")
						|| mp.string.startsWith("~"))
				{
					return false;
				}
				return true;
			}
		}
		return false;
	}


	/**
	 * <pre>
	 * ���糪 ����翡 ���� ������ ���� �߻��ϴ� �λ簡 ���յǾ����� Ȯ���Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 20
	 * @return
	 */
	boolean isAdverbCombined()
	{
		if( size() > 1 ) {
			Morpheme mp1 = get(0);
			Morpheme mp2 = get(1);
			if( mp1.isSufficientByAnd(HgEncoded.AD) && mp2.isSufficientByOr(HgEncoded.OR_VV_AJ) )
				return true;
		}
		return false;
	}


	/**
	 * <pre>
	 * ���糪 ����翡 ���յ� �λ縦 �������ش�.
	 * �ݵ�� isAdverbCombined() �Ǵ� isPrenounCombined() �Լ��� ȣ���Ͽ� �λ簡 �����Ǿ������� Ȯ���� �Ŀ� ó���ؾ� �Ѵ�.
	 * ����� ������ �λ翡 ���� ���¼� ��ü�� ���� ������ ��ȯ�ǰ�, ȣ���� ��ü������ �μ��� ���ŵȴ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 20
	 * @return
	 */
	Eojeol removeCombinedFirst()
	{
		Eojeol ret = new Eojeol();
		Morpheme advMp = get(0);
		ret.exp = advMp.string;
		ret.add(advMp);
		exp = exp.replaceFirst(advMp.string, "");
		this.remove(0);
		return ret;
	}


	/**
	 * <pre>
	 * ü�� ���� ������ ���� �߻��ϴ� �����簡 ���յǾ����� Ȯ���Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 20
	 * @return
	 */
	boolean isPrenounCombined()
	{
		if( size() > 1 ) {
			Morpheme mp1 = get(0);
			Morpheme mp2 = get(1);
			if( mp1.isSufficientByAnd(HgEncoded.DT) && mp2.isSufficientByOr(HgEncoded.OR_NOUN_CLASSES) )
				return true;
		}
		return false;
	}


	/**
	 * <pre>
	 * ������ ���� ����� ��Ʈ������ �����Ѵ�.
	 * </pre>
	 * @author myung
	 * @since  2007. 6. 27
	 * @return
	 */
	public String getExp()
	{
		return exp;
	}


	public String toString()
	{
		return Util.getTabbedString(getExp(), 4, 16) + "=> [" + super.toString() + "]";
	}
}
