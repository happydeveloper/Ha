/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 6. 3
 */
package org.snu.ids.ha.ma;

import org.snu.ids.ha.constants.HgClass;
import org.snu.ids.ha.constants.HgEncoded;
import org.snu.ids.ha.constants.HgFunc;
import org.snu.ids.ha.constants.HgType;


/**
 * <pre>
 * 형태소 정보를 가지는 Class
 * 형태소, + 부가 class 정보 가짐
 * string : 형태소 정보
 * composed : 복합어 여부
 * hgClass : 형태소의 구분 품사, 어미, 어근,
 * hgEncoded 에 encoding한 상태로 저장하도록 수정함
 * hgEncoded 에 30,15,15bit를 사용하여 각각을 표현함
 * [composed]
 * 추가 색인어를 추출할 필요가 있는지를 구분하는 어휘
 * 사전에 있는 어휘에서 추가로 분해하여 반환한다.
 * 사랑방손님 -> 사랑방손님, 사랑방,손님
 * 둥글게둥글게 -> 둥글게둥글게, 둥글게
 * [hgType]
 * 각 형태소의 종류에 따른 구분 정보 저장
 * 어말 어미
 * 	-> 종결형, 연결형, 의존형인지의 구분 정보 저장
 * 선어말 어미
 * 	-> 시제, 공손, 존칭, 전성형인지 구분
 * 조사인
 * 	-> 격조사인지, 보조사인지 등의 정보 저장
 * [hgFunc]
 * 각 형태소 종류에 따른 역할 정보 저장
 * 어말 어미
 *  -> 종결형 : 기본, 의문, ...
 *  -> 연결형 : ...
 *  -> 의존형 : ...
 * 조사
 *  -> 격조사 : 주격, 목적격, 부사격, 관형격, 보격..., 서술격
 *  -> 보조사 : ...
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 3
 */
