package com.tradehero.th.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import com.tradehero.common.utils.THToast;
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
                MatchingPasswordText.this.postDelayed(validateRunnable, validateDelay);
            }
        }
    };

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

    @Override protected void init(Context context, AttributeSet attrs)
    {
        super.init(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MatchingPasswordText);
        targetId = a.getResourceId(R.styleable.MatchingPasswordText_matchWith, 0);
        a.recycle();

        validateRunnable = new Runnable()
        {
            @Override public void run()
            {
                conditionalValidation();
            }
        };
    }

    @Override protected boolean validate()
    {
        boolean superValidate = super.validate();
        if (!superValidate)
        {
            return false;
        }

        return superValidate && matchesWithTarget ();
    }

    private boolean superValidate ()
    {
        return super.validate();
    }

    protected boolean matchesWithTarget ()
    {
        associateTargetIfNone();

        String targetPassword = target.getText().toString();

        return getText().toString().equals(targetPassword);
    }

    private void associateTargetIfNone()
    {
        if (target == null)
        {
            if (targetId == 0)
            {
                throw new IllegalArgumentException("TargetId cannot be 0. MatchWith attribute needs to be set.");
            }

            target = (ValidatedPasswordText) getRootView().findViewById(targetId);
            if (target != null)
            {
                // We want to know when the original password no longer matches the confirmation one.
                target.addTextChangedListener(targetWatcher);
            }
            else
            {
                throw new IllegalArgumentException("TargetId was not found. MatchWith attribute needs to be set.");
            }
        }
    }

    public boolean needsConfirmFailNotification ()
    {
        return super.validate() && target.validate() && !matchesWithTarget();
    }

    @Override protected void conditionalValidation()
    {
        super.conditionalValidation();
        if (needsConfirmFailNotification())
        {
            notifyInvalidMatchTarget();
        }
    }

    protected void notifyInvalidMatchTarget ()
    {
        THToast.show(R.string.password_validation_confirm_fail_string);
    }

    @Override protected void notifyInvalidPattern()
    {
        // Do nothing on purpose
    }
}
