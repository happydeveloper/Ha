package org.snu.ids.ha.ma;


/**
 * <pre>
 *
 * </pre>
 * @author 	therocks
 * @since	2007. 4. 30
 */
public class Token
{
	public static final int	CHAR_SET_SPACE		= 1;
	public static final int	CHAR_SET_HANGUL		= 2;
	public static final int	CHAR_SET_HANMUN		= 3;
	public static final int	CHAR_SET_ENGLISH	= 4;
	public static final int	CHAR_SET_NUMBER		= 5;
	public static final int	CHAR_SET_EXTRA		= 6;
	public static final int	CHAR_SET_ETC		= 7;
	public static final int	CHAR_SET_COMBINED	= 8;

	protected String		string				= null;			// ��ū ���ڿ�
	protected int			charSet				= CHAR_SET_ETC; // ��ū�� ���� ����
	protected int			index				= 0;			// ���ڿ������� ��ū�� ���� ����


	/**
	 * <pre>
	 * default constructor
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 14
	 */
	protected Token()
	{
		super();
	}


	/**
	 *
	 * @param string
	 * @param tokenType
	 */
	protected Token(String string, int tokenType)
	{
		this(string, tokenType, 0);
	}


	/**
	 *
	 * @param string
	 * @param charSet
	 * @param index
	 */
	public Token(String string, int charSet, int index)
	{
		setString(string);
		setCharSet(charSet);
		setIndex(index);
	}


	/**
	 * <pre>
	 * ������ ������ ���� Token�� �����Ͽ� ��ȯ�Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 14
	 * @return
	 */
	public Object clone()
	{
		return new Token(this);
	}


	/**
	 * <pre>
	 * ���ڿ��� �־��� string�� ������ Ȯ���Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 15
	 * @param string
	 * @return
	 */
	public boolean equals(String string)
	{
		return this.string != null && string != null && this.string.equals(string);
	}


	/**
	 * <pre>
	 * copy ���� ������.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 15
	 * @param token
	 */
	public Token(Token token)
	{
		this(token.string, token.charSet, token.index);
	}


	/**
	 * @return Returns the charSet.
	 */
	public int getCharSet()
	{
		return charSet;
	}


	/**
	 * <pre>
	 * Char Set Name�� ��ȯ�Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 3
	 * @return
	 */
	public String getCharSetName()
	{
		return getCharSet(charSet);
	}


	/**
	 * @return Returns the index.
	 */
	public int getIndex()
	{
		return index;
	}


	/**
	 * @return Returns the string.
	 */
	public String getString()
	{
		return string;
	}


	/**
	 * @param charSet The charSet to set.
	 */
	public void setCharSet(int charSet)
	{
		this.charSet = charSet;
	}


	/**
	 * @param index The index to set.
	 */
	public void setIndex(int index)
	{
		this.index = index;
	}


	/**
	 * @param string The string to set.
	 */
	public void setString(String string)
	{
		this.string = string;
	}


	/**
	 * <pre>
	 * �־��� token Ÿ�������� Ȯ���ϴ� �Լ�
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 3
	 * @param charSet
	 * @return
	 */
	public boolean isCharSetOf(final int charSet)
	{
		return this.charSet == charSet;
	}


	/**
	 * <pre>
	 * debugging �� �� Ȯ���Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 14
	 * @return
	 */
	public String toString()
	{
		return "(" + index + "," + string + "," + getCharSet(charSet) + ")";
	}


	/**
	 * <pre>
	 * char set name�� ��ȯ�Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 3
	 * @param tokenType
	 * @return
	 */
	public static String getCharSet(int tokenType)
	{
		String ret = null;
		switch (tokenType) {
			case CHAR_SET_SPACE:	ret = "Space";		break;
			case CHAR_SET_HANGUL:	ret = "Hangul";		break;
			case CHAR_SET_ENGLISH:	ret = "English";	break;
			case CHAR_SET_ETC:		ret = "Etc";		break;
			case CHAR_SET_NUMBER:	ret = "Number";		break;
			case CHAR_SET_HANMUN:	ret = "Hanmun";		break;
			case CHAR_SET_EXTRA:
			default:				ret = "Extra";		break;
		}
		return ret;
	}


	public Token copyToken()
	{
		Token copy = new Token();
		copy.string = string;
		copy.charSet = charSet;
		copy.index = index;
		return copy;
	}
}
