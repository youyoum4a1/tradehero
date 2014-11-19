package com.tradehero.th.fragments.discovery;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.games.MiniGameDefDTO;
import com.tradehero.th.inject.HierarchyInjector;
import javax.inject.Inject;

public class MiniGameDefItemView extends FrameLayout
        implements DTOView<MiniGameDefDTO>
{
    @NonNull private MiniGameDefDTO miniGameDefDTO;
    @InjectView(R.id.minigame_item_background) ImageView gameBackground;
    @InjectView(R.id.minigame_item_title) TextView title;

    @Inject Picasso picasso;

    public MiniGameDefItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        picasso.cancelRequest(gameBackground);
    }

    @Override public void display(@NonNull MiniGameDefDTO miniGameDefDTO)
    {
        this.miniGameDefDTO = miniGameDefDTO;

        displayImage();
        displayTitle();
    }

    private void displayTitle()
    {
        title.setText(miniGameDefDTO.name);
    }

    private void displayImage()
    {
        picasso.load(miniGameDefDTO.image)
                .placeholder(miniGameDefDTO.comingSoon ? R.color.gray_3 : R.color.tradehero_blue)
                .into(gameBackground);
        if (miniGameDefDTO.comingSoon && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            gameBackground.setImageAlpha(80);
        }
    }
}
