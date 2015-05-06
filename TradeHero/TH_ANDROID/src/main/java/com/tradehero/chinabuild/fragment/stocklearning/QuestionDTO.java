package com.tradehero.chinabuild.fragment.stocklearning;

import com.tradehero.common.persistence.DTO;

/**
 * Created by palmer on 15/5/4.
 */
public class QuestionDTO implements DTO{

    public Question[] questions;
    public QuestionGroup[] subcategories;
    public QuestionCategory[] categories;

}
