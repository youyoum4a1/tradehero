package com.tradehero.th.fragments.onboarding.hero;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class SelectableUserViewRelative extends RelativeLayout
    implements DTOView<SelectableUserDTO>
{
    @Inject UserBaseDTOUtil userBaseDTOUtil;
    @Inject Picasso picasso;
    @Inject @ForUserPhoto Transformation transformation;
    @NotNull SelectableUserViewHolder viewHolder;

    @SuppressWarnings("UnusedDeclaration")
    public SelectableUserViewRelative(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        if (!isInEditMode())
        {
            viewHolder = new SelectableUserViewHolder(getContext(), userBaseDTOUtil, picasso, transformation);
            viewHolder.attachView(this);
        }
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (!isInEditMode())
        {
            viewHolder.attachView(this);
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        viewHolder.detachView();
        super.onDetachedFromWindow();
    }

    @Override public void display(SelectableUserDTO selectableUserDTO)
    {
        viewHolder.display(selectableUserDTO);
    }
}
