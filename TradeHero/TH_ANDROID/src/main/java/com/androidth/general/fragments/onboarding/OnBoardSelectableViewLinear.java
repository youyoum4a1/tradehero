package com.androidth.general.fragments.onboarding;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.androidth.general.common.api.SelectableDTO;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.R;
import com.androidth.general.api.DTOView;
import com.androidth.general.inject.HierarchyInjector;

public class OnBoardSelectableViewLinear<
        ValueDTOType extends DTO,
        SelectableDTOType extends SelectableDTO<ValueDTOType>> extends LinearLayout
        implements DTOView<SelectableDTOType>
{
    @Bind(android.R.id.icon2) View selectedView;
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
            ButterKnife.bind(this);
        }
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (!isInEditMode())
        {
            ButterKnife.bind(this);
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.unbind(this);
        super.onDetachedFromWindow();
    }

    @Override public void display(@NonNull SelectableDTOType dto)
    {
        if (selectedView != null)
        {
            selectedView.setVisibility(dto.selected ? VISIBLE : INVISIBLE);
        }
        setAlpha(dto.selected ? 1f : alphaUnSelected);
    }
}
