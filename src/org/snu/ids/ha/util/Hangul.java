/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 5. 7
 */
package org.snu.ids.ha.util;





/**
 * <pre>
 *
 * </pre>
 * @author 	therocks
 * @since	2007. 5. 7
 */
public class Hangul
{
	public char	cho		= 0;
	public char	jung	= 0;
	public char	jong	= 0;


	public String toString()
	{
		return "(" + cho + "," + jung + "," + jong + ")";
	}


	/**
	 * <pre>
	 * endsWith�� �����ϱ� ���ؼ� �߰��� �Լ�
	 * ���ص� �� ����, ������ �ٿ��� ��ȯ���ش�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 8
	 * @return
	 */
	private String get()
	{
		String ret = (cho == 0 ? "" : cho + "");
		ret += (jung == 0 ? "" : jung + "");

		switch (jong) {
			case 0:		ret += "";		break;
			case '��':	ret += "����";	break;
			case '��':	ret += "����";	break;
			case '��':	ret += "����";	break;
			case '��':	ret += "����";	break;
			case '��':	ret += "����";	break;
			case '��':	ret += "����";	break;
			case '��':	ret += "����";	break;
			case '��':	ret += "����";	break;
			case '��':	ret += "����";	break;
			case '��':	ret += "����";	break;
			case '��':	ret += "����";	break;
			default:	ret += jong;	break;
		}
		return ret;
	}


	public boolean hasCho()
	{
		return cho != 0;
	}


	public boolean hasJung()
	{
		return jung != 0;
	}


	public boolean hasJong()
	{
		return jong != 0;
	}


	private static final char getCho(int idx)
	{
		char ret = 0;
		switch (idx) {
			case 0:		ret = '��';	break;
			case 1:		ret = '��';	break;
			case 2:		ret = '��';	break;
			case 3:		ret = '��';	break;
			case 4:		ret = '��';	break;
			case 5:		ret = '��';	break;
			case 6:		ret = '��';	break;
			case 7:		ret = '��';	break;
			case 8:		ret = '��';	break;
			case 9:		ret = '��';	break;
			case 10:	ret = '��';	break;
			case 11:	ret = '��';	break;
			case 12:	ret = '��';	break;
			case 13:	ret = '��';	break;
			case 14:	ret = '��';	break;
			case 15:	ret = '��';	break;
			case 16:	ret = '��';	break;
			case 17:	ret = '��';	break;
			case 18:	ret = '��';	break;
		}
		return ret;
	}


	/**
	 * <pre>
	 * �ʼ��� ���� index�� ��ȯ�Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 22
	 * @param ch
	 * @return
	 */
	private static final int getChoIdx(char ch)
	{
		int ret = -1;
		switch (ch) {
			case '��':	ret = 0;	break;
			case '��':	ret = 1;	break;
			case '��':	ret = 2;	break;
			case '��':	ret = 3;	break;
			case '��':	ret = 4;	break;
			case '��':	ret = 5;	break;
			case '��':	ret = 6;	break;
			case '��':	ret = 7;	break;
			case '��':	ret = 8;	break;
			case '��':	ret = 9;	break;
			case '��':	ret = 10;	break;
			case '��':	ret = 11;	break;
			case '��':	ret = 12;	break;
			case '��':	ret = 13;	break;
			case '��':	ret = 14;	break;
			case '��':	ret = 15;	break;
			case '��':	ret = 16;	break;
			case '��':	ret = 17;	break;
			case '��':	ret = 18;	break;
		}
		return ret;
	}


	private static final char getJung(int idx)
	{
		char ret = 0;
		switch (idx) {
			case 0:		ret = '��';	break;
			case 1:		ret = '��';	break;
			case 2:		ret = '��';	break;
			case 3:		ret = '��';	break;
			case 4:		ret = '��';	break;
			case 5:		ret = '��';	break;
			case 6:		ret = '��';	break;
			case 7:		ret = '��';	break;
			case 8:		ret = '��';	break;
			case 9:		ret = '��';	break;
			case 10:	ret = '��';	break;
			case 11:	ret = '��';	break;
			case 12:	ret = '��';	break;
			case 13:	ret = '��';	break;
			case 14:	ret = '��';	break;
			case 15:	ret = '��';	break;
			case 16:	ret = '��';	break;
			case 17:	ret = '��';	break;
			case 18:	ret = '��';	break;
			case 19:	ret = '��';	break;
			case 20:	ret = '��';	break;
		}
		return ret;
	}


