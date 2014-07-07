package com.tradehero.th.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import com.tradehero.th.R;
import java.util.regex.Pattern;

public class SelfValidatedText extends ValidatedText
{
    private final int DEFAULT_VALIDATE_DELAY = 200;

    // Inclusive boundaries
    private int minTextLength;
    private int maxTextLength;
    private long validateDelay;
    protected Runnable validateRunnable;
    protected Pattern validatePattern;
    protected boolean hasHadInteraction = false;
    private String invalidMinTextLengthMessage;
    private String invalidMaxTextLengthMessage;
    private String invalidBetweenTextLengthMessage;
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
        minTextLength = a.getInt(R.styleable.SelfValidatedText_minTextLength, 0);
        maxTextLength = a.getInt(R.styleable.SelfValidatedText_maxTextLength, Integer.MAX_VALUE);
        validateDelay = a.getInt(R.styleable.SelfValidatedText_delayValidationByMilliSec, DEFAULT_VALIDATE_DELAY);
        String validatePatternString = a.getString(R.styleable.SelfValidatedText_validatePattern);
        if (validatePatternString != null)
        {
            validatePattern = Pattern.compile(validatePatternString);
        }
        invalidMinTextLengthMessage = a.getString(R.styleable.SelfValidatedText_invalidMinTextLengthMessage);
        invalidMinTextLengthMessage = invalidMinTextLengthMessage != null ? invalidMinTextLengthMessage : context.getString(R.string.validation_size_too_short);

        invalidMaxTextLengthMessage = a.getString(R.styleable.SelfValidatedText_invalidMaxTextLengthMessage);
        invalidMaxTextLengthMessage = invalidMaxTextLengthMessage != null ? invalidMaxTextLengthMessage : context.getString(R.string.validation_size_too_long);

        invalidBetweenTextLengthMessage = a.getString(R.styleable.SelfValidatedText_invalidBetweenTextLengthMessage);
        invalidBetweenTextLengthMessage = invalidBetweenTextLengthMessage != null ? invalidBetweenTextLengthMessage : context.getString(R.string.validation_size_in_between);

        invalidPatternMessage = a.getString(R.styleable.SelfValidatedText_invalidPatternMessage);
        invalidPatternMessage = invalidPatternMessage != null ? invalidPatternMessage : context.getString(R.string.validation_incorrect_pattern);

        a.recycle();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        validateRunnable = new Runnable()
        {
            @Override public void run()
            {
                conditionalValidation();
            }
        };
        launchDelayedValidation();
    }

    @Override protected void onDetachedFromWindow()
    {
        removeCallbacks(validateRunnable);
        validateRunnable = null;
        super.onDetachedFromWindow();
    }

    @Override protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect)
    {
        if (!focused)
        {
            // This means the player has moved away
            // It assumes that this method is not called as part of the constructor.
            hasHadInteraction = true;
        }
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
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

    protected void launchDelayedValidation()
    {
        if (validateRunnable != null)
        {
            removeCallbacks(validateRunnable);
            postDelayed(validateRunnable, validateDelay);
        }
    }

    @Override public boolean needsToHintValidStatus()
    {
        return hasHadInteraction;
    }

    public boolean needsToValidate()
    {
        return hasHadInteraction;
    }

    protected void conditionalValidation()
    {
        if (needsToValidate())
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
        return minTextLength <= getText().length();
    }

    protected boolean validateMaxSize()
    {
        return getText().length() <= maxTextLength;
    }

    protected boolean validatePattern ()
    {
        return validatePattern == null || validatePattern.matcher(getText()).matches();
    }

    @Override public void forceValidate()
    {
        hasHadInteraction = true;
        conditionalValidation();
        super.forceValidate();
    }

    @Override public ValidationMessage getCurrentValidationMessage()
    {
        if (0 < minTextLength && maxTextLength < Integer.MAX_VALUE && !validateSize())
        {
            return new ValidationMessage(this, false, String.format(invalidBetweenTextLengthMessage, minTextLength, maxTextLength));
        }
        if (0 < minTextLength && !validateMinSize())
        {
            return new ValidationMessage(this, false, String.format(invalidMinTextLengthMessage, minTextLength));
        }
        if (maxTextLength < Integer.MAX_VALUE && !validateMaxSize())
        {
            return new ValidationMessage(this, false, String.format(invalidMaxTextLengthMessage, maxTextLength));
        }
        if (!validatePattern())
        {
            return new ValidationMessage(this, false, invalidPatternMessage);
        }
        return super.getCurrentValidationMessage();
    }
}
