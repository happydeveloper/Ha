/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 6. 4
 */
package org.snu.ids.ha.ma;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;

import org.snu.ids.ha.constants.Condition;
import org.snu.ids.ha.constants.HgClass;
import org.snu.ids.ha.constants.HgType;
import org.snu.ids.ha.util.Hangul;
import org.snu.ids.ha.util.StringSet;
import org.snu.ids.ha.util.Timer;
import org.snu.ids.ha.util.Util;


/**
 * <pre>
 * Singleton 으로 사용할 수 있는 형태소 사전
 * 표층 형태소에 의한 Set으로 구성되어 있다.
 * 표층 형태소에 대해 가질 수 있는,
 * 실재 형태소의 기분석 결과와와 이들의 접속 제한 조건등에 대한 정보를 가진다.
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 4
 */
public class Dictionary
{
	/**
	 * <pre>
	 * </pre>
	 * @since	2008. 02. 14
	 * @author	therocks
	 */
	public static final String DIC_ROOT = getDicRoot();

	/**
	 * <pre>
	 * 사전 저장 경로를 읽어들인다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 28
	 * @return
	 */
	private static final String getDicRoot()
	{
		String ret = System.getProperty("dicRoot");
		if( ret == null ) ret = "dic/";
		return ret;
	}

	/**
	 * <pre>
	 * Singleton으로 사용하기 위한 사전 객체
	 * </pre>
	 * @since	2007. 6. 4
	 * @author	therocks
	 */
	private static Dictionary	dictionary	= null;


	/**
	 * <pre>
	 * 사전 객체를 얻어온다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @return
	 */
	public static final Dictionary getInstance()
	{
		if( dictionary == null ) {
			dictionary = new Dictionary();
		}
		return dictionary;
	}


	/**
	 * <pre>
	 * 표층 형태소에 대한 정보를 저장하기 위한 Hashtable
	 * </pre>
	 * @since	2007. 6. 4
	 * @author	therocks
	 */
	Hashtable	table				= null;
	Hashtable	additionalCondTable	= null;
	int			maxLen				= 0;
	HashSet		stemSet	= null;


	/**
	 * <pre>
	 * singleton으로 사용하기 위해 private 으로 지정함
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 */
	private Dictionary()
	{
		Timer timer = new Timer();
		try {
			timer.start();
			loadDic();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			timer.stop();
			timer.printMsg("Dictionary Loading Time");
			System.out.println("Loaded Item " + table.size());
		}
	}


	/**
	 * <pre>
	 * 사전에서, 형태소 분석을 위한 기분석 사전을 생성해둔다.
	 * 사전 로딩 순서에 따라서 동일한 조건상에서의 우선 순위가 결정됨
	 * 기타 처리를 최우선으로 함!!
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 */
	private void loadDic()
		throws Exception
	{
		// 저장 공간 초기화
		table = new Hashtable(530000);
		additionalCondTable = new Hashtable(1000);

		// 추가 조건 로딩
		loadAddCond(DIC_ROOT + "MorphemeAdd.dic");

		// 사전 정보 로딩
		loadDic(DIC_ROOT + "MorphemeEomi.dic");
		loadDic(DIC_ROOT + "MorphemeJosa.dic");
		loadDic(DIC_ROOT + "MorphemeEtc.dic");
		loadFixed(DIC_ROOT + "MorphemeFixed.dic");
		loadVerb(DIC_ROOT + "MorphemeVerb.dic");
		loadDic(DIC_ROOT + "MorphemeSuffix.dic");
	}


	/**
	 * <pre>
	 * 자동 조건 외의 조건을 로딩함
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 28
	 * @param fileName
	 * @throws Exception
	 */
	private void loadAddCond(String fileName)
		throws Exception
	{
		System.out.println("Loading " + fileName);
		Timer timer = new Timer();
		timer.start();

		BufferedReader br = null;
		String line = null;
		try {
			br = new BufferedReader(new FileReader(fileName));

			String[] arr = null;
			String string = null, temp = null;
			while( (line = br.readLine()) != null ) {
				if( !Util.valid(line) || line.startsWith("//") ) continue;
				line = line.trim();
				arr = Util.split(line, ":");
				string = arr[0];
				if( arr.length < 2 ) continue;
				arr = Util.split(arr[1], ";");
				for( int i = 0, stop = arr.length; i < stop; i++ ) {
					temp = arr[i].trim();
					additionalCondTable.put(string, MCandidate.create(string, temp.substring(1, temp.length() - 1)));
				}
			}
			br.close();
		} catch (Exception e) {
			throw e;
		} finally {
			timer.stop();
			System.out.println("Loaded " + timer.getInterval() + "secs");
		}
	}


