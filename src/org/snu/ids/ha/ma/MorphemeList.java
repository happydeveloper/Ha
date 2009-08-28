/**
 * <pre>
 * </pre>
 * @author	therocks
 * @since	2007. 6. 3
 */
package org.snu.ids.ha.ma;


import java.util.ArrayList;

import org.snu.ids.ha.constants.HgEncoded;


/**
 * <pre>
 * 형태소 정보를 저장하는 Class
 * List의 간단 버젼
 * </pre>
 * @author 	therocks
 * @since	2007. 6. 3
 */
public class MorphemeList
{
	Morpheme	firstMorpheme	= null;	// 분석열의 첫번째 형태소
	Morpheme	lastMorpheme	= null;	// 분석열의 마지막 형태소
	ArrayList	list			= null; // 형태소 분석 정보


	/**
	 * <pre>
	 * deault constructor
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 */
	public MorphemeList()
	{
		list = new ArrayList();
	}


	/**
	 * <pre>
	 * 크기 정보 반환
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @return
	 */
	public int size()
	{
		return list.size();
	}


	/**
	 * <pre>
	 * 형태소 정보 저장
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param mp
	 */
	public void add(Morpheme mp)
	{
		// 어말 어미가 붙으면 합쳐준다.
		if(lastMorpheme != null
				&& lastMorpheme.isSufficientByAnd(HgEncoded.EM)
				&& mp.isSufficientByAnd(HgEncoded.EM) )
		{
			Morpheme temp = lastMorpheme.copy();
			temp.string += mp.string;
			// 높임인 경우에는 단지 높임이 되도록 설정
			if( mp.isSufficientByAnd(HgEncoded.EM_ED_HR) ) {
				// 보조적 어미일 때에는 종결형으로 전환한다.
				if( lastMorpheme.isSufficientByAnd(HgEncoded.EM_CN_SU) ) {
					temp.hgEncoded = HgEncoded.EM_ED_NM;
				}
				// 어요, 아요 면 종결형으로 처리
				else if( lastMorpheme.isSufficientByAnd(HgEncoded.EM_CN_DP)
						&& (lastMorpheme.string.equals("아")
								|| lastMorpheme.string.equals("어")
								|| lastMorpheme.string.equals("야")
								|| lastMorpheme.string.equals("여")
								|| lastMorpheme.string.endsWith("고")
								|| lastMorpheme.string.endsWith("구")) )
				{
					temp.hgEncoded = HgEncoded.EM_ED_NM;
				}
			} else {
				temp.hgEncoded = mp.hgEncoded;
			}
			if( firstMorpheme == lastMorpheme) {
				firstMorpheme = temp;
			}
			list.remove(size() - 1);
			mp = temp;
		}
		list.add(mp);
		if( firstMorpheme == null ) firstMorpheme = mp;
		lastMorpheme = mp;
	}


	/**
	 * <pre>
	 * 형태소 정보 반환
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param i
	 * @return
	 */
	public Morpheme get(int i)
	{
		return (Morpheme) list.get(i);
	}


	/**
	 * <pre>
	 * i번째 형태소를 삭제한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @param i
	 */
	public void remove(int i)
	{
		list.remove(i);
	}


	/**
	 * <pre>
	 * 형태소 분석 결과의 동일성을 확인한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 6
	 * @param ml
	 * @return
	 */
	public boolean equals(MorphemeList ml)
	{
		return getEncodedString().equals(ml.getEncodedString());
	}


	/**
	 * <pre>
	 * 분석된 형태소 목록을 출력한다.
	 * 형태소+형태소+형태소 와 같은 형태로 출력한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 6. 4
	 * @return
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		for( int i = 0, stop = size(); i < stop; i++ ) {
			if( i > 0 ) sb.append("+");
			sb.append(get(i));
		}
		return sb.toString();
	}


	/**
	 * <pre>
	 * 분석된 형태소 목록을 출력한다.
	 * encoding된 형태로 출력한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 20
	 * @return
	 */
	String getEncodedString()
	{
		StringBuffer sb = new StringBuffer();
		for( int i = 0, stop = size(); i < stop; i++ ) {
			if( i > 0 ) sb.append("+");
			sb.append(get(i).getEncodedString());
		}
		return sb.toString();
	}


	/**
	 * <pre>
	 * 합성어가 될 수 있는 것들은 합성어로 만들어 낸후 Encoded String을 만들어낸다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 27
	 * @return
	 */
	String getMergedEncodedString()
	{
		return getMerged().getEncodedString();
	}


	/**
	 * <pre>
	 * 합성어가 될 수 있는 것들을 합쳐서 반환한다.
	 * </pre>
	 * @author	therocks
	 * @since	2007. 7. 27
	 * @return
	 */
	MorphemeList getMerged()
	{
		MorphemeList ml = new MorphemeList();
		Morpheme preMp = null, curMp = null;
		for( int i = 0, stop = size(); i < stop; i++ ) {
			curMp = get(i).copy();
			if( preMp != null && preMp.appendable(curMp) ) {
				preMp.append(curMp);
				continue;
			}
			ml.add(curMp);
			preMp = curMp;
		}
		return ml;
	}
}
