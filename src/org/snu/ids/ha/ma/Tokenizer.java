/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 4. 30
 */
package org.snu.ids.ha.ma;

import java.util.ArrayList;

import org.snu.ids.ha.util.Hangul;
import org.snu.ids.ha.util.Util;



/**
 * <pre>
 *
 * </pre>
 * @author 	therocks
 * @since	2007. 4. 30
 */
public class Tokenizer
{
	/**
	 *
	 * @param string
	 * @return
	 */
	public static TokenList tokenize(String string)
	{
		if( !Util.valid(string) ) return null;
		ArrayList tokenizedResult = new ArrayList();

		char ch;
		String temp = "";
		int presentToken = 0, lastToken = 0, tokenIndex = 0;
		Character.UnicodeBlock ub = null;

		Hangul hg = null;
		for( int i = 0, stop = string.length(); i < stop; i++ ) {
			ch = string.charAt(i);
			lastToken = presentToken;
			ub = Character.UnicodeBlock.of(ch);
			if( ub == Character.UnicodeBlock.HANGUL_SYLLABLES
					|| ub == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO )
			{
				hg = Hangul.split(ch);
				if( !hg.hasCho() || !hg.hasJung() ) {
					presentToken = Token.CHAR_SET_ETC;
				} else {
					presentToken = Token.CHAR_SET_HANGUL;
				}
			} else if( ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
					|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS )
			{
				presentToken = Token.CHAR_SET_HANMUN;
			} else if( (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') ) {
				presentToken = Token.CHAR_SET_ENGLISH;
			} else if( ch >= '0' && ch <= '9' ) {
				presentToken = Token.CHAR_SET_NUMBER;
			} else if( ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n' ) {
				presentToken = Token.CHAR_SET_SPACE;
			} else if( ub == Character.UnicodeBlock.LETTERLIKE_SYMBOLS
					|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY
					|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
					|| ub == Character.UnicodeBlock.GREEK )
			{
				presentToken = Token.CHAR_SET_EXTRA;
			} else {
				presentToken = Token.CHAR_SET_ETC;
			}

			if( i != 0 && (lastToken != presentToken
					|| (presentToken == Token.CHAR_SET_ETC && !(temp.length() > 0 && temp.charAt(temp.length() - 1) == ch))) )
			{
				tokenizedResult.add(new Token(temp, lastToken, tokenIndex));
				tokenIndex = i;
				temp = "";
			}
			temp = temp + ch;

		}//end for i

		if( Util.valid(temp) ) tokenizedResult.add(new Token(temp, presentToken, tokenIndex));

		// ,ǥ�õ� ���� �ν� ó��
		Token token0, token1, token;
		int delimitedNumberStartIndex = 0;
		boolean validDelimitedNumber = false;
		for( int i = 0; tokenizedResult != null && i < tokenizedResult.size(); i++ ) {
			token0 = (Token) tokenizedResult.get(i);
			if( token0.isCharSetOf(Token.CHAR_SET_NUMBER) ) {
				delimitedNumberStartIndex = i;
				token = (Token)token0.clone();

				validDelimitedNumber = true;

				for( int j = 1; i + j < tokenizedResult.size(); j++ ) {
					token1 = (Token)tokenizedResult.get(i+j);
					// ���ڸ� ���߾��� ���ڰ� ����
					validDelimitedNumber = false;
					if( j % 2 == 0 ) {
						if( token1.isCharSetOf(Token.CHAR_SET_NUMBER) && token1.string.length() == 3) {
							token.string += token1.string;
							validDelimitedNumber = true;
						} else {
							i += j;
							break;
						}
					}
					// , Ȥ�� ���� �̿��� ���� ���;� ��
					else if(token1.equals(",")) {
						token.string += token1.string;
					}

					// �������� ��쳪 ,�� �ƴ϶� ������ ��쿡�� ����
					if( (j % 2 == 0 && validDelimitedNumber && i + j == tokenizedResult.size() -1)
							|| (j % 2 != 0 && !token1.equals(",")))
					{
						// �������̶� �P���� ���� �ϳ� �� ���־�� ��
						if(j % 2 == 0 && validDelimitedNumber && i + j == tokenizedResult.size() -1) {
							tokenizedResult.remove(delimitedNumberStartIndex);
						}
						for(int k = 0; k < j; k++) {
							tokenizedResult.remove(delimitedNumberStartIndex);
						}
						tokenizedResult.add(delimitedNumberStartIndex, token);
						break;
					}
				}
			}
		}

		// �Ҽ��� ���� �ν�ó��  : ����.���� => double�� ���ڷ� �ν�
		// 1000.00.00 ���� �͵��� ó�� ��������, �ϴ��� �Է��� ���� ���� ������ �����ϰ� ���ܵ� 2007-05-03
		for( int i = 0; tokenizedResult != null && i < tokenizedResult.size(); i++ ) {
			if( i < tokenizedResult.size() - 2
					&& (token0 = (Token) tokenizedResult.get(i)).isCharSetOf(Token.CHAR_SET_NUMBER)
					&& token0.string.indexOf(".") < 0
					&& ((Token) tokenizedResult.get(i + 1)).equals(".")
					&& (token1 = (Token) tokenizedResult.get(i + 2)).isCharSetOf(Token.CHAR_SET_NUMBER)
					&& token1.string.indexOf(".") < 0
					)
			{
				((Token) tokenizedResult.get(i)).string = token0.string + "." + token1.string;
				tokenizedResult.remove(i + 1);
				tokenizedResult.remove(i + 1);
				i--;
			}
		}


		return new TokenList(tokenizedResult);
	}
}