	/**
	 * <pre>
	 * 형태 변화를 하지 않는 고정된 형태에 대한 기분석 사전 로딩
	 * 명사, 대명사, 수사, 관형사, 부사
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param fileName
	 * @throws Exception
	 */
	private void loadFixed(String fileName)
		throws Exception
	{
		System.out.println("Loading " + fileName);
		Timer timer = new Timer();
		timer.start();

		BufferedReader br = null;
		String line = null;
		try {
			br = new BufferedReader(new FileReader(fileName));

			while( (line = br.readLine()) != null ) {
				if( !Util.valid(line) || line.startsWith("//") ) continue;
				line = line.trim();
				String[] arr = Util.split(line, ":");
				if( arr.length < 3 ) throw new Exception("사전 정보가 정상적이지 않습니다.\n=>[" + line + "]");

				String string = arr[0], hgClass = arr[1], compType = arr[2];
				MCandidate mCandidate = new MCandidate(string, hgClass, compType);
				// 접속 부사에 대해서 부가 처리해줌
				if( arr.length > 3 ) mCandidate.firstMorpheme.hgEncoded |= HgType.getHgTypeNum(arr[3]);
				add(string, mCandidate);


				// 명사는 '는', '를'에 대한 조사 줄임말 처리를 해준다.
				// 모음으로 끝난 말에 대해서만 처리해줌
				// 일단 줄임말 처리하지 않음 -- 2007-07-23
//				if( mCandidate.firstMorpheme.isSufficientByOr(HgEncoded.OR_NOUN_CLASSES)
//						&& mCandidate.isHavingCondition(Condition.COND_NUM_MOEUM) )
//				{
//					MCandidate mCandidateClone = null;
//					char lastCh = string.charAt(string.length() -1);
//					String stem = string.substring(0, string.length() - 1), exp = null;
//					Hangul lastHg = Hangul.split(lastCh);
//
//					// '는' 하나는 -> 하난
//					mCandidateClone = mCandidate.copy();
//					mCandidateClone.clearHavingCondition();
//					mCandidateClone.add(new Morpheme("는", "JO", "S", "CL", "SB"));
//					mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM);
//					exp = stem + Hangul.combine(lastHg.cho, lastHg.jung, 'ㄴ');
//					add(exp, mCandidateClone);
//
//					// '를' 하나를 -> 하날
//					mCandidateClone = mCandidate.copy();
//					mCandidateClone.clearHavingCondition();
//					mCandidateClone.add(new Morpheme("를", "JO", "S", "CL", "OB"));
//					mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM);
//					exp = stem + Hangul.combine(lastHg.cho, lastHg.jung, 'ㄹ');
//					add(exp, mCandidateClone);
//				}
			}
			br.close();
		} catch (Exception e) {
			throw e;
		} finally {
			timer.stop();
			System.out.println("Loaded " + timer.getInterval() + "secs");
		}
	}


