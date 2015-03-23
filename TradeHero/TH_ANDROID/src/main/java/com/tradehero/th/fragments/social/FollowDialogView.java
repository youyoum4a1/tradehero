package com.tradehero.th.fragments.social;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.models.social.OnFollowRequestedListener;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

/**
 * Refactor the code inside AlertDialogUtil
 */
public class FollowDialogView extends LinearLayout
    implements DTOView<UserBaseDTO>
{
    @InjectView(R.id.user_profile_avatar) ImageView mUserPhoto;
    @InjectView(R.id.user_name) TextView mUsername;
    @InjectView(R.id.title) TextView mUserTitle;

    @InjectView(R.id.free_follow_layout) LinearLayout mFreeFollowArea;
    @InjectView(R.id.not_follow_layout) LinearLayout mNotFollowArea;

    @InjectView(R.id.btn_free) Button mFreeFollowButton;

    @Inject @ForUserPhoto Lazy<Transformation> peopleIconTransformation;
    @Inject Lazy<Picasso> picasso;

    @Nullable private UserBaseDTO userBaseDTO;
    @Nullable private OnFollowRequestedListener followRequestedListener;

    //<editor-fold desc="Constructors">
    public FollowDialogView(Context context)
    {
        super(context);
    }

    public FollowDialogView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FollowDialogView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);
        DaggerUtils.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    public void display(@Nullable UserBaseDTO userBaseDTO)
    {
        this.userBaseDTO = userBaseDTO;

        displayUserPhoto();
        displayUserName();
    }

    @OnClick({
            R.id.btn_free,
            R.id.free_follow})
    void onFreeFollowClicked()
    {
        OnFollowRequestedListener followRequestedListenerCopy = followRequestedListener;
        if (followRequestedListenerCopy != null && userBaseDTO != null)
        {
            followRequestedListenerCopy.freeFollowRequested(userBaseDTO.getBaseKey());
        }
    }

    @OnClick({
            R.id.btn_premium,
            R.id.premium_follow
    })
    void onPremiumFollowButtonClicked()
    {
        OnFollowRequestedListener followRequestedListenerCopy = followRequestedListener;
        if (followRequestedListenerCopy != null && userBaseDTO != null)
        {
            followRequestedListenerCopy.premiumFollowRequested(userBaseDTO.getBaseKey());
        }
    }


    private void displayUserName()
    {
        mUsername.setText(userBaseDTO == null ? getContext().getString(R.string.loading_loading) : userBaseDTO.displayName);
    }

    private void displayUserPhoto()
    {
        loadDefaultPicture(mUserPhoto);
        if (userBaseDTO != null && userBaseDTO.picture != null)
        {
            picasso.get().load(userBaseDTO.picture)
                    .transform(peopleIconTransformation.get())
                    .placeholder(mUserPhoto.getDrawable())
                    .into(mUserPhoto);
        }
    }

    protected void loadDefaultPicture(ImageView imageView)
    {
        if (imageView != null)
        {
            picasso.get().load(R.drawable.superman_facebook)
                    .transform(peopleIconTransformation.get())
                    .into(imageView);
        }
    }


}
