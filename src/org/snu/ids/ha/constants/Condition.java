/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 6. 6
 */
package org.snu.ids.ha.constants;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * <pre>
 * 후보가 가진 조건에 대한 관리를 수행한다.
 * long형에 중복해서 가질 수 있는 조건을 encoding하기 때문에,
 * 최대 64가지 조건을 정의할 수 있다.
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 6
 */
public class Condition
{
	public static final String[]	CONDITION_ARR	= {
		"양성",	// 양성 모음으로 끝남
		"음성",	// 음성 모음으로 끝남
		"모음",	// 모음으로 끝남
		"자음",	// 자자음으로 끝남
		"아",	// 형태소 '아'로 종결됨
		"-아",	// 형태소 '아'로 종결되지 않음
		"ㄴ",	// 자음 'ㄴ'이 붙여짐
		"ㄹ",	// 자음 'ㄹ'이 붙여짐
		"-ㄹ",	// 자음 'ㄹ'이 탈락함
		"ㅁ",	// 자음 'ㅁ'이 붙여짐
		"ㅂ",	// 자음 'ㅂ'이 붙여짐
		"-ㅂ",	// 자음 'ㅂ'이 탈락함
		"ㅆ",	// 자음 'ㅆ'이 붙여짐
		"-ㅎ",	// 자음 'ㅎ'이 탈락함
		"-ㅅ",	// 자음 'ㅅ'이 탈락함
		"었",	// 선어말 어미 '었'이 부착됨
		"겠",	// 선어말 어미 '겠'이 부착됨
		"하",	// '하'로 끝나는 용언
		"이",	// '이'로 끝나는 용언
		"체언",	// 체언으로 사용됨
		"관형어",	// 관형어로 사용됨
		"부사어",	// 부사어로 사용됨
		"서술어",	// 서술어로 사용됨
		"SU",	// 보조적으로 종결됨
		"EQ",	// 서술어의 대등적 연결 관계 (대등적으로 연결되면 바로 다음에 서술어가 위치하는 것이 바람직)
		"JO",	// 조사로 끝나는지 확인
		"ENG",	// 영문을 소리나는 대로 읽은 말
		"ㅇ",	// 시작되는 자음이 없음 ('ㅇ'으로 시작함)
		"VV_A",	// 다음에 오는 말이 용언일 때 띄어쓰기 없이 붙여서 사용됨
		"NN_A",	// 다음에 오는 말이 용언일 때 띄어쓰기 없이 붙여서 사용됨
		"려"		// 려로 끝나서 다음에 '하+어미'형태로 준말
	};


	public static final Hashtable	CONDITION_HASH		= new Hashtable();
	public static final Hashtable	CONDITION_NUM_HASH	= new Hashtable();
	static {
		long conditionNum = 0;
		for( int i = 0, stop = CONDITION_ARR.length; i < stop; i++ ) {
			conditionNum = getConditionNum(i);
			CONDITION_HASH.put(CONDITION_ARR[i], new Long(conditionNum));
			CONDITION_NUM_HASH.put(new Long(conditionNum), CONDITION_ARR[i]);
		}
	}


	/**
	 * <pre>
	 * i 번째 조건을 encoding하는 long값을 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 19
	 * @param i
	 * @return
	 */
	private static final long getConditionNum(int i)
	{
		return (1l << i);
	}


	/**
	 * <pre>
	 * hgClass에 대한 long number를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param condition
	 * @return
	 */
	public static long getConditionNum(String condition)
	{
		if( condition == null )	return 0l;
		long l = 0;
		try {
			l = ((Long) CONDITION_HASH.get(condition)).longValue();
		} catch (Exception e) {
			System.err.println("["+condition+"] 정의되지 않은 조건입니다.");
		}
		return l;
	}


	/**
	 * <pre>
	 * 조건을 가지는 문자열을 받아들여서 이에 해당하는 인코딩된 값을 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 19
	 * @param conditions
	 * @return
	 */
	public static long getConditionNum(String[] conditions)
	{
		long l = 0;
		for( int i = 0, size = (conditions == null ? 0 : conditions.length); i < size; i++ ) {
			l |= getConditionNum(conditions[i]);
		}
		return l;
	}


	/**
	 * <pre>
	 * conditionNum에 대한 hgClass문자를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param conditionNum
	 * @return
	 */
	public static String getCondition(long conditionNum)
	{
		return conditionNum == 0 ? null : (String) CONDITION_NUM_HASH.get(new Long(conditionNum));
	}


