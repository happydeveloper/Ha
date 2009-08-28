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
 * �ִ� 31������ ���¼Ҹ� ������ �� �ִ�.
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 6
 */
public class HgClass
{
	/**
	 * <pre>
	 * �缺 ����
	 * </pre>
	 * @since	2007. 7. 21
	 * @author	therocks
	 */
	public static final StringSet	MO_POSITIVE_SET	= new StringSet(
			new String[] {
					"��",
					"��",
					"��",
					"��",
					"��",
					"��",
					"��" });

	/**
	 * <pre>
	 * ���� ����
	 * </pre>
	 * @since	2007. 7. 21
	 * @author	therocks
	 */
	public static final StringSet	MO_NEGATIVE_SET	= new StringSet(
			new String[] {
					"��",
					"��",
					"��",
					"��",
					"��",
					"��",
					"��",
					"��",
					"��",
					"��",
					"��"	});

	/**
	 * <pre>
	 * �߼� ����
	 * </pre>
	 * @since	2007. 7. 21
	 * @author	therocks
	 */
	public static final StringSet	MO_NEUTRIAL_SET	= new StringSet(
			new String[] {
					"��",
					"��",
					"��" });

	/**
	 * <pre>
	 * �����
	 * </pre>
	 * @since	2007. 7. 21
	 * @author	therocks
	 */
	public static final StringSet	MO_DOUBLE_SET	= new StringSet(
			new String[] {
					"��",
					"��",
					"��",
					"��",
					"��",
					"��" });


	/**
	 * <pre>
	 * ���¼� ���� ����
	 * </pre>
	 * @since	2007. 7. 21
	 * @author	therocks
	 */
	public static final String[]	HG_CLASS_ARR	= {
		"NN",	// ���
		"NP",	// ����
		"NX",	// ���� ���
		"NU",	// ����
		"UM",	// ���� ���
		"NR",	// �̵�Ͼ�
		"VV",	// ����, �
		"VI",	// �ڵ���
		"VT",	// Ÿ����
		"VX",	// ��������
		"AJ",	// �����
		"AX",	// ���� �����
		"DN",	// ��������
		"DT",	// �� �̿� ������
		"AD",	// �λ�
		"PF",	// ���λ�
		"SV",	// ����ȭ ���̻�
		"SJ",	// �����ȭ �����̻�
		"SN",	// ���ȭ ���̻�
		"SA",	// �λ�ȭ ���̻�
		"SF",	// ��Ÿ ���̻�
		"CP",	// ������ ����
		"JO",	// ��Ÿ ����
		"EP",	// ��� ���
		"EM",	// � ���
		"EX",	// ��ź��
		"SY",	// ��ȣ, �ܷ���
		"UK",	// �̵�Ͼ�
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
	 * Array�� �ִ� i��° HgClassNum�� ��ȯ�Ѵ�.
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
	 * hgClass�� ���� long number�� ��ȯ�Ѵ�.
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
			System.err.println("["+hgClass+"] ���ǵ��� ���� �����Դϴ�.");
		}
		return l;
	}


	/**
	 * <pre>
	 * hgClasses�� �ش��ϴ� ǰ�� �������� ���ڵ��� ������ ��ȯ�Ѵ�.
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
	 * hgClassNum�� ���� hgClass���ڸ� ��ȯ�Ѵ�.
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
	 * encoding�� ���� ��Ÿ���� class�� Ȯ���Ѵ�.
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
	 * encodedHgClassNum �� �����ϰ� �ִ� hgClass�������� ��ȯ�Ѵ�.
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
