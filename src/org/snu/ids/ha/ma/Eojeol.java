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
	 * 문장의 종결을 나타내는 어절인지 확인한다.
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
				// 괄호가 아니면 불필요한 Symbol이라고 판단한다.
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
	 * 동사나 형용사에 띄어쓰기 오류가 흔히 발생하는 부사가 결합되었는지 확인한다.
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
	 * 동사나 형용사에 결합된 부사를 제거해준다.
	 * 반드시 isAdverbCombined() 또는 isPrenounCombined() 함수를 호출하여 부사가 부착되었는지를 확인한 후에 처리해야 한다.
	 * 결과로 떨어진 부사에 대한 형태소 객체를 가진 어절이 반환되고, 호출한 객체에서는 부서가 제거된다.
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
	 * 체언에 띄어쓰기 오류가 흔히 발생하는 관형사가 결합되었는지 확인한다.
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
	 * 어절의 원래 모양을 스트링으로 리턴한다.
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
