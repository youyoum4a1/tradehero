package com.tradehero.th.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import com.tradehero.th.R;

public class MatchingPasswordText extends ValidatedPasswordText
{
    private int targetId;
    private ValidatedPasswordText target;
    private TextWatcher targetWatcher;

    //<editor-fold desc="Constructors">
    public MatchingPasswordText(Context context)
    {
        super(context);
    }

    public MatchingPasswordText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MatchingPasswordText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void init(Context context, AttributeSet attrs)
    {
        super.init(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MatchingPasswordText);
        targetId = a.getResourceId(R.styleable.MatchingPasswordText_matchWith, 0);
        a.recycle();
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        targetWatcher = createTargetPasswordWatcher();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (targetWatcher == null)
        {
            targetWatcher = createTargetPasswordWatcher();
        }
        findTargetIfNone();
        if (target != null)
        {
            target.addTextChangedListener(targetWatcher);
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        if (target != null)
        {
            target.removeTextChangedListener(targetWatcher);
        }
        targetWatcher = null;
        target = null;
        super.onDetachedFromWindow();
    }

    private void findTargetIfNone()
    {
        if (target != null)
        {
            return;
        }

        if (targetId == 0)
        {
            throw new IllegalArgumentException("TargetId cannot be 0. MatchWith attribute needs to be set.");
        }

        target = (ValidatedPasswordText) getRootView().findViewById(targetId);
    }

    @Override protected boolean validate()
    {
        return super.validate() && matchesWithTarget ();
    }

    protected boolean matchesWithTarget ()
    {
        if (target == null)
        {
            return false;
        }
        String targetPassword = target.getText().toString();

        return target.validate() && getText().toString().equals(targetPassword);
    }

    @Override public boolean needsToNotifyListener()
    {
        return target != null && super.needsToNotifyListener() && target.validate() && !validate();
    }

    @Override public ValidationMessage getCurrentValidationMessage()
    {
        return new ValidationMessage(this, false, getContext().getString(R.string.password_validation_confirm_fail_string));
    }

    @Override public void forceValidate()
    {
        hasHadInteraction = true;
        setValid(validate());
        super.forceValidate();
    }

    protected TextWatcher createTargetPasswordWatcher()
    {
        return new TargetPasswordWatcher();
    }

    protected class TargetPasswordWatcher implements TextWatcher
    {
        @Override public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
        {
        }

        @Override public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
        {
        }

        @Override public void afterTextChanged(Editable editable)
        {
            launchDelayedValidation();
        }
    }
}