public class Morpheme
	extends Token
{
	long	hgEncoded	= 0;


	/**
	 * <pre>
	 * copy에 사용하기 위한 constructor
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 6
	 */
	protected Morpheme()
	{
		super();
	}


	/**
	 * <pre>
	 * default constructor
	 * 미등록어에 대한 기본적인 분석 결과를 생성한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param string
	 */
	public Morpheme(String string, int index)
	{
		this.index = index;
		this.string = string;
		this.charSet = Token.CHAR_SET_HANGUL;
		hgEncoded = HgEncoded.NR;
	}


	/**
	 * <pre>
	 *
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param string	형태소
	 * @param hgClass	형태소의 품사
	 * @param compType	복합여부
	 */
	public Morpheme(String string, String hgClass, String compType)
	{
		this.string = string;
		this.charSet = Token.CHAR_SET_HANGUL;
		hgEncoded = HgEncoded.encodeHgInfo(hgClass, compType, null, null);
	}


	/**
	 * <pre>
	 *
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param string	형태소
	 * @param hgClass	형태소의 품사
	 * @param compType	복합여부
	 * @param type		추가 구분
	 * @param func		기능 구분
	 */
	public Morpheme(String string, String hgClass, String compType, String type, String func)
	{
		this.string = string;
		this.charSet = Token.CHAR_SET_HANGUL;
		hgEncoded = HgEncoded.encodeHgInfo(hgClass, compType, type, func);
	}


	/**
	 * <pre>
	 * 한글이외의 token정보를 받아들여서 형태소 정보를 설정해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param token
	 */
	public Morpheme(Token token)
	{
		this.index = token.index;
		this.string = token.string;
		this.charSet = token.charSet;
		// 숫자는 수사로 설정해줌
		if( token.isCharSetOf(CHAR_SET_NUMBER) ) {
			hgEncoded = HgEncoded.NU;
		// 영문은 단순히 명사로 설정해줌
		} else if( token.isCharSetOf(CHAR_SET_ENGLISH) || token.isCharSetOf(CHAR_SET_COMBINED) ) {
			this.string = this.string.toUpperCase();
			hgEncoded = HgEncoded.NR;
		// 이외
		} else {
			hgEncoded = HgEncoded.SY;
		}
	}


	/**
	 * @return Returns the hgClass.
	 */
	public String getHgClass()
	{
		return HgClass.getHgClass(getHgClassNum());
	}


	/**
	 * <pre>
	 * 품사 번호를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 6
	 * @return
	 */
	public long getHgClassNum()
	{
		return hgEncoded & HgClass.HG_CLASS_DECODE_NUM;
	}


	/**
	 * @return Returns the composed.
	 */
	public boolean isComposed()
	{
		return hgEncoded < 0;
	}


	/**
	 * <pre>
	 * 
	 * </pre>
	 * @param composed
	 */
	public void setComposed(boolean composed)
	{
		if( composed ) {
			hgEncoded |= HgEncoded.COMPOSED;
		} else {
			hgEncoded &= HgEncoded.MASK_COMPOSED;
		}
	}


	/**
	 * @param compType The composed to set.
	 */
	public void setComposed(String compType)
	{
		if( compType != null && compType.equals("C") ) {
			hgEncoded |= HgEncoded.COMPOSED;
		} else {
			hgEncoded &= HgEncoded.MASK_COMPOSED;
		}
	}


	/**
	 * @return Returns the type.
	 */
	public String getHgType()
	{
		return HgType.getHgType(getHgTypeNum());
	}


	/**
	 * @return
	 */
	public long getHgTypeNum()
	{
		return hgEncoded & HgType.HG_TYPE_DECODE_NUM;
	}


	/**
	 * @return Returns the func.
	 */
	public String getHgFunc()
	{
		return HgFunc.getHgFunc(getHgFuncNum());
	}


	/**
	 * @return
	 */
	public long getHgFuncNum()
	{
		return hgEncoded & HgFunc.HG_FUNC_DECODE_NUM;
	}


	/**
	 * @return Returns the hgEncoded.
	 */
	public long getHgEncoded()
	{
		return hgEncoded;
	}


	/**
	 * <pre>
	 * 형태소가 가진 정보가 주어진 조건을 충족하는지 확인한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 6
	 * @param condHgEncoded
	 * @return
	 */
	public boolean isSufficientByAnd(long condHgEncoded)
	{
		return (hgEncoded & condHgEncoded) == condHgEncoded;
	}


	/**
	 * <pre>
	 * 형태소가 가진 정보가 주어진 조건을 충족하는지 확인한다.
	 * OR 형태로 확인한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 6
	 * @param condHgEncoded
	 * @return
	 */
	public boolean isSufficientByOr(long condHgEncoded)
	{
		long temp = hgEncoded & condHgEncoded;

		if( (condHgEncoded & HgClass.HG_CLASS_DECODE_NUM) == 0 || (temp & HgClass.HG_CLASS_DECODE_NUM) > 0 ) {
			if( (condHgEncoded & HgType.HG_TYPE_DECODE_NUM) == 0 || (temp & HgType.HG_TYPE_DECODE_NUM) > 0 ) {
				if( (condHgEncoded & HgFunc.HG_FUNC_DECODE_NUM) == 0 || (temp & HgFunc.HG_FUNC_DECODE_NUM) > 0 ) {
					return true;
				}
			}
		}
		return false;
	}


	/**
	 * <pre>
	 * 형태소 정보가 복합어나 새로운 단어를 만들어 낼 수 있는지 확인
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 27
	 * @param mp
	 * @return
	 */
	boolean appendable(Morpheme mp)
	{
		return  isSufficientByAnd(HgEncoded.NN)
			&& mp.isSufficientByOr(HgEncoded.OR_NN_APPENDABLE_CLASSES);
	}


	/**
	 * <pre>
	 * 두 형태소가 새로운 단어를 만들어 낼 수 있는 경우에 합쳐줌
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 27
	 * @param mp
	 */
	void append(Morpheme mp)
	{
		this.string += mp.string;
		if( mp.isSufficientByAnd(HgEncoded.FUNC_DT) ) {
			setHgClass(HgEncoded.DT);
		} else {
			setHgClass(mp.hgEncoded);
		}
	}


	/**
	 * <pre>
	 * 품사 정보를 설정한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 27
	 * @param hgClassNum
	 */
	private void setHgClass(long hgClassNum)
	{
		this.hgEncoded &= HgEncoded.UNMASK_HG_CLASS;
		this.hgEncoded |= (HgEncoded.MASK_HG_CLASS & hgClassNum);
	}


	/**
	 * <pre>
	 * 복사본을 반환
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 7
	 * @return
	 */
	public Morpheme copy()
	{
		Morpheme copy = new Morpheme();
		copy.string = this.string;
		copy.charSet = this.charSet;
		copy.index = this.index;
		copy.hgEncoded = this.hgEncoded;
		return copy;
	}


	/**
	 * <pre>
	 * 형태소 정보를 생성해서 저장한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param source
	 */
	static Morpheme create(String source)
	{
		Morpheme ret = null;
		String[] arr = source.split("/");
		ret = new Morpheme(arr[0], arr[1], arr[2],
				arr.length > 3 ? arr[3] : null,
				arr.length > 4 ? arr[4] : null);
		return ret;
	}


	/**
	 * <pre>
	 * 형태소 정보를 문자열로 출력한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @return
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		String[] hgInfo = HgEncoded.decodeHgInfo(hgEncoded);
		sb.append(index + "/" + string + "/" + hgInfo[0] + "/" + hgInfo[1]);
		sb.append(hgInfo[2] != null ? ("/" + hgInfo[2]) : "");
		sb.append(hgInfo[3] != null ? ("/" + hgInfo[3]) : "");
		return sb.toString();
	}


	/**
	 * <pre>
	 * 형태소 정보를 encoding된 형태로 출력한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 20
	 * @return
	 */
	String getEncodedString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(string + "/" + hgEncoded);
		return sb.toString();
	}
}
