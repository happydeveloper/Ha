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
 * �ĺ��� ���� ���ǿ� ���� ������ �����Ѵ�.
 * long���� �ߺ��ؼ� ���� �� �ִ� ������ encoding�ϱ� ������,
 * �ִ� 64���� ������ ������ �� �ִ�.
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 6
 */
public class Condition
{
	public static final String[]	CONDITION_ARR	= {
		"�缺",	// �缺 �������� ����
		"����",	// ���� �������� ����
		"����",	// �������� ����
		"����",	// ���������� ����
		"��",	// ���¼� '��'�� �����
		"-��",	// ���¼� '��'�� ������� ����
		"��",	// ���� '��'�� �ٿ���
		"��",	// ���� '��'�� �ٿ���
		"-��",	// ���� '��'�� Ż����
		"��",	// ���� '��'�� �ٿ���
		"��",	// ���� '��'�� �ٿ���
		"-��",	// ���� '��'�� Ż����
		"��",	// ���� '��'�� �ٿ���
		"-��",	// ���� '��'�� Ż����
		"-��",	// ���� '��'�� Ż����
		"��",	// ��� ��� '��'�� ������
		"��",	// ��� ��� '��'�� ������
		"��",	// '��'�� ������ ���
		"��",	// '��'�� ������ ���
		"ü��",	// ü������ ����
		"������",	// ������� ����
		"�λ��",	// �λ��� ����
		"������",	// ������� ����
		"SU",	// ���������� �����
		"EQ",	// �������� ����� ���� ���� (��������� ����Ǹ� �ٷ� ������ ����� ��ġ�ϴ� ���� �ٶ���)
		"JO",	// ����� �������� Ȯ��
		"ENG",	// ������ �Ҹ����� ��� ���� ��
		"��",	// ���۵Ǵ� ������ ���� ('��'���� ������)
		"VV_A",	// ������ ���� ���� ����� �� ���� ���� �ٿ��� ����
		"NN_A",	// ������ ���� ���� ����� �� ���� ���� �ٿ��� ����
		"��"		// ���� ������ ������ '��+���'���·� �ظ�
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
	 * i ��° ������ encoding�ϴ� long���� ��ȯ�Ѵ�.
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
	 * hgClass�� ���� long number�� ��ȯ�Ѵ�.
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
			System.err.println("["+condition+"] ���ǵ��� ���� �����Դϴ�.");
		}
		return l;
	}


	/**
	 * <pre>
	 * ������ ������ ���ڿ��� �޾Ƶ鿩�� �̿� �ش��ϴ� ���ڵ��� ���� ��ȯ�Ѵ�.
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
	 * conditionNum�� ���� hgClass���ڸ� ��ȯ�Ѵ�.
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
	 * encoding�� ���� ��Ÿ���� class�� Ȯ���Ѵ�.
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
	 * encoding�� ���� ���ڿ��� ��ȯ�Ѵ�.
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
	 * ���� ������ �����صΰ�, ����ϵ��� �Ѵ�.
	 */
	public static final long	COND_NUM_YANGSEONG	= getConditionNum("�缺");
	public static final long	COND_NUM_EUMSEONG	= getConditionNum("����");
	public static final long	COND_NUM_MOEUM		= getConditionNum("����");
	public static final long	COND_NUM_JAEUM		= getConditionNum("����");
	public static final long	COND_NUM_AH			= getConditionNum("��");
	public static final long	COND_NUM_MINUS_AH	= getConditionNum("-��");
	public static final long	COND_NUM_NIEUN		= getConditionNum("��");
	public static final long	COND_NUM_LIEUL		= getConditionNum("��");
	public static final long	COND_NUM_MINUS_LIEUL= getConditionNum("-��");
	public static final long	COND_NUM_MIEUM		= getConditionNum("��");
	public static final long	COND_NUM_BIEUB		= getConditionNum("��");
	public static final long	COND_NUM_MINUS_BIEUB= getConditionNum("-��");
	public static final long	COND_NUM_MINUS_HIEUT= getConditionNum("-��");
	public static final long	COND_NUM_MINUS_SIOT	= getConditionNum("-��");
	public static final long	COND_NUM_SIOT2		= getConditionNum("��");
	public static final long	COND_NUM_GET		= getConditionNum("��");
	public static final long	COND_NUM_EUT		= getConditionNum("��");
	public static final long	COND_NUM_HA			= getConditionNum("��");
	public static final long	COND_NUM_YI			= getConditionNum("��");
	public static final long	COND_NUM_NN			= getConditionNum("ü��");
	public static final long	COND_NUM_DT			= getConditionNum("������");
	public static final long	COND_NUM_AD			= getConditionNum("�λ��");
	public static final long	COND_NUM_VV			= getConditionNum("������");
	public static final long	COND_NUM_SU			= getConditionNum("SU");
	public static final long	COND_NUM_EQ			= getConditionNum("EQ");
	public static final long	COND_NUM_JO			= getConditionNum("JO");
	public static final long	COND_NUM_ENG		= getConditionNum("ENG");
	public static final long	COND_NUM_YIEUNG		= getConditionNum("��");
	public static final long	COND_NUM_VV_A		= getConditionNum("VV_A");
	public static final long	COND_NUM_NN_A		= getConditionNum("NN_A");
	public static final long	COND_NUM_LYEO		= getConditionNum("��");

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
