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

import org.snu.ids.ha.util.StringSet;


/**
 * <pre>
 * 최대 31가지의 형태소를 정의할 수 있다.
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 6
 */
public class HgClass
{
	/**
	 * <pre>
	 * 양성 모음
	 * </pre>
	 * @since	2007. 7. 21
	 * @author	therocks
	 */
	public static final StringSet	MO_POSITIVE_SET	= new StringSet(
			new String[] {
					"ㅏ",
					"ㅐ",
					"ㅑ",
					"ㅒ",
					"ㅗ",
					"ㅛ",
					"ㅘ" });

	/**
	 * <pre>
	 * 음성 모음
	 * </pre>
	 * @since	2007. 7. 21
	 * @author	therocks
	 */
	public static final StringSet	MO_NEGATIVE_SET	= new StringSet(
			new String[] {
					"ㅓ",
					"ㅔ",
					"ㅕ",
					"ㅖ",
					"ㅜ",
					"ㅠ",
					"ㅝ",
					"ㅞ",
					"ㅟ",
					"ㅚ",
					"ㅙ"	});

	/**
	 * <pre>
	 * 중성 모음
	 * </pre>
	 * @since	2007. 7. 21
	 * @author	therocks
	 */
	public static final StringSet	MO_NEUTRIAL_SET	= new StringSet(
			new String[] {
					"ㅡ",
					"ㅣ",
					"ㅢ" });

	/**
	 * <pre>
	 * 겹모음
	 * </pre>
	 * @since	2007. 7. 21
	 * @author	therocks
	 */
	public static final StringSet	MO_DOUBLE_SET	= new StringSet(
			new String[] {
					"ㅘ",
					"ㅝ",
					"ㅞ",
					"ㅟ",
					"ㅚ",
					"ㅙ" });


	/**
	 * <pre>
	 * 형태소 구분 변수
	 * </pre>
	 * @since	2007. 7. 21
	 * @author	therocks
	 */
	public static final String[]	HG_CLASS_ARR	= {
		"NN",	// 명사
		"NP",	// 대명사
		"NX",	// 의존 명사
		"NU",	// 수사
		"UM",	// 단위 명사
		"NR",	// 미등록어
		"VV",	// 동사, 어간
		"VI",	// 자동사
		"VT",	// 타동사
		"VX",	// 보조동사
		"AJ",	// 형용사
		"AX",	// 보조 형용사
		"DN",	// 수관형사
		"DT",	// 수 이외 관형사
		"AD",	// 부사
		"PF",	// 접두사
		"SV",	// 동사화 접미사
		"SJ",	// 형용사화 ㅈ버미사
		"SN",	// 명사화 접미사
		"SA",	// 부사화 접미사
		"SF",	// 기타 접미사
		"CP",	// 서술격 조사
		"JO",	// 기타 조사
		"EP",	// 선어말 어미
		"EM",	// 어말 어미
		"EX",	// 감탄사
		"SY",	// 부호, 외래어
		"UK",	// 미등록어
		};

	public static final Hashtable	HG_CLASS_HASH		= new Hashtable();
	public static final Hashtable	HG_CLASS_NUM_HASH	= new Hashtable();

	public static final long	HG_CLASS_SHIFT_NUM	= 32;
	public static final long	HG_CLASS_DECODE_NUM	= 0x7FFFFFFFl << HG_CLASS_SHIFT_NUM;

	static {
		long hgClassNum = 0;
		for( int i = 0, stop = HG_CLASS_ARR.length; i < stop; i++ ) {
			hgClassNum = getHgClassNum(i);
			HG_CLASS_HASH.put(HG_CLASS_ARR[i], new Long(hgClassNum));
			HG_CLASS_NUM_HASH.put(new Long(hgClassNum), HG_CLASS_ARR[i]);
		}
	}


	/**
	 * <pre>
	 * Array에 있는 i번째 HgClassNum을 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 18
	 * @param i
	 * @return
	 */
	private static final long getHgClassNum(int i)
	{
		return (1l << i) << HG_CLASS_SHIFT_NUM;
	}


	/**
	 * <pre>
	 * hgClass에 대한 long number를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param hgClass
	 * @return
	 */
	public static long getHgClassNum(String hgClass)
	{
		if( hgClass == null )	return 0l;
		long l = 0;
		try {
			l = ((Long) HG_CLASS_HASH.get(hgClass)).longValue();
		} catch (Exception e) {
			System.err.println("["+hgClass+"] 정의되지 않은 조건입니다.");
		}
		return l;
	}


	/**
	 * <pre>
	 * hgClasses에 해당하는 품사 정보들을 인코딩된 값으로 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 19
	 * @param hgClasses
	 * @return
	 */
	public static long getHgClassNum(String[] hgClasses)
	{
		long l = 0;
		for( int i = 0, stop = (hgClasses == null ? 0 : hgClasses.length); i < stop; i++ ) {
			l |= getHgClassNum(hgClasses[i]);
		}
		return l;
	}


	/**
	 * <pre>
	 * hgClassNum에 대한 hgClass문자를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param hgClassNum
	 * @return
	 */
	public static String getHgClass(long hgClassNum)
	{
		return hgClassNum == 0 ? null : (String) HG_CLASS_NUM_HASH.get(new Long(hgClassNum));
	}


	/**
	 * <pre>
	 * encoding된 값이 나타내는 class를 확인한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param encodedHgClassNum
	 * @return
	 */
	public static List getHgClassList(long encodedHgClassNum)
	{
		List ret = new ArrayList();
		for( int i = 0, stop = HG_CLASS_ARR.length; i < stop; i++ ) {
			if( (encodedHgClassNum & getHgClassNum(i)) > 0 )
				ret.add(HG_CLASS_ARR[i]);
		}
		return ret;
	}


	/**
	 * <pre>
	 * encodedHgClassNum 가 저장하고 있는 hgClass정보들을 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 19
	 * @param encodedHgClassNum
	 * @return
	 */
	public static String getHgClassString(long encodedHgClassNum)
	{
		StringBuffer sb = new StringBuffer();
		for( int i = 0, stop = HG_CLASS_ARR.length; i < stop; i++ ) {
			if( (encodedHgClassNum & getHgClassNum(i)) > 0 ) {
				if( sb.length() > 0 ) sb.append(",");
				sb.append(HG_CLASS_ARR[i]);
			}
		}
		return sb.length() == 0 ? null : sb.toString();
	}
}
