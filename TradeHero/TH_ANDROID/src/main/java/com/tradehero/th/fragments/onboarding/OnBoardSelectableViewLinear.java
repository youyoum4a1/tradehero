package com.tradehero.th.fragments.onboarding;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.api.SelectableDTO;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.inject.HierarchyInjector;

public class OnBoardSelectableViewLinear<T extends DTO> extends LinearLayout
        implements DTOView<SelectableDTO<T>>
{
    @InjectView(android.R.id.icon2) View selectedView;
    private final float alphaUnSelected;

    //<editor-fold desc="Constructors">
    public OnBoardSelectableViewLinear(Context context)
    {
        super(context);
        alphaUnSelected = 1f;
    }

    public OnBoardSelectableViewLinear(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        alphaUnSelected = getAlpha(context, attrs);
    }

    public OnBoardSelectableViewLinear(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        alphaUnSelected = getAlpha(context, attrs);
    }
    //</editor-fold>

    static float getAlpha(@NonNull Context context, @NonNull AttributeSet attrs)
    {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OnBoardSelectableViewLinear);
        float region = a.getFloat(R.styleable.OnBoardSelectableViewLinear_alphaUnSelected, 1f);
        a.recycle();
        return region;
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        HierarchyInjector.inject(this);
        if (!isInEditMode())
        {
            ButterKnife.inject(this);
        }
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (!isInEditMode())
        {
            ButterKnife.inject(this);
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @Override public void display(@NonNull SelectableDTO<T> dto)
    {
        if (selectedView != null)
        {
            selectedView.setVisibility(dto.selected ? VISIBLE : INVISIBLE);
        }
        setAlpha(dto.selected ? 1f : alphaUnSelected);
    }
}