	/**
	 * <pre>
	 * 동사, 형용사에 대한 사전을 구성한다.
	 * 동사, 형용사의 기본 사전으로부터 어간을 구성하여 올려준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 5
	 * @param fileName
	 * @throws Exception
	 */
	private void loadVerb(String fileName)
		throws Exception
	{
		System.out.println("Loading " + fileName);
		Timer timer = new Timer();
		timer.start();

		BufferedReader br = null;
		String line = null;
		stemSet = new HashSet();
		try {
			br = new BufferedReader(new FileReader(fileName));

			String[] arr = null;
			MCandidate mCandidate = null, mCandidateClone = null, mCandidateAddCond = null;
			String exp = null, stem = null, preStem = null;
			char lastCh = 0, preLastCh = 0, mo = 0;
			Hangul lastHg = null, preLastHg = null;
			int strlen = 0;

			StringSet set1 = new StringSet(new String[] {"ㅏ", "ㅓ", "ㅐ", "ㅔ"});
			StringSet set2 = new StringSet(new String[] {"ㅗ", "ㅜ", "ㅡ"});
			while( (line = br.readLine()) != null ) {
				if( !Util.valid(line) || line.startsWith("//") ) continue;
				line = line.trim();
				arr = Util.split(line, ":");

				String string = arr[0], hgClass = arr[1];
				stem = string.substring(0, string.length() - 1);
				stemSet.add(stem);

				strlen = stem.length();
				preStem = stem.substring(0, strlen - 1);
				lastCh = stem.charAt(strlen - 1);
				lastHg = Hangul.split(lastCh);
				if( strlen > 1 ) {
					preLastCh = stem.charAt(strlen - 2);
					preLastHg = Hangul.split(preLastCh);
				} else {
					preLastCh = 0;
					preLastHg = null;
				}

				// 기본 어간 출력
				exp = stem;
				mCandidate = new MCandidate(exp, hgClass);
				mCandidate.addPreferedCondition(Condition.COND_NUM_AD);
				// having condition 생성
				if( lastHg.hasJong() ) {
					mCandidate.addHavingCondition(Condition.COND_NUM_JAEUM);
				} else {
					mCandidate.addHavingCondition(Condition.COND_NUM_MOEUM);
					// 겹모음은 었을 붙여주기 위해 자음 조건도 추가해줌
					if( HgClass.MO_DOUBLE_SET.contains(lastHg.jung) ) {
						mCandidate.addHavingCondition(Condition.COND_NUM_JAEUM);
					}
				}
				if( HgClass.MO_POSITIVE_SET.contains(lastHg.jung + "") ) {
					mCandidate.addHavingCondition(Condition.COND_NUM_YANGSEONG);
				} else {
					mCandidate.addHavingCondition(Condition.COND_NUM_EUMSEONG);
				}
				if( !lastHg.hasJong() && lastHg.jung == 'ㅏ') {
					mCandidate.addHavingCondition(Condition.COND_NUM_AH);
				}
				if( lastCh == '하' ) {
					mCandidate.addHavingCondition(Condition.COND_NUM_HA);
				} else if( lastCh == '이' || exp.equals("아니")) {
					mCandidate.addHavingCondition(Condition.COND_NUM_YI);
				}
				// 추가 조건 설정
				mCandidateAddCond = (MCandidate) additionalCondTable.get(stem);
				if( mCandidateAddCond != null ) {
					mCandidate.appendableHgClassEncoded |= mCandidateAddCond.appendableHgClassEncoded;
					mCandidate.havingConditionEncoded |= mCandidateAddCond.havingConditionEncoded;
					mCandidate.checkingConditionEncoded |= mCandidateAddCond.checkingConditionEncoded;
					mCandidate.preferedConditionEncoded |= mCandidateAddCond.preferedConditionEncoded;
				}
				mCandidateClone = mCandidate.copy();
				mCandidateClone.candDicLen = (byte)exp.length();
				add(exp, mCandidateClone);


				// 사 주다 -> 사+아+주+다 와 같이 한글자 어간 'ㅏ'로 끝나는 말
				// 2007-07-06 너무 많은 후보군들이 생성되버려서 문제 생김
				// 많이 사용되는 것만 따로 사전에 추가하도록 함
				if( false && stem.length() == 1 && !lastHg.hasJong() && lastHg.cho != 'ㅎ'  ) {
					if(  lastHg.jung == 'ㅏ') {
						mCandidateClone = mCandidate.copy();
						mCandidateClone.clearHavingCondition();
						mCandidateClone.add(new Morpheme("아", "EM", "S", "CN", "SU"));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_YANGSEONG | Condition.COND_NUM_SU | Condition.COND_NUM_AH);
						mCandidateClone.realDicLen = (byte)exp.length();
						add(exp, mCandidateClone);
					} else if(  lastHg.jung == 'ㅓ') {
						mCandidateClone = mCandidate.copy();
						mCandidateClone.clearHavingCondition();
						mCandidateClone.add(new Morpheme("어", "EM", "S", "CN", "SU"));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_SU | Condition.COND_NUM_AH);
						mCandidateClone.realDicLen = (byte)exp.length();
						add(exp, mCandidateClone);
					}
				}


				// 겹모음 'ㄶ'의 경우 'ㅎ'을 빼먹고 사용하는 경우가 많으므로 이를 처리해줌
				if( lastCh == '찮' || lastCh == '잖' ) {
					mCandidateClone = mCandidate.copy();
					exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, 'ㄴ');
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					mCandidateClone.bonus--;
					add(exp, mCandidateClone);
				}


