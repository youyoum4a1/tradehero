package com.tradehero.th.fragments.onboarding.stock;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.tradehero.th.api.DTOView;
import org.jetbrains.annotations.NotNull;

public class SecuritySelectableViewRelative extends RelativeLayout
    implements DTOView<SelectableSecurityDTO>
{
    @NotNull SecuritySelectableViewHolder securitySelectableViewHolder;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public SecuritySelectableViewRelative(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public SecuritySelectableViewRelative(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public SecuritySelectableViewRelative(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        if (!isInEditMode())
        {
            securitySelectableViewHolder = new SecuritySelectableViewHolder();
            securitySelectableViewHolder.attachView(this);
        }
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (!isInEditMode())
        {
            securitySelectableViewHolder.attachView(this);
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        securitySelectableViewHolder.detachView();
        super.onDetachedFromWindow();
    }

    @Override public void display(SelectableSecurityDTO selectableSecurityDTO)
    {
        securitySelectableViewHolder.display(selectableSecurityDTO);
    }
}
