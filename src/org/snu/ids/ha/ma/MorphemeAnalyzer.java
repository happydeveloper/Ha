/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 6. 3
 */
package org.snu.ids.ha.ma;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.snu.ids.ha.constants.HgEncoded;
import org.snu.ids.ha.util.Timer;
import org.snu.ids.ha.util.Util;


/**
 * <pre>
 * �ѱ�, ����, ������ ���� ������ ���ڿ��� ���� ���¼� �м��� �����Ѵ�.
 * ���� ���α׷��� ����� Ȱ���ؼ� �Ѵ�.
 * ���� ������ �������� �ʰ�, token������ ���� ���α׷����� �Ͽ�,
 * ���⿡ ���� ������ �������� �Ѵ�.
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 3
 */
public class MorphemeAnalyzer
{
	Dictionary	dic	= null;


	/**
	 * <pre>
	 * default constructor
	 * ���� ��ü�� ���´�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 24
	 */
	public MorphemeAnalyzer()
	{
		dic = Dictionary.getInstance();
	}


	/**
	 * <pre>
	 * 
	 * </pre>
	 * @author	therocks
	 * @since	2008. 02. 14
	 * @param args
	 */
	public static final void main(String[] args)
	{
		
		String string = "���� ����Ƽ�� ������ ��Ȯ���� ���ϴ�. ��, ���� ����Ƽ�� ����Ǵ� ������ü�� ������ �ٸ� �μ��� �߽��� �ϸ� �ٸ� ������ �Ǵ� ������ ������ �� ���� ������ ���� �������� �ʴ� ������, Ȥ�� ���缱���� �߽��ϸ� ������ �״�� �ִ� ������ � ���� ��Ȯ�� ���ǰ� ����. ���� ����� ������ �̷��� ������ ��Ȯ�� �������� ���ϰ� �ִ�.";
//		string = "�ŵ������ؾ�";
//		string = "����,�ڿ��׸����ΰ�����ü�α��׽�õ����";
		string = "�츮�����鿡������ �Ϻ������ �Ļ��ǽþ��ݴ�?";
		
//		TokenList tl = Tokenizer.tokenize(string);
//		for(int i=0; i < tl.size(); i++) {
//			System.out.println(tl.get(i));
//		}
//		if( true ) return;
		
		
		try {
			MorphemeAnalyzer ma = new MorphemeAnalyzer();
			Timer timer = new Timer();
			ma.createLogger(null);
			timer.start();
			List ret = ma.analyze(string);
			ret = ma.postProcess(ret);
			timer.stop();
			timer.printMsg("Time");
			
			for( int i = 0, size = ret == null ? 0 : ret.size(); i < size; i++ ) {
//				System.out.println(((MExpression)ret.get(i)).toSimpleString());
				System.out.println(((MExpression)ret.get(i)).toString());
			}
			ma.closeLogger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * <pre>
	 * ���¼� �м��� �����Ѵ�.
	 * ���� ������� �ĺ� �м� ����� List�� ��ܼ� ��ȯ�ȴ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param string
	 * @return
	 * @throws Exception 
	 */
	public List analyze(String string)
		throws Exception
	{
		if( !Util.valid(string) ) return null;
		string = string.trim();

		// ��� ��ü
		ArrayList ret = new ArrayList();

		// ���� �� ������ �ɰ����� ��ū ����Ʈ�� �����.
		TokenList tokenList = Tokenizer.tokenize(string);

		// �𵨸��� �� �� �ִ� �� ó��
		combineModelToken(tokenList);
		
		// �ѱ� ��ū�� ���ؼ� ���¼� �м��� �����ϰ�, Ư����ȣ, ��ġ � ���� ���¼� ���� ������ �������ش�.
		MExpression preME = null, curME = null;
		for( int i = 0, stop = tokenList.size(); i < stop; i++ ) {
			Token token = tokenList.get(i);
			if( token.isCharSetOf(Token.CHAR_SET_SPACE) ) continue;
			List meList = analyze(preME, token);

			// ���� ����� Ȯ���ϸ鼭 �������� ���� ����� ���ش�.
			for( int j = 0, jStop = meList.size(); j < jStop; j++ ) {
				curME = (MExpression) meList.get(j);
				if( preME != null ) preME.pruneWithNext(curME);
				ret.add(curME);
				preME = curME;
			}
		}
		
		return ret;
	}


	/**
	 * <pre>
	 * �� ���� �Ǵ� �� ��������
	 * 1) ����-����
	 * 2) ����,����
	 * 3) ����,����
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 23
	 * @param tokenList
	 */
	private void combineModelToken(TokenList tokenList)
	{
		Token token = null, preToken = null;

		for( int i = 0, stop = tokenList.size(); i < stop; i++ ) {
			token = tokenList.get(i);

			// �𵨸��� �� �� �ִ� ��ū Ȯ��
			if( i > 0 ) {
				preToken = tokenList.get(i-1);
				if( preToken.charSet == Token.CHAR_SET_ENGLISH && token.string.equals("-")
						|| preToken.charSet == Token.CHAR_SET_COMBINED && token.string.equals("-")
						|| preToken.charSet == Token.CHAR_SET_ENGLISH && token.charSet == Token.CHAR_SET_NUMBER
						|| preToken.charSet == Token.CHAR_SET_COMBINED && token.charSet == Token.CHAR_SET_NUMBER
						|| preToken.charSet == Token.CHAR_SET_NUMBER && token.charSet == Token.CHAR_SET_ENGLISH
						|| preToken.charSet == Token.CHAR_SET_COMBINED && token.charSet == Token.CHAR_SET_ENGLISH )
				{
					preToken.string += token.string;
					preToken.charSet = Token.CHAR_SET_COMBINED;
					tokenList.remove(i);
					i--;
					stop--;
				}
			}
		}
	}


	/**
	 * <pre>
	 * �ش� token�� ���� ������ ���¼� �м� ����� ��ȯ�Ѵ�.
	 * �� ���忡 ���ؼ��� �������� MExpression�� ��ȯ�Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param preMe
	 * @param token
	 * @return
	 */
	private List analyze(MExpression preME, Token token)
		throws Exception
	{
		List ret = new ArrayList();
		// �ѱ� �̿��� �м� ���
		if( !token.isCharSetOf(Token.CHAR_SET_HANGUL) ) {
			// �ʹ� �� Ư�� ���� ����
			if( token.isCharSetOf(Token.CHAR_SET_ETC) && token.string.length() > 3 ) {
				token.string = token.string.substring(0, 3);
				token.charSet = Token.CHAR_SET_ETC;
			}
			// �ʹ� �� ���� �ܾ� ���� -
			else if( token.isCharSetOf(Token.CHAR_SET_ENGLISH) && token.string.length() > 10 ) {
				token.string = token.string.substring(0, 10);
				token.charSet = Token.CHAR_SET_ETC;
			}
			ret.add(new MExpression(token.string, new MCandidate(token)));
			return ret;
		}

		// �ѱۿ� ���� ��ū �м� ���
		String string = token.string;
		int strlen = string.length();
		MExpression tempHeadME = null, tempTailME = null, newME = null;

		MExpression[] exps = new MExpression[strlen];
		String substr = null, tail = null;
		int tailCutPos = 1;
		for( ; tailCutPos <= strlen; tailCutPos++ ) {
			// ���� �м� ���ڿ� ����
			substr = string.substring(0, tailCutPos);

			// ���� �м� �ĺ� ����
			MExpression curME = exps[tailCutPos - 1] = getMExpression(substr, token.index);

			// ���� ����� �������� �Ұ����� ��� ����
			curME.pruneWithPre(preME);

			// ���� ��������� ������ ���� Ȯ��
			for( int headCutPos = 1; headCutPos < tailCutPos; headCutPos++ ) {
				tail = substr.substring(headCutPos, tailCutPos);
				tempTailME = getMExpression(tail, token.index + headCutPos);
				tempHeadME = exps[headCutPos - 1];
				newME = tempHeadME.derive(tempTailME);
				newME.pruneWithPre(preME);
//				writeLog("==========================================[" + substr + "]");
//				writeLog(tempHeadME);
//				writeLog(tempTailME);
//				writeLog(newME);
//				writeLog(curME);
				curME.merge(newME);
//				writeLog(curME);
//				writeLog("==================================================");
			}
			
			// ������ ���������� head �������� �ʴ´�.
			if( tailCutPos == strlen ) continue;

			// �ҿ����� ���� head�� �������� �ʴ´�.
			if( !curME.isComplete() ) continue;

			
			// TODO Ȯ����� ���� Ȱ���ؼ� ó�� ���� �����ϵ��� �غ���.
			// �ĺ��� 3�� �̻��̰ų�, �ĺ��� 2�� �̻��̸� ���ڿ��� ���̰� 4�̻��̸� ����� �պκ�(commonHead)�� Ȯ���Ѵ�.
			// 2008-03-18: �Ϲ������� 5���� �̻󿡼� ©���ִ� ���� ���Ƽ� ����
			if( strlen > 5 && curME.size() > 3 && tailCutPos > 4 ) {
				// commonHead �� ��������
				String headStr = curME.getCommonHead();

				// ����� �պκ��� ���� �� �̸� �������� -> ���� ó��!!
				if( headStr != null ) {
					// commonHead�� �и����ش�.
					//writeLog("[HEAD]==============" + headStr);
					int headLen = headStr.length();
					String tailStr = curME.getExp().substring(headLen);
					MExpression[] headTailME = curME.divideHeadTailAt(headStr, token.index, tailStr, token.index + headLen);
					MExpression headME = headTailME[0];
					ret.add(headME);
					preME = headME;
					//writeLog(ret);

					// ��м� ������� �������ٰ� �״�� �������ش�.
					MExpression[] newExps = new MExpression[tailCutPos - headLen];
					for( int k = headLen, l = 0; k < tailCutPos; k++, l++ ) {
						headTailME = exps[k].divideHeadTailAt(headStr, token.index, tailStr.substring(0, l + 1), token.index + k);
						newExps[l] = headTailME[1];
					}

					// ���ο� ���ڿ��� �м��ϵ��� �м� ��� ���ڿ� ���� ����
					string = string.substring(headStr.length());
					strlen = string.length();
					exps = new MExpression[strlen];
					tailCutPos = 0;

					// ���� ��� copy
					for( int j = 0, stop = newExps.length; j < stop; j++) {
						exps[j] = newExps[j];
					}
					tailCutPos = tailStr.length();
				}
			}
		}

		// ������ ��� ����
		if( tailCutPos > 1 ) ret.add(exps[exps.length - 1]);

		// ������ ����� �ϰ� �ƴ� ��� �̵�Ͼ �߰��Ͽ� ��ȯ
		for( int i = 0, stop = ret.size(); i < stop; i++ ) {
			MExpression me = (MExpression) ret.get(i);
			if( me.size() == 0 || me.get(0).getDicLenOnlyReal() == 0 ) {
				me.add(new MCandidate(me.exp, token.index));
			}
		}

		return ret;
	}


	/**
	 * <pre>
	 * ��м� �������κ��� �ĺ� �м� ����� �����ų�, ����� ����� �ĺ��� �����Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param string
	 * @return
	 */
	private MExpression getMExpression(String string, int index)
		throws Exception
	{
		MExpression ret = dic.getMExpression(string);
		if( ret == null ) {
			ret = new MExpression(string, new MCandidate(string, index));
		} else {
			ret.setIndex(index);
		}
		
		return ret;
	}


	/**
	 * <pre>
	 * ���¼� �м� ����� �������� ���� ����, ���� ���� ���� �۾��� �����Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 26
	 * @param analyzedMorphemeResult
	 * @return 
	 * @throws Exception 
	 */
	public List postProcess(List analyzedMorphemeResult)
		throws Exception
	{
		// ���� ���� ����
		MExpression me1 = null, me2 = null, me3 = null;
		List temp = analyzedMorphemeResult;
		analyzedMorphemeResult = new ArrayList();

		// �յ� ���� Ȯ���ϸ鼭 ������
		int tempSize = temp == null ? 0 : temp.size();
		for( int i = 0; i < tempSize; i++ ) {
			me1 = (MExpression) temp.get(i);
			me1.resort(me2);
			me2 = me1;
		}

		// ���ĵ� ����� �������� ���� ����
		for( int i = 0; i < tempSize; i++ ) {
			me1 = (MExpression) temp.get(i);
			me1.prune();
			me1.sortFinally();
			try {
				analyzedMorphemeResult.addAll(me1.split());
			} catch (Exception e) {
				System.err.println(me1);
				throw e;
			}
		}

		// ����� Ȯ���ϸ鼭 �ٿ����� �Ǿ�� �ϴ� ���� �ٿ����� ����
		for( int i = 0; i < analyzedMorphemeResult.size() - 1; i++ ) {
			me1 = (MExpression) analyzedMorphemeResult.get(i);
			me2 = (MExpression) analyzedMorphemeResult.get(i + 1);
			if( !me2.isComplete() || me1.isOneEojeolCheckable() ) {
				me3 = me1.derive(me2);
				if( me3.isOneEojeol() ) {
					analyzedMorphemeResult.remove(i);
					analyzedMorphemeResult.remove(i);
					analyzedMorphemeResult.add(i, me3);
					i--;
				}
			}
		}
		
		// ù��° �ĺ� �м� ����� �տ� �����ΰ� ����Ǿ�߸� �ϴ� ��쿡�� ���ش�.
		me1 = (MExpression) analyzedMorphemeResult.get(0);
		for( int i = 0; i < me1.size(); i++ ) {
			MCandidate mc = me1.get(i);
			if( mc.checkingConditionEncoded != 0 || mc.firstMorpheme.isSufficientByOr(HgEncoded.OR_MUST_ATTACH_CLASSES) ) {
				me1.remove(i);
				i--;
			}
		}
		
		return analyzedMorphemeResult;
	}
	
	
	/**
	 * <pre>
	 * 
	 * </pre>
	 * @author	therocks
	 * @since	2008. 02. 14
	 * @param analyzedMorphemeResult
	 * @return
	 */
	public List divideToSentences(List analyzedMorphemeResult)
	{
		List ret = new ArrayList();
		
		// ���� ���� ����
		MExpression me1 = null;
		
		// ���� ������ �ɰ���
		Eojeol eojeol = null;
		Sentence sentence = null;
		for( int i = 0; i < analyzedMorphemeResult.size(); i++ ) {
			if( sentence == null) {
				sentence = new Sentence();
			}
			me1 = (MExpression) analyzedMorphemeResult.get(i);
			eojeol =  new Eojeol(me1);
			sentence.add(eojeol);

			if( eojeol.isEnding() ) {
				if( i < analyzedMorphemeResult.size() - 1 ) {
					while( i < analyzedMorphemeResult.size() - 1 ) {
						me1 = (MExpression) analyzedMorphemeResult.get(i + 1);
						if( me1.getExp().startsWith(".")
								|| me1.getExp().startsWith(",")
								|| me1.getExp().startsWith("?")
								|| me1.getExp().startsWith(";")
								|| me1.getExp().startsWith("~")
								|| HgEncoded.RIGHT_PARENTHESIS_SET.contains(me1.getExp()) )
						{
							sentence.add(new Eojeol(me1));
							i++;
						} else {
							break;
						}
					}
				}
				ret.add(sentence);
				sentence = null;
			}
		}
		// ������ ���� ����
		if( sentence != null && sentence.size() > 0) {
			ret.add(sentence);
		}

		// ���ʿ��� Symbol���� ó�����ش�.
		removeDummySymbol(ret);

		return ret;
	}


	/**
	 * <pre>
	 * ���ʿ��� Symbol���� �������ش�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 19
	 * @param sentenceList
	 */
	public void removeDummySymbol(List sentenceList)
	{
		Sentence sentence = null;

		Eojeol eojeol = null;
		for( int i = 0; i < sentenceList.size(); i++ ) {
			sentence = (Sentence) sentenceList.get(i);
			for( int j = 0; j < sentence.size(); j++ ) {
				eojeol = sentence.get(j);
				if( eojeol.isDummySymbol() ) {
					sentence.remove(j);
					j--;
				}
			}
			if( sentence.size() == 0 ) {
				sentenceList.remove(i);
				i--;
			}
		}
	}


	PrintWriter	logger		= null;		// �α� ��ü
	boolean		doLogging	= false;	// �α� ���� ����


	/**
	 * <pre>
	 * �ΰŸ� �����Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 24
	 */
	void createLogger(String fileName)
	{
		try {
			System.out.println("DO LOGGING!!");
			if( fileName == null )	logger = new PrintWriter(System.out);
			else 	logger = new PrintWriter(new FileWriter(fileName));
			doLogging = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * <pre>
	 * �α��� �����Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 24
	 */
	void closeLogger()
	{
		if( doLogging && logger != null ) logger.close();
		doLogging = false;
	}


	/**
	 * <pre>
	 * �α� ����
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 24
	 * @param obj
	 */
	private void writeLog(Object obj)
	{
		if( doLogging ) {
			if( obj instanceof ArrayList ) {
				List list = (List) obj;
				for( int i = 0, stop = list.size(); i < stop; i++ ) {
					logger.println(list.get(i));
				}
			} else {
				logger.println(obj);
			}
		}
	}
}