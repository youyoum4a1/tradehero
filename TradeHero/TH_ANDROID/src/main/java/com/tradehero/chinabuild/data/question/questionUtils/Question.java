package com.tradehero.chinabuild.data.question.questionUtils;

import java.io.Serializable;

public class Question implements Serializable{
	  
	private String Qid;
	private String QTitle;//题目
	private String QAnswerOne;//答案一
	private String QAnswerTwo;//答案二
	private String QAnswerThree;//答案三
	private String QAnswerFour;//答案四
	private String QAnswerCorrect;//正确答案
	private String QType;//大类【新手】【老手】【专家】

	public Question(){}
	
	public Question(String QTitle,String A1,String A2,String A3,String A4,String AC,String Qt,String Qid){
		this.QTitle = QTitle;
		this.QAnswerOne = A1;
		this.QAnswerTwo = A2;
		this.QAnswerThree = A3;
		this.QAnswerFour = A4;
		this.QAnswerCorrect = AC;
		this.QType = Qt;
        this.Qid = Qid;
	}
	 
	public String getQid() {
		return Qid;
	}

	public void setQid(String qid) {
		Qid = qid;
	}

	public String getQTitle() {
		return QTitle;
	}

	public void setQTitle(String qTitle) {
		QTitle = qTitle;
	}

	public String getQAnswerOne() {
		return QAnswerOne;
	}

	public void setQAnswerOne(String qAnswerOne) {
		QAnswerOne = qAnswerOne;
	}

	public String getQAnswerTwo() {
		return QAnswerTwo;
	}

	public void setQAnswerTwo(String qAnswerTwo) {
		QAnswerTwo = qAnswerTwo;
	}

	public String getQAnswerThree() {
		return QAnswerThree;
	}

	public void setQAnswerThree(String qAnswerThree) {
		QAnswerThree = qAnswerThree;
	}

	public String getQAnswerFour() {
		return QAnswerFour;
	}

	public void setQAnswerFour(String qAnswerFour) {
		QAnswerFour = qAnswerFour;
	}

	public String getQAnswerCorrect() {
		return QAnswerCorrect;
	}

	public void setQAnswerCorrect(String qAnswerCorrect) {
		QAnswerCorrect = qAnswerCorrect;
	}

	public String getQType() {
		return QType;
	}

	public void setQType(String qType) {
		QType = qType;
	}
	
	public boolean isAnswerCorrect(String answer)
	{
		if(answer != null && answer.equals(QAnswerCorrect))
		{
			return true;
		}
		return false;
	}
	
}
