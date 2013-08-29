package com.tradehero.th.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import com.tradehero.th.R;
import java.util.regex.Pattern;

/** Created with IntelliJ IDEA. User: tho Date: 8/27/13 Time: 2:58 PM Copyright (c) TradeHero */
public class SelfValidatedText extends ValidatedText
{
    private final int DEFAULT_VALIDATE_DELAY = 200;

    protected long validateDelay;
    protected Runnable validateRunnable;
    protected Pattern validatePattern;
    protected boolean hasHadInteraction = false;

    public SelfValidatedText(Context context)
    {
        super(context);
    }

    public SelfValidatedText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SelfValidatedText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override protected void init(Context context, AttributeSet attrs)
    {
        super.init(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SelfValidatedText);
        String validatePatternString = a.getString(R.styleable.SelfValidatedText_validatePattern);
        validateDelay = a.getInt(R.styleable.SelfValidatedText_validateDelay, DEFAULT_VALIDATE_DELAY);
        if (validatePatternString != null)
        {
            validatePattern = Pattern.compile(validatePatternString);
        }

        a.recycle();

        validateRunnable = new Runnable()
        {
            @Override public void run()
            {
                conditionalValidation();
            }
        };
    }

    @Override public void onFocusChange(View view, boolean hasFocus)
    {
        if (!hasFocus)
        {
            // This means the player has moved away
            // It assumes that this method is not called as part of the constructor.
            hasHadInteraction = true;
        }
        super.onFocusChange(view, hasFocus);
        conditionalValidation();
    }

    @Override protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter)
    {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        if (!hasHadInteraction && text != null && text.length() > 0)
        {
            hasHadInteraction = true;
        }

        if (validateRunnable != null)
        {
            this.removeCallbacks(validateRunnable);
            this.postDelayed(validateRunnable, validateDelay);
        }
    }

    @Override protected void hintValidStatus()
    {
        if (hasHadInteraction)
        {
            super.hintValidStatus();
        }
    }

    protected void conditionalValidation()
    {
        if (hasHadInteraction)
        {
            setValid(validate());
        }
    }

    protected boolean validate()
    {
        return validateSize() && validatePattern();
    }

    protected boolean validateSize ()
    {
        return (getText() != null && getText().length() > 0) || allowEmpty;
    }

    protected boolean validatePattern ()
    {
        if (getText() == null || validatePattern == null)
        {
            return true;
        }
        return validatePattern.matcher(getText()).matches();
    }

    @Override public ValidationMessage getCurrentValidationMessage()
    {
        if (!validateSize())
        {
            return (new ValidationMessage(this, false, getContext().getString(R.string.validation_bad_size)));
        }
        if (!validatePattern())
        {
            return new ValidationMessage(this, false, getContext().getString(R.string.validation_incorrect_format));
        }
        return super.getCurrentValidationMessage();
    }
}
