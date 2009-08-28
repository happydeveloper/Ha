/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 6. 4
 */
package org.snu.ids.ha.ma;


import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.snu.ids.ha.constants.Condition;
import org.snu.ids.ha.constants.HgClass;
import org.snu.ids.ha.constants.HgEncoded;
import org.snu.ids.ha.util.Hangul;
import org.snu.ids.ha.util.Util;


/**
 * <pre>
 * 표현형에 대한 하나의 형태소 분석 후보를 저장한다.
 * 분석 후보 형태소 목록에 덧붙여 접속 조건, 합성 조건 등의 부가 정보를 저장한다.
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 4
 */
public class MCandidate
	extends MorphemeList
	implements Comparable
{
	long		appendableHgClassEncoded	= 0;	// 접속 가능한 품사 정보
	long		havingConditionEncoded		= 0;	// 현재 후보가 가진 접속 조건
	long		checkingConditionEncoded	= 0;	// 접속할 때 확인해야 하는 조건
	long		preferedConditionEncoded	= 0;	// 이전에 나오는 것이 선호되는 품사
													// 관형어 + 체언
													// 부사어 + 용언
													// 부사 + 관형사
													// 부사 + 부사

	/**
	 * <pre>
	 * 후보간의 Scoring을 위해서 완전한 사전어와, 후보사전어를 구분하여 사전어 길이 판단
	 *  - 완전 사전어 : 완전히 한 단어로 인식된 사전어
	 *    -> 예) 체언, 체언과 결합된 조사, 어간과 어미가 완전히 결합된 단어
	 *  - 후보 사전어 : 사전에서 찾아졌지만, 그 완전성이 미비한 것
	 *    -> 예) 체언과 않은 조사, 결합하지 않은 어미, 어간, 체언과 결합하지 않은 서술격 조사(활용 포함)
	 * 점수 계산
	 *  - MCandidate.calculateScore()
	 * 최종적으로 Sorting할 때에는 MExpression.sortFinally()에 의해서 정렬 순서 재조정 됨
	 * </pre>
	 */
	byte				realDicLen					= 0;	// 실재 사전어로 취급할 수 있는 완전어의 길이
	byte				candDicLen					= 0;	// 후보 사전어의 길이
	byte				spaceCnt					= 0;	// 추가되어야 할 띄어쓰기의 수
	byte				bonus						= 0;	// 선호 조건에 의한 BONUS
	byte				size						= 0;	// 분석된 형태소의 수 (Occams Razor를 적용하기 위함) 사전어 계산시에 사용
	byte				asize						= 0;	// 접속 가능한 앞뒤 MCandidate의 갯수
	int					score						= 0;	// 계산된 점수
	/**
	 * <pre>
	 * 중복을 피하기 위해서 hashCode를 사용함.
	 * MCandidate.getEncodedString().hashCode()을 사용하여 계산
	 * hashCode를 중복하여 계산하는 것을 피하기 위해서 구성이 변하지 않으면 이미 계산된 hashCode를 사용하도록 함
	 * <중요!!> 새로 계산되어야 하는 시점을 잘 파악하여 MCandidate.calculateHashCode() 함수를 호출해주어야 함!!
	 * </pre>
	 * @since	2007. 7. 26
	 * @author	therocks
	 */
	int					hashCode					= 0;	// 중복을 피하기 위해 hashCode를 미리 계산해두기 위함
	/**
	 * <pre>
	 * 분리된 표현형을 저장하기 위함
	 * 띄어쓰기 기준으로 각각의 표현형을 나타내는 문자열을 저장함
	 * 띄어쓰기가 되어야 하는 시점에 공백 문자열을 추가하는 방법으로 띄어쓰기 구현
	 * MCandidate.derive() 함수에서 띄어쓰기 처리
	 * </pre>
	 * @since	2007. 7. 26
	 * @author	therocks
	 */
	private ArrayList	expList						= null; // 띄어쓰기 된 표층형 리스트


	/**
	 * <pre>
	 * default constructor 기본 정보들을 설정해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 */
	private MCandidate()
	{
		super();
		expList = new ArrayList();
	}


	/**
	 * <pre>
	 * 확인되지 않은 어휘에 대한 기분석 결과 생성
	 * 미등록 명사에 대한 후보 생성시에만 사용됨~
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param string
	 * @param index
	 */
	MCandidate(String string, int index)
		throws Exception
	{
		this();
		add(new Morpheme(string, index));
		initCondition(string);
		expList.add(string);
		calculateHashCode();
	}


	/**
	 * <pre>
	 * 활용하지 않는 어휘에 대한 기분석 결과 생성
	 * Dictionary.loadFixed()에서만 사용됨
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param string
	 * @param hgClass
	 * @param compType
	 */
	MCandidate(String string, String hgClass, String compType)
		throws Exception
	{
		this();
		add(new Morpheme(string, hgClass, compType));
		initCondition(string);
		expList.add(string);
		realDicLen = (byte)string.length();
	}


	/**
	 * <pre>
	 * 활용하는 동사, 형용사에 대한 어간 기분석 결과 생성
	 * Dictionary.loadVerb()에서만 사용됨!!
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 22
	 * @param string
	 * @param hgClass
	 * @throws Exception
	 */
	MCandidate(String string, String hgClass)
		throws Exception
	{
		this();
		add(new Morpheme(string, hgClass, "S"));
		initCondition(string);
		expList.add(string);
	}


	/**
	 * <pre>
	 * 한글 이외의 Token정보를 받아들여서, Token을 형태소의 적합한 부분으로 설정해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param token
	 */
	MCandidate(Token token)
		throws Exception
	{
		this();
		// 미등록어 분석 결과 추가
		add(new Morpheme(token));
		realDicLen = (byte)token.string.length();
		expList.add(token.string);
		initCondition();
	}


	/**
	 * <pre>
	 * 자모 조건을 생성해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 */
	private void initCondition(String string)
	{
		// 음운 정보 초기화
		initPhonemeCondition(string);
		// 체언에 대해서는 조사와 접속시의 정보를 설정해주어야 함
		if( lastMorpheme.isSufficientByOr(HgEncoded.OR_NOUN_CLASSES) ) {
			addHavingCondition(Condition.COND_NUM_NN);
			addPreferedCondition(Condition.COND_NUM_DT);
			addAppendableHgClass(HgEncoded.PF);
		}
		// 관형어
		else if( lastMorpheme.isSufficientByOr(HgEncoded.OR_DT_CLASSES)) {
			addHavingCondition(Condition.COND_NUM_DT);
			addPreferedCondition(Condition.COND_NUM_AD);
		}
		// 부사어
		else if( lastMorpheme.isSufficientByAnd(HgEncoded.AD) ) {
			addHavingCondition(Condition.COND_NUM_AD);
			addPreferedCondition(Condition.COND_NUM_AD);
		}
	}


	/**
	 * <pre>
	 * 특수문자 영문자에 대한 음운 정보 및 선호 조건 정보 설정
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 26
	 * @throws Exception
	 */
	private void initCondition()
		throws Exception
	{
		if( lastMorpheme.isCharSetOf(Morpheme.CHAR_SET_ENGLISH) ) {
			addHavingCondition(Condition.COND_NUM_ENG);
		}
		addHavingCondition(Condition.COND_NUM_NR_SET);
		addHavingCondition(Condition.COND_NUM_NN);
		addPreferedCondition(Condition.COND_NUM_DT);
	}


	/**
	 * <pre>
	 * 음운 정보를 초기화 한다. (모음,자음,양성,음성) 정보 설정
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 26
	 * @param string
	 */
	private void initPhonemeCondition(String string)
	{
		Hangul hg = Hangul.split(string.charAt(string.length() - 1));
		if( hg.hasJong() ) {
			addHavingCondition(Condition.COND_NUM_JAEUM);
			if( !lastMorpheme.isSufficientByOr(HgEncoded.OR_EOMI_CLASSES) && hg.jong == 'ㄹ' ) {
				addHavingCondition(Condition.COND_NUM_LIEUL);
			}
		} else {
			addHavingCondition(Condition.COND_NUM_MOEUM);
		}
		if( HgClass.MO_POSITIVE_SET.contains(hg.jung) ) {
			addHavingCondition(Condition.COND_NUM_YANGSEONG);
		} else {
			addHavingCondition(Condition.COND_NUM_EUMSEONG);
		}
	}


	/**
	 * <pre>
	 * 복사본을 만들어서 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 5
	 * @return
	 */
	MCandidate copy()
	{
		MCandidate clone = new MCandidate();
		clone.addAll(this);
		clone.appendableHgClassEncoded = this.appendableHgClassEncoded;
		clone.havingConditionEncoded = this.havingConditionEncoded;
		clone.checkingConditionEncoded = this.checkingConditionEncoded;
		clone.preferedConditionEncoded = this.preferedConditionEncoded;
		clone.candDicLen = this.candDicLen;
		clone.realDicLen = this.realDicLen;
		clone.spaceCnt = this.spaceCnt;
		clone.size = this.size;
		clone.asize = this.asize;
		clone.bonus = this.bonus;
		clone.hashCode = this.hashCode;
		clone.expList.addAll(this.expList);
		return clone;
	}
	
	
	/**
	 * <pre>
	 * 각 후보들의 분석 결과에 대한 index(offset)정보를 설정해준다.
	 * 분석 결과가 실재 문장보다 길어질 수 있으므로 offset정보는 정확히 일치하지는 않을 수도 있다.
	 * 예를 들어 준말의 경우 원형으로 복원되기 때문에 길어진다.
	 * "하0/길1/ 2/바3/란4/다5/" -> 하0/+기1/+를2/ 3/바라4/+ㄴ다6/
	 * </pre>
	 * @author	therocks
	 * @since	2008. 03. 31
	 * @param index
	 */
	void setIndex(int index)
	{
		Morpheme mp = null;
		int offset = 0;
		for( int i = 0, size = size(); i < size; i++ ) {
			mp = get(i);
			mp.setIndex(index + offset);
			offset += mp.string.length();
		}
	}


	/**
	 * <pre>
	 * 형태소 정보를 모두 붙여준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param mpList
	 */
	void addAll(MorphemeList mpList)
	{
		for( int i = 0, stop = mpList.size(); i < stop; i++ ) {
			add(mpList.get(i).copy());
		}
	}


	/**
	 * <pre>
	 * 접속 가능한 품사정보를 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param hgClass
	 */
	void addAppendableHgClass(String hgClass)
	{
		addAppendableHgClass(HgClass.getHgClassNum(hgClass));
	}


	/**
	 * <pre>
	 * 접속 가능한 품사정보를 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param hgClassNum
	 */
	void addAppendableHgClass(long hgClassNum)
	{
		appendableHgClassEncoded |= hgClassNum;
	}


	/**
	 * <pre>
	 * 주어진 품사가 접속 가능 품사에 포함되어 있는지 확인
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param hgClassNum
	 * @return
	 */
	boolean isAppendableHgClass(long hgClassNum)
	{
		return (appendableHgClassEncoded & hgClassNum) > 0;
	}


	/**
	 * <pre>
	 * 주어진 품사가 접속 가능 품사에 포함되어 있는지 확인
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param hgClass
	 * @return
	 */
	boolean isAppendableHgClass(String hgClass)
	{
		return isAppendableHgClass(HgClass.getHgClassNum(hgClass));
	}


	/**
	 * <pre>
	 * 접속 가능한 품사정보를 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param hgClasses
	 */
	void addAppendableHgClass(String[] hgClasses)
	{
		for( int i = 0, stop = hgClasses.length; i < stop; i++ ) {
			addAppendableHgClass(hgClasses[i]);
		}
	}


	/**
	 * <pre>
	 * 접속 가능한 품사정보를 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param hgClassNums
	 */
	void addAppendableHgClass(long[] hgClassNums)
	{
		for( int i = 0, stop = hgClassNums.length; i < stop; i++ ) {
			addAppendableHgClass(hgClassNums[i]);
		}
	}


	/**
	 * <pre>
	 * 기분석 결과가 가지는 접속 조건을 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param condition
	 */
	void addHavingCondition(String condition)
	{
		addHavingCondition(Condition.getConditionNum(condition));
	}


	/**
	 * <pre>
	 * 기분석 결과가 가지는 접속 조건을 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param conditionNum
	 */
	void addHavingCondition(long conditionNum)
	{
		havingConditionEncoded |= conditionNum;
	}


	/**
	 * <pre>
	 * 주어진 조건을 가지고 있는지 확인
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param conditionNum
	 * @return
	 */
	boolean isHavingCondition(long conditionNum)
	{
		return (havingConditionEncoded & conditionNum) > 0;
	}


	/**
	 * <pre>
	 * 주어진 조건을 가지고 있는지 확인
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param condition
	 * @return
	 */
	boolean isHavingCondition(String condition)
	{
		return isHavingCondition(Condition.getConditionNum(condition));
	}


	/**
	 * <pre>
	 * 기분석 결과가 가지는 접속 조건을 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param conditions
	 */
	void addHavingCondition(String[] conditions)
	{
		for( int i = 0, stop = conditions.length; i < stop; i++ ) {
			addHavingCondition(conditions[i]);
		}
	}


	/**
	 * <pre>
	 * 기분석 결과가 가지는 접속 조건을 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param conditionNums
	 */
	void addHavingCondition(long[] conditionNums)
	{
		for( int i = 0, stop = conditionNums.length; i < stop; i++ ) {
			addHavingCondition(conditionNums[i]);
		}
	}


	/**
	 * <pre>
	 * 후보 기분석 결과가 가진 조건 정보를 삭제한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 */
	void clearHavingCondition()
	{
		this.havingConditionEncoded = 0;
	}


	/**
	 * <pre>
	 * 기분석 결과가 접속시 확인해야하는 조건 정보를 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param condition
	 */
	void addCheckingCondition(String condition)
	{
		addCheckingCondition(Condition.getConditionNum(condition));
	}


	/**
	 * <pre>
	 * 기분석 결과가 접속시 확인해야하는 조건 정보를 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param conditionNum
	 */
	void addCheckingCondition(long conditionNum)
	{
		checkingConditionEncoded |= conditionNum;
	}


	/**
	 * <pre>
	 * 주어진 조건을 확인 조건으로 가지고 있는지 확인
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param conditionNum
	 * @return
	 */
	boolean isCheckingCondition(long conditionNum)
	{
		return (checkingConditionEncoded & conditionNum) > 0;
	}


	/**
	 * <pre>
	 * 주어진 조건을 확인 조건으로 가지고 있는지 확인
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param conditionNum
	 * @return
	 */
	boolean isCheckingCondition(String conditionNum)
	{
		return isCheckingCondition(Condition.getConditionNum(conditionNum));
	}


	/**
	 * <pre>
	 * 기분석 결과가 접속시 확인해야하는 조건 정보를 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param conditions
	 */
	void addCheckingCondition(String[] conditions)
	{
		for( int i = 0, stop = conditions.length; i < stop; i++ ) {
			addCheckingCondition(conditions[i]);
		}
	}


	/**
	 * <pre>
	 * 기분석 결과가 접속시 확인해야하는 조건 정보를 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param conditions
	 */
	void addCheckingCondition(long[] conditioNums)
	{
		for( int i = 0, stop = conditioNums.length; i < stop; i++ ) {
			addCheckingCondition(conditioNums[i]);
		}
	}


	/**
	 * <pre>
	 * 띄어쓰기가 가능할 때 추가될 수 있는 품사 정보를 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param hgClass
	 */
	void addPreferedCondition(String condition)
	{
		addPreferedCondition(Condition.getConditionNum(condition));
	}


	/**
	 * <pre>
	 * 띄어쓰기가 가능할 때 추가될 수 있는 품사 정보를 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param conditionNum
	 */
	void addPreferedCondition(long conditionNum)
	{
		preferedConditionEncoded |= conditionNum;
	}


	/**
	 * <pre>
	 * 주어진 품사가 뛰어쓰기 포함해서 접속 가능 품사에 포함되어 있는지 확인
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param conditionNum
	 * @return
	 */
	boolean isPreferedCondition(long conditionNum)
	{
		return (preferedConditionEncoded & conditionNum) > 0;
	}


	/**
	 * <pre>
	 * 주어진 품사가 뛰어쓰기 포함해서 접속 가능 품사에 포함되어 있는지 확인
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param condition
	 * @return
	 */
	boolean isPreferedCondition(String condition)
	{
		return isPreferedCondition(Condition.getConditionNum(condition));
	}


	/**
	 * <pre>
	 * 띄어쓰기가 가능할 때 추가될 수 있는 품사 정보를 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param conditions
	 */
	void addPreferedCondition(String[] conditions)
	{
		for( int i = 0, stop = conditions.length; i < stop; i++ ) {
			addPreferedCondition(conditions[i]);
		}
	}


	/**
	 * <pre>
	 * 띄어쓰기가 가능할 때 추가될 수 있는 품사 정보를 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param conditionNums
	 */
	void addPreferedCondition(long[] conditionNums)
	{
		for( int i = 0, stop = conditionNums.length; i < stop; i++ ) {
			addPreferedCondition(conditionNums[i]);
		}
	}


	/**
	 * <pre>
	 * 품사외 조건을 만족할 수 있는지 확인함
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param conditionEncoded
	 * @return
	 */
	boolean isConditionAppendable(long conditionEncoded)
	{
		return (havingConditionEncoded & conditionEncoded) == conditionEncoded;
	}


	/**
	 * <pre>
	 * 표현형을 설정해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 20
	 * @param exp
	 */
	void setExp(String exp)
	{
		expList.clear();
		expList.add(exp);
	}


	/**
	 * <pre>
	 * 표현형 문자열을 표현형/표현형 식으로 /로 연결해서 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 20
	 * @return
	 */
	String getExp()
	{
		StringBuffer sb = new StringBuffer();
		for( int i = 0, stop = expList.size(); i < stop; i++ ) {
			sb.append(expList.get(i));
		}
		return sb.toString();
	}


	/**
	 * <pre>
	 * toIdx에 해당하는 순서까지의 표현형을 가져온다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 25
	 * @param toIdx
	 * @return
	 */
	String getExp(int toIdx)
	{
		StringBuffer sb = new StringBuffer();
		for( int i = 0, stop = Math.min(expList.size(), toIdx + 1); i < stop; i++ ) {
			sb.append(expList.get(i));
		}
		return sb.toString();
	}


	/**
	 * <pre>
	 * 띄어쓰기를 /로 연결해서 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 24
	 * @return
	 */
	String geExpStringWithSpace()
	{
		StringBuffer sb = new StringBuffer();
		for( int i = 0, stop = expList.size(); i < stop; i++ ) {
			if( i > 0 ) sb.append(" ");
			sb.append(expList.get(i));
		}
		return sb.toString();
	}


	/**
	 * <pre>
	 * 현재의 기분석 결과에 후보가 접속 가능한지를 확인함!!
	 * 어미, 어간, 접미사 등일 때 strict하게 확인함!!
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param addMCandidate
	 * @return
	 */
	boolean checkBasicAppendingCondition(MCandidate addMCandidate)
	{
		boolean ret = isConditionAppendable(addMCandidate.checkingConditionEncoded);
		if( ret ) {
			// 조사의 경우 확인 격조사, 연결 조사는 앞에 조사가 오지 못한다.
			if( addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.JO)
					&& lastMorpheme.isSufficientByAnd(HgEncoded.JO) )
			{
				// 관형격 조사 앞에는 다양한 조사들이 올 수 있다.
				if( (addMCandidate.firstMorpheme.isSufficientByOr(HgEncoded.OR_CL_CN))
						&& !addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.JO_CL_DT)
						&& !lastMorpheme.isSufficientByAnd(HgEncoded.TYPE_SU)
						&& !lastMorpheme.isSufficientByAnd(HgEncoded.JO_CL_AD) ) {
					ret = false;
				}
			}
			// 조사 앞에 특수 문자는 괄호 형만 올 수 있다. (')', ']', '}')
//			else if( lastMorpheme.isSufficientByAnd(HgEncoded.SY)
//					&& addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.JO)
//					&& !HgEncoded.RIGHT_PARENTHESIS_SET.contains(lastMorpheme.string) )
//			{
//				ret = false;
//			}
			// 서술격 조사 앞에는 조사일 경우 보조사만 올 수 있음
			else if ( addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.CP)
					&& lastMorpheme.isSufficientByAnd(HgEncoded.JO)
					&& !lastMorpheme.isSufficientByAnd(HgEncoded.TYPE_SU)
					&& !lastMorpheme.isSufficientByAnd(HgEncoded.JO_CL_AD) )
			{
				ret = false;
			}
			// 서술격 조사 앞에 오는 어미는 명사형만 올 수 있음
			else if ( addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.CP)
					&& lastMorpheme.isSufficientByAnd(HgEncoded.EM)
					&& !lastMorpheme.isSufficientByAnd(HgEncoded.EM_FM_NN) )
			{
				ret = false;
			}
			// 서술격 조사 앞에는 접속 부사가 올 수 없음
			else if ( addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.CP)
					&& lastMorpheme.isSufficientByAnd(HgEncoded.AD_CN) )
			{
				ret = false;
			}
			// 보조족 연결 어미 다음에는 보조 동사 or 형용사가 와야함
			else if(this.lastMorpheme.isSufficientByAnd(HgEncoded.EM_SU)
					&& !addMCandidate.firstMorpheme.isSufficientByOr(HgEncoded.OR_VV_AJ_EM_JO) )
			{
				ret = false;
			}
			// 부사격 조사 앞에는 어말 어미일 경우 명사형 전성어미만 와야함
			else if ( addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.JO_CL_AD)
					&& lastMorpheme.isSufficientByAnd(HgEncoded.EM)
					&& !lastMorpheme.isSufficientByAnd(HgEncoded.EM_FM_NN) )
			{
				ret = false;
			}
			// 전성형에는 존칭형 어미가 연결될 수 없음
			else if( addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.EM_ED_HR)
					&& this.lastMorpheme.isSufficientByAnd(HgEncoded.EM_FM) )
			{
				ret = false;
			}
			// 어말 어미와 어말 어미를 합쳐줄 때 앞이 'ㄴ', 'ㄹ', 'ㅁ'등이 있을 때에는
			// 확인 조건에도 'ㄴ', 'ㄹ', 'ㅁ'등이 있어야 한다.
			else if( lastMorpheme.isSufficientByAnd(HgEncoded.EM)
					&& addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.EM)
					&& (this.havingConditionEncoded & Condition.COND_NUM_JA_SET) > 0
					&& (this.havingConditionEncoded
							& addMCandidate.checkingConditionEncoded
							& Condition.COND_NUM_JA_SET) == 0 )
			{
				ret = false;
			}
			// 어말 어미를 합쳐줄 때 앞이 'ㅂ'이 있을 때에는
			// 확인 조건에도 'ㅂ'이 있어야 한다.
			else if( addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.EM)
					&& (this.havingConditionEncoded & Condition.COND_NUM_BIEUB) > 0
					&& (this.havingConditionEncoded
							& addMCandidate.checkingConditionEncoded
							& Condition.COND_NUM_BIEUB) == 0 )
			{
				ret = false;
			}
			// 어말 어미를 합쳐줄 때 앞이 'ㄹ', 'ㅂ', 'ㅎ' 탈락조건이 있을 때에는
			// 확인 조건에도 'ㄹ', 'ㅂ', 'ㅎ' 탈락조건이 있어야 한다.
			else if( addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.EM)
					&& (this.havingConditionEncoded & Condition.COND_NUM_MINUS_JA_SET) > 0
					&& (this.havingConditionEncoded
							& addMCandidate.checkingConditionEncoded
							& Condition.COND_NUM_MINUS_JA_SET) == 0 )
			{
				ret = false;
			}
			// 기본형에 포함되었는지 확인하여 기본형에 포함되면 접속하지 않도록 함.
			// * 예) 사랑+하 : '사랑하다'에 의해 '사랑하'가 이미 기본형으로 등록되어 있기 때문에 결합하지 않도록 함
			else if( addMCandidate.firstMorpheme.isSufficientByOr(HgEncoded.OR_VV_AJ) 
					&& lastMorpheme.isSufficientByAnd(HgEncoded.NN) 
					&& Dictionary.getInstance().contains(lastMorpheme.string + addMCandidate.firstMorpheme.string) )
			{
				ret = false;
			}
			// 'ㅅ'
			else if( (this.havingConditionEncoded & Condition.COND_NUM_MINUS_SIOT) > 0
					&& (!addMCandidate.firstMorpheme.isSufficientByOr(HgEncoded.OR_EOMI_CLASSES)
					|| !addMCandidate.isHavingCondition(Condition.COND_NUM_YIEUNG)) )
			{
				ret = false;
			}
		}

		return ret;
	}


	/**
	 * <pre>
	 * 접속 가능한 조건인지 확인
	 * 접미사 역할을 하는 명사 앞에는 한글자 단어가 붙지 않는다고 가정
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 27
	 * @param addMCandidate
	 * @return
	 */
	boolean isAppendable(MCandidate addMCandidate)
	{
		boolean appendable = addMCandidate.isAppendableHgClass(lastMorpheme.hgEncoded);
		if( appendable ) {
			if( addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.NN_FM_NN)
					&& lastMorpheme.string.length() == 1)
			{
				appendable = false;
			}
		}
		return appendable;
	}


	/**
	 * <pre>
	 * 띄어쓰기가 되었을 때 연결이 가능한지 확인
	 * 마지막이 VV, AJ, CP등 활용의 시작이면, 다음은 반드시 선어말 혹은 어말 어미가 와야 함!!
	 *
	 * 2007-07-20 수정
	 *
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param addMCandidate
	 * @return
	 */
	boolean isAppendableAllowingSpace(MCandidate addMCandidate)
	{
		boolean appendable = false;

		// 앞뒤 특수 문자는 모두 연결 가능
		if( this.lastMorpheme.isSufficientByAnd(HgEncoded.SY)
				|| addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.SY) )
		{
			return true;
		}

		// 띄어쓰기 불가능한 경우에는 기본 접속 조건 확인
		if( (appendable = this.isAppendable(addMCandidate))
				|| lastMorpheme.isSufficientByOr(HgEncoded.OR_PRE_STRICT_CHECK_CLASSES)
				|| addMCandidate.firstMorpheme.isSufficientByOr(HgEncoded.OR_POST_STRICT_CHECK_CLASSES)
				|| addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.NN_FM_NN) )
		{
			appendable = appendable && checkBasicAppendingCondition(addMCandidate);
		}
		// 띄어쓰기 가능한 경우에도 선후 관계 불가능한 것 선별
		else if(lastMorpheme.isSufficientByOr(HgEncoded.OR_DEFAULT_PRE_CLASS)){
			appendable = true;

			// 어간 다음에는 어미, 선어말 어미가 와야함
			if( lastMorpheme.isSufficientByOr(HgEncoded.OR_EOGAN_CLASSES) &&
					!addMCandidate.firstMorpheme.isSufficientByOr(HgEncoded.OR_EOMI_CLASSES))
			{
				appendable = false;
			}
			// 관형어 다음에는 체언만 온다.
			// 연속된 관형어는 허용해줌 (연결 조사등을 생략한 것이라 생각)
			else if( this.isHavingCondition(Condition.COND_NUM_DT) ) {
				if( addMCandidate.firstMorpheme.isSufficientByOr(HgEncoded.OR_EOGAN_CLASSES)
						&& !addMCandidate.isHavingCondition(Condition.COND_NUM_DT))
				{
					appendable = false;
				}
			}
			// 
		}

		// 조사 앞에는 특수 문자가 올 수도 있음
		// "색상(핑크색)도 좋아요" 과 같이 괄호를 이용하여 조사를 나중에 사용할 수도 있음
		if( !appendable ) {
			appendable = lastMorpheme.isSufficientByAnd(HgEncoded.SY)
						&& addMCandidate.lastMorpheme.isSufficientByAnd(HgEncoded.JO);
		}

		return appendable;
	}


	/**
	 * <pre>
	 * 현재의 후보에 부착할 후보를 생성하여 반환해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param addMCandidate
	 * @return
	 */
	MCandidate derive(MCandidate addMCandidate)
	{
		MCandidate newMCandidate = null;

		// 활용되는 것은 붙여질 수 있는 경우에만 확장해준다.
		if( isAppendableAllowingSpace(addMCandidate) ) {
			newMCandidate = new MCandidate();
			newMCandidate.addAll(this);
			newMCandidate.addAll(addMCandidate);
			newMCandidate.expList.addAll(this.expList);
			newMCandidate.spaceCnt = (byte) (this.spaceCnt + addMCandidate.spaceCnt);
			newMCandidate.bonus = (byte) (this.bonus + addMCandidate.bonus);
			newMCandidate.appendableHgClassEncoded = this.appendableHgClassEncoded;
			newMCandidate.preferedConditionEncoded = this.preferedConditionEncoded;
			newMCandidate.checkingConditionEncoded = this.checkingConditionEncoded;
			newMCandidate.havingConditionEncoded = addMCandidate.havingConditionEncoded;
			byte newBonus = (byte) Util.bitCount(this.havingConditionEncoded & addMCandidate.preferedConditionEncoded);

			// 접속 가능 확인
			boolean isAppendable = this.isAppendable(addMCandidate);
			// 앞 조건에 의한 접속 확인 -- 어휘 별로 따로 설정된 사전
			if( !isAppendable ) {
				if( (isHavingCondition(Condition.COND_NUM_VV_A)
						&& addMCandidate.firstMorpheme.isSufficientByOr(HgEncoded.OR_VV_AJ))
					|| (isHavingCondition(Condition.COND_NUM_NN_A)
						&& addMCandidate.firstMorpheme.isSufficientByOr(HgEncoded.NN)) )
				{
					isAppendable = true;
				}
			}
			

			// 접속 불가능일 때에는 띄어쓰기 해줌
			if( !isAppendable ) {
				// 띄어쓰기 숫자 증가
				newMCandidate.spaceCnt++;

				// 접속 불가능일 때에만 선호 점수 부여
				newMCandidate.bonus += newBonus;

				// 띄어쓰기, 어절 표현형 구분
				newMCandidate.list.add(this.size(), new MorphemeSpace(
						addMCandidate.appendableHgClassEncoded,
						this.havingConditionEncoded,
						addMCandidate.checkingConditionEncoded,
						addMCandidate.preferedConditionEncoded));
				newMCandidate.expList.add("");
			}
			// 띄어쓰기 하지 않을 때
			else if( false ) {
				// 보조적 연결 어미의 정보는 그대로 유지
				newMCandidate.havingConditionEncoded |= (this.havingConditionEncoded & Condition.COND_NUM_SU);
			}

			// 띄어쓰기가 처리된 표현형을 정리해준다.
			// 띄어쓰기가 되었다면 "" 이 추가되었기 때문에 띄어쓰기 처리된 표현형으로 추가된다.
			newMCandidate.expList.add(
					// 이전 표현형에
					(String) newMCandidate.expList.remove(newMCandidate.expList.size() - 1)
					// 바로 다음 표현형을 붙여서
					+ addMCandidate.expList.get(0));
			for( int i = 1, stop = addMCandidate.expList.size(); i < stop; i++ ) {
				newMCandidate.expList.add(addMCandidate.expList.get(i));
			}

			// 호격 조사 penalty
			if( addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.JO_CL_EX) ) {
				newMCandidate.spaceCnt++;
				newMCandidate.spaceCnt++;
			}
			
			// 사랑+해 와 같이 [명사]+하다 형으로 만들어지는 경우에는 [명사]+하다 를 하나의 동사로 취급하여
			// 앞에 관형어가 오지 않고, 부사어가 올 수 있도록 조건을 수정해줌
			if( isAppendable && this.firstMorpheme == this.lastMorpheme && this.firstMorpheme.isSufficientByOr(HgEncoded.OR_NN_AD) 
					&& addMCandidate.firstMorpheme.isSufficientByOr(HgEncoded.OR_VV_AJ) )
			{
				newMCandidate.preferedConditionEncoded = addMCandidate.preferedConditionEncoded;
			}
			
			// 새로 만들어진 후보의 사전 어휘 길이 계산
			newMCandidate.calculateDicLen();
		}

		return newMCandidate;
	}


	/**
	 * <pre>
	 * 후보 분석 결과를 띄어쓰기를 기준으로 분리해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 20
	 * @return
	 */
	List split()
	{
		// 첫번째가 공백으로 시작하면 삭제해준다.
		if( this.get(0) instanceof MorphemeSpace ) {
			expList.remove(0);
			remove(0);
		}

		ArrayList ret = new ArrayList();
		MCandidate mc = new MCandidate();
		mc.appendableHgClassEncoded = appendableHgClassEncoded;
		mc.checkingConditionEncoded = checkingConditionEncoded;
		mc.preferedConditionEncoded = preferedConditionEncoded;
		Morpheme mp = null;
		int expIdx = 0;
		for( int i = 0, stop = size(); i < stop; i++ ) {
			mp = get(i);
			if( mp instanceof MorphemeSpace ) {
				if( i == 0 ) continue;
				mc.setExp((String)expList.get(expIdx));
				mc.havingConditionEncoded = ((MorphemeSpace)mp).havingConditionEncoded;
				expIdx++;
				ret.add(mc);
				mc = new MCandidate();
				mc.appendableHgClassEncoded = ((MorphemeSpace)mp).appendableHgClassEncoded;
				mc.checkingConditionEncoded = ((MorphemeSpace)mp).checkingConditionEncoded;
				mc.preferedConditionEncoded = ((MorphemeSpace)mp).preferedConditionEncoded;
			} else {
				mc.add(mp);
			}
		}
		mc.setExp((String)expList.get(expIdx));
		mc.havingConditionEncoded = havingConditionEncoded;
		mc.calculateDicLen();
		ret.add(mc);
		return ret;
	}


	/**
	 * <pre>
	 * 띄어쓰기 되지 않은 head정보를 추출해준다.
	 * spaceCnt가 최소 1개 이상일 때에만 수행
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 23
	 * @return
	 */
	MCandidate[] divideHeadTail()
	{
		if( spaceCnt < 1 ) return null;
		MCandidate[] ret = new MCandidate[2];

		MCandidate headMC = ret[0] = new MCandidate();
		MCandidate tailMC = ret[1] = new MCandidate();

		// head 생성
		headMC.appendableHgClassEncoded = appendableHgClassEncoded;
		headMC.checkingConditionEncoded = checkingConditionEncoded;
		headMC.preferedConditionEncoded = preferedConditionEncoded;
		Morpheme mp = null;
		int idx = 0, stop = size();
		for( ; idx < stop; idx++ ) {
			mp = get(idx);
			if( mp instanceof MorphemeSpace ) {
				if( idx == 0 ) continue;
				headMC.setExp((String) expList.get(0));
				headMC.havingConditionEncoded = ((MorphemeSpace) mp).havingConditionEncoded;

				// tail 생성
				tailMC.appendableHgClassEncoded = ((MorphemeSpace)mp).appendableHgClassEncoded;
				tailMC.checkingConditionEncoded = ((MorphemeSpace)mp).checkingConditionEncoded;
				tailMC.preferedConditionEncoded = ((MorphemeSpace)mp).preferedConditionEncoded;
				tailMC.havingConditionEncoded = havingConditionEncoded;
				idx++;
				break;
			}
			headMC.add(mp);
		}

		// 나머지 분석 결과 삽입
		if( idx < stop ) {
			for( ; idx < stop; idx++ ) {
				tailMC.add(get(idx));
			}
			// 표현형 추가
			for( int i = 1, iStop = expList.size(); i < iStop; i++ ) {
				tailMC.expList.add(expList.get(i));
			}
		}
		headMC.calculateDicLen();
		tailMC.calculateDicLen();
		return ret;
	}


	/**
	 * <pre>
	 * head tail 문자를 받아들여서 해당 head, tail로 잘라질 수 있는 위치를 찾아서,
	 * head, tail로 잘라서 반환해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 25
	 * @param headStr
	 * @param headIndex
	 * @param tailStr
	 * @param tailIndex
	 * @return
	 * @throws Exception
	 */
	MCandidate[] divideHeadTailAt(String headStr,int headIndex, String tailStr, int tailIndex)
		throws Exception
	{
		int divideIdx = 0;
		for( int i = 0, stop = expList.size(); i < stop; i++ ) {
			if( getExp(i).equals(headStr) ) {
				break;
			}
			divideIdx++;
		}
		if( spaceCnt <= divideIdx ) {
			return new MCandidate[] {new MCandidate(headStr, headIndex), new MCandidate(tailStr, tailIndex)};
		}
		MCandidate[] ret = new MCandidate[2];

		MCandidate headMC = ret[0] = new MCandidate();
		MCandidate tailMC = ret[1] = new MCandidate();

		// head 생성
		headMC.appendableHgClassEncoded = appendableHgClassEncoded;
		headMC.checkingConditionEncoded = checkingConditionEncoded;
		headMC.preferedConditionEncoded = preferedConditionEncoded;

		int spaceIdx = 0;
		int idx = 0, stop = size();
		for( ; idx < stop; idx++ ) {
			Morpheme mp = get(idx);
			if( mp instanceof MorphemeSpace ) {
				if( spaceIdx < divideIdx ) {
					headMC.add(mp);
					spaceIdx++;
					continue;
				}
				for( int j = 0, jStop = divideIdx + 1; j < jStop; j++ ) {
					headMC.expList.add(expList.get(j));
				}
				headMC.havingConditionEncoded = ((MorphemeSpace) mp).havingConditionEncoded;

				// tail 생성
				tailMC.appendableHgClassEncoded = ((MorphemeSpace) mp).appendableHgClassEncoded;
				tailMC.checkingConditionEncoded = ((MorphemeSpace) mp).checkingConditionEncoded;
				tailMC.preferedConditionEncoded = ((MorphemeSpace) mp).preferedConditionEncoded;
				tailMC.havingConditionEncoded = havingConditionEncoded;
				idx++;
				break;
			}
			headMC.add(mp);
		}


		// 나머지 분석 결과 삽입
		if( idx < stop ) {
			for( ; idx < stop; idx++ ) {
				tailMC.add(get(idx));
			}
			// 표현형 추가
			for( int i = divideIdx + 1, iStop = expList.size(); i < iStop; i++ ) {
				tailMC.expList.add(expList.get(i));
			}
		}
		headMC.calculateDicLen();
		tailMC.calculateDicLen();

		return ret;
	}



	/**
	 * <pre>
	 * idx번째 띄어쓰기 다음에 오는 형태소가 미등록어인지 확인한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 25
	 * @param idx
	 * @return
	 */
	boolean isNRBeforeOrAfterIthSpace(int idx)
	{
		if( idx >= this.spaceCnt ) return false;
		int spaceIdx = 0;
		for( int i = 0, stop = size() - 1; i < stop; i++ ) {
			Morpheme mp = get(i);
			if( mp instanceof MorphemeSpace ) {
				if( spaceIdx != idx ) {
					spaceIdx++;
					continue;
				}
				mp = get(i + 1);
				return get(i + 1).isSufficientByAnd(HgEncoded.NR)
					|| get(i - 1).isSufficientByAnd(HgEncoded.NR);
			}
		}
		return false;
	}


	/**
	 * <pre>
	 * 한글자 명사로만 띄어쓰기 되어서 연결된 분석 후보인지 확인
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 26
	 * @return
	 */
	boolean isComposedOfOnlyOneNouns()
	{
		if( getExp().length() != spaceCnt + 1 ) return false;
		if( bonus > 0 ) return false;
		for( int i = 0; i < size; i++ ) {
			Morpheme mp = get(i);
			if( mp instanceof MorphemeSpace ) continue;
			if( !mp.isSufficientByOr(HgEncoded.OR_NN_NP_UM_VV_AJ_EM) ) return false;
		}
		return true;
	}


	/**
	 * <pre>
	 * 분석 결과를 바탕으로 hashCode를 생성한다.
	 * hashCode는 한번만 생성한다.
	 * calculateDicLen() 호출시에 생성되는 문자열을 바탕으로 한번만 계산한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @return
	 */
	public int hashCode()
	{
		return hashCode;
	}


	/**
	 * <pre>
	 * hashCode를 계산한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 25
	 */
	void calculateHashCode()
	{
		hashCode = getEncodedString().hashCode();
	}


	/**
	 * <pre>
	 * 기분석 결과가 동일한지 확인
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param mc
	 * @return
	 */
	public boolean equals(Object obj)
	{
		return hashCode() == obj.hashCode();
	}


	/**
	 * <pre>
	 * 사전에 나온 어휘의 길이가 길수록 더 적합한 것이라고 판단하여
	 * Sorting할 때 사용함!!
	 * 분석 어휘의 수가 적은 것일 수록 유리하도록 설정!!
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param arg0
	 * @return
	 */
	public int compareTo(Object arg0)
	{
		MCandidate comp = (MCandidate) arg0;
		int ret = comp.getScore() - this.getScore();
		return ret;
	}


	/**
	 * <pre>
	 * 현재 분석 후보의 점수를 반환한다.
	 * 점수는 calculateScore()에서 그 정책을 정하고 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 22
	 * @return
	 */
	int getScore()
	{
		calculateScore();
		return score;
	}


	/**
	 * <pre>
	 * 현재 후보에 대한 점수를 계산한다.
	 * 한번 계산된 점수는 계속 유지된다.
	 *
	 * # 점수 부여 정책
	 *   완전 사전어의 길이
	 *   후보 사전어의 길이
	 *   띄어쓰기 수
	 *   선호 조건의 갯수
	 *   의 순으로 정렬될 수 있도록 점수 부여
	 *
	 * 점수 계산은 다음의 경우에만 수행된다.
	 *   1) 기분석 사전 구축시
	 *   2) 후보간의 derive 호출시
	 *   3) bonus가 변할 때
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 22
	 */
	void calculateScore()
	{
		score = this.realDicLen + this.candDicLen;
		score = score * 100 - spaceCnt;
		score = score * 100 + this.realDicLen;
		score = score * 100 + this.bonus;
		// Occams Razor적용
		//score = score * 100 - size;
	}


	/**
	 * <pre>
	 * 사전 어휘 길이를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 21
	 * @return
	 */
	int getDicLenOnlyReal()
	{
		return this.realDicLen;
	}


	/**
	 * <pre>
	 * 종결되지 않은 것까지 사전 어휘로 고려하여 사전 어휘 길이를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 21
	 * @return
	 */
	int getDicLenWithCand()
	{
		return this.candDicLen + this.realDicLen;
	}


	/**
	 * <pre>
	 * 후보 사전어의 길이를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 25
	 * @return
	 */
	int getDicLenOnlyCand()
	{
		return this.candDicLen;
	}


	/**
	 * <pre>
	 * 시작이나 끝이 불완전한 class로 끝나면 불완전한 형태소 분석 결과임
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 21
	 * @return
	 */
	boolean isComplete()
		throws Exception
	{
		return candDicLen == 0;
	}


	/**
	 * <pre>
	 * 사전어와 비사전어의 길이를 계산한다.
	 * overhead를 유발하는 method이므로 꼭 필요한 경우에만 호출한다.
	 * derive method다음에만 호출한다!!
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 23
	 */
	private void calculateDicLen()
	{
		// 형태소 수 반환
		size = (byte)size();
		spaceCnt = 0;

		byte realDicLen = 0, candDicLen = 0;

		int expIdx = 0;
		int tempDicLen = 0, nrDicLen = 0;
		boolean isPreWord = false, hasJo = false;
		short conjugationCnt = 0;


		Morpheme mp = null;
		for( int i = 0, stop = size + 1; i < stop; i++ ) {
			if( i < size ) mp = get(i);
			else mp = null;

			if( mp == null || mp instanceof MorphemeSpace ) {
				// 완료 여부 확인
				boolean conjugationCompleted = conjugationCnt == 0
					|| (!(conjugationCnt % 100 == 0) && (conjugationCnt % 2 == 0));

				// 표현형 만들어주기 '_' 를 포함한 경우는 띄어쓰기를 강제로 한것으로 줄임말로 처리
				String exp = (String) expList.get(expIdx);

				// 사전어, 비사전어 설정
				// 줄임말인 경우에는 줄임말 앞부분의 완료 여부에 따라서 완료 여부 설정
				if( (!hasJo || isPreWord) && conjugationCompleted ) {
					realDicLen += exp.length() - nrDicLen;
				} else {
					candDicLen += exp.length() - nrDicLen;
				}

				// 선호 조건 설정
				// 대등한 구조를 확인하기 위함..
				// 사실은 영어에서의 and, or 처럼 구조적으로 동일해야하지만,
				// 지금은 서술어에 대해서만 처리 (서술격 조사 빼고)
				if( mp == null ) {
					if( !hasJo && conjugationCompleted && conjugationCnt > 0 ) {
						this.addPreferedCondition(Condition.COND_NUM_EQ);
					}
					// 대등 연결 조건 추가
					if( !hasJo && lastMorpheme.isSufficientByAnd(HgEncoded.EM_CN_EQ) ) {
						this.addHavingCondition(Condition.COND_NUM_EQ);
					}
					// 조사 조건 추가
					else if( lastMorpheme.isSufficientByAnd(HgEncoded.JO) ) {
						this.addHavingCondition(Condition.COND_NUM_JO);

						// 미등록어 + 조사인 경우는 미등록어를 후보 사전어로 취급할 수 있도록 한다.
						// 사전어 길이에서 1을 빼줌으로 다른 완전히 사전어보다는 우선순위를 낮추어준다.
						if( size == 2 && firstMorpheme.isSufficientByAnd(HgEncoded.NR) ) {
							candDicLen += nrDicLen - 1;
						}
					}
				}
				// 공백 크기 증가시키기
				else {
					spaceCnt++;
				}

				// 다음을 위해 초기화
				conjugationCnt = 0;
				isPreWord = false;
				hasJo = false;
				nrDicLen = 0;
				tempDicLen = 0;
				expIdx++;
			}
			// 띄어쓰기를 기준으로 표현형에 대한 사전어 여부 처리
			else {
				// 어간이 오면 미종결로 처리
				if( mp.isSufficientByOr(HgEncoded.OR_VV_AJ) ) {
					conjugationCnt++;
				}
				// 서술격 조사는 어간이며, 조사로 처리
				else if( mp.isSufficientByAnd(HgEncoded.CP) ) {
					hasJo = true;
					conjugationCnt++;
				}
				// 미종결이고, 어미가 오면 종결로 처리, 이전에 어간이 오지 않았으면 미종결로 처리
				else if( mp.isSufficientByAnd(HgEncoded.EM) ) {
					conjugationCnt++;
					// 명사형으로 종결이면 조사 앞말 출현으로 설정
					if( mp.isSufficientByAnd(HgEncoded.EM_FM_NN) ) {
						isPreWord = true;
					}
				}
				// 조사인 경우 앞 어휘가 나왔는지 확인해야하므로 설정
				else if( mp.isSufficientByAnd(HgEncoded.JO) ) {
					hasJo = true;
				}
				// 미등록어인 경우
				else if( mp.isSufficientByAnd(HgEncoded.NR) ) {
					isPreWord = true;
					nrDicLen += mp.string.length();
				}
				// 선어말 어미가 아닌 경우에는 임시 사전 어휘로 추가하고, 앞말 존재로 설정
				else if( mp.isSufficientByAnd(HgEncoded.EP) ) {
					conjugationCnt += 100;
				} else {
					isPreWord = true;
					tempDicLen += mp.string.length();
				}
			}
		}

		this.realDicLen = realDicLen;
		this.candDicLen = candDicLen;

		// hashCode 계산
		calculateHashCode();
	}


	/**
	 * 접속 가능한 품사 정보
	 */
	public static final String	DLT_AHCL	= "#";
	/**
	 * 현재 후보가 가진 접속 조건
	 */
	public static final String	DLT_HCL		= "&"; 
	/**
	 * 접속할 때 확인해야 하는 조건
	 */
	public static final String	DLT_CCL		= "@";
	/**
	 * 뛰어스기 포함해서 이전에 나올 수 있는 품사
	 */
	public static final String	DLT_PHCL	= "%";


	/**
	 * <pre>
	 * 사전의 후보 분석 결과 문자열로부터 객체를 생성한다.
	 * 공백에 대한 처리를 추가하기 위하여 수정 2007-07-19
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param exp
	 * @param source
	 */
	static MCandidate create(String exp, String source)
	{
		MCandidate mCandidate = new MCandidate();
		mCandidate.setExp(exp);
		StringTokenizer st = new StringTokenizer(source, "[]", false);

		// 기분석 결과 저장
		String token = null, infos = "";
		String[] arr = null;
		for( int i = 0; st.hasMoreTokens(); i++ ) {
			token = st.nextToken();
			if( i == 0 ) {
				arr = token.split("\\+");
				for( int j = 0; j < arr.length; j++ ) {
					// 앞 뒤조건 정보들을 가지는 공백 문자열 생성
					if( arr[j].startsWith(" ") ) {
						mCandidate.add(new MorphemeSpace(arr[j]));
						mCandidate.expList.add(0, "_");
					}
					// 일반적인 형태소 분석 결과 저장
					else {
						mCandidate.add(Morpheme.create(arr[j]));
					}
				}
			} else {
				infos = token;
			}
		}


		// 부가 정보들에 대한 처리 수행
		st = new StringTokenizer(infos, "*" + DLT_AHCL + DLT_HCL + DLT_CCL + DLT_PHCL, true);
		while(st.hasMoreTokens()) {
			token = st.nextToken();
			// 접속 가능한 품사 정보
			if(token.equals(DLT_AHCL)) {
				token = st.nextToken().trim();
				token = token.substring(1, token.length() - 1);
				mCandidate.addAppendableHgClass(token.split(","));
			}
			// 현재 후보가 가진 접속 조건
			else if(token.equals(DLT_HCL)) {
				token = st.nextToken().trim();
				token = token.substring(1, token.length() - 1);
				mCandidate.addHavingCondition(token.split(","));
			}
			// 접속할 때 확인해야 하는 조건
			else if(token.equals(DLT_CCL)) {
				token = st.nextToken().trim();
				token = token.substring(1, token.length() - 1);
				mCandidate.addCheckingCondition(token.split(","));
			}
			// 뛰어스기 포함해서 이전에 나올 수 있는 품사
			else if(token.equals(DLT_PHCL)) {
				token = st.nextToken().trim();
				token = token.substring(1, token.length() - 1);
				mCandidate.addPreferedCondition(token.split(","));
			}
		}
		mCandidate.initCondition(exp);
		mCandidate.calculateDicLen();
		return mCandidate;
	}


	/**
	 * <pre>
	 * 기분석 후보 정보를 반환한다.
	 * 분석 사전에서 { } 내에 들어갈 정보를 반환해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @return
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();

		// 형태소 분석 결과
		sb.append("[" + super.toString() + "]");

		// 접속 가능한 품사 정보
		String temp = HgClass.getHgClassString(appendableHgClassEncoded);
		if( temp != null ) sb.append(DLT_AHCL + "(" + temp + ")");

		// 현재 후보가 가진 접속 조건
		temp = Condition.getConditionString(havingConditionEncoded);
		if( temp != null ) sb.append(DLT_HCL + "(" + temp + ")");

		// 접속할 때 확인해야 하는 조건
		temp = Condition.getConditionString(checkingConditionEncoded);
		if( temp != null ) sb.append(DLT_CCL + "(" + temp + ")");

		// 뛰어스기 포함해서 이전에 나올 수 있는 품사
		temp = Condition.getConditionString(preferedConditionEncoded);
		if( temp != null ) sb.append(DLT_PHCL + "(" + temp + ")");

		sb.append("*(" + getScore() + "," + realDicLen + "," + candDicLen + "," + spaceCnt + "," + bonus + "," + size + "," + hashCode + ")");
		return sb.toString();
	}
	
	
	public String toSimpleString()
	{
		return super.toString();
	}


	/**
	 * <pre>
	 * 형태소 분석 정보에 부가 정보를 encoding된 상태로 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 11
	 * @return
	 */
	String getEncodedString()
	{
		StringBuffer sb = new StringBuffer();
		//sb.append(super.getEncodedString());
		sb.append(super.getMergedEncodedString());
		sb.append("!" + appendableHgClassEncoded);
		sb.append("!"  + havingConditionEncoded);
		sb.append("!"  + checkingConditionEncoded);
		sb.append("!"  + preferedConditionEncoded);
		return sb.toString();
	}
}