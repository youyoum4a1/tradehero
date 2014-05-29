package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.tradehero.th.R;
import com.tradehero.th.api.translation.TranslationToken;
import com.tradehero.th.persistence.translation.TranslationTokenCache;
import com.tradehero.th.persistence.translation.TranslationTokenKey;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import timber.log.Timber;

public class DiscussionActionButtonsView extends LinearLayout
{
    @InjectView(R.id.discussion_action_button_comment_count) TextView comment;
    @InjectView(R.id.discussion_action_button_share) View shareButton;
    @InjectView(R.id.discussion_action_button_translate) @Optional View translateButton;
    @InjectView(R.id.discussion_action_button_translate_image) @Optional ImageView translateButtonImage;
    @InjectView(R.id.discussion_action_button_more) View more;

    @Inject TranslationTokenCache translationTokenCache;

    //<editor-fold desc="Constructors">
    public DiscussionActionButtonsView(Context context)
    {
        super(context);
    }

    public DiscussionActionButtonsView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DiscussionActionButtonsView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        displayTranslateButtonImage();
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
    }

    protected void displayTranslateButtonImage()
    {
        if (translateButton != null)
        {
            TranslationToken token = translationTokenCache.get(new TranslationTokenKey());
            if (token != null)
            {
                try
                {
                    translateButtonImage.setImageResource(token.logoResId());
                }
                catch (OutOfMemoryError e)
                {
                    Timber.e(e, null);
                }
            }
        }
    }
}
