/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 7. 6
 */
package org.snu.ids.ha.constants;

import org.snu.ids.ha.util.StringSet;

/**
 * <pre>
 * �ѱ� ǰ�� ���� ������ ���� ����� ������ class
 * </pre>
 * @author 	therocks
 * @since	2007. 7. 6
 */
public class HgEncoded
{
	/**
	 * <pre>
	 * ǰ��, ����, ������ ������ ������ long�� �����Ϳ� encoding�Ͽ� ��ȯ�Ѵ�.
	 * ������ or�������� ������ �� �ִ�.
	 * �ϴ��� �ϳ��� �������� �����ص�
	 * ó�� 1bit�� ���� ��� ����
	 * ���� 31bit�� hgClass����
	 * ���� 16bit�� hgType����
	 * ���� 16bit�� hgFunc����
	 * [31bit-hgClass][16bit-hgType][16bit-hgFunc]
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 6
	 * @param hgClass
	 * @param hgType
	 * @param hgFunc
	 * @return
	 */
	public static long encodeHgInfo(String hgClass, String hgType, String hgFunc)
	{
		return HgClass.getHgClassNum(hgClass)
			| HgType.getHgTypeNum(hgType)
			| HgFunc.getHgFuncNum(hgFunc);
	}


	/**
	 * <pre>
	 * ���վ� ���α��� �޾Ƶ鿩�� ���� ����
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 20
	 * @param hgClass
	 * @param compType
	 * @param hgType
	 * @param hgFunc
	 * @return
	 */
	public static long encodeHgInfo(String hgClass, String compType, String hgType, String hgFunc)
	{
		long ret = encodeHgInfo(hgClass, hgType, hgFunc);
		if( compType != null && compType.equals("C") ) {
			ret |= COMPOSED;
		}
		return ret;
	}


	/**
	 * <pre>
	 * ���վ� ���θ� ��ȯ�Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 20
	 * @param hgEncoded
	 * @return
	 */
	public static String getComposed(long hgEncoded)
	{
		return hgEncoded > 0 ? "S" : "C";
	}


	/**
	 * <pre>
	 * encoding�� hgInfo �κ��� hgClass, hgType, hgFunc ������ ���� ���ڿ� �迭�� ��ȯ�Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 6
	 * @param hgEncoded
	 * @return
	 */
	public static String[] decodeHgInfo(long hgEncoded)
	{
		return new String[]{
				HgClass.getHgClass(hgEncoded & HgClass.HG_CLASS_DECODE_NUM),
				getComposed(hgEncoded),
				HgType.getHgType(hgEncoded & HgType.HG_TYPE_DECODE_NUM),
				HgFunc.getHgFunc(hgEncoded & HgFunc.HG_FUNC_DECODE_NUM)
		};
	}


	/**
	 * <pre>
	 * ü������ Ȱ��Ǵ� ǰ�� ����
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long OR_NOUN_CLASSES =
		HgClass.getHgClassNum("NN")
			| HgClass.getHgClassNum("NP")
			| HgClass.getHgClassNum("NX")
			| HgClass.getHgClassNum("NU")
			| HgClass.getHgClassNum("UM")
			| HgClass.getHgClassNum("NR");

	/**
	 * <pre>
	 * ������� ���Ǵ� ǰ�� ����
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long OR_DT_CLASSES	=
		HgClass.getHgClassNum("DT")
			| HgClass.getHgClassNum("DN");

	/**
	 * <pre>
	 * �λ��� ���Ǵ� ǰ�� ����
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long AD	= HgClass.getHgClassNum("AD");

	public static final long DT	= HgClass.getHgClassNum("DT");

	public static final long NN	= HgClass.getHgClassNum("NN");

	public static final long NU	= HgClass.getHgClassNum("NU");

	public static final long NR	= HgClass.getHgClassNum("NR");

	public static final long EP	= HgClass.getHgClassNum("EP");

	public static final long PF	= HgClass.getHgClassNum("PF");


	/**
	 * <pre>
	 * ��翡 �پ ���ο� �ܾ ����� �ִ� ǰ�� (����� �)
	 * 	1) ��� + ��� -> ���ո��
	 *  2) ��� + ���� -> ����, �����
	 *  3) ��� + ����� -> �����
	 * </pre>
	 * @since	2007. 7. 27
	 * @author	therocks
	 */
	public static final long OR_NN_APPENDABLE_CLASSES =
		HgClass.getHgClassNum("NN")
			| HgClass.getHgClassNum("VV")
			| HgClass.getHgClassNum("AJ");


