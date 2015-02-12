package com.tradehero.th.fragments.discovery;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import com.maurycy.ScaleImageView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.games.MiniGameDefDTO;
import com.tradehero.th.inject.HierarchyInjector;
import javax.inject.Inject;

public class MiniGameDefItemView extends ScaleImageView
        implements DTOView<MiniGameDefDTO>
{
    @NonNull private MiniGameDefDTO miniGameDefDTO;
    @Inject Picasso picasso;

    public MiniGameDefItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        picasso.cancelRequest(this);
    }

    @Override public void display(@NonNull MiniGameDefDTO miniGameDefDTO)
    {
        this.miniGameDefDTO = miniGameDefDTO;

        displayImage();
    }

    private void displayImage()
    {
        picasso.load(miniGameDefDTO.image)
                .fit()
                .centerInside()
                .placeholder(R.color.light_grey)
                .into(this);
    }
}
