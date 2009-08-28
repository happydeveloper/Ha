/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 6. 4
 */
package org.snu.ids.ha.ma;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.snu.ids.ha.constants.Condition;
import org.snu.ids.ha.constants.HgEncoded;
import org.snu.ids.ha.util.Util;


/**
 * <pre>
 *
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 4
 */
public class MExpression
	implements Comparable
{
	/**
	 * <pre>
	 * ���¼��� ǥ����
	 * </pre>
	 * @since	2007. 6. 4
	 * @author	therocks
	 */
	String				exp				= null;

	/**
	 * <pre>
	 * ���¼��� ǥ������ ���ؼ� ������ ��м� ���¼� �м� �����
	 * </pre>
	 * @since	2007. 6. 4
	 * @author	therocks
	 */
	private ArrayList	mCandidateList	= null;


	/**
	 * <pre>
	 * default constructor
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 25
	 * @param exp
	 */
	MExpression(String exp)
	{
		this.exp = exp;
		mCandidateList = new ArrayList();
	}


	/**
	 * <pre>
	 * default constructor
	 * �ϳ��� ��м� ���¼� �м� ����� �����Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param exp
	 * @param mc
	 */
	MExpression(String exp, MCandidate mc)
		throws Exception
	{
		this(exp);
		add(mc);
	}


	/**
	 * <pre>
	 * ��м� �ĺ� �ϳ��� ���� ǥ���� ����
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 24
	 * @param mc
	 * @throws Exception
	 */
	MExpression(MCandidate mc)
		throws Exception
	{
		this(mc.getExp());
		add(mc);
	}


	/**
	 * <pre>
	 * ǥ������ ���� ��м� ���¼� �м� ����� �߰��Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param mc
	 */
	void add(MCandidate mc)
	{
		if( mc != null && !contains(mc) ) mCandidateList.add(mc);
	}


	/**
	 * @return Returns the exp.
	 */
	String getExp()
	{
		return exp;
	}


	/**
	 * <pre>
	 * i��° �ĺ��� �����Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param idx
	 */
	void remove(int idx)
	{
		mCandidateList.remove(idx);
	}


	/**
	 * <pre>
	 * ��м� �ĺ� ����� �����ϰ� �ִ��� Ȯ���Ѵ�.
	 * MCandidate.equals(Object obj) �Լ��� over riding�ؼ� ���
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param mc
	 * @return
	 */
	boolean contains(MCandidate mc)
	{
		return mCandidateList.contains(mc);
	}


	/**
	 * <pre>
	 * ��м� �ĺ��� �� ������ ��ȯ�Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @return
	 */
	int size()
	{
		return mCandidateList.size();
	}


	/**
	 * <pre>
	 * i���翡 ����Ǿ� �ִ� ��м� �ĺ��� ��ȯ�Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param i
	 * @return
	 */
	MCandidate get(int i)
	{
		return (MCandidate) mCandidateList.get(i);
	}
	
	
	/**
	 * <pre>
	 * �� �ĺ����� �м� ����� ���� index(offset)������ �������ش�.
	 * </pre>
	 * @author	therocks
	 * @since	2008. 03. 31
	 * @param index	���� offset
	 */
	void setIndex(int index)
	{
		for(int i=0, size = size(); i < size; i++) {
			get(i).setIndex(index);
		}
	}
	
	
	/**
	 * <pre>
	 * ǥ���� ���� ������ ���ڿ��� ��ȯ�Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @return
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer(exp);
		for( int i = 0, stop = size(); i < stop; i++ ) {
			sb.append(Util.LINE_SEPARATOR);
			sb.append("\t{" + get(i) + "};");
		}
		return sb.toString();
	}
	
	
	public String toSimpleString()
	{
		StringBuffer sb = new StringBuffer(exp);
		for( int i = 0, stop = size(); i < stop; i++ ) {
			sb.append(Util.LINE_SEPARATOR);
			sb.append("\t{" + get(i).toSimpleString() + "};");
		}
		return sb.toString();
	}


	/**
	 * <pre>
	 * mCandidate�� encoding�� ���ڿ��� �о���δ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 11
	 * @return
	 */
	String getEncodedString()
	{
		StringBuffer sb = new StringBuffer(exp + ":");
		for( int i = 0, stop = size(); i < stop; i++ ) {
			if( i > 0 ) sb.append(";");
			sb.append(get(i).getEncodedString());
		}
		return sb.toString();
	}


	/**
	 * <pre>
	 * ���ٴ� mExp�� ���ؼ� ���� ������ ���¼� �м� ����� �����Ͽ� ��ȯ�Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param mExp
	 * @return
	 */
	MExpression derive(MExpression mExp)
	{
		MExpression ret = new MExpression(this.exp + mExp.exp);
		MCandidate thisMCandidate = null, addMCandidate = null;
		int jStop = mExp.size();
		for( int i = 0, iStop = size(); i < iStop; i++ ) {
			thisMCandidate = get(i);
			for( int j = 0; j < jStop; j++ ) {
				addMCandidate = mExp.get(j);
				ret.add(thisMCandidate.derive(addMCandidate));
			}
		}
		ret.prune();
		return ret;
	}


	private static final int PRUNE_SIZE = 7;


	/**
	 * <pre>
	 * �켱 ������ �������� �͵��� �ĺ� �м� ������� �����ع���
	 * ���� ������~
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 */
	void prune()
	{
		int size = size();
		if( size < 2 ) return;

		int maxDicLen = -1, maxSpaceCnt = 0;
		int expLen = exp.length(), tempDicLen = 0;

		// ���� �ĺ� Ȯ��
		sort();
		MCandidate mc = get(0);

		// ������ ������� ������ ������ ����� ���ָ� ���ܵд�.
		boolean uncomplete = mc.candDicLen > 0 || expLen > mc.getDicLenWithCand();

		// �����ĺ��� �� ������ ���� Ȯ��
		maxDicLen = mc.realDicLen;
		maxSpaceCnt = mc.spaceCnt;
		if( maxDicLen == expLen ) {
			// �������� �ѱ��� ������ ������ ���⿡ ���� prunning�� �� �� ���� �ϳ��� �� ������ش�.
			// ���� �ѱ��ڷ� ���ؼ� �� ������ ����� ���� �� �ִµ� �̰��� �߸��� ���� �����ϱ� ����
			if( mc.lastMorpheme.isSufficientByAnd(HgEncoded.JO)
					&& mc.lastMorpheme.string.length() == 1 )
			{
				maxSpaceCnt++;
			}
		}
		// ������� ���� ���� ������ prunning���� �ʴ´�.
		else if( !uncomplete && size < PRUNE_SIZE ) {
			return;
		}

		// �ѱ��� ���θ� �̷������ ���� prunning���� �ʴ´�.
		if( mc.isComposedOfOnlyOneNouns() ) return;

		// �̿� �ĺ� Ȯ��
		int pruneIdx = 1;
		for( int stop = mCandidateList.size(); pruneIdx < stop; ) {
			mc = (MCandidate) mCandidateList.get(pruneIdx);

			// ������ ���� Ȯ��
			tempDicLen = mc.getDicLenWithCand();

			// ����� �ʹ� ���� ������ ������ prunning�ع�����.
			// �ϰ���� �ʾ�����
			if( uncomplete && mc.getDicLenOnlyCand() == 0 && pruneIdx < PRUNE_SIZE ) {
				pruneIdx++;
				continue;
			}

			// ������ ���̰� ª���� ����
			if( tempDicLen < maxDicLen ) break;

			// ���Ⱑ ���� ���� ����
			if( (maxSpaceCnt > 0 || stop > PRUNE_SIZE) && mc.spaceCnt > maxSpaceCnt ) break;

			pruneIdx++;
		}

		// ������ ��� �͵��� ��������
		for( int i = pruneIdx, stop = mCandidateList.size(); i < stop; i++ ) {
			// 2008-03-18: ������ ������� �̷������ ���� ��� ������ �̵�Ͼ ���ܵξ� ���� �̻��� ����� ����� ���� ���� ����
			if( uncomplete && i == stop - 1 && ((MCandidate) mCandidateList.get(pruneIdx)).realDicLen == 0 ) break;
			mCandidateList.remove(pruneIdx);
		}
	}


	/**
	 * <pre>
	 * ���� ������ �ĺ� ������ �����Ǵ� ���� ������ �����Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 24
	 * @param preME
	 * @throws Exception
	 */
	void pruneWithPre(MExpression preME)
		throws Exception
	{
		if( preME == null ) return;
		int thisMESize = this.size(), preMESize = preME.size();
		if( preMESize == 0 ) return;
		for( int i = 0; i < thisMESize; i++ ) {
			MCandidate thisMC = this.get(i);
			thisMC.asize = 0;
			for( int j = 0; j < preMESize; j++ ) {
				MCandidate preMC = preME.get(j);
				if( preMC.isAppendableAllowingSpace(thisMC) ) {
					thisMC.asize++;
					break;
				}
			}
			if( thisMC.asize == 0 ) {
				this.remove(i);
				i--;
				thisMESize--;
			}
		}
	}


	/**
	 * <pre>
	 * ���� ��м� �ĺ��� �߿� ������ ����� ���ܵд�.
	 * prune�� ���ϰ� �ϸ� �ӵ��� ����������, �м� ������ �߻��� �� �ִ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param nextME
	 */
	void pruneWithNext(MExpression nextME)
		throws Exception
	{
		int thisMESize = this.size(), nextMESize = nextME.size();
		if( nextMESize == 0 ) return;
		for( int i = 0; i < thisMESize; i++ ) {
			MCandidate thisMC = get(i);
			thisMC.asize = 0;
			for( int j = 0; j < nextMESize; j++ ) {
				MCandidate nextMC = nextME.get(j);

				// ���� ���� ���� ���� Ȯ��
				if( thisMC.isAppendableAllowingSpace(nextMC) ) {
					thisMC.asize++;
					break;
				}
			}
			if( thisMC.asize == 0 && this.size() > 1 ) {
				remove(i);
				i--;
				thisMESize--;
			}
		}
	}



	/**
	 * <pre>
	 * ���� ���ڿ��� headStr�� �ϰ�, ���� ���ڿ��� tailStr�� �ϴ� �и��� ��м� �ĺ��� �����Ͽ� ��ȯ�Ѵ�.
	 * ��, �ڸ� �ڸ��� ��ġ�� divideIdx��° ����� �Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 25
	 * @param headStr
	 * @param headIndex	head �κ��� token index����
	 * @param tailStr
	 * @param tailIndex	tail �κ��� token index����
	 * @return
	 */
	MExpression[] divideHeadTailAt(String headStr, int headIndex, String tailStr, int tailIndex)
		throws Exception
	{
		MExpression[] ret = new MExpression[2];
		MExpression headME = ret[0] = new MExpression(headStr);
		MExpression tailME = ret[1] = new MExpression(tailStr);

		for( int j = 0, stop = size(); j < stop; j++ ) {
			MCandidate[] mcHeadTail = get(j).divideHeadTailAt(headStr, headIndex, tailStr, tailIndex);
			if( mcHeadTail != null && mcHeadTail[0].getExp().equals(headStr) ) {
				headME.add(mcHeadTail[0]);
				tailME.add(mcHeadTail[1]);
			} else {
				headME.add(new MCandidate(headStr, headIndex));
				tailME.add(new MCandidate(tailStr, tailIndex));
			}
		}
		return ret;
	}


	/**
	 * <pre>
	 * �ĺ� �м� ������� �����ش�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param mExp
	 */
	void merge(MExpression mExp)
	{
		for( int i = 0, stop = mExp.size(); i < stop; i++ ) {
			add(mExp.get(i));
		}
		prune();
	}


	/**
	 * <pre>
	 * ���⸦ �������� �� ǥ������ �����ؼ� ��ȯ���ش�.
	 * ���Ⱑ ����� ������� ���� ������ ǥ������ �������� �״�� ��ȯ���ش�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 20
	 * @return
	 */
	List split()
		throws Exception
	{
		if( size() == 0 ) return null;
		ArrayList ret = new ArrayList();

		// ���� ����
		MCandidate mc = this.get(0);
		List splitedMCList = mc.split();
		int splitedMCSize = splitedMCList.size();
		for( int i = 0; i < splitedMCSize; i++ ) {
			ret.add(new MExpression((MCandidate) splitedMCList.get(i)));
		}

		int size = size();

		// ���� ������ ���� ���̴� �ĺ� ����
		if( size > 1 ) {
			String preExpWithSpace = mc.geExpStringWithSpace();
			for( int i = 1; i < size; i++ ) {
				mc = get(i);
				// ������ �������� Ȯ��
				String curExpWithSpace = mc.geExpStringWithSpace();
				if( !preExpWithSpace.equals(curExpWithSpace) ) break;

				// �ɰ��� ����
				splitedMCList = mc.split();
				for( int j = 0; j < splitedMCSize; j++ ) {
					((MExpression) ret.get(j)).add((MCandidate) splitedMCList.get(j));
				}
			}
		}

		return ret;
	}


	/**
	 * <pre>
	 * ���Ⱑ
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 25
	 * @return
	 */
	boolean isOneEojeol()
	{
		return size() > 0 && get(0).spaceCnt == 0;
	}


	/**
	 * <pre>
	 * ���� �ĺ��� ������ ���� �����Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 26
	 */
	void sort()
	{
		Collections.sort(mCandidateList);
	}


	/**
	 * <pre>
	 * ���� �м� ����� Ȯ���Ͽ�, ���� ������ ���� �켱 ������ ���� �� �ֵ��� ����
	 * ���� �Ŀ��� ��ȣ ������ ������ �� �ֵ���
	 * Collections.sort(preME.mCandidateList); �߰���!!
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 26
	 * @param preME
	 */
	void resort(MExpression preME)
	{
		if( preME == null ) return;
		MCandidate preMC = null, curMC = null;
		int iStop = preME.size(), jStop = size();
		for( int i = 0; i < iStop; i++ ) {
			preMC = preME.get(i);
			for( int j = 0; j < jStop; j++ ) {
				curMC = get(j);
				// ü���� ���ӵǸ� �켱 ���� ���߾���
//				if( preMC.isHavingCondition(Condition.COND_NUM_NN)
//						&& curMC.firstMorpheme.isSufficientByOr(HgEncoded.OR_NOUN_CLASSES) )
//				{
//					curMC.bonus--;
//				}
				// �����簡 ���ӵǸ� �ڿ� ��ġ�� ������ ���� ���߾���
				if( preMC.isHavingCondition(Condition.COND_NUM_DT)
						&& curMC.firstMorpheme.isSufficientByOr(HgEncoded.OR_VV_AJ) )
				{
					curMC.bonus--;
				}
				// ��ȣ ������ �������� Ȯ��
				byte bonus = (byte)Util.bitCount(preMC.havingConditionEncoded & curMC.preferedConditionEncoded);
				preMC.bonus += bonus;
				curMC.bonus += bonus;
			}
		}
		preME.sort();
		sort();
	}


	/**
	 * <pre>
	 * �Ұ� �� �ϰ� �հ� �� �ʿ��ϸ� rule�� �����Ͽ� ó���Ѵ�.
	 * ������ �켱 �����̸� ù��° ���¼Ұ� ����� �켱�� �Ѵ�.
	 * ���� �м��ÿ� ���� ��� �м��Ǵ� ��찡 �����Ƿ�!!
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 24
	 */
	void sortFinally()
	{
		if( this.size() < 2 ) return;
		final int expLen = exp.length();
		Collections.sort(mCandidateList,
				new Comparator()
				{
					public int compare(Object arg0, Object arg1)
					{
						int ret = 0;

						MCandidate mc1 = (MCandidate) arg0, mc2 = (MCandidate) arg1;

						// �ѱ��ڷθ� ���� �Ǿ� �̷���� ���� �켱���� ���߾��ֱ�
						if( mc1.isComposedOfOnlyOneNouns() ) mc1.realDicLen = 0;
						if( mc2.isComposedOfOnlyOneNouns() ) mc2.realDicLen = 0;

						if( mc1.realDicLen < expLen && mc2.realDicLen < expLen ) {
							ret = mc1.spaceCnt - mc2.spaceCnt;
						}

						// �⺻ ����
						if( ret == 0 ) ret = mc1.compareTo(mc2);

						// Occams Razor
						if( ret == 0 ) ret = mc1.size() - mc2.size();

						// ª�� ���� �켱 ��Ģ
						if( ret == 0
								&& mc1.firstMorpheme != mc1.lastMorpheme
								&& mc2.firstMorpheme != mc2.lastMorpheme)
						{
							ret = mc1.lastMorpheme.string.length() - mc2.lastMorpheme.string.length();
							if( ret == 0 ) {
								ret = mc2.firstMorpheme.string.length() - mc1.firstMorpheme.string.length();
							}
						}
						return ret;
					}
				});
	}




	/**
	 * <pre>
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 28
	 * @param arg0
	 * @return
	 */
	public int compareTo(Object arg0)
	{
		MExpression me = (MExpression)arg0;
		return this.exp.compareTo(me.exp);
	}


	/**
	 * <pre>
	 * ����� ���⸦ �ϴ� head�� ��ȯ�Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 25
	 * @return
	 */
	String getCommonHead()
	{
		sort();
		MCandidate mc = get(0);
		int spaceCnt = mc.spaceCnt;
		if( spaceCnt < 1 ) return null;

		int size = size();
		for( int i = spaceCnt - 1; i > -1; i-- ) {
			String tempCommonHead = null, maxCommonHead = null;
			for( int j = 0; j < size; j++ ) {
				tempCommonHead = get(j).getExp(i);
				if( j == 0 ) {
					maxCommonHead = tempCommonHead;
				} else if( !maxCommonHead.equals(tempCommonHead) ) {
					maxCommonHead = null;
					break;
				}
			}
			// ���� ���ڿ��� ��ȯ
			if( maxCommonHead != null && maxCommonHead.length() > 1 ) {
				// �ٷ� �����̳� �����ڰ� �̵�Ͼ��̸� �� �м� �ǵ��� ��
				if( mc.isNRBeforeOrAfterIthSpace(i) ) {
					maxCommonHead = null;
					continue;
				}

				// ���� head�� ��ȯ
				return maxCommonHead;
			}
		}
		return null;
	}


	/**
	 * <pre>
	 * ���� ������ ���� ������ �������� Ȯ��
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 19
	 * @return
	 */
	boolean isComplete()
		throws Exception
	{
		return size() > 0 && get(0).isComplete();
	}


	/**
	 * <pre>
	 * ���ڸ� �м��� �ĺ����� Ȯ��
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 20
	 * @return
	 */
	boolean isOneEojeolCheckable()
	{
		if( size() == 1 ) {
			MCandidate mc = get(0);
			Morpheme mp = mc.firstMorpheme;
			if( mc.size() == 1
					&& (mp.charSet == Token.CHAR_SET_NUMBER
							|| mp.charSet == Token.CHAR_SET_ENGLISH
							|| mp.charSet == Token.CHAR_SET_COMBINED) )
			{
				return true;
			}
		}
		return false;
	}


	/**
	 * <pre>
	 * ���� �������� ��ġ�°� �����ϱ� ����
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 19
	 * @return
	 */
	MExpression copy()
	{
		MExpression copy = new MExpression(this.exp);
		for( int i = 0, stop = size(); i < stop; i++ ) {
			copy.mCandidateList.add(get(i).copy());
		}
		return copy;
	}
}
