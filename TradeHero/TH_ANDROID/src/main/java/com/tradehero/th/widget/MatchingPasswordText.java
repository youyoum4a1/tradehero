package com.tradehero.th.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: xavier Date: 8/29/13 Time: 12:01 PM */
public class MatchingPasswordText extends ValidatedPasswordText
{
    private int targetId;
    ValidatedPasswordText target;
    private TextWatcher targetWatcher = new TextWatcher()
    {
        @Override public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
        {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
        {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override public void afterTextChanged(Editable editable)
        {
            if (validateRunnable != null)
            {
                MatchingPasswordText.this.removeCallbacks(validateRunnable);
                MatchingPasswordText.this.postDelayed(validateRunnable, getValidateDelay());
            }
        }
    };

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

    @Override protected boolean validate()
    {
        return super.validate() && matchesWithTarget ();
    }

    protected boolean matchesWithTarget ()
    {
        associateTargetIfNone();

        if (target == null)
        {
            return false;
        }
        String targetPassword = target.getText().toString();

        return target.validate() && getText().toString().equals(targetPassword);
    }

    private void associateTargetIfNone()
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
        if (target == null)
        {
            return;
            //throw new IllegalArgumentException("TargetId was not found. MatchWith attribute needs to be set.");
        }

        // We want to know when the original password no longer matches the confirmation one.
        target.addTextChangedListener(targetWatcher);
    }

    @Override public boolean needsToNotifyListeners()
    {
        associateTargetIfNone();
        return target != null && super.needsToNotifyListeners() && target.validate() && !validate();
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

    //<editor-fold desc="Accessors">
    public void setTargetId(int targetId)
    {
        if (this.target == null || this.target.getId() != targetId)
        {
            View view = getRootView().findViewById(targetId);
            if (!(view instanceof ValidatedPasswordText))
            {
                throw new IllegalArgumentException("Target view has to be a ValidatedPasswordText");
            }
            setTarget((ValidatedPasswordText) view);
        }
    }

    public ValidatedPasswordText getTarget()
    {
        if (targetId != 0)
        {
            setTargetId(targetId);
        }
        return target;
    }

    public void setTarget(ValidatedPasswordText target)
    {
        this.target = target;
        this.targetId = target == null ? 0 : target.getId();
    }
    //</editor-fold>

}