	/**
	 * <pre>
	 * �ѱ��ڷθ� ���� �Ǿ �м��Ǵ� ���� �����ϱ� ���ؼ� ����ϴ� ����
	 * </pre>
	 * @since	2007. 7. 26
	 * @author	therocks
	 */
	public static final long OR_NN_NP_UM_VV_AJ_EM =
		HgClass.getHgClassNum("NN")
			| HgClass.getHgClassNum("NP")
			| HgClass.getHgClassNum("UM")
			| HgClass.getHgClassNum("VV")
			| HgClass.getHgClassNum("AJ")
			| HgClass.getHgClassNum("EM");


	/**
	 * <pre>
	 * ���¼� �߿� Ȱ��Ǿ �ڿ� �ִ� �Ͱ� ���ռ��� �¾ƾ߸� �ϴ� �͵�
	 * �ݵ�� �ڿ� �Ϸ�Ǵ� � ���� �;� �Ѵ�.
	 * </pre>
	 * @since	2007. 7. 18
	 * @author	therocks
	 */
	public static final long OR_PRE_STRICT_CHECK_CLASSES	=
		HgClass.getHgClassNum("VV")
			| HgClass.getHgClassNum("AJ")
			| HgClass.getHgClassNum("CP")
			| HgClass.getHgClassNum("PF")
			| HgClass.getHgClassNum("EP");


	/**
	 * <pre>
	 * ���¼��߿� Ȱ��Ǿ �տ� �ִ� �Ͱ� ���ռ��� �� �¾ƾ߸� �ϴ� �͵�
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long OR_POST_STRICT_CHECK_CLASSES	=
		HgClass.getHgClassNum("SV")
			| HgClass.getHgClassNum("SJ")
			| HgClass.getHgClassNum("SN")
			| HgClass.getHgClassNum("SA")
			| HgClass.getHgClassNum("SF")
			| HgClass.getHgClassNum("CP")
			| HgClass.getHgClassNum("JO")
			| HgClass.getHgClassNum("EP")
			| HgClass.getHgClassNum("EM");

	/**
	 * <pre>
	 * ���Ⱑ �Ǿ��� �� �տ� ��ġ�� �� �ִ� ǰ�� ����
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long OR_DEFAULT_PRE_CLASS =
		HgClass.getHgClassNum("NN")
			| HgClass.getHgClassNum("NP")
			| HgClass.getHgClassNum("NX")
			| HgClass.getHgClassNum("NU")
			| HgClass.getHgClassNum("UM")
			| HgClass.getHgClassNum("NR")
			| HgClass.getHgClassNum("JO")
			| HgClass.getHgClassNum("EM")
			| HgClass.getHgClassNum("AD")
			| HgClass.getHgClassNum("DT")
			| HgClass.getHgClassNum("DN")
			| HgClass.getHgClassNum("EX")
			| HgClass.getHgClassNum("SN")
			| HgClass.getHgClassNum("SY");

	/**
	 * <pre>
	 * ������ ������ ������ �� �� �ִ� ǰ�� ����
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long OR_VV_AJ_EM_JO	=
		HgClass.getHgClassNum("VV")
			| HgClass.getHgClassNum("AJ")
			| HgClass.getHgClassNum("EM")
			| HgClass.getHgClassNum("JO");

	/**
	 * <pre>
	 * ���糪 ������� � ����
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long OR_VV_AJ =
		HgClass.getHgClassNum("VV")
			| HgClass.getHgClassNum("AJ");

	/**
	 * <pre>
	 * ����� Ȱ��Ǵ� ǰ�� ����
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long OR_EOGAN_CLASSES =
		HgClass.getHgClassNum("VV")
			| HgClass.getHgClassNum("AJ")
			| HgClass.getHgClassNum("CP");

	/**
	 * <pre>
	 * �ҿ����� ���¼� ǰ�� ����
	 * -- "NR"�� ������� ���
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long OR_INCOMPLETE_CLASSES =
		HgClass.getHgClassNum("VV")
			| HgClass.getHgClassNum("AJ")
			| HgClass.getHgClassNum("CP")
			| HgClass.getHgClassNum("EM")
			| HgClass.getHgClassNum("EP")
			| HgClass.getHgClassNum("NR");


	/**
	 * <pre>
	 * �ҿ����� ���¼� ǰ�� ����
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long OR_MUST_ATTACH_CLASSES =
		HgClass.getHgClassNum("JO")
			| HgClass.getHgClassNum("CP")
			| HgClass.getHgClassNum("EM")
			| HgClass.getHgClassNum("EP");


	/**
	 * <pre>
	 * ����
	 * </pre>
	 * @since	2007. 7. 23
	 * @author	therocks
	 */
	public static final long OR_JO_CP =
		HgClass.getHgClassNum("JO")
			| HgClass.getHgClassNum("CP");



