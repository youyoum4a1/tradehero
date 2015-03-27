package com.tradehero.th.fragments.news;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewLinear;

public class NewsViewLinear extends AbstractDiscussionCompactItemViewLinear
{
    //<editor-fold desc="Constructors">
    public NewsViewLinear(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @NonNull @Override protected NewsItemViewHolder createViewHolder()
    {
        return new NewsItemViewHolder(getContext());
    }

    public void setTitleBackground(int resId)
    {
        viewHolder.setBackgroundResource(resId);
    }
}
