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
public class HgType
{
	// type 변수 배열
	public static final String[]	HG_TYPE_ARR	= {
		// 조사 구분
		"CL",	// 격조사
		"SU",	// 보조사
		"CN",	// 접속조사, 연결형 어말 어미, 접속 부사
		// 선어말 어미 구분
		"HR",	// 존칭
		"TM",	// 시제
		"PL",	// 공손
		// 어말 어미 구분
		"ED",	// 종결형
		"FM"	// 전성형
	};


	public static final Hashtable	HG_TYPE_HASH		= new Hashtable();
	public static final Hashtable	HG_TYPE_NUM_HASH	= new Hashtable();

	public static final long	HG_TYPE_SHIFT_NUM	= 16;
	public static final long	HG_TYPE_DECODE_NUM	= 0xFFFFl << HG_TYPE_SHIFT_NUM;

	static {
		long hgTypeNum = 1;
		for( int i = 0, stop = HG_TYPE_ARR.length; i < stop; i++ ) {
			hgTypeNum = getHgTypeNum(i);
			HG_TYPE_HASH.put(HG_TYPE_ARR[i], new Long(hgTypeNum));
			HG_TYPE_NUM_HASH.put(new Long(hgTypeNum), HG_TYPE_ARR[i]);
		}
	}


	/**
	 * <pre>
	 * Array에 있는 i번째 HgTypeNum을 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 18
	 * @param i
	 * @return
	 */
	private static final long getHgTypeNum(int i)
	{
		return (1l << i) << HG_TYPE_SHIFT_NUM;
	}


	/**
	 * <pre>
	 * hgType에 대한 int number를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param hgType
	 * @return
	 */
	public static long getHgTypeNum(String hgType)
	{
		return hgType == null ? 0l : ((Long) HG_TYPE_HASH.get(hgType)).longValue();
	}


	/**
	 * <pre>
	 * hgTypeNum에 대한 hgType문자를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param hgTypeNum
	 * @return
	 */
	public static String getHgType(long hgTypeNum)
	{
		return hgTypeNum == 0 ? null : (String) HG_TYPE_NUM_HASH.get(new Long(hgTypeNum));
	}


	/**
	 * <pre>
	 * encoding된 값이 나타내는 class를 확인한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param encodedHgTypeNum
	 * @return
	 */
	public static List getHgTypeList(long encodedHgTypeNum)
	{
		List ret = new ArrayList();
		for( int i = 0, stop = HG_TYPE_ARR.length; i < stop; i++ ) {
			if( (encodedHgTypeNum & getHgTypeNum(i)) > 0 ) ret.add(HG_TYPE_ARR[i]);
		}
		return ret;
	}
}
