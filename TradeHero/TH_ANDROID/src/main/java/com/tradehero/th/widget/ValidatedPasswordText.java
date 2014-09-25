package com.tradehero.th.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.tradehero.th.R;

public class ValidatedPasswordText extends SelfValidatedText
{
    private static final boolean DEFAULT_VALIDATE_ONLY_IF_NOT_EMPTY = false;

    private boolean validateOnlyIfNotEmpty;

    //<editor-fold desc="Constructors">
    public ValidatedPasswordText(Context context)
    {
        super(context);
    }

    public ValidatedPasswordText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ValidatedPasswordText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void init(Context context, AttributeSet attrs)
    {
        super.init(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ValidatedPasswordText);
        validateOnlyIfNotEmpty = a.getBoolean(R.styleable.ValidatedPasswordText_validateOnlyIfNotEmpty, DEFAULT_VALIDATE_ONLY_IF_NOT_EMPTY);
        a.recycle();
    }

    public void setValidateOnlyIfNotEmpty(boolean validateOnlyIfNotEmpty)
    {
        this.validateOnlyIfNotEmpty = validateOnlyIfNotEmpty;
    }

    @Override protected boolean validate()
    {
        return validateCareOnlyIfNotEmpty() || super.validate();
    }

    protected boolean validateCareOnlyIfNotEmpty()
    {
        String text = getText().toString();
        return !text.isEmpty() || validateOnlyIfNotEmpty;
    }

    @Override public ValidationMessage getCurrentValidationMessage()
    {
        if (validateCareOnlyIfNotEmpty())
        {
            return new ValidationMessage(null, true, null);
        }
        return super.getCurrentValidationMessage();
    }
}