	/**
	 * <pre>
	 * �߼��� ���� index�� ��ȯ�Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 22
	 * @param ch
	 * @return
	 */
	private static final int getJungIdx(char ch)
	{
		int ret = -1;
		switch(ch)
		{
			case '��':	ret = 0;	break;
			case '��':	ret = 1;	break;
			case '��':	ret = 2;	break;
			case '��':	ret = 3;	break;
			case '��':	ret = 4;	break;
			case '��':	ret = 5;	break;
			case '��':	ret = 6;	break;
			case '��':	ret = 7;	break;
			case '��':	ret = 8;	break;
			case '��':	ret = 9;	break;
			case '��':	ret = 10;	break;
			case '��':	ret = 11;	break;
			case '��':	ret = 12;	break;
			case '��':	ret = 13;	break;
			case '��':	ret = 14;	break;
			case '��':	ret = 15;	break;
			case '��':	ret = 16;	break;
			case '��':	ret = 17;	break;
			case '��':	ret = 18;	break;
			case '��':	ret = 19;	break;
			case '��':	ret = 20;	break;
		}

		return ret;
	}


	private static final char getJong(int idx)
	{
		char ret = 0;
		switch (idx) {
			case 0:		ret = 0;	break;
			case 1:		ret = '��';	break;
			case 2:		ret = '��';	break;
			case 3:		ret = '��';	break;
			case 4:		ret = '��';	break;
			case 5:		ret = '��';	break;
			case 6:		ret = '��';	break;
			case 7:		ret = '��';	break;
			case 8:		ret = '��';	break;
			case 9:		ret = '��';	break;
			case 10:	ret = '��';	break;
			case 11:	ret = '��';	break;
			case 12:	ret = '��';	break;
			case 13:	ret = '��';	break;
			case 14:	ret = '��';	break;
			case 15:	ret = '��';	break;
			case 16:	ret = '��';	break;
			case 17:	ret = '��';	break;
			case 18:	ret = '��';	break;
			case 19:	ret = '��';	break;
			case 20:	ret = '��';	break;
			case 21:	ret = '��';	break;
			case 22:	ret = '��';	break;
			case 23:	ret = '��';	break;
			case 24:	ret = '��';	break;
			case 25:	ret = '��';	break;
			case 26:	ret = '��';	break;
			case 27:	ret = '��';	break;
		}
		return ret;
	}


	/**
	 * <pre>
	 * ������ ���� index�� ��ȯ�Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 22
	 * @param ch
	 * @return
	 */
	private static final int getJongIdx(char ch)
	{
		int ret = -1;
		switch(ch)
		{
			case 0:		ret = 0;	break;
			case ' ':	ret = 0;	break;
			case '��':	ret = 1;	break;
			case '��':	ret = 2;	break;
			case '��':	ret = 3;	break;
			case '��':	ret = 4;	break;
			case '��':	ret = 5;	break;
			case '��':	ret = 6;	break;
			case '��':	ret = 7;	break;
			case '��':	ret = 8;	break;
			case '��':	ret = 9;	break;
			case '��':	ret = 10;	break;
			case '��':	ret = 11;	break;
			case '��':	ret = 12;	break;
			case '��':	ret = 13;	break;
			case '��':	ret = 14;	break;
			case '��':	ret = 15;	break;
			case '��':	ret = 16;	break;
			case '��':	ret = 17;	break;
			case '��':	ret = 18;	break;
			case '��':	ret = 19;	break;
			case '��':	ret = 20;	break;
			case '��':	ret = 21;	break;
			case '��':	ret = 22;	break;
			case '��':	ret = 23;	break;
			case '��':	ret = 24;	break;
			case '��':	ret = 25;	break;
			case '��':	ret = 26;	break;
			case '��':	ret = 27;	break;
		}
		return ret;
	}