	/**
	 * <pre>
	 * encoding된 값이 나타내는 class를 확인한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param encodedConditionNum
	 * @return
	 */
	public static List getConditionList(long encodedConditionNum)
	{
		List ret = new ArrayList();
		for( int i = 0, stop = CONDITION_ARR.length; i < stop; i++ ) {
			if( (encodedConditionNum & getConditionNum(i)) > 0 )
				ret.add(CONDITION_ARR[i]);
		}
		return ret;
	}


	/**
	 * <pre>
	 * encoding된 값을 문자열로 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 19
	 * @param encodedConditionNum
	 * @return
	 */
	public static String getConditionString(long encodedConditionNum)
	{
		StringBuffer sb = new StringBuffer();
		for( int i = 0, stop = CONDITION_ARR.length; i < stop; i++ ) {
			if( (encodedConditionNum & getConditionNum(i)) > 0 ) {
				if( sb.length() > 0 ) sb.append(",");
				sb.append(CONDITION_ARR[i]);
			}
		}
		return sb.length() == 0 ? null : sb.toString();
	}


	/**
	 * 조건 값들을 저장해두고, 사용하도록 한다.
	 */
	public static final long	COND_NUM_YANGSEONG	= getConditionNum("양성");
	public static final long	COND_NUM_EUMSEONG	= getConditionNum("음성");
	public static final long	COND_NUM_MOEUM		= getConditionNum("모음");
	public static final long	COND_NUM_JAEUM		= getConditionNum("자음");
	public static final long	COND_NUM_AH			= getConditionNum("아");
	public static final long	COND_NUM_MINUS_AH	= getConditionNum("-아");
	public static final long	COND_NUM_NIEUN		= getConditionNum("ㄴ");
	public static final long	COND_NUM_LIEUL		= getConditionNum("ㄹ");
	public static final long	COND_NUM_MINUS_LIEUL= getConditionNum("-ㄹ");
	public static final long	COND_NUM_MIEUM		= getConditionNum("ㅁ");
	public static final long	COND_NUM_BIEUB		= getConditionNum("ㅂ");
	public static final long	COND_NUM_MINUS_BIEUB= getConditionNum("-ㅂ");
	public static final long	COND_NUM_MINUS_HIEUT= getConditionNum("-ㅎ");
	public static final long	COND_NUM_MINUS_SIOT	= getConditionNum("-ㅅ");
	public static final long	COND_NUM_SIOT2		= getConditionNum("ㅆ");
	public static final long	COND_NUM_GET		= getConditionNum("겠");
	public static final long	COND_NUM_EUT		= getConditionNum("었");
	public static final long	COND_NUM_HA			= getConditionNum("하");
	public static final long	COND_NUM_YI			= getConditionNum("이");
	public static final long	COND_NUM_NN			= getConditionNum("체언");
	public static final long	COND_NUM_DT			= getConditionNum("관형어");
	public static final long	COND_NUM_AD			= getConditionNum("부사어");
	public static final long	COND_NUM_VV			= getConditionNum("서술어");
	public static final long	COND_NUM_SU			= getConditionNum("SU");
	public static final long	COND_NUM_EQ			= getConditionNum("EQ");
	public static final long	COND_NUM_JO			= getConditionNum("JO");
	public static final long	COND_NUM_ENG		= getConditionNum("ENG");
	public static final long	COND_NUM_YIEUNG		= getConditionNum("ㅇ");
	public static final long	COND_NUM_VV_A		= getConditionNum("VV_A");
	public static final long	COND_NUM_NN_A		= getConditionNum("NN_A");
	public static final long	COND_NUM_LYEO		= getConditionNum("려");

	public static final long COND_NUM_JA_SET =
		COND_NUM_NIEUN
		| COND_NUM_LIEUL
		| COND_NUM_MIEUM;

	public static final long COND_NUM_MINUS_JA_SET =
		COND_NUM_MINUS_LIEUL
		| COND_NUM_MINUS_BIEUB
		| COND_NUM_MINUS_HIEUT;

	public static final long COND_NUM_NR_SET =
		COND_NUM_JAEUM
		| COND_NUM_MOEUM
		| COND_NUM_YANGSEONG
		| COND_NUM_EUMSEONG;
}
