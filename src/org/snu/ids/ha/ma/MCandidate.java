/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 6. 4
 */
package org.snu.ids.ha.ma;


import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.snu.ids.ha.constants.Condition;
import org.snu.ids.ha.constants.HgClass;
import org.snu.ids.ha.constants.HgEncoded;
import org.snu.ids.ha.util.Hangul;
import org.snu.ids.ha.util.Util;


/**
 * <pre>
 * ǥ������ ���� �ϳ��� ���¼� �м� �ĺ��� �����Ѵ�.
 * �м� �ĺ� ���¼� ��Ͽ� ���ٿ� ���� ����, �ռ� ���� ���� �ΰ� ������ �����Ѵ�.
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 4
 */
public class MCandidate
	extends MorphemeList
	implements Comparable
{
	long		appendableHgClassEncoded	= 0;	// ���� ������ ǰ�� ����
	long		havingConditionEncoded		= 0;	// ���� �ĺ��� ���� ���� ����
	long		checkingConditionEncoded	= 0;	// ������ �� Ȯ���ؾ� �ϴ� ����
	long		preferedConditionEncoded	= 0;	// ������ ������ ���� ��ȣ�Ǵ� ǰ��
													// ������ + ü��
													// �λ�� + ���
													// �λ� + ������
													// �λ� + �λ�

	/**
	 * <pre>
	 * �ĺ����� Scoring�� ���ؼ� ������ �������, �ĺ������ �����Ͽ� ������ ���� �Ǵ�
	 *  - ���� ������ : ������ �� �ܾ�� �νĵ� ������
	 *    -> ��) ü��, ü��� ���յ� ����, ��� ��̰� ������ ���յ� �ܾ�
	 *  - �ĺ� ������ : �������� ã��������, �� �������� �̺��� ��
	 *    -> ��) ü��� ���� ����, �������� ���� ���, �, ü��� �������� ���� ������ ����(Ȱ�� ����)
	 * ���� ���
	 *  - MCandidate.calculateScore()
	 * ���������� Sorting�� ������ MExpression.sortFinally()�� ���ؼ� ���� ���� ������ ��
	 * </pre>
	 */
	byte				realDicLen					= 0;	// ���� ������� ����� �� �ִ� �������� ����
	byte				candDicLen					= 0;	// �ĺ� �������� ����
	byte				spaceCnt					= 0;	// �߰��Ǿ�� �� ������ ��
	byte				bonus						= 0;	// ��ȣ ���ǿ� ���� BONUS
	byte				size						= 0;	// �м��� ���¼��� �� (Occams Razor�� �����ϱ� ����) ������ ���ÿ� ���
	byte				asize						= 0;	// ���� ������ �յ� MCandidate�� ����
	int					score						= 0;	// ���� ����
	/**
	 * <pre>
	 * �ߺ��� ���ϱ� ���ؼ� hashCode�� �����.
	 * MCandidate.getEncodedString().hashCode()�� ����Ͽ� ���
	 * hashCode�� �ߺ��Ͽ� ����ϴ� ���� ���ϱ� ���ؼ� ������ ������ ������ �̹� ���� hashCode�� ����ϵ��� ��
	 * <�߿�!!> ���� ���Ǿ�� �ϴ� ������ �� �ľ��Ͽ� MCandidate.calculateHashCode() �Լ��� ȣ�����־�� ��!!
	 * </pre>
	 * @since	2007. 7. 26
	 * @author	therocks
	 */
	int					hashCode					= 0;	// �ߺ��� ���ϱ� ���� hashCode�� �̸� ����صα� ����
	/**
	 * <pre>
	 * �и��� ǥ������ �����ϱ� ����
	 * ���� �������� ������ ǥ������ ��Ÿ���� ���ڿ��� ������
	 * ���Ⱑ �Ǿ�� �ϴ� ������ ���� ���ڿ��� �߰��ϴ� ������� ���� ����
	 * MCandidate.derive() �Լ����� ���� ó��
	 * </pre>
	 * @since	2007. 7. 26
	 * @author	therocks
	 */
	private ArrayList	expList						= null; // ���� �� ǥ���� ����Ʈ


	/**
	 * <pre>
	 * default constructor �⺻ �������� �������ش�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 */
	private MCandidate()
	{
		super();
		expList = new ArrayList();
	}


	/**
	 * <pre>
	 * Ȯ�ε��� ���� ���ֿ� ���� ��м� ��� ����
	 * �̵�� ��翡 ���� �ĺ� �����ÿ��� ����~
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param string
	 * @param index
	 */
	MCandidate(String string, int index)
		throws Exception
	{
		this();
		add(new Morpheme(string, index));
		initCondition(string);
		expList.add(string);
		calculateHashCode();
	}


	/**
	 * <pre>
	 * Ȱ������ �ʴ� ���ֿ� ���� ��м� ��� ����
	 * Dictionary.loadFixed()������ ����
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param string
	 * @param hgClass
	 * @param compType
	 */
	MCandidate(String string, String hgClass, String compType)
		throws Exception
	{
		this();
		add(new Morpheme(string, hgClass, compType));
		initCondition(string);
		expList.add(string);
		realDicLen = (byte)string.length();
	}


	/**
	 * <pre>
	 * Ȱ���ϴ� ����, ����翡 ���� � ��м� ��� ����
	 * Dictionary.loadVerb()������ ����!!
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 22
	 * @param string
	 * @param hgClass
	 * @throws Exception
	 */
	MCandidate(String string, String hgClass)
		throws Exception
	{
		this();
		add(new Morpheme(string, hgClass, "S"));
		initCondition(string);
		expList.add(string);
	}


	/**
	 * <pre>
	 * �ѱ� �̿��� Token������ �޾Ƶ鿩��, Token�� ���¼��� ������ �κ����� �������ش�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param token
	 */
	MCandidate(Token token)
		throws Exception
	{
		this();
		// �̵�Ͼ� �м� ��� �߰�
		add(new Morpheme(token));
		realDicLen = (byte)token.string.length();
		expList.add(token.string);
		initCondition();
	}


	/**
	 * <pre>
	 * �ڸ� ������ �������ش�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 */
	private void initCondition(String string)
	{
		// ���� ���� �ʱ�ȭ
		initPhonemeCondition(string);
		// ü�� ���ؼ��� ����� ���ӽ��� ������ �������־�� ��
		if( lastMorpheme.isSufficientByOr(HgEncoded.OR_NOUN_CLASSES) ) {
			addHavingCondition(Condition.COND_NUM_NN);
			addPreferedCondition(Condition.COND_NUM_DT);
			addAppendableHgClass(HgEncoded.PF);
		}
		// ������
		else if( lastMorpheme.isSufficientByOr(HgEncoded.OR_DT_CLASSES)) {
			addHavingCondition(Condition.COND_NUM_DT);
			addPreferedCondition(Condition.COND_NUM_AD);
		}
		// �λ��
		else if( lastMorpheme.isSufficientByAnd(HgEncoded.AD) ) {
			addHavingCondition(Condition.COND_NUM_AD);
			addPreferedCondition(Condition.COND_NUM_AD);
		}
	}


	/**
	 * <pre>
	 * Ư������ �����ڿ� ���� ���� ���� �� ��ȣ ���� ���� ����
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 26
	 * @throws Exception
	 */
	private void initCondition()
		throws Exception
	{
		if( lastMorpheme.isCharSetOf(Morpheme.CHAR_SET_ENGLISH) ) {
			addHavingCondition(Condition.COND_NUM_ENG);
		}
		addHavingCondition(Condition.COND_NUM_NR_SET);
		addHavingCondition(Condition.COND_NUM_NN);
		addPreferedCondition(Condition.COND_NUM_DT);
	}


	/**
	 * <pre>
	 * ���� ������ �ʱ�ȭ �Ѵ�. (����,����,�缺,����) ���� ����
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 26
	 * @param string
	 */
	private void initPhonemeCondition(String string)
	{
		Hangul hg = Hangul.split(string.charAt(string.length() - 1));
		if( hg.hasJong() ) {
			addHavingCondition(Condition.COND_NUM_JAEUM);
			if( !lastMorpheme.isSufficientByOr(HgEncoded.OR_EOMI_CLASSES) && hg.jong == '��' ) {
				addHavingCondition(Condition.COND_NUM_LIEUL);
			}
		} else {
			addHavingCondition(Condition.COND_NUM_MOEUM);
		}
		if( HgClass.MO_POSITIVE_SET.contains(hg.jung) ) {
			addHavingCondition(Condition.COND_NUM_YANGSEONG);
		} else {
			addHavingCondition(Condition.COND_NUM_EUMSEONG);
		}
	}


	/**
	 * <pre>
	 * ���纻�� ���� ��ȯ�Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 5
	 * @return
	 */
	MCandidate copy()
	{
		MCandidate clone = new MCandidate();
		clone.addAll(this);
		clone.appendableHgClassEncoded = this.appendableHgClassEncoded;
		clone.havingConditionEncoded = this.havingConditionEncoded;
		clone.checkingConditionEncoded = this.checkingConditionEncoded;
		clone.preferedConditionEncoded = this.preferedConditionEncoded;
		clone.candDicLen = this.candDicLen;
		clone.realDicLen = this.realDicLen;
		clone.spaceCnt = this.spaceCnt;
		clone.size = this.size;
		clone.asize = this.asize;
		clone.bonus = this.bonus;
		clone.hashCode = this.hashCode;
		clone.expList.addAll(this.expList);
		return clone;
	}
	
	
	/**
	 * <pre>
	 * �� �ĺ����� �м� ����� ���� index(offset)������ �������ش�.
	 * �м� ����� ���� ���庸�� ����� �� �����Ƿ� offset������ ��Ȯ�� ��ġ������ ���� ���� �ִ�.
	 * ���� ��� �ظ��� ��� �������� �����Ǳ� ������ �������.
	 * "��0/��1/ 2/��3/��4/��5/" -> ��0/+��1/+��2/ 3/�ٶ�4/+����6/
	 * </pre>
	 * @author	therocks
	 * @since	2008. 03. 31
	 * @param index
	 */
	void setIndex(int index)
	{
		Morpheme mp = null;
		int offset = 0;
		for( int i = 0, size = size(); i < size; i++ ) {
			mp = get(i);
			mp.setIndex(index + offset);
			offset += mp.string.length();
		}
	}


	/**
	 * <pre>
	 * ���¼� ������ ��� �ٿ��ش�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param mpList
	 */
	void addAll(MorphemeList mpList)
	{
		for( int i = 0, stop = mpList.size(); i < stop; i++ ) {
			add(mpList.get(i).copy());
		}
	}


	/**
	 * <pre>
	 * ���� ������ ǰ�������� �߰��Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param hgClass
	 */
	void addAppendableHgClass(String hgClass)
	{
		addAppendableHgClass(HgClass.getHgClassNum(hgClass));
	}


	/**
	 * <pre>
	 * ���� ������ ǰ�������� �߰��Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param hgClassNum
	 */
	void addAppendableHgClass(long hgClassNum)
	{
		appendableHgClassEncoded |= hgClassNum;
	}


	/**
	 * <pre>
	 * �־��� ǰ�簡 ���� ���� ǰ�翡 ���ԵǾ� �ִ��� Ȯ��
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param hgClassNum
	 * @return
	 */
	boolean isAppendableHgClass(long hgClassNum)
	{
		return (appendableHgClassEncoded & hgClassNum) > 0;
	}


	/**
	 * <pre>
	 * �־��� ǰ�簡 ���� ���� ǰ�翡 ���ԵǾ� �ִ��� Ȯ��
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param hgClass
	 * @return
	 */
	boolean isAppendableHgClass(String hgClass)
	{
		return isAppendableHgClass(HgClass.getHgClassNum(hgClass));
	}


	/**
	 * <pre>
	 * ���� ������ ǰ�������� �߰��Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param hgClasses
	 */
	void addAppendableHgClass(String[] hgClasses)
	{
		for( int i = 0, stop = hgClasses.length; i < stop; i++ ) {
			addAppendableHgClass(hgClasses[i]);
		}
	}


	/**
	 * <pre>
	 * ���� ������ ǰ�������� �߰��Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param hgClassNums
	 */
	void addAppendableHgClass(long[] hgClassNums)
	{
		for( int i = 0, stop = hgClassNums.length; i < stop; i++ ) {
			addAppendableHgClass(hgClassNums[i]);
		}
	}


	/**
	 * <pre>
	 * ��м� ����� ������ ���� ������ �߰��Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param condition
	 */
	void addHavingCondition(String condition)
	{
		addHavingCondition(Condition.getConditionNum(condition));
	}


	/**
	 * <pre>
	 * ��м� ����� ������ ���� ������ �߰��Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param conditionNum
	 */
	void addHavingCondition(long conditionNum)
	{
		havingConditionEncoded |= conditionNum;
	}


	/**
	 * <pre>
	 * �־��� ������ ������ �ִ��� Ȯ��
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param conditionNum
	 * @return
	 */
	boolean isHavingCondition(long conditionNum)
	{
		return (havingConditionEncoded & conditionNum) > 0;
	}


	/**
	 * <pre>
	 * �־��� ������ ������ �ִ��� Ȯ��
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param condition
	 * @return
	 */
	boolean isHavingCondition(String condition)
	{
		return isHavingCondition(Condition.getConditionNum(condition));
	}


	/**
	 * <pre>
	 * ��м� ����� ������ ���� ������ �߰��Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param conditions
	 */
	void addHavingCondition(String[] conditions)
	{
		for( int i = 0, stop = conditions.length; i < stop; i++ ) {
			addHavingCondition(conditions[i]);
		}
	}


	/**
	 * <pre>
	 * ��м� ����� ������ ���� ������ �߰��Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param conditionNums
	 */
	void addHavingCondition(long[] conditionNums)
	{
		for( int i = 0, stop = conditionNums.length; i < stop; i++ ) {
			addHavingCondition(conditionNums[i]);
		}
	}


	/**
	 * <pre>
	 * �ĺ� ��м� ����� ���� ���� ������ �����Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 */
	void clearHavingCondition()
	{
		this.havingConditionEncoded = 0;
	}


	/**
	 * <pre>
	 * ��м� ����� ���ӽ� Ȯ���ؾ��ϴ� ���� ������ �߰��Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param condition
	 */
	void addCheckingCondition(String condition)
	{
		addCheckingCondition(Condition.getConditionNum(condition));
	}


	/**
	 * <pre>
	 * ��м� ����� ���ӽ� Ȯ���ؾ��ϴ� ���� ������ �߰��Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param conditionNum
	 */
	void addCheckingCondition(long conditionNum)
	{
		checkingConditionEncoded |= conditionNum;
	}


	/**
	 * <pre>
	 * �־��� ������ Ȯ�� �������� ������ �ִ��� Ȯ��
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param conditionNum
	 * @return
	 */
	boolean isCheckingCondition(long conditionNum)
	{
		return (checkingConditionEncoded & conditionNum) > 0;
	}


	/**
	 * <pre>
	 * �־��� ������ Ȯ�� �������� ������ �ִ��� Ȯ��
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param conditionNum
	 * @return
	 */
	boolean isCheckingCondition(String conditionNum)
	{
		return isCheckingCondition(Condition.getConditionNum(conditionNum));
	}


	/**
	 * <pre>
	 * ��м� ����� ���ӽ� Ȯ���ؾ��ϴ� ���� ������ �߰��Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param conditions
	 */
	void addCheckingCondition(String[] conditions)
	{
		for( int i = 0, stop = conditions.length; i < stop; i++ ) {
			addCheckingCondition(conditions[i]);
		}
	}


	/**
	 * <pre>
	 * ��м� ����� ���ӽ� Ȯ���ؾ��ϴ� ���� ������ �߰��Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param conditions
	 */
	void addCheckingCondition(long[] conditioNums)
	{
		for( int i = 0, stop = conditioNums.length; i < stop; i++ ) {
			addCheckingCondition(conditioNums[i]);
		}
	}


	/**
	 * <pre>
	 * ���Ⱑ ������ �� �߰��� �� �ִ� ǰ�� ������ �߰��Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param hgClass
	 */
	void addPreferedCondition(String condition)
	{
		addPreferedCondition(Condition.getConditionNum(condition));
	}


	/**
	 * <pre>
	 * ���Ⱑ ������ �� �߰��� �� �ִ� ǰ�� ������ �߰��Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param conditionNum
	 */
	void addPreferedCondition(long conditionNum)
	{
		preferedConditionEncoded |= conditionNum;
	}


	/**
	 * <pre>
	 * �־��� ǰ�簡 �پ�� �����ؼ� ���� ���� ǰ�翡 ���ԵǾ� �ִ��� Ȯ��
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param conditionNum
	 * @return
	 */
	boolean isPreferedCondition(long conditionNum)
	{
		return (preferedConditionEncoded & conditionNum) > 0;
	}


	/**
	 * <pre>
	 * �־��� ǰ�簡 �پ�� �����ؼ� ���� ���� ǰ�翡 ���ԵǾ� �ִ��� Ȯ��
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param condition
	 * @return
	 */
	boolean isPreferedCondition(String condition)
	{
		return isPreferedCondition(Condition.getConditionNum(condition));
	}


	/**
	 * <pre>
	 * ���Ⱑ ������ �� �߰��� �� �ִ� ǰ�� ������ �߰��Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param conditions
	 */
	void addPreferedCondition(String[] conditions)
	{
		for( int i = 0, stop = conditions.length; i < stop; i++ ) {
			addPreferedCondition(conditions[i]);
		}
	}


	/**
	 * <pre>
	 * ���Ⱑ ������ �� �߰��� �� �ִ� ǰ�� ������ �߰��Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param conditionNums
	 */
	void addPreferedCondition(long[] conditionNums)
	{
		for( int i = 0, stop = conditionNums.length; i < stop; i++ ) {
			addPreferedCondition(conditionNums[i]);
		}
	}


	/**
	 * <pre>
	 * ǰ��� ������ ������ �� �ִ��� Ȯ����
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param conditionEncoded
	 * @return
	 */
	boolean isConditionAppendable(long conditionEncoded)
	{
		return (havingConditionEncoded & conditionEncoded) == conditionEncoded;
	}


	/**
	 * <pre>
	 * ǥ������ �������ش�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 20
	 * @param exp
	 */
	void setExp(String exp)
	{
		expList.clear();
		expList.add(exp);
	}


	/**
	 * <pre>
	 * ǥ���� ���ڿ��� ǥ����/ǥ���� ������ /�� �����ؼ� ��ȯ�Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 20
	 * @return
	 */
	String getExp()
	{
		StringBuffer sb = new StringBuffer();
		for( int i = 0, stop = expList.size(); i < stop; i++ ) {
			sb.append(expList.get(i));
		}
		return sb.toString();
	}


	/**
	 * <pre>
	 * toIdx�� �ش��ϴ� ���������� ǥ������ �����´�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 25
	 * @param toIdx
	 * @return
	 */
	String getExp(int toIdx)
	{
		StringBuffer sb = new StringBuffer();
		for( int i = 0, stop = Math.min(expList.size(), toIdx + 1); i < stop; i++ ) {
			sb.append(expList.get(i));
		}
		return sb.toString();
	}


	/**
	 * <pre>
	 * ���⸦ /�� �����ؼ� ��ȯ�Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 24
	 * @return
	 */
	String geExpStringWithSpace()
	{
		StringBuffer sb = new StringBuffer();
		for( int i = 0, stop = expList.size(); i < stop; i++ ) {
			if( i > 0 ) sb.append(" ");
			sb.append(expList.get(i));
		}
		return sb.toString();
	}


	/**
	 * <pre>
	 * ������ ��м� ����� �ĺ��� ���� ���������� Ȯ����!!
	 * ���, �, ���̻� ���� �� strict�ϰ� Ȯ����!!
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param addMCandidate
	 * @return
	 */
	boolean checkBasicAppendingCondition(MCandidate addMCandidate)
	{
		boolean ret = isConditionAppendable(addMCandidate.checkingConditionEncoded);
		if( ret ) {
			// ������ ��� Ȯ�� ������, ���� ����� �տ� ���簡 ���� ���Ѵ�.
			if( addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.JO)
					&& lastMorpheme.isSufficientByAnd(HgEncoded.JO) )
			{
				// ������ ���� �տ��� �پ��� ������� �� �� �ִ�.
				if( (addMCandidate.firstMorpheme.isSufficientByOr(HgEncoded.OR_CL_CN))
						&& !addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.JO_CL_DT)
						&& !lastMorpheme.isSufficientByAnd(HgEncoded.TYPE_SU)
						&& !lastMorpheme.isSufficientByAnd(HgEncoded.JO_CL_AD) ) {
					ret = false;
				}
			}
			// ���� �տ� Ư�� ���ڴ� ��ȣ ���� �� �� �ִ�. (')', ']', '}')
//			else if( lastMorpheme.isSufficientByAnd(HgEncoded.SY)
//					&& addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.JO)
//					&& !HgEncoded.RIGHT_PARENTHESIS_SET.contains(lastMorpheme.string) )
//			{
//				ret = false;
//			}
			// ������ ���� �տ��� ������ ��� �����縸 �� �� ����
			else if ( addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.CP)
					&& lastMorpheme.isSufficientByAnd(HgEncoded.JO)
					&& !lastMorpheme.isSufficientByAnd(HgEncoded.TYPE_SU)
					&& !lastMorpheme.isSufficientByAnd(HgEncoded.JO_CL_AD) )
			{
				ret = false;
			}
			// ������ ���� �տ� ���� ��̴� ������� �� �� ����
			else if ( addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.CP)
					&& lastMorpheme.isSufficientByAnd(HgEncoded.EM)
					&& !lastMorpheme.isSufficientByAnd(HgEncoded.EM_FM_NN) )
			{
				ret = false;
			}
			// ������ ���� �տ��� ���� �λ簡 �� �� ����
			else if ( addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.CP)
					&& lastMorpheme.isSufficientByAnd(HgEncoded.AD_CN) )
			{
				ret = false;
			}
			// ������ ���� ��� �������� ���� ���� or ����簡 �;���
			else if(this.lastMorpheme.isSufficientByAnd(HgEncoded.EM_SU)
					&& !addMCandidate.firstMorpheme.isSufficientByOr(HgEncoded.OR_VV_AJ_EM_JO) )
			{
				ret = false;
			}
			// �λ�� ���� �տ��� � ����� ��� ����� ������̸� �;���
			else if ( addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.JO_CL_AD)
					&& lastMorpheme.isSufficientByAnd(HgEncoded.EM)
					&& !lastMorpheme.isSufficientByAnd(HgEncoded.EM_FM_NN) )
			{
				ret = false;
			}
			// ���������� ��Ī�� ��̰� ����� �� ����
			else if( addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.EM_ED_HR)
					&& this.lastMorpheme.isSufficientByAnd(HgEncoded.EM_FM) )
			{
				ret = false;
			}
			// � ��̿� � ��̸� ������ �� ���� '��', '��', '��'���� ���� ������
			// Ȯ�� ���ǿ��� '��', '��', '��'���� �־�� �Ѵ�.
			else if( lastMorpheme.isSufficientByAnd(HgEncoded.EM)
					&& addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.EM)
					&& (this.havingConditionEncoded & Condition.COND_NUM_JA_SET) > 0
					&& (this.havingConditionEncoded
							& addMCandidate.checkingConditionEncoded
							& Condition.COND_NUM_JA_SET) == 0 )
			{
				ret = false;
			}
			// � ��̸� ������ �� ���� '��'�� ���� ������
			// Ȯ�� ���ǿ��� '��'�� �־�� �Ѵ�.
			else if( addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.EM)
					&& (this.havingConditionEncoded & Condition.COND_NUM_BIEUB) > 0
					&& (this.havingConditionEncoded
							& addMCandidate.checkingConditionEncoded
							& Condition.COND_NUM_BIEUB) == 0 )
			{
				ret = false;
			}
			// � ��̸� ������ �� ���� '��', '��', '��' Ż�������� ���� ������
			// Ȯ�� ���ǿ��� '��', '��', '��' Ż�������� �־�� �Ѵ�.
			else if( addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.EM)
					&& (this.havingConditionEncoded & Condition.COND_NUM_MINUS_JA_SET) > 0
					&& (this.havingConditionEncoded
							& addMCandidate.checkingConditionEncoded
							& Condition.COND_NUM_MINUS_JA_SET) == 0 )
			{
				ret = false;
			}
			// �⺻���� ���ԵǾ����� Ȯ���Ͽ� �⺻���� ���ԵǸ� �������� �ʵ��� ��.
			// * ��) ���+�� : '����ϴ�'�� ���� '�����'�� �̹� �⺻������ ��ϵǾ� �ֱ� ������ �������� �ʵ��� ��
			else if( addMCandidate.firstMorpheme.isSufficientByOr(HgEncoded.OR_VV_AJ) 
					&& lastMorpheme.isSufficientByAnd(HgEncoded.NN) 
					&& Dictionary.getInstance().contains(lastMorpheme.string + addMCandidate.firstMorpheme.string) )
			{
				ret = false;
			}
			// '��'
			else if( (this.havingConditionEncoded & Condition.COND_NUM_MINUS_SIOT) > 0
					&& (!addMCandidate.firstMorpheme.isSufficientByOr(HgEncoded.OR_EOMI_CLASSES)
					|| !addMCandidate.isHavingCondition(Condition.COND_NUM_YIEUNG)) )
			{
				ret = false;
			}
		}

		return ret;
	}


	/**
	 * <pre>
	 * ���� ������ �������� Ȯ��
	 * ���̻� ������ �ϴ� ��� �տ��� �ѱ��� �ܾ ���� �ʴ´ٰ� ����
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 27
	 * @param addMCandidate
	 * @return
	 */
	boolean isAppendable(MCandidate addMCandidate)
	{
		boolean appendable = addMCandidate.isAppendableHgClass(lastMorpheme.hgEncoded);
		if( appendable ) {
			if( addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.NN_FM_NN)
					&& lastMorpheme.string.length() == 1)
			{
				appendable = false;
			}
		}
		return appendable;
	}


	/**
	 * <pre>
	 * ���Ⱑ �Ǿ��� �� ������ �������� Ȯ��
	 * �������� VV, AJ, CP�� Ȱ���� �����̸�, ������ �ݵ�� ��� Ȥ�� � ��̰� �;� ��!!
	 *
	 * 2007-07-20 ����
	 *
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param addMCandidate
	 * @return
	 */
	boolean isAppendableAllowingSpace(MCandidate addMCandidate)
	{
		boolean appendable = false;

		// �յ� Ư�� ���ڴ� ��� ���� ����
		if( this.lastMorpheme.isSufficientByAnd(HgEncoded.SY)
				|| addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.SY) )
		{
			return true;
		}

		// ���� �Ұ����� ��쿡�� �⺻ ���� ���� Ȯ��
		if( (appendable = this.isAppendable(addMCandidate))
				|| lastMorpheme.isSufficientByOr(HgEncoded.OR_PRE_STRICT_CHECK_CLASSES)
				|| addMCandidate.firstMorpheme.isSufficientByOr(HgEncoded.OR_POST_STRICT_CHECK_CLASSES)
				|| addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.NN_FM_NN) )
		{
			appendable = appendable && checkBasicAppendingCondition(addMCandidate);
		}
		// ���� ������ ��쿡�� ���� ���� �Ұ����� �� ����
		else if(lastMorpheme.isSufficientByOr(HgEncoded.OR_DEFAULT_PRE_CLASS)){
			appendable = true;

			// � �������� ���, ��� ��̰� �;���
			if( lastMorpheme.isSufficientByOr(HgEncoded.OR_EOGAN_CLASSES) &&
					!addMCandidate.firstMorpheme.isSufficientByOr(HgEncoded.OR_EOMI_CLASSES))
			{
				appendable = false;
			}
			// ������ �������� ü�� �´�.
			// ���ӵ� ������� ������� (���� ������� ������ ���̶� ����)
			else if( this.isHavingCondition(Condition.COND_NUM_DT) ) {
				if( addMCandidate.firstMorpheme.isSufficientByOr(HgEncoded.OR_EOGAN_CLASSES)
						&& !addMCandidate.isHavingCondition(Condition.COND_NUM_DT))
				{
					appendable = false;
				}
			}
			// 
		}

		// ���� �տ��� Ư�� ���ڰ� �� ���� ����
		// "����(��ũ��)�� ���ƿ�" �� ���� ��ȣ�� �̿��Ͽ� ���縦 ���߿� ����� ���� ����
		if( !appendable ) {
			appendable = lastMorpheme.isSufficientByAnd(HgEncoded.SY)
						&& addMCandidate.lastMorpheme.isSufficientByAnd(HgEncoded.JO);
		}

		return appendable;
	}


	/**
	 * <pre>
	 * ������ �ĺ��� ������ �ĺ��� �����Ͽ� ��ȯ���ش�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param addMCandidate
	 * @return
	 */
	MCandidate derive(MCandidate addMCandidate)
	{
		MCandidate newMCandidate = null;

		// Ȱ��Ǵ� ���� �ٿ��� �� �ִ� ��쿡�� Ȯ�����ش�.
		if( isAppendableAllowingSpace(addMCandidate) ) {
			newMCandidate = new MCandidate();
			newMCandidate.addAll(this);
			newMCandidate.addAll(addMCandidate);
			newMCandidate.expList.addAll(this.expList);
			newMCandidate.spaceCnt = (byte) (this.spaceCnt + addMCandidate.spaceCnt);
			newMCandidate.bonus = (byte) (this.bonus + addMCandidate.bonus);
			newMCandidate.appendableHgClassEncoded = this.appendableHgClassEncoded;
			newMCandidate.preferedConditionEncoded = this.preferedConditionEncoded;
			newMCandidate.checkingConditionEncoded = this.checkingConditionEncoded;
			newMCandidate.havingConditionEncoded = addMCandidate.havingConditionEncoded;
			byte newBonus = (byte) Util.bitCount(this.havingConditionEncoded & addMCandidate.preferedConditionEncoded);

			// ���� ���� Ȯ��
			boolean isAppendable = this.isAppendable(addMCandidate);
			// �� ���ǿ� ���� ���� Ȯ�� -- ���� ���� ���� ������ ����
			if( !isAppendable ) {
				if( (isHavingCondition(Condition.COND_NUM_VV_A)
						&& addMCandidate.firstMorpheme.isSufficientByOr(HgEncoded.OR_VV_AJ))
					|| (isHavingCondition(Condition.COND_NUM_NN_A)
						&& addMCandidate.firstMorpheme.isSufficientByOr(HgEncoded.NN)) )
				{
					isAppendable = true;
				}
			}
			

			// ���� �Ұ����� ������ ���� ����
			if( !isAppendable ) {
				// ���� ���� ����
				newMCandidate.spaceCnt++;

				// ���� �Ұ����� ������ ��ȣ ���� �ο�
				newMCandidate.bonus += newBonus;

				// ����, ���� ǥ���� ����
				newMCandidate.list.add(this.size(), new MorphemeSpace(
						addMCandidate.appendableHgClassEncoded,
						this.havingConditionEncoded,
						addMCandidate.checkingConditionEncoded,
						addMCandidate.preferedConditionEncoded));
				newMCandidate.expList.add("");
			}
			// ���� ���� ���� ��
			else if( false ) {
				// ������ ���� ����� ������ �״�� ����
				newMCandidate.havingConditionEncoded |= (this.havingConditionEncoded & Condition.COND_NUM_SU);
			}

			// ���Ⱑ ó���� ǥ������ �������ش�.
			// ���Ⱑ �Ǿ��ٸ� "" �� �߰��Ǿ��� ������ ���� ó���� ǥ�������� �߰��ȴ�.
			newMCandidate.expList.add(
					// ���� ǥ������
					(String) newMCandidate.expList.remove(newMCandidate.expList.size() - 1)
					// �ٷ� ���� ǥ������ �ٿ���
					+ addMCandidate.expList.get(0));
			for( int i = 1, stop = addMCandidate.expList.size(); i < stop; i++ ) {
				newMCandidate.expList.add(addMCandidate.expList.get(i));
			}

			// ȣ�� ���� penalty
			if( addMCandidate.firstMorpheme.isSufficientByAnd(HgEncoded.JO_CL_EX) ) {
				newMCandidate.spaceCnt++;
				newMCandidate.spaceCnt++;
			}
			
			// ���+�� �� ���� [���]+�ϴ� ������ ��������� ��쿡�� [���]+�ϴ� �� �ϳ��� ����� ����Ͽ�
			// �տ� ����� ���� �ʰ�, �λ� �� �� �ֵ��� ������ ��������
			if( isAppendable && this.firstMorpheme == this.lastMorpheme && this.firstMorpheme.isSufficientByOr(HgEncoded.OR_NN_AD) 
					&& addMCandidate.firstMorpheme.isSufficientByOr(HgEncoded.OR_VV_AJ) )
			{
				newMCandidate.preferedConditionEncoded = addMCandidate.preferedConditionEncoded;
			}
			
			// ���� ������� �ĺ��� ���� ���� ���� ���
			newMCandidate.calculateDicLen();
		}

		return newMCandidate;
	}


	/**
	 * <pre>
	 * �ĺ� �м� ����� ���⸦ �������� �и����ش�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 20
	 * @return
	 */
	List split()
	{
		// ù��°�� �������� �����ϸ� �������ش�.
		if( this.get(0) instanceof MorphemeSpace ) {
			expList.remove(0);
			remove(0);
		}

		ArrayList ret = new ArrayList();
		MCandidate mc = new MCandidate();
		mc.appendableHgClassEncoded = appendableHgClassEncoded;
		mc.checkingConditionEncoded = checkingConditionEncoded;
		mc.preferedConditionEncoded = preferedConditionEncoded;
		Morpheme mp = null;
		int expIdx = 0;
		for( int i = 0, stop = size(); i < stop; i++ ) {
			mp = get(i);
			if( mp instanceof MorphemeSpace ) {
				if( i == 0 ) continue;
				mc.setExp((String)expList.get(expIdx));
				mc.havingConditionEncoded = ((MorphemeSpace)mp).havingConditionEncoded;
				expIdx++;
				ret.add(mc);
				mc = new MCandidate();
				mc.appendableHgClassEncoded = ((MorphemeSpace)mp).appendableHgClassEncoded;
				mc.checkingConditionEncoded = ((MorphemeSpace)mp).checkingConditionEncoded;
				mc.preferedConditionEncoded = ((MorphemeSpace)mp).preferedConditionEncoded;
			} else {
				mc.add(mp);
			}
		}
		mc.setExp((String)expList.get(expIdx));
		mc.havingConditionEncoded = havingConditionEncoded;
		mc.calculateDicLen();
		ret.add(mc);
		return ret;
	}


	/**
	 * <pre>
	 * ���� ���� ���� head������ �������ش�.
	 * spaceCnt�� �ּ� 1�� �̻��� ������ ����
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 23
	 * @return
	 */
	MCandidate[] divideHeadTail()
	{
		if( spaceCnt < 1 ) return null;
		MCandidate[] ret = new MCandidate[2];

		MCandidate headMC = ret[0] = new MCandidate();
		MCandidate tailMC = ret[1] = new MCandidate();

		// head ����
		headMC.appendableHgClassEncoded = appendableHgClassEncoded;
		headMC.checkingConditionEncoded = checkingConditionEncoded;
		headMC.preferedConditionEncoded = preferedConditionEncoded;
		Morpheme mp = null;
		int idx = 0, stop = size();
		for( ; idx < stop; idx++ ) {
			mp = get(idx);
			if( mp instanceof MorphemeSpace ) {
				if( idx == 0 ) continue;
				headMC.setExp((String) expList.get(0));
				headMC.havingConditionEncoded = ((MorphemeSpace) mp).havingConditionEncoded;

				// tail ����
				tailMC.appendableHgClassEncoded = ((MorphemeSpace)mp).appendableHgClassEncoded;
				tailMC.checkingConditionEncoded = ((MorphemeSpace)mp).checkingConditionEncoded;
				tailMC.preferedConditionEncoded = ((MorphemeSpace)mp).preferedConditionEncoded;
				tailMC.havingConditionEncoded = havingConditionEncoded;
				idx++;
				break;
			}
			headMC.add(mp);
		}

		// ������ �м� ��� ����
		if( idx < stop ) {
			for( ; idx < stop; idx++ ) {
				tailMC.add(get(idx));
			}
			// ǥ���� �߰�
			for( int i = 1, iStop = expList.size(); i < iStop; i++ ) {
				tailMC.expList.add(expList.get(i));
			}
		}
		headMC.calculateDicLen();
		tailMC.calculateDicLen();
		return ret;
	}


	/**
	 * <pre>
	 * head tail ���ڸ� �޾Ƶ鿩�� �ش� head, tail�� �߶��� �� �ִ� ��ġ�� ã�Ƽ�,
	 * head, tail�� �߶� ��ȯ���ش�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 25
	 * @param headStr
	 * @param headIndex
	 * @param tailStr
	 * @param tailIndex
	 * @return
	 * @throws Exception
	 */
	MCandidate[] divideHeadTailAt(String headStr,int headIndex, String tailStr, int tailIndex)
		throws Exception
	{
		int divideIdx = 0;
		for( int i = 0, stop = expList.size(); i < stop; i++ ) {
			if( getExp(i).equals(headStr) ) {
				break;
			}
			divideIdx++;
		}
		if( spaceCnt <= divideIdx ) {
			return new MCandidate[] {new MCandidate(headStr, headIndex), new MCandidate(tailStr, tailIndex)};
		}
		MCandidate[] ret = new MCandidate[2];

		MCandidate headMC = ret[0] = new MCandidate();
		MCandidate tailMC = ret[1] = new MCandidate();

		// head ����
		headMC.appendableHgClassEncoded = appendableHgClassEncoded;
		headMC.checkingConditionEncoded = checkingConditionEncoded;
		headMC.preferedConditionEncoded = preferedConditionEncoded;

		int spaceIdx = 0;
		int idx = 0, stop = size();
		for( ; idx < stop; idx++ ) {
			Morpheme mp = get(idx);
			if( mp instanceof MorphemeSpace ) {
				if( spaceIdx < divideIdx ) {
					headMC.add(mp);
					spaceIdx++;
					continue;
				}
				for( int j = 0, jStop = divideIdx + 1; j < jStop; j++ ) {
					headMC.expList.add(expList.get(j));
				}
				headMC.havingConditionEncoded = ((MorphemeSpace) mp).havingConditionEncoded;

				// tail ����
				tailMC.appendableHgClassEncoded = ((MorphemeSpace) mp).appendableHgClassEncoded;
				tailMC.checkingConditionEncoded = ((MorphemeSpace) mp).checkingConditionEncoded;
				tailMC.preferedConditionEncoded = ((MorphemeSpace) mp).preferedConditionEncoded;
				tailMC.havingConditionEncoded = havingConditionEncoded;
				idx++;
				break;
			}
			headMC.add(mp);
		}


		// ������ �м� ��� ����
		if( idx < stop ) {
			for( ; idx < stop; idx++ ) {
				tailMC.add(get(idx));
			}
			// ǥ���� �߰�
			for( int i = divideIdx + 1, iStop = expList.size(); i < iStop; i++ ) {
				tailMC.expList.add(expList.get(i));
			}
		}
		headMC.calculateDicLen();
		tailMC.calculateDicLen();

		return ret;
	}



	/**
	 * <pre>
	 * idx��° ���� ������ ���� ���¼Ұ� �̵�Ͼ����� Ȯ���Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 25
	 * @param idx
	 * @return
	 */
	boolean isNRBeforeOrAfterIthSpace(int idx)
	{
		if( idx >= this.spaceCnt ) return false;
		int spaceIdx = 0;
		for( int i = 0, stop = size() - 1; i < stop; i++ ) {
			Morpheme mp = get(i);
			if( mp instanceof MorphemeSpace ) {
				if( spaceIdx != idx ) {
					spaceIdx++;
					continue;
				}
				mp = get(i + 1);
				return get(i + 1).isSufficientByAnd(HgEncoded.NR)
					|| get(i - 1).isSufficientByAnd(HgEncoded.NR);
			}
		}
		return false;
	}


	/**
	 * <pre>
	 * �ѱ��� ���θ� ���� �Ǿ ����� �м� �ĺ����� Ȯ��
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 26
	 * @return
	 */
	boolean isComposedOfOnlyOneNouns()
	{
		if( getExp().length() != spaceCnt + 1 ) return false;
		if( bonus > 0 ) return false;
		for( int i = 0; i < size; i++ ) {
			Morpheme mp = get(i);
			if( mp instanceof MorphemeSpace ) continue;
			if( !mp.isSufficientByOr(HgEncoded.OR_NN_NP_UM_VV_AJ_EM) ) return false;
		}
		return true;
	}


	/**
	 * <pre>
	 * �м� ����� �������� hashCode�� �����Ѵ�.
	 * hashCode�� �ѹ��� �����Ѵ�.
	 * calculateDicLen() ȣ��ÿ� �����Ǵ� ���ڿ��� �������� �ѹ��� ����Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @return
	 */
	public int hashCode()
	{
		return hashCode;
	}


	/**
	 * <pre>
	 * hashCode�� ����Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 25
	 */
	void calculateHashCode()
	{
		hashCode = getEncodedString().hashCode();
	}


	/**
	 * <pre>
	 * ��м� ����� �������� Ȯ��
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param mc
	 * @return
	 */
	public boolean equals(Object obj)
	{
		return hashCode() == obj.hashCode();
	}


	/**
	 * <pre>
	 * ������ ���� ������ ���̰� ����� �� ������ ���̶�� �Ǵ��Ͽ�
	 * Sorting�� �� �����!!
	 * �м� ������ ���� ���� ���� ���� �����ϵ��� ����!!
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param arg0
	 * @return
	 */
	public int compareTo(Object arg0)
	{
		MCandidate comp = (MCandidate) arg0;
		int ret = comp.getScore() - this.getScore();
		return ret;
	}


	/**
	 * <pre>
	 * ���� �м� �ĺ��� ������ ��ȯ�Ѵ�.
	 * ������ calculateScore()���� �� ��å�� ���ϰ� ��ȯ�Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 22
	 * @return
	 */
	int getScore()
	{
		calculateScore();
		return score;
	}


	/**
	 * <pre>
	 * ���� �ĺ��� ���� ������ ����Ѵ�.
	 * �ѹ� ���� ������ ��� �����ȴ�.
	 *
	 * # ���� �ο� ��å
	 *   ���� �������� ����
	 *   �ĺ� �������� ����
	 *   ���� ��
	 *   ��ȣ ������ ����
	 *   �� ������ ���ĵ� �� �ֵ��� ���� �ο�
	 *
	 * ���� ����� ������ ��쿡�� ����ȴ�.
	 *   1) ��м� ���� �����
	 *   2) �ĺ����� derive ȣ���
	 *   3) bonus�� ���� ��
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 22
	 */
	void calculateScore()
	{
		score = this.realDicLen + this.candDicLen;
		score = score * 100 - spaceCnt;
		score = score * 100 + this.realDicLen;
		score = score * 100 + this.bonus;
		// Occams Razor����
		//score = score * 100 - size;
	}


	/**
	 * <pre>
	 * ���� ���� ���̸� ��ȯ�Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 21
	 * @return
	 */
	int getDicLenOnlyReal()
	{
		return this.realDicLen;
	}


	/**
	 * <pre>
	 * ������� ���� �ͱ��� ���� ���ַ� ����Ͽ� ���� ���� ���̸� ��ȯ�Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 21
	 * @return
	 */
	int getDicLenWithCand()
	{
		return this.candDicLen + this.realDicLen;
	}


	/**
	 * <pre>
	 * �ĺ� �������� ���̸� ��ȯ�Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 25
	 * @return
	 */
	int getDicLenOnlyCand()
	{
		return this.candDicLen;
	}


	/**
	 * <pre>
	 * �����̳� ���� �ҿ����� class�� ������ �ҿ����� ���¼� �м� �����
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 21
	 * @return
	 */
	boolean isComplete()
		throws Exception
	{
		return candDicLen == 0;
	}


	/**
	 * <pre>
	 * ������� ��������� ���̸� ����Ѵ�.
	 * overhead�� �����ϴ� method�̹Ƿ� �� �ʿ��� ��쿡�� ȣ���Ѵ�.
	 * derive method�������� ȣ���Ѵ�!!
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 23
	 */
	private void calculateDicLen()
	{
		// ���¼� �� ��ȯ
		size = (byte)size();
		spaceCnt = 0;

		byte realDicLen = 0, candDicLen = 0;

		int expIdx = 0;
		int tempDicLen = 0, nrDicLen = 0;
		boolean isPreWord = false, hasJo = false;
		short conjugationCnt = 0;


		Morpheme mp = null;
		for( int i = 0, stop = size + 1; i < stop; i++ ) {
			if( i < size ) mp = get(i);
			else mp = null;

			if( mp == null || mp instanceof MorphemeSpace ) {
				// �Ϸ� ���� Ȯ��
				boolean conjugationCompleted = conjugationCnt == 0
					|| (!(conjugationCnt % 100 == 0) && (conjugationCnt % 2 == 0));

				// ǥ���� ������ֱ� '_' �� ������ ���� ���⸦ ������ �Ѱ����� ���Ӹ��� ó��
				String exp = (String) expList.get(expIdx);

				// ������, ������� ����
				// ���Ӹ��� ��쿡�� ���Ӹ� �պκ��� �Ϸ� ���ο� ���� �Ϸ� ���� ����
				if( (!hasJo || isPreWord) && conjugationCompleted ) {
					realDicLen += exp.length() - nrDicLen;
				} else {
					candDicLen += exp.length() - nrDicLen;
				}

				// ��ȣ ���� ����
				// ����� ������ Ȯ���ϱ� ����..
				// ����� ������� and, or ó�� ���������� �����ؾ�������,
				// ������ ����� ���ؼ��� ó�� (������ ���� ����)
				if( mp == null ) {
					if( !hasJo && conjugationCompleted && conjugationCnt > 0 ) {
						this.addPreferedCondition(Condition.COND_NUM_EQ);
					}
					// ��� ���� ���� �߰�
					if( !hasJo && lastMorpheme.isSufficientByAnd(HgEncoded.EM_CN_EQ) ) {
						this.addHavingCondition(Condition.COND_NUM_EQ);
					}
					// ���� ���� �߰�
					else if( lastMorpheme.isSufficientByAnd(HgEncoded.JO) ) {
						this.addHavingCondition(Condition.COND_NUM_JO);

						// �̵�Ͼ� + ������ ���� �̵�Ͼ �ĺ� ������� ����� �� �ֵ��� �Ѵ�.
						// ������ ���̿��� 1�� �������� �ٸ� ������ ������ٴ� �켱������ ���߾��ش�.
						if( size == 2 && firstMorpheme.isSufficientByAnd(HgEncoded.NR) ) {
							candDicLen += nrDicLen - 1;
						}
					}
				}
				// ���� ũ�� ������Ű��
				else {
					spaceCnt++;
				}

				// ������ ���� �ʱ�ȭ
				conjugationCnt = 0;
				isPreWord = false;
				hasJo = false;
				nrDicLen = 0;
				tempDicLen = 0;
				expIdx++;
			}
			// ���⸦ �������� ǥ������ ���� ������ ���� ó��
			else {
				// ��� ���� ������� ó��
				if( mp.isSufficientByOr(HgEncoded.OR_VV_AJ) ) {
					conjugationCnt++;
				}
				// ������ ����� ��̸�, ����� ó��
				else if( mp.isSufficientByAnd(HgEncoded.CP) ) {
					hasJo = true;
					conjugationCnt++;
				}
				// �������̰�, ��̰� ���� ����� ó��, ������ ��� ���� �ʾ����� ������� ó��
				else if( mp.isSufficientByAnd(HgEncoded.EM) ) {
					conjugationCnt++;
					// ��������� �����̸� ���� �ո� �������� ����
					if( mp.isSufficientByAnd(HgEncoded.EM_FM_NN) ) {
						isPreWord = true;
					}
				}
				// ������ ��� �� ���ְ� ���Դ��� Ȯ���ؾ��ϹǷ� ����
				else if( mp.isSufficientByAnd(HgEncoded.JO) ) {
					hasJo = true;
				}
				// �̵�Ͼ��� ���
				else if( mp.isSufficientByAnd(HgEncoded.NR) ) {
					isPreWord = true;
					nrDicLen += mp.string.length();
				}
				// ��� ��̰� �ƴ� ��쿡�� �ӽ� ���� ���ַ� �߰��ϰ�, �ո� ����� ����
				else if( mp.isSufficientByAnd(HgEncoded.EP) ) {
					conjugationCnt += 100;
				} else {
					isPreWord = true;
					tempDicLen += mp.string.length();
				}
			}
		}

		this.realDicLen = realDicLen;
		this.candDicLen = candDicLen;

		// hashCode ���
		calculateHashCode();
	}


	/**
	 * ���� ������ ǰ�� ����
	 */
	public static final String	DLT_AHCL	= "#";
	/**
	 * ���� �ĺ��� ���� ���� ����
	 */
	public static final String	DLT_HCL		= "&"; 
	/**
	 * ������ �� Ȯ���ؾ� �ϴ� ����
	 */
	public static final String	DLT_CCL		= "@";
	/**
	 * �پ�� �����ؼ� ������ ���� �� �ִ� ǰ��
	 */
	public static final String	DLT_PHCL	= "%";


	/**
	 * <pre>
	 * ������ �ĺ� �м� ��� ���ڿ��κ��� ��ü�� �����Ѵ�.
	 * ���鿡 ���� ó���� �߰��ϱ� ���Ͽ� ���� 2007-07-19
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param exp
	 * @param source
	 */
	static MCandidate create(String exp, String source)
	{
		MCandidate mCandidate = new MCandidate();
		mCandidate.setExp(exp);
		StringTokenizer st = new StringTokenizer(source, "[]", false);

		// ��м� ��� ����
		String token = null, infos = "";
		String[] arr = null;
		for( int i = 0; st.hasMoreTokens(); i++ ) {
			token = st.nextToken();
			if( i == 0 ) {
				arr = token.split("\\+");
				for( int j = 0; j < arr.length; j++ ) {
					// �� ������ �������� ������ ���� ���ڿ� ����
					if( arr[j].startsWith(" ") ) {
						mCandidate.add(new MorphemeSpace(arr[j]));
						mCandidate.expList.add(0, "_");
					}
					// �Ϲ����� ���¼� �м� ��� ����
					else {
						mCandidate.add(Morpheme.create(arr[j]));
					}
				}
			} else {
				infos = token;
			}
		}


		// �ΰ� �����鿡 ���� ó�� ����
		st = new StringTokenizer(infos, "*" + DLT_AHCL + DLT_HCL + DLT_CCL + DLT_PHCL, true);
		while(st.hasMoreTokens()) {
			token = st.nextToken();
			// ���� ������ ǰ�� ����
			if(token.equals(DLT_AHCL)) {
				token = st.nextToken().trim();
				token = token.substring(1, token.length() - 1);
				mCandidate.addAppendableHgClass(token.split(","));
			}
			// ���� �ĺ��� ���� ���� ����
			else if(token.equals(DLT_HCL)) {
				token = st.nextToken().trim();
				token = token.substring(1, token.length() - 1);
				mCandidate.addHavingCondition(token.split(","));
			}
			// ������ �� Ȯ���ؾ� �ϴ� ����
			else if(token.equals(DLT_CCL)) {
				token = st.nextToken().trim();
				token = token.substring(1, token.length() - 1);
				mCandidate.addCheckingCondition(token.split(","));
			}
			// �پ�� �����ؼ� ������ ���� �� �ִ� ǰ��
			else if(token.equals(DLT_PHCL)) {
				token = st.nextToken().trim();
				token = token.substring(1, token.length() - 1);
				mCandidate.addPreferedCondition(token.split(","));
			}
		}
		mCandidate.initCondition(exp);
		mCandidate.calculateDicLen();
		return mCandidate;
	}


	/**
	 * <pre>
	 * ��м� �ĺ� ������ ��ȯ�Ѵ�.
	 * �м� �������� { } ���� �� ������ ��ȯ���ش�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @return
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();

		// ���¼� �м� ���
		sb.append("[" + super.toString() + "]");

		// ���� ������ ǰ�� ����
		String temp = HgClass.getHgClassString(appendableHgClassEncoded);
		if( temp != null ) sb.append(DLT_AHCL + "(" + temp + ")");

		// ���� �ĺ��� ���� ���� ����
		temp = Condition.getConditionString(havingConditionEncoded);
		if( temp != null ) sb.append(DLT_HCL + "(" + temp + ")");

		// ������ �� Ȯ���ؾ� �ϴ� ����
		temp = Condition.getConditionString(checkingConditionEncoded);
		if( temp != null ) sb.append(DLT_CCL + "(" + temp + ")");

		// �پ�� �����ؼ� ������ ���� �� �ִ� ǰ��
		temp = Condition.getConditionString(preferedConditionEncoded);
		if( temp != null ) sb.append(DLT_PHCL + "(" + temp + ")");

		sb.append("*(" + getScore() + "," + realDicLen + "," + candDicLen + "," + spaceCnt + "," + bonus + "," + size + "," + hashCode + ")");
		return sb.toString();
	}
	
	
	public String toSimpleString()
	{
		return super.toString();
	}


	/**
	 * <pre>
	 * ���¼� �м� ������ �ΰ� ������ encoding�� ���·� �߰��Ѵ�.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 11
	 * @return
	 */
	String getEncodedString()
	{
		StringBuffer sb = new StringBuffer();
		//sb.append(super.getEncodedString());
		sb.append(super.getMergedEncodedString());
		sb.append("!" + appendableHgClassEncoded);
		sb.append("!"  + havingConditionEncoded);
		sb.append("!"  + checkingConditionEncoded);
		sb.append("!"  + preferedConditionEncoded);
		return sb.toString();
	}
}