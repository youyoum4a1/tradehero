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

    // Inclusive boundaries
    private int minSize;
    private int maxSize;
    private long validateDelay;
    protected Runnable validateRunnable;
    protected Pattern validatePattern;
    protected boolean hasHadInteraction = false;
    private String invalidMinSizeMessage;
    private String invalidMaxSizeMessage;
    private String invalidBetweenSizeMessage;
    private String invalidPatternMessage;

    //<editor-fold desc="Constructors">
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
    //</editor-fold>

    @Override protected void init(Context context, AttributeSet attrs)
    {
        super.init(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SelfValidatedText);
        minSize = a.getInt(R.styleable.SelfValidatedText_minSize, 0);
        maxSize = a.getInt(R.styleable.SelfValidatedText_maxSize, Integer.MAX_VALUE);
        String validatePatternString = a.getString(R.styleable.SelfValidatedText_validatePattern);
        validateDelay = a.getInt(R.styleable.SelfValidatedText_delayValidationByMilliSec, DEFAULT_VALIDATE_DELAY);
        if (validatePatternString != null)
        {
            validatePattern = Pattern.compile(validatePatternString);
        }
        invalidMinSizeMessage = a.getString(R.styleable.SelfValidatedText_invalidMinSizeMessage);
        invalidMinSizeMessage = invalidMinSizeMessage != null ? invalidMinSizeMessage : context.getString(R.string.validation_size_too_short);

        invalidMaxSizeMessage = a.getString(R.styleable.SelfValidatedText_invalidMaxSizeMessage);
        invalidMaxSizeMessage = invalidMaxSizeMessage != null ? invalidMaxSizeMessage : context.getString(R.string.validation_size_too_long);

        invalidBetweenSizeMessage = a.getString(R.styleable.SelfValidatedText_invalidBetweenSizeMessage);
        invalidBetweenSizeMessage = invalidBetweenSizeMessage != null ? invalidBetweenSizeMessage : context.getString(R.string.validation_size_in_between);

        invalidPatternMessage = a.getString(R.styleable.SelfValidatedText_invalidPatternMessage);
        invalidPatternMessage = invalidPatternMessage != null ? invalidPatternMessage : context.getString(R.string.validation_incorrect_pattern);

        a.recycle();

        validateRunnable = new Runnable()
        {
            @Override public void run()
            {
                conditionalValidation();
            }
        };
    }

    public long getValidateDelay()
    {
        return validateDelay;
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

        launchDelayedValidation();
    }

    protected void launchDelayedValidation ()
    {
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

    protected boolean validateSize()
    {
        return validateMinSize() && validateMaxSize();
    }

    protected boolean validateMinSize()
    {
        return minSize <= getText().length();
    }

    protected boolean validateMaxSize()
    {
        return getText().length() <= maxSize;
    }

    protected boolean validatePattern ()
    {
        if (validatePattern == null)
        {
            return true;
        }
        return validatePattern.matcher(getText()).matches();
    }

    @Override public ValidationMessage getCurrentValidationMessage()
    {
        if (0 < minSize && maxSize < Integer.MAX_VALUE && !validateSize())
        {
            return new ValidationMessage(this, false, String.format(invalidBetweenSizeMessage, minSize, maxSize));
        }
        if (0 < minSize  && !validateMinSize())
        {
            return new ValidationMessage(this, false, String.format(invalidMinSizeMessage, minSize));
        }
        if (maxSize < Integer.MAX_VALUE && !validateMaxSize())
        {
            return new ValidationMessage(this, false, String.format(invalidMaxSizeMessage, maxSize));
        }
        if (!validatePattern())
        {
            return new ValidationMessage(this, false, invalidPatternMessage);
        }
        return super.getCurrentValidationMessage();
    }

}
