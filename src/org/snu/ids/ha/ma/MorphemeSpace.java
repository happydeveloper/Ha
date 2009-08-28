/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 6. 25
 */
package org.snu.ids.ha.ma;


import java.util.StringTokenizer;

import org.snu.ids.ha.constants.Condition;
import org.snu.ids.ha.constants.HgClass;
import org.snu.ids.ha.constants.HgEncoded;
import org.snu.ids.ha.util.Util;


/**
 * <pre>
 * 띄어 쓰기를 해주기 위한 class
 * MCandidate가 앞뒤 조건들을 저장하듯이, space는 앞뒤 조건들을 설정할 수 있도록 함
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 25
 */
public class MorphemeSpace
	extends Morpheme
{
	long	appendableHgClassEncoded	= 0;
	long	havingConditionEncoded		= 0;
	long	checkingConditionEncoded	= 0;
	long	preferedConditionEncoded	= 0;


	protected MorphemeSpace()
	{
		super(" ", 0);
		super.charSet = Token.CHAR_SET_SPACE;
		super.hgEncoded = HgEncoded.SY;
	}


	/**
	 * <pre>
	 * 띄어쓰기된 것을 정의할 수 있도록 함
	 * 앞뒤 연결될 때의 부가 정보들을 정의하여 가지고 있을 수 있도록 하고,
	 * 이를 지정하도록 지원하는 생성자
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 19
	 * @param source
	 */
	MorphemeSpace(String source)
	{
		this();
		String[] arr = Util.split(source, "/");
		if( arr.length > 1) {
			StringTokenizer st = new StringTokenizer(arr[1], "*" + MCandidate.DLT_AHCL + MCandidate.DLT_HCL + MCandidate.DLT_CCL + MCandidate.DLT_PHCL, true);
			String token = null;
			while(st.hasMoreTokens()) {
				token = st.nextToken();
				// 접속 가능한 품사 정보
				if(token.equals(MCandidate.DLT_AHCL)) {
					token = st.nextToken().trim();
					token = token.substring(1, token.length() - 1);
					this.appendableHgClassEncoded = HgClass.getHgClassNum(token.split(","));
				}
				// 현재 후보가 가진 접속 조건
				else if(token.equals(MCandidate.DLT_HCL)) {
					token = st.nextToken().trim();
					token = token.substring(1, token.length() - 1);
					this.havingConditionEncoded = Condition.getConditionNum(token.split(","));
				}
				// 접속할 때 확인해야 하는 조건
				else if(token.equals(MCandidate.DLT_CCL)) {
					token = st.nextToken().trim();
					token = token.substring(1, token.length() - 1);
					this.checkingConditionEncoded = Condition.getConditionNum(token.split(","));
				}
				// 뛰어스기 포함해서 이전에 나올 수 있는 품사
				else if(token.equals(MCandidate.DLT_PHCL)) {
					token = st.nextToken().trim();
					token = token.substring(1, token.length() - 1);
					this.preferedConditionEncoded = Condition.getConditionNum(token.split(","));
				}
			}
		}
	}


	/**
	 * <pre>
	 *
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 25
	 * @param string
	 */
	MorphemeSpace(
			long appendableHgClassEncoded,
			long havingConditionEncoded,
			long checkingConditionEncoded,
			long preferedConditionEncoded)
	{
		this();
		super.charSet = Token.CHAR_SET_SPACE;
		super.hgEncoded = HgEncoded.SY;
		this.appendableHgClassEncoded = appendableHgClassEncoded;
		this.havingConditionEncoded = havingConditionEncoded;
		this.checkingConditionEncoded = checkingConditionEncoded;
		this.preferedConditionEncoded = preferedConditionEncoded;
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
		return " ";
	}


	private String getToString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(string + "/");

		// 접속 가능한 품사 정보
		String temp = HgClass.getHgClassString(appendableHgClassEncoded);
		if( temp != null ) sb.append(MCandidate.DLT_AHCL + "(" + temp + ")");

		// 현재 후보가 가진 접속 조건
		temp = Condition.getConditionString(havingConditionEncoded);
		if( temp != null ) sb.append(MCandidate.DLT_HCL + "(" + temp + ")");

		// 접속할 때 확인해야 하는 조건
		temp = Condition.getConditionString(checkingConditionEncoded);
		if( temp != null ) sb.append(MCandidate.DLT_CCL + "(" + temp + ")");

		// 뛰어스기 포함해서 이전에 나올 수 있는 품사
		temp = Condition.getConditionString(preferedConditionEncoded);
		if( temp != null ) sb.append(MCandidate.DLT_PHCL + "(" + temp + ")");

		return sb.toString();
	}


	static Morpheme createFromEncodedString(String source)
	{
		MorphemeSpace ret = new MorphemeSpace();
		String[] arr = source.split("~");
		ret.appendableHgClassEncoded = Long.parseLong(arr[1]);
		ret.havingConditionEncoded = Long.parseLong(arr[2]);
		ret.checkingConditionEncoded = Long.parseLong(arr[3]);
		ret.preferedConditionEncoded = Long.parseLong(arr[4]);
		return ret;
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
		sb.append("_~" + appendableHgClassEncoded);
		sb.append("~" + havingConditionEncoded);
		sb.append("~" + checkingConditionEncoded);
		sb.append("~" + preferedConditionEncoded);
		return sb.toString();
	}
	
	
	/**
	 * <pre>
	 * </pre>
	 * @author	therocks
	 * @since	2008. 03. 31
	 * @return
	 */
	public Morpheme copy()
	{
		return new MorphemeSpace(
				this.appendableHgClassEncoded,
				this.havingConditionEncoded,
				this.checkingConditionEncoded,
				this.preferedConditionEncoded);
	}
}