	/**
	 * <pre>
	 * ��� ��̿� � ��� ����
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long OR_EOMI_CLASSES =
		 HgClass.getHgClassNum("EM")
			| HgClass.getHgClassNum("EP");


	/**
	 * <pre>
	 * ���¼� �м� �����̾��� �� �ҿ����� class
	 * </pre>
	 * @since	2007. 7. 21
	 * @author	therocks
	 */
	public static final long OR_PRE_UNCOMPLETE_CLASSES =
		HgClass.getHgClassNum("EM")
			| HgClass.getHgClassNum("EP")
			| HgClass.getHgClassNum("JO")
			| HgClass.getHgClassNum("CP");


	/**
	 * <pre>
	 * ���¼� �м� ���� ���� �� �ҿ����� class
	 * </pre>
	 * @since	2007. 7. 21
	 * @author	therocks
	 */
	public static final long OR_POST_UNCOMPLETE_CLASSES =
		HgClass.getHgClassNum("VV")
			| HgClass.getHgClassNum("AJ")
			| HgClass.getHgClassNum("CP");


	/**
	 * <pre>
	 * Ư�� ���� ǰ�� ����
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long SY = HgClass.getHgClassNum("SY");


	/**
	 * <pre>
	 * ���� ����
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long JO = HgClass.getHgClassNum("JO");


	/**
	 * <pre>
	 * �� Ȥ�� ������ ���� ����
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long OR_CL_CN =
		HgType.getHgTypeNum("CL")
		| HgType.getHgTypeNum("CN");
	
	/**
	 * <pre>
	 * ���, �λ�
	 * ���糪, ������ �������� ���糪 ������ ���� �� �ִ� ��츦 Ȯ���ϱ� ����
	 * </pre>
	 * @since	2007. 7. 23
	 * @author	therocks
	 */
	public static final long OR_NN_AD =
		HgClass.getHgClassNum("NN")
			| HgClass.getHgClassNum("AD");



	/**
	 * <pre>
	 * ȣ�� ���� ����
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long JO_CL_EX = encodeHgInfo("JO", "CL", "EX");

	public static final long JO_CL_DT = encodeHgInfo("JO", "CL", "DT");

	public static final long JO_CL_AD = encodeHgInfo("JO", "CL", "AD");

	/**
	 * <pre>
	 * ���̻� ����
	 * </pre>
	 * @since	2007. 7. 27
	 * @author	therocks
	 */
	public static final long NN_FM_NN = encodeHgInfo("NN", "FM", "NN");


	/**
	 * <pre>
	 * ������ ����
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long TYPE_SU = HgType.getHgTypeNum("SU");


	/**
	 * <pre>
	 * ����ȯ ����� �������� Ȯ���ϴ� ����
	 * </pre>
	 * @since	2007. 7. 27
	 * @author	therocks
	 */
	public static final long TYPE_FM = HgType.getHgTypeNum("FM");



