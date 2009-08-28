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
	// func ���� �迭
	public static final String[]	HG_FUNC_ARR	= {
		// ������ ��
		"SB", // �ְ�
		"OB", // ������
		"CO", // ����
		"DT", // ������, �������� �������
		"AD", // �λ��, �λ��� �������
		"EX", // ȣ��, ��ź�� ������
		// � ���
		// - ������
		"NM", // �⺻
		"QT", // �ǹ�
		"OD", // ���
		"AK", // û��
		"HR", // ��Ī
		// - ������
		"EQ", // �����
		"DP", // ������
		"SU", // ������
		// - ������
		"NN", // �����
		"VV"  // ������
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
	 * Array�� �ִ� i��° HgFuncNum�� ��ȯ�Ѵ�.
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
	 * hgFunc�� ���� int number�� ��ȯ�Ѵ�.
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
	 * hgFuncNum�� ���� hgFunc���ڸ� ��ȯ�Ѵ�.
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
	 * encoding�� ���� ��Ÿ���� class�� Ȯ���Ѵ�.
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
