/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 6. 4
 */
package org.snu.ids.ha.util;


import java.util.HashSet;


/**
 * <pre>
 * String������ HashSet���� �����Ͽ� ������ �ִµ�,
 * �ش� String ������ �ִ����� Ȯ���ϴ� �Լ�
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 4
 */
public class StringSet
	extends HashSet
{
	int	maxLen	= 0;


	public StringSet(String[] words)
	{
		super();
		addAll(words);
	}


	public boolean contains(char ch)
	{
		return super.contains(ch + "");
	}


	/**
	 * <pre>
	 * �ش� �迭�� ��� �ִ� ���ָ� �¿� �����Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 5. 7
	 * @param fileName
	 * @param showLog
	 */
	public void addAll(String[] words)
	{
		if( words == null ) return;
		int len = -1;
		String temp = null;
		for( int i = 0, stop = words.length; i < stop; i++ ) {
			temp = words[i];
			len = temp.length();
			add(temp);
			if( len > maxLen ) maxLen = len;
		}
	}
}
