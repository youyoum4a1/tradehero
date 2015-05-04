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
    public boolean isError;
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


}

