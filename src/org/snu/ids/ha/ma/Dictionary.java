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
 * Singleton ���� ����� �� �ִ� ���¼� ����
 * ǥ�� ���¼ҿ� ���� Set���� �����Ǿ� �ִ�.
 * ǥ�� ���¼ҿ� ���� ���� �� �ִ�,
 * ���� ���¼��� ��м� ����Ϳ� �̵��� ���� ���� ���ǵ ���� ������ ������.
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
	 * ���� ���� ��θ� �о���δ�.
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
	 * Singleton���� ����ϱ� ���� ���� ��ü
	 * </pre>
	 * @since	2007. 6. 4
	 * @author	therocks
	 */
	private static Dictionary	dictionary	= null;


	/**
	 * <pre>
	 * ���� ��ü�� ���´�.
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
	 * ǥ�� ���¼ҿ� ���� ������ �����ϱ� ���� Hashtable
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
	 * singleton���� ����ϱ� ���� private ���� ������
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
	 * ��������, ���¼� �м��� ���� ��м� ������ �����صд�.
	 * ���� �ε� ������ ���� ������ ���ǻ󿡼��� �켱 ������ ������
	 * ��Ÿ ó���� �ֿ켱���� ��!!
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 */
	private void loadDic()
		throws Exception
	{
		// ���� ���� �ʱ�ȭ
		table = new Hashtable(530000);
		additionalCondTable = new Hashtable(1000);

		// �߰� ���� �ε�
		loadAddCond(DIC_ROOT + "MorphemeAdd.dic");

		// ���� ���� �ε�
		loadDic(DIC_ROOT + "MorphemeEomi.dic");
		loadDic(DIC_ROOT + "MorphemeJosa.dic");
		loadDic(DIC_ROOT + "MorphemeEtc.dic");
		loadFixed(DIC_ROOT + "MorphemeFixed.dic");
		loadVerb(DIC_ROOT + "MorphemeVerb.dic");
		loadDic(DIC_ROOT + "MorphemeSuffix.dic");
	}


	/**
	 * <pre>
	 * �ڵ� ���� ���� ������ �ε���
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
	 * ���� ��ȭ�� ���� �ʴ� ������ ���¿� ���� ��м� ���� �ε�
	 * ���, ����, ����, ������, �λ�
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
				if( arr.length < 3 ) throw new Exception("���� ������ ���������� �ʽ��ϴ�.\n=>[" + line + "]");

				String string = arr[0], hgClass = arr[1], compType = arr[2];
				MCandidate mCandidate = new MCandidate(string, hgClass, compType);
				// ���� �λ翡 ���ؼ� �ΰ� ó������
				if( arr.length > 3 ) mCandidate.firstMorpheme.hgEncoded |= HgType.getHgTypeNum(arr[3]);
				add(string, mCandidate);


				// ���� '��', '��'�� ���� ���� ���Ӹ� ó���� ���ش�.
				// �������� ���� ���� ���ؼ��� ó������
				// �ϴ� ���Ӹ� ó������ ���� -- 2007-07-23
//				if( mCandidate.firstMorpheme.isSufficientByOr(HgEncoded.OR_NOUN_CLASSES)
//						&& mCandidate.isHavingCondition(Condition.COND_NUM_MOEUM) )
//				{
//					MCandidate mCandidateClone = null;
//					char lastCh = string.charAt(string.length() -1);
//					String stem = string.substring(0, string.length() - 1), exp = null;
//					Hangul lastHg = Hangul.split(lastCh);
//
//					// '��' �ϳ��� -> �ϳ�
//					mCandidateClone = mCandidate.copy();
//					mCandidateClone.clearHavingCondition();
//					mCandidateClone.add(new Morpheme("��", "JO", "S", "CL", "SB"));
//					mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM);
//					exp = stem + Hangul.combine(lastHg.cho, lastHg.jung, '��');
//					add(exp, mCandidateClone);
//
//					// '��' �ϳ��� -> �ϳ�
//					mCandidateClone = mCandidate.copy();
//					mCandidateClone.clearHavingCondition();
//					mCandidateClone.add(new Morpheme("��", "JO", "S", "CL", "OB"));
//					mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM);
//					exp = stem + Hangul.combine(lastHg.cho, lastHg.jung, '��');
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
	 * ����, ����翡 ���� ������ �����Ѵ�.
	 * ����, ������� �⺻ �������κ��� ��� �����Ͽ� �÷��ش�.
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

			StringSet set1 = new StringSet(new String[] {"��", "��", "��", "��"});
			StringSet set2 = new StringSet(new String[] {"��", "��", "��"});
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

				// �⺻ � ���
				exp = stem;
				mCandidate = new MCandidate(exp, hgClass);
				mCandidate.addPreferedCondition(Condition.COND_NUM_AD);
				// having condition ����
				if( lastHg.hasJong() ) {
					mCandidate.addHavingCondition(Condition.COND_NUM_JAEUM);
				} else {
					mCandidate.addHavingCondition(Condition.COND_NUM_MOEUM);
					// ������� ���� �ٿ��ֱ� ���� ���� ���ǵ� �߰�����
					if( HgClass.MO_DOUBLE_SET.contains(lastHg.jung) ) {
						mCandidate.addHavingCondition(Condition.COND_NUM_JAEUM);
					}
				}
				if( HgClass.MO_POSITIVE_SET.contains(lastHg.jung + "") ) {
					mCandidate.addHavingCondition(Condition.COND_NUM_YANGSEONG);
				} else {
					mCandidate.addHavingCondition(Condition.COND_NUM_EUMSEONG);
				}
				if( !lastHg.hasJong() && lastHg.jung == '��') {
					mCandidate.addHavingCondition(Condition.COND_NUM_AH);
				}
				if( lastCh == '��' ) {
					mCandidate.addHavingCondition(Condition.COND_NUM_HA);
				} else if( lastCh == '��' || exp.equals("�ƴ�")) {
					mCandidate.addHavingCondition(Condition.COND_NUM_YI);
				}
				// �߰� ���� ����
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


				// �� �ִ� -> ��+��+��+�� �� ���� �ѱ��� � '��'�� ������ ��
				// 2007-07-06 �ʹ� ���� �ĺ������� �����ǹ����� ���� ����
				// ���� ���Ǵ� �͸� ���� ������ �߰��ϵ��� ��
				if( false && stem.length() == 1 && !lastHg.hasJong() && lastHg.cho != '��'  ) {
					if(  lastHg.jung == '��') {
						mCandidateClone = mCandidate.copy();
						mCandidateClone.clearHavingCondition();
						mCandidateClone.add(new Morpheme("��", "EM", "S", "CN", "SU"));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_YANGSEONG | Condition.COND_NUM_SU | Condition.COND_NUM_AH);
						mCandidateClone.realDicLen = (byte)exp.length();
						add(exp, mCandidateClone);
					} else if(  lastHg.jung == '��') {
						mCandidateClone = mCandidate.copy();
						mCandidateClone.clearHavingCondition();
						mCandidateClone.add(new Morpheme("��", "EM", "S", "CN", "SU"));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_SU | Condition.COND_NUM_AH);
						mCandidateClone.realDicLen = (byte)exp.length();
						add(exp, mCandidateClone);
					}
				}


				// ����� '��'�� ��� '��'�� ���԰� ����ϴ� ��찡 �����Ƿ� �̸� ó������
				if( lastCh == '��' || lastCh == '��' ) {
					mCandidateClone = mCandidate.copy();
					exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, '��');
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					mCandidateClone.bonus--;
					add(exp, mCandidateClone);
				}


				// ������ �ٿ��ֱ�
				if( lastCh == '��' ) {
					// �� -> �Ͽ�
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					exp = preStem + "��";
					mCandidateClone.add(new Morpheme("��", "EP", "S", "TM", null));
					mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_EUT);
					mCandidateClone.setExp(exp);
					mCandidateClone.candDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					// �� -> �Ͽ�
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					exp = preStem + "��";
					mCandidateClone.add(new Morpheme("��", "EM", "S", "CN", "SU"));
					mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_AH | Condition.COND_NUM_SU);
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					add(exp, mCandidateClone);
					
					// ������
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					exp = preStem + "��";
					mCandidateClone.add(new Morpheme("��", "EM", "S", "ED", "NM"));
					mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_AH | Condition.COND_NUM_SU);
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					// ������ ���� -> ġ �� �ٿ��� �� �ִ�.
					if( hgClass.equals("AJ") ) {
						mCandidateClone = mCandidate.copy();
						mCandidateClone.clearHavingCondition();
						exp = preStem + "ġ";
						mCandidateClone.add(new Morpheme("��", "EM", "S", "CN", "SU"));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_SU);
						mCandidateClone.setExp(exp);
						mCandidateClone.realDicLen = (byte) exp.length();
						add(exp, mCandidateClone);
					}
				}
				// '��'�� ������ ��
				else if( !lastHg.hasJong() && lastHg.jung == '��' ) {
					// �� -> �Ť�
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					exp = preStem + Hangul.combine(lastHg.cho, '��', '��');
					mCandidateClone.add(new Morpheme("��", "EP", "S", "TM", null));
					mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_EUT);
					mCandidateClone.setExp(exp);
					mCandidateClone.candDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					// �� -> �Ť�
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					exp = preStem + Hangul.combine(lastHg.cho, '��', ' ');
					mCandidateClone.add(new Morpheme("��", "EM", "S", "CN", "SU"));
					mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_AH| Condition.COND_NUM_SU);
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					add(exp, mCandidateClone);
				}
				// ��, ����, ����, �Ť�  ���տ� ���� � ���
				else if( !lastHg.hasJong() && set1.contains(lastHg.jung + "") ) {
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, '��');
					mCandidateClone.add(new Morpheme("��", "EP", "S", "TM", null));
					mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_EUT);
					mCandidateClone.setExp(exp);
					mCandidateClone.candDicLen = (byte)exp.length();
					add(exp, mCandidateClone);
				}
				// '��'�� ������ ��
				else if( lastCh == '��' ) {
					// ��
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					if( preLastCh == '��' ) {
						exp = preStem + "��";
						mCandidateClone.add(new Morpheme("��", "EP", "S", "TM", null));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM | Condition.COND_NUM_YANGSEONG | Condition.COND_NUM_EUT);
					} else if( preLastCh == 'Ǫ' ) {
						exp = stem + "��";
						mCandidateClone.add(new Morpheme("��", "EP", "S", "TM", null));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_EUT);
					} else {
						mo = getMoeum(lastHg, preLastHg);
						exp = stem.substring(0, strlen - 2)
						+ Hangul.combine(preLastHg.cho, preLastHg.jung, '��')
						+ Hangul.combine(lastHg.cho, mo, '��');
						if( mo == '��' ) {
							mCandidateClone.add(new Morpheme("��", "EP", "S", "TM", null));
						} else {
							mCandidateClone.add(new Morpheme("��", "EP", "S", "TM", null));
						}
						mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_EUT);
					}
					mCandidateClone.setExp(exp);
					mCandidateClone.candDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					// ��
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					if( preLastCh == '��' ) {
						exp = preStem + "��";
						mCandidateClone.add(new Morpheme("��", "EM", "S", "CN", "SU"));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_YANGSEONG | Condition.COND_NUM_AH | Condition.COND_NUM_SU);
					} else if( preLastCh == 'Ǫ' ) {
						exp = stem + "��";
						mCandidateClone.add(new Morpheme("��", "EM", "S", "CN", "SU"));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_AH | Condition.COND_NUM_SU);
					} else {
						mo = getMoeum(lastHg, preLastHg);
						exp = stem.substring(0, strlen - 2)
						+ Hangul.combine(preLastHg.cho, preLastHg.jung, '��')
						+ Hangul.combine(lastHg.cho, mo, ' ');
						if( mo == '��' ) {
							mCandidateClone.add(new Morpheme("��", "EM", "S", "CN", "SU"));
							mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_YANGSEONG | Condition.COND_NUM_AH | Condition.COND_NUM_SU);
						} else {
							mCandidateClone.add(new Morpheme("��", "EM", "S", "CN", "SU"));
							mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_AH | Condition.COND_NUM_SU);
						}
					}
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

				}
				// �� ���տ� ���� � ���
				else if( !lastHg.hasJong() && lastHg.jung == '��' ) {
					// �缺���� �ѹ� ����
					mo = getMoeum(lastHg, preLastHg);
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					exp = preStem + Hangul.combine(lastHg.cho, mo, '��');
					if( mo == '��' ) {
						mCandidateClone.add(new Morpheme("��", "EP", "S", "TM", null));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM | Condition.COND_NUM_YANGSEONG | Condition.COND_NUM_EUT);
					} else {
						mCandidateClone.add(new Morpheme("��", "EP", "S", "TM", null));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_EUT);
					}
					mCandidateClone.setExp(exp);
					mCandidateClone.candDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					// ��, ��
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					exp = preStem + Hangul.combine(lastHg.cho, mo, ' ');
					if( mo == '��' ) {
						mCandidateClone.add(new Morpheme("��", "EM", "S", "CN", "SU"));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_YANGSEONG | Condition.COND_NUM_AH | Condition.COND_NUM_SU);
					} else {
						mCandidateClone.add(new Morpheme("��", "EM", "S", "CN", "SU"));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_AH | Condition.COND_NUM_SU);
					}
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					add(exp, mCandidateClone);
				}
				// ��, �ǰ��տ� ���� � ���
				else if( !lastHg.hasJong() && set2.contains(lastHg.jung + "") ) {
					// ��, ��
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					exp = preStem + Hangul.combine(lastHg.cho, getMoeum(lastHg, preLastHg), '��');
					if( lastHg.jung == '��' ) {
						mCandidateClone.add(new Morpheme("��", "EP", "S", "TM", null));
					} else {
						mCandidateClone.add(new Morpheme("��", "EP", "S", "TM", null));
					}
					mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_EUT);
					mCandidateClone.setExp(exp);
					mCandidateClone.candDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					// ��, ��
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					exp = preStem + Hangul.combine(lastHg.cho, getMoeum(lastHg, preLastHg), ' ');
					if( lastHg.jung == '��' ) {
						mCandidateClone.add(new Morpheme("��", "EM", "S", "CN", "SU"));
					} else {
						mCandidateClone.add(new Morpheme("��", "EM", "S", "CN", "SU"));
					}
					mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_AH | Condition.COND_NUM_SU);
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					add(exp, mCandidateClone);
				}
				// ����� ó��
				else if( !lastHg.hasJong() && lastHg.jung != '��' ) {
					// '��' ����
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					exp = preStem + Hangul.combine(lastHg.cho, '��', ' ');
					mCandidateClone.add(new Morpheme("��", "EM", "S", "CN", "SU"));
					mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_AH | Condition.COND_NUM_SU);
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					// '��' ����
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					exp = preStem + Hangul.combine(lastHg.cho, '��', '��');
					mCandidateClone.add(new Morpheme("��", "EP", "S", "TM", null));
					mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_EUT);
					mCandidateClone.setExp(exp);
					mCandidateClone.candDicLen = (byte)exp.length();
					add(exp, mCandidateClone);
				}


				// �� �ұ�Ģ
				// ���ұ�Ģ Ȱ���ϴ� ��� ������ ����
				// '�˻̾þ�����������' ���� Ȱ�� ����~
				if( "���̰����������ų���������Ӷ��������ƷӸ����ʹӺ���������������ž".indexOf(lastCh) > -1 ) {

					// ��Ż���� ���� ����
					char bChar = Hangul.combine(lastHg.cho, lastHg.jung, ' ');

					// ���� '����' �Ӹ� �ƴ϶� ª�� '��' �����ε� Ȱ���
					if( lastCh == '��' ) {
						mCandidateClone = mCandidate.copy();
						mCandidateClone.clearHavingCondition();
						mCandidateClone.add(new Morpheme("��", "EM", "S", "FM", "DT"));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_NIEUN | Condition.COND_NUM_DT | Condition.COND_NUM_MINUS_BIEUB);
						exp = preStem + '��';
						mCandidateClone.setExp(exp);
						mCandidateClone.bonus--;
						mCandidateClone.realDicLen = (byte)exp.length();
						add(exp, mCandidateClone);
					}

					// ��, ��
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					if( lastHg.jung == '��') {
						mo = '��';
						mCandidateClone.add(new Morpheme("��", "EM", "S", "CN", "SU"));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_YANGSEONG | Condition.COND_NUM_AH | Condition.COND_NUM_SU);
					} else {
						mo = '��';
						mCandidateClone.add(new Morpheme("��", "EM", "S", "CN", "SU"));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_AH | Condition.COND_NUM_SU);
					}
					exp = preStem + bChar + Hangul.combine('��', mo, ' ');
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					// ��, ��
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					if( lastHg.jung == '��') {
						mo = '��';
						mCandidateClone.add(new Morpheme("��", "EP", "S", "TM", null));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM | Condition.COND_NUM_YANGSEONG | Condition.COND_NUM_EUT);
					} else {
						mo = '��';
						mCandidateClone.add(new Morpheme("��", "EP", "S", "TM", null));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_JAEUM | Condition.COND_NUM_EUMSEONG | Condition.COND_NUM_EUT);
					}
					exp = preStem + bChar + Hangul.combine('��', mo, '��');
					mCandidateClone.setExp(exp);
					mCandidateClone.candDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					// ��
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					mCandidateClone.addHavingCondition(Condition.COND_NUM_MOEUM | Condition.COND_NUM_EUMSEONG);
					exp = preStem + bChar + '��';
					mCandidateClone.setExp(exp);
					mCandidateClone.candDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					// ��, ��, �� �� ���� Ȱ��
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					mCandidateClone.add(new Morpheme("��", "EM", "S", "FM", "DT"));
					mCandidateClone.addHavingCondition(Condition.COND_NUM_NIEUN | Condition.COND_NUM_DT);
					exp = preStem + bChar + '��';
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					mCandidateClone.add(new Morpheme("��", "EM", "S", "FM", "DT"));
					mCandidateClone.addHavingCondition(Condition.COND_NUM_LIEUL | Condition.COND_NUM_DT);
					exp = preStem + bChar + '��';
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					mCandidateClone.add(new Morpheme("��", "EM", "S", "FM", "NN"));
					mCandidateClone.addHavingCondition(Condition.COND_NUM_MIEUM | Condition.COND_NUM_NN);
					exp = preStem + bChar + '��';
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					add(exp, mCandidateClone);
				}
				// '��' ���Ģ
				else if( "�����߳�����".indexOf(lastCh) > -1 )
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
				// �׿� ó��
				else if( !lastHg.hasJong() || lastHg.jong == '��'
					// �� �ұ�Ģ ó��
					|| lastCh == '��' || lastCh == '��' || lastCh == '��'
					)
				{
					// ��, ��, ��, �� �� ���� Ȱ��
					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					mCandidateClone.add(new Morpheme("��", "EM", "S", "FM", "DT"));
					mCandidateClone.addHavingCondition(Condition.COND_NUM_NIEUN | Condition.COND_NUM_DT);
					exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, '��');
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					mCandidateClone.add(new Morpheme("��", "EM", "S", "FM", "DT"));
					mCandidateClone.addHavingCondition(Condition.COND_NUM_LIEUL | Condition.COND_NUM_DT);
					exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, '��');
					mCandidateClone.setExp(exp);
					mCandidateClone.realDicLen = (byte)exp.length();
					add(exp, mCandidateClone);

					if( lastHg.jong == '��' ) {
						mCandidateClone = mCandidate.copy();
						mCandidateClone.clearHavingCondition();
						mCandidateClone.add(new Morpheme("��", "EM", "S", "FM", "NN"));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_MIEUM | Condition.COND_NUM_NN);
						exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, '��');
						mCandidateClone.setExp(exp);
						mCandidateClone.realDicLen = (byte)exp.length();
						add(exp, mCandidateClone);

						// ��Ż�� ���� ó��
						mCandidateClone = mCandidate.copy();
						mCandidateClone.clearHavingCondition();
						mCandidateClone.addHavingCondition(Condition.COND_NUM_MINUS_LIEUL);
						exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, ' ');
						mCandidateClone.setExp(exp);
						mCandidateClone.bonus--;
						mCandidateClone.candDicLen = (byte)exp.length();
						add(exp, mCandidateClone);
					}
					else if( lastHg.jong == '��' ) {
						// ��Ż�� ���� ó��
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
						mCandidateClone.add(new Morpheme("��", "EM", "S", "FM", "NN"));
						mCandidateClone.addHavingCondition(Condition.COND_NUM_MIEUM | Condition.COND_NUM_NN);
						exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, '��');
						mCandidateClone.setExp(exp);
						mCandidateClone.realDicLen = (byte)exp.length();
						add(exp, mCandidateClone);
					}

					mCandidateClone = mCandidate.copy();
					mCandidateClone.clearHavingCondition();
					mCandidateClone.addHavingCondition(Condition.COND_NUM_BIEUB);
					exp = preStem + Hangul.combine(lastHg.cho, lastHg.jung, '��');
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
	 * �⺻���� ���ԵǾ����� Ȯ��
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
	 * ��, ��, �ѿ� ����, �ä� �� ���յ� ���� ������ ��ȯ�Ѵ�.
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
		if( mo1 == '��' ) {
			mo = '��';
		} else if( mo1 == '��' ) {
			if( lastHg.cho == '��') {
				mo = '��';
			} else {
				mo = '��';
			}
		} else if( mo1 == '��' ) {
			if( preLastHg != null && HgClass.MO_POSITIVE_SET.contains(preLastHg.jung + "") ) {
				mo = '��';
			} else {
				mo = '��';
			}
		}
		return mo;
	}


	/**
	 * <pre>
	 * ���� ���·� �ۼ��� ���� ���Ϸκ��� ������ �о�鿩�� �������ش�.
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
	 * �ش� ǥ������ ���� ������ ��м� ����� �߰��Ѵ�.
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

		// ���� ���
		mc.calculateScore();
		// hashCode���
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
	 * ǥ�� ���¼ҿ� ���ؼ� ������ ��м� ����� ��ȯ�Ѵ�.
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
	 * ������ ���ָ� ������ �ε� �� ��� �̵�Ͼ ������ ���Խ����ش�.
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
	 * �ε��� ������ �־��� ���Ͽ� �ۼ��Ѵ�.
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
