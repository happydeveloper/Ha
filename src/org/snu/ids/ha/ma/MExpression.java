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
	 * 형태소의 표층형
	 * </pre>
	 * @since	2007. 6. 4
	 * @author	therocks
	 */
	String				exp				= null;

	/**
	 * <pre>
	 * 형태소의 표층형에 대해서 가능한 기분석 형태소 분석 결과들
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
	 * 하나의 기분석 형태소 분석 결과를 저장한다.
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
	 * 기분석 후보 하나를 가진 표현형 생성
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
	 * 표층형에 대한 기분석 형태소 분석 결과를 추가한다.
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
	 * i번째 후보를 삭제한다.
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
	 * 기분석 후보 결과를 포함하고 있는지 확인한다.
	 * MCandidate.equals(Object obj) 함수를 over riding해서 사용
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
	 * 기분석 후보의 총 개수를 반환한다.
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
	 * i번재에 저장되어 있는 기분석 후보를 반환한다.
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
	 * 각 후보들의 분석 결과에 대한 index(offset)정보를 설정해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2008. 03. 31
	 * @param index	시작 offset
	 */
	void setIndex(int index)
	{
		for(int i=0, size = size(); i < size; i++) {
			get(i).setIndex(index);
		}
	}
	
	
	/**
	 * <pre>
	 * 표층형 사전 정보를 문자열로 반환한다.
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
	 * mCandidate의 encoding된 문자열을 읽어들인다.
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
	 * 덧붙는 mExp에 대해서 실재 가능한 형태소 분석 결과를 생성하여 반환한다.
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
	 * 우선 순위가 떨어지는 것들은 후보 분석 결과에서 제외해버림
	 * 성능 문제로~
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

		// 최적 후보 확인
		sort();
		MCandidate mc = get(0);

		// 완전히 종결되지 않으면 완전히 종결된 어휘를 남겨둔다.
		boolean uncomplete = mc.candDicLen > 0 || expLen > mc.getDicLenWithCand();

		// 최적후보의 실 사전어 길이 확인
		maxDicLen = mc.realDicLen;
		maxSpaceCnt = mc.spaceCnt;
		if( maxDicLen == expLen ) {
			// 마지막이 한글자 조사일 때에는 띄어쓰기에 의한 prunning을 할 때 띄어쓰기 하나를 더 허용해준다.
			// 다음 한글자로 인해서 더 적합한 결과가 나올 수 있는데 이것이 잘리는 것을 방지하기 위함
			if( mc.lastMorpheme.isSufficientByAnd(HgEncoded.JO)
					&& mc.lastMorpheme.string.length() == 1 )
			{
				maxSpaceCnt++;
			}
		}
		// 종결되지 않은 것이 있으면 prunning하지 않는다.
		else if( !uncomplete && size < PRUNE_SIZE ) {
			return;
		}

		// 한글자 명사로만 이루어졌을 때는 prunning하지 않는다.
		if( mc.isComposedOfOnlyOneNouns() ) return;

		// 이외 후보 확인
		int pruneIdx = 1;
		for( int stop = mCandidateList.size(); pruneIdx < stop; ) {
			mc = (MCandidate) mCandidateList.get(pruneIdx);

			// 사전어 길이 확인
			tempDicLen = mc.getDicLenWithCand();

			// 결과가 너무 많이 나오면 강제로 prunning해버린다.
			// 완결되지 않았으면
			if( uncomplete && mc.getDicLenOnlyCand() == 0 && pruneIdx < PRUNE_SIZE ) {
				pruneIdx++;
				continue;
			}

			// 사전어 길이가 짧으면 삭제
			if( tempDicLen < maxDicLen ) break;

			// 띄어쓰기가 많은 것은 제외
			if( (maxSpaceCnt > 0 || stop > PRUNE_SIZE) && mc.spaceCnt > maxSpaceCnt ) break;

			pruneIdx++;
		}

		// 순위를 벗어난 것들을 삭제해줌
		for( int i = pruneIdx, stop = mCandidateList.size(); i < stop; i++ ) {
			// 2008-03-18: 완전히 사전어로 이루어지지 않은 경우 마지막 미등록어를 남겨두어 추후 이상한 결과를 만들어 내는 것을 방지
			if( uncomplete && i == stop - 1 && ((MCandidate) mCandidateList.get(pruneIdx)).realDicLen == 0 ) break;
			mCandidateList.remove(pruneIdx);
		}
	}


	/**
	 * <pre>
	 * 앞의 어절의 후보 결과들과 성립되는 것이 없으면 삭제한다.
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
	 * 앞의 기분석 후보들 중에 가능한 결과만 남겨둔다.
	 * prune을 강하게 하면 속도는 빨라지지만, 분석 오류가 발생할 수 있다.
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

				// 띄어쓰기 포함 결합 가능 확인
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
	 * 앞쪽 문자열을 headStr로 하고, 뒷쪽 문자열을 tailStr로 하는 분리된 기분석 후보를 생성하여 반환한다.
	 * 앞, 뒤를 자르는 위치는 divideIdx번째 띄어쓰기로 한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 25
	 * @param headStr
	 * @param headIndex	head 부분의 token index시작
	 * @param tailStr
	 * @param tailIndex	tail 부분의 token index시작
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
	 * 후보 분석 결과들을 합쳐준다.
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
	 * 띄어쓰기를 기준으로 각 표현형을 분해해서 반환해준다.
	 * 띄어쓰기가 제대로 수행되지 않을 때에는 표현형을 기준으로 그대로 반환해준다.
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

		// 기준 설정
		MCandidate mc = this.get(0);
		List splitedMCList = mc.split();
		int splitedMCSize = splitedMCList.size();
		for( int i = 0; i < splitedMCSize; i++ ) {
			ret.add(new MExpression((MCandidate) splitedMCList.get(i)));
		}

		int size = size();

		// 이후 동일한 띄어쓰기 보이는 후보 저장
		if( size > 1 ) {
			String preExpWithSpace = mc.geExpStringWithSpace();
			for( int i = 1; i < size; i++ ) {
				mc = get(i);
				// 동일한 띄어쓰기인지 확인
				String curExpWithSpace = mc.geExpStringWithSpace();
				if( !preExpWithSpace.equals(curExpWithSpace) ) break;

				// 쪼개서 저장
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
	 * 띄어쓰기가
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
	 * 현재 후보를 점수에 따라서 정렬한다.
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
	 * 이전 분석 결과를 확인하여, 좀더 적합한 것이 우선 순위로 나올 수 있도록 수정
	 * 띄어쓰기 후에도 선호 정보를 적용할 수 있도록
	 * Collections.sort(preME.mCandidateList); 추가함!!
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
				// 체언이 연속되면 우선 순위 낮추어줌
//				if( preMC.isHavingCondition(Condition.COND_NUM_NN)
//						&& curMC.firstMorpheme.isSufficientByOr(HgEncoded.OR_NOUN_CLASSES) )
//				{
//					curMC.bonus--;
//				}
				// 관형사가 역속되면 뒤에 위치한 관형사 점수 낮추어줌
				if( preMC.isHavingCondition(Condition.COND_NUM_DT)
						&& curMC.firstMorpheme.isSufficientByOr(HgEncoded.OR_VV_AJ) )
				{
					curMC.bonus--;
				}
				// 선호 정보를 가졌는지 확인
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
	 * 할거 다 하고 먼가 더 필요하면 rule을 적용하여 처리한다.
	 * 동일한 우선 순위이면 첫번째 형태소가 긴것을 우선시 한다.
	 * 보통 분석시에 앞이 길게 분석되는 경우가 많으므로!!
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

						// 한글자로만 띄어쓰기 되어 이루어진 말의 우선순위 낮추어주기
						if( mc1.isComposedOfOnlyOneNouns() ) mc1.realDicLen = 0;
						if( mc2.isComposedOfOnlyOneNouns() ) mc2.realDicLen = 0;

						if( mc1.realDicLen < expLen && mc2.realDicLen < expLen ) {
							ret = mc1.spaceCnt - mc2.spaceCnt;
						}

						// 기본 정렬
						if( ret == 0 ) ret = mc1.compareTo(mc2);

						// Occams Razor
						if( ret == 0 ) ret = mc1.size() - mc2.size();

						// 짧은 끝말 우선 원칙
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
	 * 공통된 띄어쓰기를 하는 head를 반환한다.
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
			// 공통 문자열을 반환
			if( maxCommonHead != null && maxCommonHead.length() > 1 ) {
				// 바로 직전이나 다음자가 미등록어이면 더 분석 되도록 둠
				if( mc.isNRBeforeOrAfterIthSpace(i) ) {
					maxCommonHead = null;
					continue;
				}

				// 공통 head로 반환
				return maxCommonHead;
			}
		}
		return null;
	}


	/**
	 * <pre>
	 * 띄어쓰기 오류가 없는 완전한 문장인지 확인
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
	 * 숫자를 분석한 후보인지 확인
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
	 * 사전 정보에서 겹치는걸 방지하기 위함
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
