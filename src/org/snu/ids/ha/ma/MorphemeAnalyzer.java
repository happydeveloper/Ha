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
 * 한글, 숫자, 영문자 등을 포함한 문자열에 대한 형태소 분석을 수행한다.
 * 동적 프로그래밍 기법을 활용해서 한다.
 * 띄어쓰기 단위로 수행하지 않고, token단위로 동적 프로그래밍을 하여,
 * 띄어쓰기에 대한 내성을 가지도록 한다.
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
	 * 사전 객체를 얻어온다.
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
		
		String string = "문서 엔터티의 개념이 명확하지 못하다. 즉, 문서 엔터티에 저장되는 단위개체인 문서가 다른 부서로 발신을 하면 다른 문서가 되는 것인지 수정을 할 때는 문서가 새로 생성되지 않는 것인지, 혹은 결재선으로 발신하면 문서가 그대로 있는 것인지 등에 대한 명확한 정의가 없다. 개발 담당자 마저도 이러한 개념을 명확히 설명하지 못하고 있다.";
//		string = "매도인이해약";
//		string = "생명,자연그리고인간공동체인구및실천모임";
		string = "우리나라라면에서부터 일본라면이 파생되시었잖니?";
		
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
	 * 형태소 분석을 수행한다.
	 * 수행 결과에는 후보 분석 결과가 List에 담겨서 반환된다.
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

		// 결과 객체
		ArrayList ret = new ArrayList();

		// 문자 셋 단위로 쪼개서어 토큰 리스트로 만든다.
		TokenList tokenList = Tokenizer.tokenize(string);

		// 모델명이 될 수 있는 것 처리
		combineModelToken(tokenList);
		
		// 한글 토큰에 대해서 형태소 분석을 수행하고, 특수기호, 수치 등에 대한 형태소 지정 과정을 수행해준다.
		MExpression preME = null, curME = null;
		for( int i = 0, stop = tokenList.size(); i < stop; i++ ) {
			Token token = tokenList.get(i);
			if( token.isCharSetOf(Token.CHAR_SET_SPACE) ) continue;
			List meList = analyze(preME, token);

			// 이전 결과를 확인하면서 적합하지 않은 결과는 없앤다.
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
	 * 모델 명이 되는 것 설정해줌
	 * 1) 영문-영문
	 * 2) 영문,숫자
	 * 3) 숫자,영문
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

			// 모델명이 될 수 있는 토큰 확인
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
	 * 해당 token에 대한 가능한 형태소 분석 결과를 반환한다.
	 * 긴 문장에 대해서는 여러개의 MExpression을 반환한다.
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
		// 한글 이외의 분석 결과
		if( !token.isCharSetOf(Token.CHAR_SET_HANGUL) ) {
			// 너무 긴 특수 문자 제거
			if( token.isCharSetOf(Token.CHAR_SET_ETC) && token.string.length() > 3 ) {
				token.string = token.string.substring(0, 3);
				token.charSet = Token.CHAR_SET_ETC;
			}
			// 너무 긴 영어 단어 제거 -
			else if( token.isCharSetOf(Token.CHAR_SET_ENGLISH) && token.string.length() > 10 ) {
				token.string = token.string.substring(0, 10);
				token.charSet = Token.CHAR_SET_ETC;
			}
			ret.add(new MExpression(token.string, new MCandidate(token)));
			return ret;
		}

		// 한글에 대한 토큰 분석 결과
		String string = token.string;
		int strlen = string.length();
		MExpression tempHeadME = null, tempTailME = null, newME = null;

		MExpression[] exps = new MExpression[strlen];
		String substr = null, tail = null;
		int tailCutPos = 1;
		for( ; tailCutPos <= strlen; tailCutPos++ ) {
			// 현재 분석 문자열 설정
			substr = string.substring(0, tailCutPos);

			// 최초 분석 후보 생성
			MExpression curME = exps[tailCutPos - 1] = getMExpression(substr, token.index);

			// 직전 결과를 바탕으로 불가능한 결과 삭제
			curME.pruneWithPre(preME);

			// 현재 결과내에서 가능한 조합 확인
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
			
			// 마지막 지점에서는 head 추출하지 않는다.
			if( tailCutPos == strlen ) continue;

			// 불완전한 경우는 head를 추출하지 않는다.
			if( !curME.isComplete() ) continue;

			
			// TODO 확률기반 모델을 활용해서 처리 성능 개선하도록 해보자.
			// 후보가 3개 이상이거나, 후보가 2개 이상이며 문자열의 길이가 4이상이면 공통된 앞부분(commonHead)을 확인한다.
			// 2008-03-18: 일반적으로 5글자 이상에서 짤라주는 것이 좋아서 수정
			if( strlen > 5 && curME.size() > 3 && tailCutPos > 4 ) {
				// commonHead 를 지정해줌
				String headStr = curME.getCommonHead();

				// 공통된 앞부분이 있을 때 이를 추출해줌 -> 띄어쓰기 처리!!
				if( headStr != null ) {
					// commonHead를 분리해준다.
					//writeLog("[HEAD]==============" + headStr);
					int headLen = headStr.length();
					String tailStr = curME.getExp().substring(headLen);
					MExpression[] headTailME = curME.divideHeadTailAt(headStr, token.index, tailStr, token.index + headLen);
					MExpression headME = headTailME[0];
					ret.add(headME);
					preME = headME;
					//writeLog(ret);

					// 기분석 결과들을 기존에다가 그대로 유지해준다.
					MExpression[] newExps = new MExpression[tailCutPos - headLen];
					for( int k = headLen, l = 0; k < tailCutPos; k++, l++ ) {
						headTailME = exps[k].divideHeadTailAt(headStr, token.index, tailStr.substring(0, l + 1), token.index + k);
						newExps[l] = headTailME[1];
					}

					// 새로운 문자열을 분석하도록 분석 대상 문자열 정보 수정
					string = string.substring(headStr.length());
					strlen = string.length();
					exps = new MExpression[strlen];
					tailCutPos = 0;

					// 기존 결과 copy
					for( int j = 0, stop = newExps.length; j < stop; j++) {
						exps[j] = newExps[j];
					}
					tailCutPos = tailStr.length();
				}
			}
		}

		// 마지막 결과 저장
		if( tailCutPos > 1 ) ret.add(exps[exps.length - 1]);

		// 마지막 결과가 완결어가 아닌 경우 미등록어도 추가하여 반환
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
	 * 기분석 사전으로부터 후보 분석 결과를 얻어오거나, 비사전 결과로 후보를 생성한다.
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
	 * 형태소 분석 결과를 바탕으로 띄어쓰기 수정, 문장 구분 등의 작업을 수행한다.
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
		// 띄어쓰기 오류 수정
		MExpression me1 = null, me2 = null, me3 = null;
		List temp = analyzedMorphemeResult;
		analyzedMorphemeResult = new ArrayList();

		// 앞뒤 조건 확인하면서 재정렬
		int tempSize = temp == null ? 0 : temp.size();
		for( int i = 0; i < tempSize; i++ ) {
			me1 = (MExpression) temp.get(i);
			me1.resort(me2);
			me2 = me1;
		}

		// 정렬된 결과를 바탕으로 띄어쓰기 수행
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

		// 결과들 확인하면서 붙여쓰기 되어야 하는 것은 붙여쓰기 수행
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
		
		// 첫번째 후보 분석 결과가 앞에 무엇인가 연결되어야만 하는 경우에는 없앤다.
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
		
		// 띄어쓰기 오류 수정
		MExpression me1 = null;
		
		// 문장 단위로 쪼개기
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
		// 마지막 문장 저장
		if( sentence != null && sentence.size() > 0) {
			ret.add(sentence);
		}

		// 불필요한 Symbol들을 처리해준다.
		removeDummySymbol(ret);

		return ret;
	}


	/**
	 * <pre>
	 * 불필요한 Symbol들을 제거해준다.
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


	PrintWriter	logger		= null;		// 로깅 객체
	boolean		doLogging	= false;	// 로깅 여부 설정


	/**
	 * <pre>
	 * 로거를 생성한다.
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
	 * 로깅을 종료한다.
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
	 * 로깅 수행
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