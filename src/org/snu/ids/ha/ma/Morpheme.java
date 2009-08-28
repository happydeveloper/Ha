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
 * ���¼� ������ ������ Class
 * ���¼�, + �ΰ� class ���� ����
 * string : ���¼� ����
 * composed : ���վ� ����
 * hgClass : ���¼��� ���� ǰ��, ���, ���,
 * hgEncoded �� encoding�� ���·� �����ϵ��� ������
 * hgEncoded �� 30,15,15bit�� ����Ͽ� ������ ǥ����
 * [composed]
 * �߰� ���ξ ������ �ʿ䰡 �ִ����� �����ϴ� ����
 * ������ �ִ� ���ֿ��� �߰��� �����Ͽ� ��ȯ�Ѵ�.
 * �����մ� -> �����մ�, �����,�մ�
 * �ձ۰Եձ۰� -> �ձ۰Եձ۰�, �ձ۰�
 * [hgType]
 * �� ���¼��� ������ ���� ���� ���� ����
 * � ���
 * 	-> ������, ������, ������������ ���� ���� ����
 * ��� ���
 * 	-> ����, ����, ��Ī, ���������� ����
 * ������
 * 	-> ����������, ���������� ���� ���� ����
 * [hgFunc]
 * �� ���¼� ������ ���� ���� ���� ����
 * � ���
 *  -> ������ : �⺻, �ǹ�, ...
 *  -> ������ : ...
 *  -> ������ : ...
 * ����
 *  -> ������ : �ְ�, ������, �λ��, ������, ����..., ������
 *  -> ������ : ...
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
	 * copy�� ����ϱ� ���� constructor
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
	 * �̵�Ͼ ���� �⺻���� �м� ����� �����Ѵ�.
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
	 * @param string	���¼�
	 * @param hgClass	���¼��� ǰ��
	 * @param compType	���տ���
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
	 * @param string	���¼�
	 * @param hgClass	���¼��� ǰ��
	 * @param compType	���տ���
	 * @param type		�߰� ����
	 * @param func		��� ����
	 */
	public Morpheme(String string, String hgClass, String compType, String type, String func)
	{
		this.string = string;
		this.charSet = Token.CHAR_SET_HANGUL;
		hgEncoded = HgEncoded.encodeHgInfo(hgClass, compType, type, func);
	}


	/**
	 * <pre>
	 * �ѱ��̿��� token������ �޾Ƶ鿩�� ���¼� ������ �������ش�.
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
		// ���ڴ� ����� ��������
		if( token.isCharSetOf(CHAR_SET_NUMBER) ) {
			hgEncoded = HgEncoded.NU;
		// ������ �ܼ��� ���� ��������
		} else if( token.isCharSetOf(CHAR_SET_ENGLISH) || token.isCharSetOf(CHAR_SET_COMBINED) ) {
			this.string = this.string.toUpperCase();
			hgEncoded = HgEncoded.NR;
		// �̿�
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
	 * ǰ�� ��ȣ�� ��ȯ�Ѵ�.
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
	 * ���¼Ұ� ���� ������ �־��� ������ �����ϴ��� Ȯ���Ѵ�.
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
	 * ���¼Ұ� ���� ������ �־��� ������ �����ϴ��� Ȯ���Ѵ�.
	 * OR ���·� Ȯ���Ѵ�.
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
	 * ���¼� ������ ���վ ���ο� �ܾ ����� �� �� �ִ��� Ȯ��
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
	 * �� ���¼Ұ� ���ο� �ܾ ����� �� �� �ִ� ��쿡 ������
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
	 * ǰ�� ������ �����Ѵ�.
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
	 * ���纻�� ��ȯ
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
	 * ���¼� ������ �����ؼ� �����Ѵ�.
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
	 * ���¼� ������ ���ڿ��� ����Ѵ�.
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
	 * ���¼� ������ encoding�� ���·� ����Ѵ�.
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
