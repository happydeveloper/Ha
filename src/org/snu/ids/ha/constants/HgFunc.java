/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 7. 6
 */
package org.snu.ids.ha.constants;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * <pre>
 *
 * </pre>
 * @author 	therocks
 * @since	2007. 7. 6
 */
public class HgFunc
{
	// func 변수 배열
	public static final String[]	HG_FUNC_ARR	= {
		// 조사의 격
		"SB", // 주격
		"OB", // 목적격
		"CO", // 보격
		"DT", // 관형격, 관형사형 전성어미
		"AD", // 부사격, 부사형 전성어미
		"EX", // 호격, 감탄형 종결어미
		// 어말 어미
		// - 종결형
		"NM", // 기본
		"QT", // 의문
		"OD", // 명령
		"AK", // 청유
		"HR", // 존칭
		// - 연결형
		"EQ", // 대등적
		"DP", // 의존적
		"SU", // 보조적
		// - 전성형
		"NN", // 명사형
		"VV"  // 동사형
	};


	public static final Hashtable	HG_FUNC_HASH		= new Hashtable();
	public static final Hashtable	HG_FUNC_NUM_HASH	= new Hashtable();


	public static final long	HG_FUNC_DECODE_NUM	= 0xFFFFl;

	static {
		long hgFuncNum = 0;
		for( int i = 0, stop = HG_FUNC_ARR.length; i < stop; i++ ) {
			hgFuncNum = getHgFuncNum(i);
			HG_FUNC_HASH.put(HG_FUNC_ARR[i], new Long(hgFuncNum));
			HG_FUNC_NUM_HASH.put(new Long(hgFuncNum), HG_FUNC_ARR[i]);
		}
	}


	/**
	 * <pre>
	 * Array에 있는 i번째 HgFuncNum을 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 18
	 * @param i
	 * @return
	 */
	private static final long getHgFuncNum(int i)
	{
		return 1l << i;
	}


	/**
	 * <pre>
	 * hgFunc에 대한 int number를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param hgFunc
	 * @return
	 */
	public static long getHgFuncNum(String hgFunc)
	{
		return hgFunc == null ? 0l : ((Long) HG_FUNC_HASH.get(hgFunc)).longValue();
	}


	/**
	 * <pre>
	 * hgFuncNum에 대한 hgFunc문자를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param hgFuncNum
	 * @return
	 */
	public static String getHgFunc(long hgFuncNum)
	{
		return hgFuncNum == 0 ? null : (String) HG_FUNC_NUM_HASH.get(new Long(hgFuncNum));
	}


	/**
	 * <pre>
	 * encoding된 값이 나타내는 class를 확인한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param encodedHgFuncNum
	 * @return
	 */
	public static List getHgFuncList(long encodedHgFuncNum)
	{
		List ret = new ArrayList();
		for( int i = 0, stop = HG_FUNC_ARR.length; i < stop; i++ ) {
			if( (encodedHgFuncNum & getHgFuncNum(i)) > 0 ) ret.add(HG_FUNC_ARR[i]);
		}
		return ret;
	}
}