	/**
	 * <pre>
	 *
	 * </pre>
	 * @author Pilho Kim [phkim@cluecom.co.kr]
	 * @since	2001. 04. 20
	 * @param ch
	 * @return
	 */
	public static Hangul split(char ch)
	{
		Hangul hangul = new Hangul();
		int x = (ch & 0xFFFF), y = 0, z = 0;
		if( x >= 0xAC00 && x <= 0xD7A3 ) {
			y = x - 0xAC00;
			z = y % (21 * 28);
			hangul.cho = getCho(y / (21 * 28));
			hangul.jung = getJung(z / 28);
			hangul.jong = getJong(z % 28);
		} else if(x >= 0x3131 && x <= 0x3163) {
			if( getChoIdx(ch) > -1 ) {
				hangul.cho = ch;
			} else if( getJungIdx(ch) > -1 ) {
				hangul.jung = ch;
			} else if( getJongIdx(ch) > -1 ) {
				hangul.jong = ch;
			}
		} else {
			hangul.cho = ch;
		}
		return hangul;
	}


	/**
	 * <pre>
	 *
	 * </pre>
	 * @author Pilho Kim [phkim@cluecom.co.kr]
	 * @since	2001. 04. 20
	 * @param string
	 * @return
	 */
	public static String split(String string)
	{
		if( string == null ) return null;

		StringBuffer sb = new StringBuffer();
		for( int i = 0, stop = string.length(); i < stop; i++ ) {
			sb.append(split(string.charAt(i)));
		}
		return sb.toString();
	}


	/**
	 * <pre>
	 * �ʼ� �߼� ������ �о�鿩�� �ѱ��ڷ� ��ģ��.
	 * </pre>
	 * @author Pilho Kim [phkim@cluecom.co.kr]
	 * @since	2001. 04. 20
	 * @param cho	�ʼ�
	 * @param jung	�߼�
	 * @param jong	����
	 * @return
	 */
	public static char combine(char cho, char jung, char jong)
	{
		return (char) (getChoIdx(cho) * 21 * 28 + getJungIdx(jung) * 28 + getJongIdx(jong) + 0xAC00);
	}


	/**
	 * <pre>
	 * ���Ӹ��� ���������� �����ϴ� ���� ������ ������ �ٿ��� �� ���ڿ��� �����ش�.
	 * ���� + �� => ����
	 * ���� + ���ž� => ����ž�
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 24
	 * @param head
	 * @param tail
	 * @return
	 */
	public static String append(String head, String tail)
	{
		String ret = null;

		Hangul headTail = split(head.charAt(head.length() - 1));
		Hangul tailHead = split(tail.charAt(0));

		if( tailHead.hasJung() || headTail.hasJong() ) {
			ret = head + tail;
		} else {
			String headHead = head.substring(0, head.length() - 1);
			String tailTail = tail.substring(1);
			ret = headHead + combine(headTail.cho, headTail.jung, tailHead.cho) + tailTail;
		}
		return ret;
	}


	/**
	 * <pre>
	 * ������ ������ �ִ����� Ȯ���Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 3. 23
	 * @param ch
	 * @return
	 */
	public static boolean hasJong(char ch)
	{
		return split(ch).hasJong();
	}


	/**
	 * <pre>
	 * ������ ������ �ִ����� Ȯ���Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 3. 23
	 * @param string
	 * @return
	 */
	public static boolean hasJong(String string)
	{
		if( !Util.valid(string) ) return false;
		return hasJong(string.charAt(string.length() - 1));
	}



	/**
	 * <pre>
	 * �� ���ں��� �ѱ��� �����ϰ�, ���ص� ���� ������ ������ ���ڷ� �ٿ��ְ� �� ���ڴ����� :�� ��� ��ȯ���ش�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 8
	 * @param string
	 * @return
	 */
	private static String split2(String string)
	{
		if( string == null ) return null;
		String ret = "";
		for( int i = 0, stop = string.length(); i < stop; i++ ) {
			ret += split(string.charAt(i)).get() + ":";
		}
		return ret;
	}


