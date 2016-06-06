package com.androidth.general.widget.validation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import com.androidth.general.R;

public class ValidatedText extends EditText
        implements ValidatedView
{
    public static final int INDEX_LEFT = 0;
    public static final int INDEX_TOP = 1;
    public static final int INDEX_RIGHT = 2;
    public static final int INDEX_BOTTOM = 3;

    @NonNull private Status status = Status.QUIET;
    private Drawable defaultDrawable;
    private Drawable invalidDrawable;
    private Drawable validDrawable;
    private Drawable defaultDrawableRight;
    private Drawable invalidDrawableRight;
    private Drawable validDrawableRight;

    @NonNull public final ValidationDTO validationDTO;
    @Nullable private View progressIndicator;

    //<editor-fold desc="Constructors">
    public ValidatedText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
        this.validationDTO = new ValidationDTO(context, attrs);
    }

    public ValidatedText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context, attrs);
        this.validationDTO = new ValidationDTO(context, attrs);
    }
    //</editor-fold>

    protected void init(@NonNull Context context, @NonNull AttributeSet attrs)
    {
        if (!isInEditMode())
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
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (validationDTO.progressIndicatorId != null)
        {
            progressIndicator = getRootView().findViewById(validationDTO.progressIndicatorId);
            if (progressIndicator != null)
            {
                progressIndicator.setVisibility(INVISIBLE);
            }
        }
    }

    //<editor-fold desc="Accessors">
    @NonNull @Override public Status getStatus()
    {
        return status;
    }

    @Override public void setStatus(@NonNull Status status)
    {
        this.status = status;
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
        switch (getStatus())
        {
            case QUIET:
                setCompoundDrawables(defaultDrawable, null, defaultDrawableRight, null);
                if (progressIndicator != null)
                {
                    progressIndicator.setVisibility(INVISIBLE);
                }
                break;

            case VALID:
                if (validDrawable != null)
                {
                    validDrawable.setBounds(defaultDrawable.getBounds());
                    replaceCompoundDrawable(INDEX_LEFT, validDrawable);
                }
                else if (defaultDrawable != null)
                {
                    replaceCompoundDrawable(INDEX_LEFT, defaultDrawable);
                }
                if (progressIndicator != null)
                {
                    progressIndicator.setVisibility(INVISIBLE);
                }
                break;

            case CHECKING:
                if (progressIndicator != null)
                {
                    progressIndicator.setVisibility(VISIBLE);
                    if (getDefaultDrawableRight() != null)
                    {
                        replaceCompoundDrawable(INDEX_RIGHT, getDefaultDrawableRight());
                    }
                }
                break;

            case INVALID:
                if (invalidDrawable != null)
                {
                    invalidDrawable.setBounds(defaultDrawable.getBounds());
                    replaceCompoundDrawable(INDEX_LEFT, invalidDrawable);
                }
                if (progressIndicator != null)
                {
                    progressIndicator.setVisibility(INVISIBLE);
                }
                break;
        }
    }

    protected void hintValidStatusRight()
    {
        switch (getStatus())
        {
            case QUIET:
                setCompoundDrawables(defaultDrawable, null, defaultDrawableRight, null);
                break;

            case VALID:
                if (validDrawableRight != null)
                {
                    validDrawableRight.setBounds(defaultDrawableRight.getBounds());
                    replaceCompoundDrawable(INDEX_RIGHT, validDrawableRight);
                }
                break;

            case CHECKING:
                break;

            case INVALID:
                if (invalidDrawableRight != null)
                {
                    invalidDrawableRight.setBounds(defaultDrawableRight.getBounds());
                    replaceCompoundDrawable(INDEX_RIGHT, invalidDrawableRight);
                }
                break;
        }
    }

    protected void replaceCompoundDrawable(int index, Drawable drawable)
    {
        Drawable[] drawables = this.getCompoundDrawables();
        drawables[index] = drawable;
        this.setCompoundDrawables(drawables[INDEX_LEFT], drawables[INDEX_TOP], drawables[INDEX_RIGHT], drawables[INDEX_BOTTOM]);
    }
    //</editor-fold>

    @NonNull public TextValidator getValidator()
    {
        return new TextValidator(getResources(), validationDTO);
    }
}