	public static final long CP = HgClass.getHgClassNum("CP");


	/**
	 * <pre>
	 * � ��� ǰ�� ����
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long EM = HgClass.getHgClassNum("EM");


	/**
	 * <pre>
	 * ���� ���� ����
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long EM_ED = encodeHgInfo("EM", "ED", null);
	
	/**
	 * ���� �λ�
	 */
	public static final long AD_CN = encodeHgInfo("AD", "CN", null);

	/**
	 * <pre>
	 * ������ � ��� Ȯ�� ����
	 * </pre>
	 * @since	2007. 7. 10
	 * @author	therocks
	 */
	public static final long EM_FM = encodeHgInfo("EM", "FM", null);

	public static final long EM_FM_NN = encodeHgInfo("EM", "FM", "NN");


	/**
	 * <pre>
	 * ������ ������ ����
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long EM_SU = encodeHgInfo("EM", "SU", null);


	/**
	 * <pre>
	 * '��' �� ���� � ��� ������ �ΰ������� �� �� �ִ� ���
	 * ��Ī�� ��Ÿ��
	 * </pre>
	 * @since	2007. 7. 10
	 * @author	therocks
	 */
	public static final long EM_ED_HR = encodeHgInfo("EM", "ED", "HR");
	public static final long EM_CN_DP = encodeHgInfo("EM", "CN", "DP");
	public static final long EM_CN_EQ = encodeHgInfo("EM", "CN", "EQ");
	public static final long EM_CN_SU = encodeHgInfo("EM", "CN", "SU");
	public static final long EM_ED_NM = encodeHgInfo("EM", "ED", "NM");


	public static final long FUNC_NN = HgFunc.getHgFuncNum("NN");
	public static final long FUNC_DT = HgFunc.getHgFuncNum("DT");
	public static final long FUNC_VV = HgFunc.getHgFuncNum("VV");


	/**
	 * <pre>
	 * ���վ� ���θ� �����ϴ� ����
	 * Encoding�� ���� �����̸� ���վ��̰�, ����̸� ���Ͼ���
	 * </pre>
	 * @since	2007. 7. 20
	 * @author	therocks
	 */
	public static final long COMPOSED = 0x8000000000000000l;
	public static final long MASK_COMPOSED = 0x7FFFFFFFFFFFFFFFl;
	public static final long UNMASK_HG_CLASS = 0x80000000FFFFFFFFl;
	public static final long MASK_HG_CLASS = 0x7FFFFFFF00000000l;
	public static final long MASK_HG_TYPE = 0x00000000FFFF0000l;
	public static final long MASK_HG_FUNC = 0x000000000000FFFFl;


	/**
	 * <pre>
	 * ���ȣ
	 * </pre>
	 * @since	2007. 7. 21
	 * @author	therocks
	 */
	public static final StringSet RIGHT_PARENTHESIS_SET =
		new StringSet(new String[] { ")", "]", "}" });

	/**
	 * <pre>
	 * �°�ȣ
	 * </pre>
	 * @since	2007. 7. 21
	 * @author	therocks
	 */
	public static final StringSet LEFT_PARENTHESIS_SET =
		new StringSet(new String[] { "(", "[", "{" });

	/**
	 * <pre>
	 * ��ȣ
	 * </pre>
	 * @since	2007. 7. 21
	 * @author	therocks
	 */
	public static final StringSet PARENTHESIS_SET =
		new StringSet(new String[] { "(", ")", "[", "]", "{", "}" });


	/**
	 * <pre>
	 * ������ ���� �տ� ��ġ�� �� �ִ� ������
	 * </pre>
	 * @since	2007. 7. 21
	 * @author	therocks
	 */
	public static final StringSet JOSA_SU_SET =
		new StringSet(new String[] {"��", "����", "����"});


	public static final int INT_CP = (int) (CP >> 32);
}