				// 과거형 붙여주기
				if( lastCh == '하' ) {
					// 했 -> 하였
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					exp = preStem + "했";
					mCandidateClone.add(new Morpheme("였", "EP", "S", "TM", null));
					mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_EUT);
					mCandidateClone.setExp(exp);
					mCandidateClone.candDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					// 해 -> 하여
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					exp = preStem + "해";
					mCandidateClone.add(new Morpheme("여", "EM", "S", "CN", "SU"));
					mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_AH | Condition.COND_NUM_SU);
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					add(exp, mCandidateClone);
					
					// 종결형
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					exp = preStem + "해";
					mCandidateClone.add(new Morpheme("여", "EM", "S", "ED", "NM"));
					mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_AH | Condition.COND_NUM_SU);
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					// 형용사는 하지 -> 치 로 줄여질 수 있다.
					if( hgClass.equals("AJ") ) {
						mCandidateClone = mCandidate.copy();
						mCandidateClone.clearHavingCondition();
						exp = preStem + "치";
						mCandidateClone.add(new Morpheme("지", "EM", "S", "CN", "SU"));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_SU);
						mCandidateClone.setExp(exp);
						mCandidateClone.realDicLen = (byte) exp.length();
						add(exp, mCandidateClone);
					}
				}
				// '이'로 끝나는 말
				else if( !lastHg.hasJong() && lastHg.jung == 'ㅣ' ) {
					// ㅣ -> ㅕㅆ
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					exp = preStem + Hangul.combine(lastHg.cho, 'ㅕ', 'ㅆ');
					mCandidateClone.add(new Morpheme("었", "EP", "S", "TM", null));
					mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_EUT);
					mCandidateClone.setExp(exp);
					mCandidateClone.candDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					// ㅣ -> ㅕㅆ
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					exp = preStem + Hangul.combine(lastHg.cho, 'ㅕ', ' ');
					mCandidateClone.add(new Morpheme("어", "EM", "S", "CN", "SU"));
					mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_AH| Condition.COND_NUM_SU);
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					add(exp, mCandidateClone);
				}
				// ㅆ, ㅏㅆ, ㅐㅆ, ㅕㅆ  결합에 의한 어간 출력
				else if( !lastHg.hasJong() && set1.contains(lastHg.jung + "") ) {
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, 'ㅆ');
					mCandidateClone.add(new Morpheme("었", "EP", "S", "TM", null));
					mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_EUT);
					mCandidateClone.setExp(exp);
					mCandidateClone.candDicLen = (byte)exp.length();
					add(exp, mCandidateClone);
				}
				// '르'로 끝나는 말
				else if( lastCh == '르' ) {
					// 았
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					if( preLastCh == '따' ) {
						exp = preStem + "랐";
						mCandidateClone.add(new Morpheme("았", "EP", "S", "TM", null));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM | Condition.COND_NUM_YANGSEONG | Condition.COND_NUM_EUT);
					} else if( preLastCh == '푸' ) {
						exp = stem + "렀";
						mCandidateClone.add(new Morpheme("었", "EP", "S", "TM", null));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_EUT);
					} else {
						mo = getMoeum(lastHg, preLastHg);
						exp = stem.substring(0, strlen - 2)
						+ Hangul.combine(preLastHg.cho, preLastHg.jung, 'ㄹ')
						+ Hangul.combine(lastHg.cho, mo, 'ㅆ');
						if( mo == 'ㅏ' ) {
							mCandidateClone.add(new Morpheme("았", "EP", "S", "TM", null));
						} else {
							mCandidateClone.add(new Morpheme("었", "EP", "S", "TM", null));
						}
						mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_EUT);
					}
					mCandidateClone.setExp(exp);
					mCandidateClone.candDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					// 아
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					if( preLastCh == '따' ) {
						exp = preStem + "라";
						mCandidateClone.add(new Morpheme("아", "EM", "S", "CN", "SU"));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_YANGSEONG | Condition.COND_NUM_AH | Condition.COND_NUM_SU);
					} else if( preLastCh == '푸' ) {
						exp = stem + "러";
						mCandidateClone.add(new Morpheme("어", "EM", "S", "CN", "SU"));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_AH | Condition.COND_NUM_SU);
					} else {
						mo = getMoeum(lastHg, preLastHg);
						exp = stem.substring(0, strlen - 2)
						+ Hangul.combine(preLastHg.cho, preLastHg.jung, 'ㄹ')
						+ Hangul.combine(lastHg.cho, mo, ' ');
						if( mo == 'ㅏ' ) {
							mCandidateClone.add(new Morpheme("아", "EM", "S", "CN", "SU"));
							mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_YANGSEONG | Condition.COND_NUM_AH | Condition.COND_NUM_SU);
						} else {
							mCandidateClone.add(new Morpheme("어", "EM", "S", "CN", "SU"));
							mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_AH | Condition.COND_NUM_SU);
						}
					}
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

				}
				// ㅡ 결합에 의한 어간 출력
				else if( !lastHg.hasJong() && lastHg.jung == 'ㅡ' ) {
					// 양성으로 한번 결합
					mo = getMoeum(lastHg, preLastHg);
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					exp = preStem + Hangul.combine(lastHg.cho, mo, 'ㅆ');
					if( mo == 'ㅏ' ) {
						mCandidateClone.add(new Morpheme("았", "EP", "S", "TM", null));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM | Condition.COND_NUM_YANGSEONG | Condition.COND_NUM_EUT);
					} else {
						mCandidateClone.add(new Morpheme("었", "EP", "S", "TM", null));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_EUT);
					}
					mCandidateClone.setExp(exp);
					mCandidateClone.candDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					// ㅓ, ㅏ
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					exp = preStem + Hangul.combine(lastHg.cho, mo, ' ');
					if( mo == 'ㅏ' ) {
						mCandidateClone.add(new Morpheme("아", "EM", "S", "CN", "SU"));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_YANGSEONG | Condition.COND_NUM_AH | Condition.COND_NUM_SU);
					} else {
						mCandidateClone.add(new Morpheme("어", "EM", "S", "CN", "SU"));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_AH | Condition.COND_NUM_SU);
					}
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					add(exp, mCandidateClone);
				}
				// ㅜ, ㅗ결합에 의한 어간 출력
				else if( !lastHg.hasJong() && set2.contains(lastHg.jung + "") ) {
					// 었, 았
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					exp = preStem + Hangul.combine(lastHg.cho, getMoeum(lastHg, preLastHg), 'ㅆ');
					if( lastHg.jung == 'ㅜ' ) {
						mCandidateClone.add(new Morpheme("었", "EP", "S", "TM", null));
					} else {
						mCandidateClone.add(new Morpheme("았", "EP", "S", "TM", null));
					}
					mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_EUT);
					mCandidateClone.setExp(exp);
					mCandidateClone.candDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					// 어, 아
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					exp = preStem + Hangul.combine(lastHg.cho, getMoeum(lastHg, preLastHg), ' ');
					if( lastHg.jung == 'ㅜ' ) {
						mCandidateClone.add(new Morpheme("어", "EM", "S", "CN", "SU"));
					} else {
						mCandidateClone.add(new Morpheme("아", "EM", "S", "CN", "SU"));
					}
					mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_AH | Condition.COND_NUM_SU);
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					add(exp, mCandidateClone);
				}
				// 겹모음 처리
				else if( !lastHg.hasJong() && lastHg.jung != 'ㅚ' ) {
					// 'ㅓ' 결합
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					exp = preStem + Hangul.combine(lastHg.cho, 'ㅙ', ' ');
					mCandidateClone.add(new Morpheme("어", "EM", "S", "CN", "SU"));
					mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_AH | Condition.COND_NUM_SU);
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					// '었' 결합
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					exp = preStem + Hangul.combine(lastHg.cho, 'ㅙ', 'ㅆ');
					mCandidateClone.add(new Morpheme("었", "EP", "S", "TM", null));
					mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_EUT);
					mCandidateClone.setExp(exp);
					mCandidateClone.candDicLen = (byte)exp.length();
					add(exp, mCandidateClone);
				}


				// ㅂ 불규칙
				// ㅂ불규칙 활용하는 어간의 마지막 어절
				// '뵙뽑씹업입잡접좁집' 들은 활용 안함~
				if( "갑겁겹곱굽깁깝껍꼽납눕답덥돕둡땁떱랍럽렵롭립맙맵밉볍섭쉽습엽줍쭙춥탑".indexOf(lastCh) > -1 ) {

					// ㅂ탈락된 음절 생성
					char bChar = Hangul.combine(lastHg.cho, lastHg.jung, ' ');

					// 럽은 '러운' 뿐만 아니라 짧게 '런' 등으로도 활용됨
					if( lastCh == '럽' ) {
						mCandidateClone = mCandidate.copy();
						mCandidateClone.clearHavingCondition();
						mCandidateClone.add(new Morpheme("ㄴ", "EM", "S", "FM", "DT"));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_NIEUN | Condition.COND_NUM_DT | Condition.COND_NUM_MINUS_BIEUB);
						exp = preStem + '런';
						mCandidateClone.setExp(exp);
						mCandidateClone.bonus--;
						mCandidateClone.realDicLen = (byte)exp.length();
						add(exp, mCandidateClone);
					}

					// 워, 와
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					if( lastHg.jung == 'ㅗ') {
						mo = 'ㅘ';
						mCandidateClone.add(new Morpheme("아", "EM", "S", "CN", "SU"));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_YANGSEONG | Condition.COND_NUM_AH | Condition.COND_NUM_SU);
					} else {
						mo = 'ㅝ';
						mCandidateClone.add(new Morpheme("어", "EM", "S", "CN", "SU"));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_AH | Condition.COND_NUM_SU);
					}
					exp = preStem + bChar + Hangul.combine('ㅇ', mo, ' ');
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					// 웠, 왔
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					if( lastHg.jung == 'ㅗ') {
						mo = 'ㅘ';
						mCandidateClone.add(new Morpheme("았", "EP", "S", "TM", null));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM | Condition.COND_NUM_YANGSEONG | Condition.COND_NUM_EUT);
					} else {
						mo = 'ㅝ';
						mCandidateClone.add(new Morpheme("었", "EP", "S", "TM", null));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_EUT);
					}
					exp = preStem + bChar + Hangul.combine('ㅇ', mo, 'ㅆ');
					mCandidateClone.setExp(exp);
					mCandidateClone.candDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					// 우
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_EUMSEONG);
					exp = preStem + bChar + '우';
					mCandidateClone.setExp(exp);
					mCandidateClone.candDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					// ㄴ, ㄹ, ㅁ 에 의한 활용
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					mCandidateClone.add(new Morpheme("ㄴ", "EM", "S", "FM", "DT"));
					mCandidateClone.addHavingCondition(Condition.COND_NUM_NIEUN | Condition.COND_NUM_DT);
					exp = preStem + bChar + '운';
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					mCandidateClone.add(new Morpheme("ㄹ", "EM", "S", "FM", "DT"));
					mCandidateClone.addHavingCondition(Condition.COND_NUM_LIEUL | Condition.COND_NUM_DT);
					exp = preStem + bChar + '울';
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					mCandidateClone.add(new Morpheme("ㅁ", "EM", "S", "FM", "NN"));
					mCandidateClone.addHavingCondition(Condition.COND_NUM_MIEUM | Condition.COND_NUM_NN);
					exp = preStem + bChar + '움';
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					add(exp, mCandidateClone);
				}
				// 'ㅅ' 뷸규칙
				else if( "젓짓긋낫붓잇".indexOf(lastCh) > -1 )
				{
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM | Condition.COND_NUM_MINUS_SIOT);
					exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, ' ');
					mCandidateClone.setExp(exp);
					mCandidateClone.bonus--;
					mCandidateClone.candDicLen = (byte)exp.length();
					add(exp, mCandidateClone);
				}
				// 그외 처리
				else if( !lastHg.hasJong() || lastHg.jong == 'ㄹ'
					// ㅎ 불규칙 처리
					|| lastCh == '맣' || lastCh == '갛' || lastCh == '랗'
					)
				{
					// ㄴ, ㄹ, ㅁ, ㅂ 에 의한 활용
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					mCandidateClone.add(new Morpheme("ㄴ", "EM", "S", "FM", "DT"));
					mCandidateClone.addHavingCondition(Condition.COND_NUM_NIEUN | Condition.COND_NUM_DT);
					exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, 'ㄴ');
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					mCandidateClone.add(new Morpheme("ㄹ", "EM", "S", "FM", "DT"));
					mCandidateClone.addHavingCondition(Condition.COND_NUM_LIEUL | Condition.COND_NUM_DT);
					exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, 'ㄹ');
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					if( lastHg.jong == 'ㄹ' ) {
						mCandidateClone = mCandidate.copy();
						mCandidateClone.clearHavingCondition();
						mCandidateClone.add(new Morpheme("ㅁ", "EM", "S", "FM", "NN"));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_MIEUM | Condition.COND_NUM_NN);
						exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, 'ㄻ');
						mCandidateClone.setExp(exp);
						mCandidateClone.realDicLen = (byte)exp.length();
						add(exp, mCandidateClone);

						// ㄹ탈락 현상 처리
						mCandidateClone = mCandidate.copy();
						mCandidateClone.clearHavingCondition();
						mCandidateClone.addHavingCondition(Condition.COND_NUM_MINUS_LIEUL);
						exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, ' ');
						mCandidateClone.setExp(exp);
						mCandidateClone.bonus--;
						mCandidateClone.candDicLen = (byte)exp.length();
						add(exp, mCandidateClone);
					}
					else if( lastHg.jong == 'ㅎ' ) {
						// ㅎ탈락 현상 처리
						mCandidateClone = mCandidate.copy();
						mCandidateClone.clearHavingCondition();
						mCandidateClone.addHavingCondition(Condition.COND_NUM_MINUS_HIEUT);
						exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, ' ');
						mCandidateClone.setExp(exp);
						mCandidateClone.bonus--;
						mCandidateClone.candDicLen = (byte)exp.length();
						add(exp, mCandidateClone);
					} else {
						mCandidateClone = mCandidate.copy();
						mCandidateClone.clearHavingCondition();
						mCandidateClone.add(new Morpheme("ㅁ", "EM", "S", "FM", "NN"));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_MIEUM | Condition.COND_NUM_NN);
						exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, 'ㅁ');
						mCandidateClone.setExp(exp);
						mCandidateClone.realDicLen = (byte)exp.length();
						add(exp, mCandidateClone);
					}

					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					mCandidateClone.addHavingCondition(Condition.COND_NUM_BIEUB);
					exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, 'ㅂ');
					mCandidateClone.setExp(exp);
					mCandidateClone.candDicLen = (byte)exp.length();
					add(exp, mCandidateClone);
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			timer.stop();
			System.out.println("Loaded  " + stemSet.size() + " declinable words.");
			System.out.println("Loaded  " + timer.getInterval() + "seconds.");
		}
	}
	
	
	/**
	 * <pre>
	 * 기본형에 포함되었는지 확인
	 * </pre>
	 * @author	therocks
	 * @since	2008. 03. 31
	 * @param stem
	 * @return
	 */
	public boolean contains(String stem)
	{
		return stemSet == null ? false : stemSet.contains(stem);
	}


	/**
	 * <pre>
	 * ㅗ, ㅜ, ㅡ에 ㅏㅆ, ㅓㅆ 이 결합될 때의 모음을 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 5
	 * @param mo1
	 * @return
	 */
	private char getMoeum(Hangul lastHg, Hangul preLastHg)
	{
		char mo = 0;
		char mo1 = lastHg.jung;
		if( mo1 == 'ㅗ' ) {
			mo = 'ㅘ';
		} else if( mo1 == 'ㅜ' ) {
			if( lastHg.cho == 'ㅍ') {
				mo = 'ㅓ';
			} else {
				mo = 'ㅝ';
			}
		} else if( mo1 == 'ㅡ' ) {
			if( preLastHg != null && HgClass.MO_POSITIVE_SET.contains(preLastHg.jung + "") ) {
				mo = 'ㅏ';
			} else {
				mo = 'ㅓ';
			}
		}
		return mo;
	}


	/**
	 * <pre>
	 * 사전 형태로 작성된 사전 파일로부터 정보를 읽어들여서 저장해준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param fileName
	 * @throws Exception
	 */
	private void loadDic(String fileName)
		throws Exception
	{
		System.out.println("Loading " + fileName);
		Timer timer = new Timer();
		timer.start();

		BufferedReader br = null;
		String line = null;
		try {
			br = new BufferedReader(new FileReader(fileName));

			String[] arr = null;
			String string = null, temp = null;
			while( (line = br.readLine()) != null ) {
				if( !Util.valid(line) || line.startsWith("//") ) continue;
				line = line.trim();
				arr = Util.split(line, ":");
				string = arr[0];
				if( arr.length < 2 ) continue;
				arr = Util.split(arr[1], ";");
				for( int i = 0, stop = arr.length; i < stop; i++ ) {
					temp = arr[i].trim();
					add(string, MCandidate.create(string, temp.substring(1, temp.length() - 1)));
				}
			}
			br.close();
		} catch (Exception e) {
			throw e;
		} finally {
			timer.stop();
			System.out.println("Loaded " + timer.getInterval() + "secs");
		}
	}


	/**
	 * <pre>
	 * 해당 표층형에 대한 가능한 기분석 결과를 추가한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param exp
	 * @param mc
	 */
	private void add(String exp, MCandidate mc)
		throws Exception
	{
		MExpression me = get(exp);

		// 점수 계산
		mc.calculateScore();
		// hashCode계산
		mc.calculateHashCode();
		if( me == null ) {
			me = new MExpression(exp, mc);
			table.put(exp, me);
			if( maxLen < exp.length() ) {
				maxLen = exp.length();
			}
		} else {
			me.add(mc);
		}
	}


	synchronized private MExpression get(String exp)
	{
		return (MExpression) table.get(exp);
	}


	/**
	 * <pre>
	 * 표층 형태소에 대해서 가능한 기분석 결과를 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param exp
	 * @return
	 */
	synchronized public MExpression getMExpression(String exp)
	{
		MExpression ret = get(exp);
		return ret == null ? null : ret.copy();
	}


	/**
	 * <pre>
	 * 미종결 어휘만 사전에 로딩 된 경우 미등록어를 사전에 포함시켜준다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 26
	 */
	private void checkComplete()
	{
		Timer timer = new Timer();
		try {
			timer.start();
			ArrayList list = new ArrayList(table.values());
			for( int i = 0, stop = list.size(); i < stop; i++ ) {
				MExpression me = (MExpression) list.get(i);
				me.sort();
				if( !me.isComplete() ) {
					me.add(new MCandidate(me.exp, 0));
				}
			}
			timer.stop();
			timer.printMsg("Unknown Word Registration!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * <pre>
	 * 로딩된 사전을 주어진 파일에 작성한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param fileName
	 */
	public void printToFile(String fileName)
	{
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileOutputStream(new File(fileName)));
			ArrayList list = new ArrayList(table.values());
			Collections.sort(list);
			for( int i = 0, stop = list.size(); i < stop; i++ ) {
				MExpression me = (MExpression) list.get(i);
				pw.println(me);
				pw.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if( pw != null ) pw.close();
		}
	}


	/**
	 * <pre>
	 *
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 11
	 * @param fileName
	 */
	public void printEncodedToFile(String fileName)
	{
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileOutputStream(new File(fileName)));
			ArrayList list = new ArrayList(table.values());
			Collections.sort(list);
			for( int i = 0, stop = list.size(); i < stop; i++ ) {
				pw.println(((MExpression)list.get(i)).getEncodedString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if( pw != null ) pw.close();
		}
	}
}
