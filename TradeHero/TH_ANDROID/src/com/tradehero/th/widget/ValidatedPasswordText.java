package com.tradehero.th.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: tho Date: 8/27/13 Time: 10:30 AM Copyright (c) TradeHero */
public class ValidatedPasswordText extends SelfValidatedText
{
    public ValidatedPasswordText(Context context) {
        super(context);
    }

    public ValidatedPasswordText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ValidatedPasswordText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter)
    {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    @Override protected void conditionalValidation()
    {
        super.conditionalValidation();
        if (hasHadInteraction && !validatePattern())
        {
            notifyInvalidPattern();
        }
    }

    protected void notifyInvalidPattern()
    {
        THToast.show(R.string.password_validation_pattern_string);
    }
}
