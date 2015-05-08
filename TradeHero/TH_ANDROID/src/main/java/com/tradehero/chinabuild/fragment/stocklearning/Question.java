package com.tradehero.chinabuild.fragment.stocklearning;

import com.tradehero.th.utils.StringUtils;
import java.io.Serializable;

public class Question implements Serializable{


    public static final int ONECHOICE = 0;
    public static final int MULTICHOISE = 1;
    public static final int JUDGECHOISE = 2;

	public int id;
	public String content = "";
	public String option1 = "";
	public String option2 = "";
	public String option3 = "";
	public String option4 = "";
	public String answer = "";
	public int subcategory;
    public String imageUrl;

    public int getChoiceType()
    {
        if (answer.length() > 1)
        {
            return MULTICHOISE;
        }
        if (StringUtils.isNullOrEmpty(option3))
        {
            return JUDGECHOISE;
        }
        return ONECHOICE;
    }


    @Override
    public String toString(){
        StringBuffer stringBuffer = new StringBuffer("id: ");
        stringBuffer.append(id).append(" content: ").append(content).append(" option1: ").append(option1)
                .append(" option2: ").append(option2).append(" option3: ").append(option3)
                .append(" option4: ").append(option4).append(" answer: ").append(answer)
                .append(" subcategory: ").append(subcategory).append(" imageUrl: ").append(imageUrl);
        return stringBuffer.toString();
    }
}