	/**
	 * <pre>
	 * �ش� String�� pattern���� �������� Ȯ���ϴµ�,
	 * �� �� �� �� ���� �����̳� �������� �����ؼ� Ȯ���� �����ؼ� Ȯ����
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 8
	 * @param string
	 * @param pattern
	 * @return
	 */
	public static boolean endsWith(String string, String pattern)
	{
		if( !Util.valid(string) || !Util.valid(pattern) ) return false;
		int slen = string.length(), plen = pattern.length();
		if( slen < plen ) return false;
		char sch = 0, pch = 0;
		for( int i = 0; i < plen; i++ ) {
			sch = string.charAt(slen - i - 1);
			pch = pattern.charAt(plen - i - 1);
			if( pch != sch ) {
				if( i == plen - 1 ) return endsWith2(sch, pch);
				return false;
			}
		}
		return true;
	}


	/**
	 * <pre>
	 * �ش� char�� �������� Ȯ��
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 13
	 * @param sch
	 * @param pch
	 * @return
	 */
	public static boolean endsWith(char sch, char pch)
	{
		if( sch == pch ) return true;
		return endsWith2(sch, pch);
	}


	/**
	 * <pre>
	 * endsWith(char sch, char pch) �� ���� ����������, �� ���ڿ��� ���ٴ� ���� Ȯ������ �ʴ´�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 13
	 * @param sch
	 * @param pch
	 * @return
	 */
	private static boolean endsWith2(char sch, char pch)
	{
		String stemp = split(sch).get(), ptemp = split(pch).get();
		return stemp.endsWith(ptemp);
	}


	/**
	 * <pre>
	 * �־��� pattern���� �������� Ȯ���ϰ�, �־��� pattern���� ������ ���
	 * string���� pattern�κ��� �������ش�.
	 * '�Դϴ�' ���� '���ϴ�' �� �����ϸ� '��' �� ��ȯ�ȴ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 8
	 * @param string
	 * @param pattern
	 * @return
	 */
	public static String removeEnd(String string, String pattern)
	{
		// validity check
		if( !Util.valid(string) || !Util.valid(pattern) ) return string;
		int slen = string.length(), plen = pattern.length();
		if( slen < plen ) return string;
		if( string.endsWith(pattern) ) return string.substring(0, slen - plen);

		// ù���� �ܿ��� �������� ��ü�� �� �¾ƾ� �Ѵ�.
		if( !pattern.substring(1).equals(string.substring(slen - plen + 1)) ) return string;

		// �ѱ��ڰ� �ȵǰ�, ���� , ���� + ������ ���� ó������
		String stemp = split(string.charAt(slen - plen)).get();
		String ptemp = split(pattern.charAt(0)).get();
		if( !stemp.endsWith(ptemp) ) return string;
		String temp = stemp.substring(0, stemp.length() - ptemp.length());
		char[] ch = { 0, 0, 0 };
		for( int i = 0, stop = temp.length(); i < stop; i++ ) {
			ch[i] = temp.charAt(i);
		}
		String ret = slen > plen ? string.substring(0, slen - plen) : "";
		char rch = combine(ch[0], ch[1], ch[2]);
		if( rch == 0 ) return ret;
		return ret += combine(ch[0], ch[1], ch[2]);
	}


	/**
	 * <pre>
	 * len�� �ش��ϴ� ���̸� ���� ��̸� ��ȯ�Ѵ�.
	 * ��̴� '���ϴ�'�� ���� ������ ������ ���� ��ȯ���ش�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 9
	 * @param string
	 * @param len
	 * @return
	 */
	public static String extractExtraEomi(final String string, int len)
	{
		int strlen = string.length();
		if( !Util.valid(string) || strlen < len ) return null;
		Hangul hg = split(string.charAt(strlen - len));
		if( !hg.hasJong() ) return null;
		String temp = hg.get();
		return temp.charAt(temp.length() - 1) + string.substring(strlen - len + 1);
	}
}