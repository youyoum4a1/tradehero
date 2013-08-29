package com.tradehero.th.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import com.tradehero.th.R;
import java.util.LinkedList;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 8/27/13 Time: 10:24 AM Copyright (c) TradeHero */
public class ValidatedText extends EditText implements ValidatedView, View.OnFocusChangeListener
{
    protected boolean allowEmpty;
    private int indicator;
    protected boolean isValid = true;
    private Drawable invalidDrawable;
    private Drawable validDrawable;
    private Drawable defaultDrawable;
    private Drawable invalidDrawableRight;
    private Drawable validDrawableRight;
    protected Drawable defaultDrawableRight;
    private List<ValidationListener> listeners = new LinkedList<>();

    public ValidatedText(Context context)
    {
        super(context);
    }

    public ValidatedText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    public ValidatedText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs)
    {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ValidatedText);
        allowEmpty = a.getBoolean(R.styleable.ValidatedText_allowEmpty, false);
        indicator = a.getResourceId(R.styleable.ValidatedText_indicator, 0);
        invalidDrawable = a.getDrawable(R.styleable.ValidatedText_invalidDrawable);
        validDrawable = a.getDrawable(R.styleable.ValidatedText_validDrawable);
        invalidDrawableRight = a.getDrawable(R.styleable.ValidatedText_invalidDrawableRight);
        validDrawableRight = a.getDrawable(R.styleable.ValidatedText_validDrawableRight);
        a.recycle();
        defaultDrawable = this.getCompoundDrawables()[0];
        defaultDrawableRight = this.getCompoundDrawables()[2];
        this.setOnFocusChangeListener(this);
    }

    @Override public void onFocusChange(View view, boolean hasFocus)
    {
        hintValidStatus();
    }

    protected void setValid(boolean isValidated)
    {
        this.isValid = isValidated;
        notifyListeners();
        hintValidStatus();
    }

    private void notifyListeners()
    {
        for (ValidationListener listener: listeners)
        {
            listener.notifyValidation(getCurrentValidationMessage());
        }
    }

    public ValidationMessage getCurrentValidationMessage()
    {
        return new ValidationMessage(this, isValid, null);
    }

    protected void hintValidStatus ()
    {
        hintValidStatusLeft();
        hintValidStatusRight();
    }

    protected void hintValidStatusLeft ()
    {
        if (!isValid && invalidDrawable != null)
        {
            invalidDrawable.setBounds(defaultDrawable.getBounds());
            replaceCompoundDrawable(0, invalidDrawable);
        }
        else if (isValid && validDrawable != null)
        {
            validDrawable.setBounds(defaultDrawable.getBounds());
            replaceCompoundDrawable(0, validDrawable);
        }
        else if (isValid && defaultDrawable != null)
        {
            replaceCompoundDrawable(0, defaultDrawable);
        }
    }

    protected void hintValidStatusRight ()
    {
        if (!isValid && invalidDrawableRight != null)
        {
            invalidDrawableRight.setBounds(defaultDrawableRight.getBounds());
            replaceCompoundDrawable(2, invalidDrawableRight);
        }
        else if (isValid && validDrawableRight != null)
        {
            validDrawableRight.setBounds(defaultDrawableRight.getBounds());
            replaceCompoundDrawable(2, validDrawableRight);
        }
    }

    protected void replaceCompoundDrawable (int index, Drawable drawable)
    {
        Drawable[] drawables = this.getCompoundDrawables();
        drawables[index] = drawable;
        this.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }


    public void setIndicator(int indicator)
    {
        this.indicator = indicator;
    }

    public int getIndicator()
    {
        return indicator;
    }

    @Override public boolean getIsValid()
    {
        return isValid;
    }

    public void addListener(ValidationListener listener)
    {
        if (!listeners.contains(listener))
        {
            listeners.add(listener);
        }
    }
}
