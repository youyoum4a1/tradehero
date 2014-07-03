package com.tradehero.th.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.EditText;
import com.tradehero.thm.R;
import org.jetbrains.annotations.Nullable;

public class ValidatedText extends EditText
        implements ValidatedView
{
    public static final int INDEX_LEFT = 0;
    public static final int INDEX_TOP = 1;
    public static final int INDEX_RIGHT = 2;
    public static final int INDEX_BOTTOM = 3;

    private boolean valid = true;
    private Drawable defaultDrawable;
    private Drawable invalidDrawable;
    private Drawable validDrawable;
    private Drawable defaultDrawableRight;
    private Drawable invalidDrawableRight;
    private Drawable validDrawableRight;
    @Nullable private ValidationListener listener;

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
        defaultDrawable = this.getCompoundDrawables()[INDEX_LEFT];
        defaultDrawableRight = this.getCompoundDrawables()[INDEX_RIGHT];
    }

    //<editor-fold desc="Accessors">
    @Override public boolean isValid()
    {
        return valid;
    }

    protected void setValid(boolean isValid)
    {
        this.valid = isValid;
        conditionalNotifyListener();
        conditionalHintValidStatus();
    }

    protected Drawable getDefaultDrawableRight()
    {
        return defaultDrawableRight;
    }
    //</editor-fold>

    //<editor-fold desc="Change look">
    @Override protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect)
    {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        conditionalHintValidStatus();
    }

    protected void hintDefaultStatus()
    {
        setCompoundDrawables(defaultDrawable, null, defaultDrawableRight, null);
    }

    private void conditionalHintValidStatus()
    {
        if (needsToHintValidStatus())
        {
            hintValidStatus();
        }
    }

    public boolean needsToHintValidStatus()
    {
        return true;
    }

    protected void hintValidStatus()
    {
        hintValidStatusLeft();
        hintValidStatusRight();
    }

    protected void hintValidStatusLeft()
    {
        if (!valid && invalidDrawable != null)
        {
            invalidDrawable.setBounds(defaultDrawable.getBounds());
            replaceCompoundDrawable(INDEX_LEFT, invalidDrawable);
        }
        else if (valid && validDrawable != null)
        {
            validDrawable.setBounds(defaultDrawable.getBounds());
            replaceCompoundDrawable(INDEX_LEFT, validDrawable);
        }
        else if (valid && defaultDrawable != null)
        {
            replaceCompoundDrawable(INDEX_LEFT, defaultDrawable);
        }
    }

    protected void hintValidStatusRight()
    {
        if (!valid && invalidDrawableRight != null)
        {
            invalidDrawableRight.setBounds(defaultDrawableRight.getBounds());
            replaceCompoundDrawable(INDEX_RIGHT, invalidDrawableRight);
        }
        else if (valid && validDrawableRight != null)
        {
            validDrawableRight.setBounds(defaultDrawableRight.getBounds());
            replaceCompoundDrawable(INDEX_RIGHT, validDrawableRight);
        }
    }

    protected void replaceCompoundDrawable(int index, Drawable drawable)
    {
        Drawable[] drawables = this.getCompoundDrawables();
        drawables[index] = drawable;
        this.setCompoundDrawables(drawables[INDEX_LEFT], drawables[INDEX_TOP], drawables[INDEX_RIGHT], drawables[INDEX_BOTTOM]);
    }
    //</editor-fold>

    //<editor-fold desc="Listeners">
    public void setListener(@Nullable ValidationListener listener)
    {
        this.listener = listener;
    }

    protected void conditionalNotifyListener()
    {
        if (needsToNotifyListener())
        {
            notifyListener();
        }
    }

    public boolean needsToNotifyListener()
    {
        return true;
    }

    protected void notifyListener()
    {
        ValidationMessage currentValidationMessage = getCurrentValidationMessage();
        ValidationListener listenerCopy = listener;
        if (listenerCopy != null)
        {
            listener.notifyValidation(currentValidationMessage);
        }
    }

    public ValidationMessage getCurrentValidationMessage()
    {
        return new ValidationMessage(this, valid, null);
    }
    //</editor-fold>

    @Override public void forceValidate()
    {
        hintValidStatus();
    }
}
