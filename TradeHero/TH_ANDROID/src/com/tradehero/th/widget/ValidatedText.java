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

/** Created with IntelliJ IDEA. User: tho Date: 8/27/13 Time: 10:24 AM Copyright (c) TradeHero
 * ValidatedText is used to change the display of an EditText depending on the value of flag.
 * */
public class ValidatedText extends EditText implements ValidatedView, View.OnFocusChangeListener
{
    private boolean valid = true;
    private Drawable defaultDrawable;
    private Drawable invalidDrawable;
    private Drawable validDrawable;
    private Drawable defaultDrawableRight;
    private Drawable invalidDrawableRight;
    private Drawable validDrawableRight;
    private List<ValidationListener> listeners = new LinkedList<>();

    //<editor-fold desc="Constructors">
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
    //</editor-fold>

    protected void init(Context context, AttributeSet attrs)
    {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ValidatedText);
        invalidDrawable = a.getDrawable(R.styleable.ValidatedText_invalidDrawable);
        validDrawable = a.getDrawable(R.styleable.ValidatedText_validDrawable);
        invalidDrawableRight = a.getDrawable(R.styleable.ValidatedText_invalidDrawableRight);
        validDrawableRight = a.getDrawable(R.styleable.ValidatedText_validDrawableRight);
        a.recycle();
        defaultDrawable = this.getCompoundDrawables()[0];
        defaultDrawableRight = this.getCompoundDrawables()[2];
        this.setOnFocusChangeListener(this);
    }

    //<editor-fold desc="Accessors">
    @Override public boolean isValid()
    {
        return valid;
    }

    protected void setValid(boolean isValidated)
    {
        this.valid = isValidated;
        notifyListeners();
        hintValidStatus();
    }

    public Drawable getDefaultDrawableRight()
    {
        return defaultDrawableRight;
    }
    //</editor-fold>

    @Override public void onFocusChange(View view, boolean hasFocus)
    {
        hintValidStatus();
    }

    //<editor-fold desc="Change look">
    protected void hintDefaultStatus ()
    {
        setCompoundDrawables(defaultDrawable, null, defaultDrawableRight, null);
    }

    protected void hintValidStatus ()
    {
        hintValidStatusLeft();
        hintValidStatusRight();
    }

    protected void hintValidStatusLeft ()
    {
        if (!valid && invalidDrawable != null)
        {
            invalidDrawable.setBounds(defaultDrawable.getBounds());
            replaceCompoundDrawable(0, invalidDrawable);
        }
        else if (valid && validDrawable != null)
        {
            validDrawable.setBounds(defaultDrawable.getBounds());
            replaceCompoundDrawable(0, validDrawable);
        }
        else if (valid && defaultDrawable != null)
        {
            replaceCompoundDrawable(0, defaultDrawable);
        }
    }

    protected void hintValidStatusRight ()
    {
        if (!valid && invalidDrawableRight != null)
        {
            invalidDrawableRight.setBounds(defaultDrawableRight.getBounds());
            replaceCompoundDrawable(2, invalidDrawableRight);
        }
        else if (valid && validDrawableRight != null)
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
    //</editor-fold>

    //<editor-fold desc="Listeners">
    public void addListener(ValidationListener listener)
    {
        if (!listeners.contains(listener))
        {
            listeners.add(listener);
        }
    }

    public void removeListener(ValidationListener listener)
    {
        listeners.remove(listener);
    }

    public void removeAllListeners()
    {
        listeners.clear();
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
        if (valid)
        {
            return null;
        }
        return new ValidationMessage(this, valid, null);
    }
    //</editor-fold>

    @Override public void forceValidate()
    {
        hintValidStatus();
    }
}
