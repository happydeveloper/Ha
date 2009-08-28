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
 * 한글 품사 조건 정보에 대한 상수를 가지는 class
 * </pre>
 * @author 	therocks
 * @since	2007. 7. 6
 */
public class HgEncoded
{
	/**
	 * <pre>
	 * 품사, 구분, 역할의 세가지 정보를 long형 데이터에 encoding하여 반환한다.
	 * 각각을 or조건으로 저장할 수 있다.
	 * 일단은 하나의 정보만을 지정해둠
	 * 처음 1bit는 복합 명사 여부
	 * 다음 31bit는 hgClass정보
	 * 다음 16bit는 hgType정보
	 * 다음 16bit는 hgFunc정보
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
	 * 복합어 여부까지 받아들여서 정보 설정
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
	 * 복합어 여부를 반환한다.
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
	 * encoding된 hgInfo 로부터 hgClass, hgType, hgFunc 정보를 가진 문자열 배열을 반환한다.
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
	 * 체언으로 활용되는 품사 조건
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
	 * 관형어로 사용되는 품사 조건
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long OR_DT_CLASSES	=
		HgClass.getHgClassNum("DT")
			| HgClass.getHgClassNum("DN");

	/**
	 * <pre>
	 * 부사어로 사용되는 품사 조건
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
	 * 명사에 붙어서 새로운 단어를 만들수 있는 품사 (용언은 어간)
	 * 	1) 명사 + 명사 -> 복합명사
	 *  2) 명사 + 동사 -> 동사, 형용사
	 *  3) 명사 + 형용사 -> 형용사
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
	 * 한글자로만 띄어쓰기 되어서 분석되는 것을 방지하기 위해서 사용하는 조건
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
	 * 형태소 중에 활용되어서 뒤에 있는 것과 접합성이 맞아야만 하는 것들
	 * 반드시 뒤에 완료되는 어떤 것이 와야 한다.
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
	 * 형태소중에 활용되어서 앞에 있는 것과 적합성이 잘 맞아야만 하는 것들
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
	 * 띄어쓰기가 되었을 때 앞에 위치할 수 있는 품사 조건
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
	 * 보조적 연결어미 다음에 올 수 있는 품사 조건
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
	 * 동사나 형용사의 어간 조건
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long OR_VV_AJ =
		HgClass.getHgClassNum("VV")
			| HgClass.getHgClassNum("AJ");

	/**
	 * <pre>
	 * 어간으로 활용되는 품사 조건
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
	 * 불완전한 형태소 품사 조건
	 * -- "NR"도 완전어로 등록
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
	 * 불완전한 형태소 품사 조건
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
	 * 조사
	 * </pre>
	 * @since	2007. 7. 23
	 * @author	therocks
	 */
	public static final long OR_JO_CP =
		HgClass.getHgClassNum("JO")
			| HgClass.getHgClassNum("CP");



	/**
	 * <pre>
	 * 선어말 어미와 어말 어미 조건
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long OR_EOMI_CLASSES =
		 HgClass.getHgClassNum("EM")
			| HgClass.getHgClassNum("EP");


	/**
	 * <pre>
	 * 형태소 분석 시작이었을 때 불완전한 class
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
	 * 형태소 분석 끝에 있을 때 불완전한 class
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
	 * 특수 문자 품사 조건
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long SY = HgClass.getHgClassNum("SY");


	/**
	 * <pre>
	 * 조사 조건
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long JO = HgClass.getHgClassNum("JO");


	/**
	 * <pre>
	 * 격 혹은 연결형 조사 조건
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long OR_CL_CN =
		HgType.getHgTypeNum("CL")
		| HgType.getHgTypeNum("CN");
	
	/**
	 * <pre>
	 * 명사, 부사
	 * 동사나, 형용사와 합쳐져셔 동사나 형용사로 사용될 수 있는 경우를 확인하기 위함
	 * </pre>
	 * @since	2007. 7. 23
	 * @author	therocks
	 */
	public static final long OR_NN_AD =
		HgClass.getHgClassNum("NN")
			| HgClass.getHgClassNum("AD");



	/**
	 * <pre>
	 * 호격 조사 조건
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long JO_CL_EX = encodeHgInfo("JO", "CL", "EX");

	public static final long JO_CL_DT = encodeHgInfo("JO", "CL", "DT");

	public static final long JO_CL_AD = encodeHgInfo("JO", "CL", "AD");

	/**
	 * <pre>
	 * 접미사 조건
	 * </pre>
	 * @since	2007. 7. 27
	 * @author	therocks
	 */
	public static final long NN_FM_NN = encodeHgInfo("NN", "FM", "NN");


	/**
	 * <pre>
	 * 보조사 조건
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long TYPE_SU = HgType.getHgTypeNum("SU");


	/**
	 * <pre>
	 * 형변환 기능을 가졌는지 확인하는 조건
	 * </pre>
	 * @since	2007. 7. 27
	 * @author	therocks
	 */
	public static final long TYPE_FM = HgType.getHgTypeNum("FM");



	public static final long CP = HgClass.getHgClassNum("CP");


	/**
	 * <pre>
	 * 어말 어미 품사 조건
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long EM = HgClass.getHgClassNum("EM");


	/**
	 * <pre>
	 * 문장 종결 조건
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long EM_ED = encodeHgInfo("EM", "ED", null);
	
	/**
	 * 접속 부사
	 */
	public static final long AD_CN = encodeHgInfo("AD", "CN", null);

	/**
	 * <pre>
	 * 전성형 어말 어미 확인 조건
	 * </pre>
	 * @since	2007. 7. 10
	 * @author	therocks
	 */
	public static final long EM_FM = encodeHgInfo("EM", "FM", null);

	public static final long EM_FM_NN = encodeHgInfo("EM", "FM", "NN");


	/**
	 * <pre>
	 * 보조적 연결어미 조건
	 * </pre>
	 * @since	2007. 7. 6
	 * @author	therocks
	 */
	public static final long EM_SU = encodeHgInfo("EM", "SU", null);


	/**
	 * <pre>
	 * '요' 와 같이 어말 어미 다음에 부가적으로 올 수 있는 어미
	 * 존칭을 나타냄
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
	 * 복합어 여부를 설정하는 조건
	 * Encoding된 값이 음수이면 복합어이고, 양수이면 단일어임
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
	 * 우괄호
	 * </pre>
	 * @since	2007. 7. 21
	 * @author	therocks
	 */
	public static final StringSet RIGHT_PARENTHESIS_SET =
		new StringSet(new String[] { ")", "]", "}" });

	/**
	 * <pre>
	 * 좌괄호
	 * </pre>
	 * @since	2007. 7. 21
	 * @author	therocks
	 */
	public static final StringSet LEFT_PARENTHESIS_SET =
		new StringSet(new String[] { "(", "[", "{" });

	/**
	 * <pre>
	 * 괄호
	 * </pre>
	 * @since	2007. 7. 21
	 * @author	therocks
	 */
	public static final StringSet PARENTHESIS_SET =
		new StringSet(new String[] { "(", ")", "[", "]", "{", "}" });


	/**
	 * <pre>
	 * 서술격 조사 앞에 위치할 수 있는 보조사
	 * </pre>
	 * @since	2007. 7. 21
	 * @author	therocks
	 */
	public static final StringSet JOSA_SU_SET =
		new StringSet(new String[] {"만", "부터", "까지"});


	public static final int INT_CP = (int) (CP >> 32);
}
