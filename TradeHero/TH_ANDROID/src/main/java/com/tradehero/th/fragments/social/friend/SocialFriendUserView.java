package com.tradehero.th.fragments.social.friend;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.th.R;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.social.UserFriendsWeiboDTO;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.utils.DaggerUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

public class SocialFriendUserView extends SocialFriendItemView implements View.OnClickListener
{
    @InjectView(R.id.social_item_logo) ImageView friendLogo;
    @InjectView(R.id.social_item_title) TextView friendTitle;
    @InjectView(R.id.social_item_action_btn) TextView actionBtn;
    @InjectView(R.id.social_item_action_cb) ImageView actionCb;

    private SocialFriendListItemUserDTO socialFriendListItemUserDTO;
    @Nullable private OnElementClickListener onElementClickListener;

    //<editor-fold desc="Constructors">
    public SocialFriendUserView(Context context)
    {
        super(context);
    }

    public SocialFriendUserView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
        DaggerUtils.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        setOnClickListener(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        setOnClickListener(null);
        super.onDetachedFromWindow();
    }

    public void onActionButtonClick()
    {
        if (onElementClickListener != null)
        {
            if (socialFriendListItemUserDTO.userFriendsDTO.isTradeHeroUser())
            {
                onElementClickListener.onFollowButtonClick(socialFriendListItemUserDTO.userFriendsDTO);
            }
            else
            {
                onElementClickListener.onInviteButtonClick(socialFriendListItemUserDTO.userFriendsDTO);
            }
        }
    }


    public void onActionCheckBoxClick()
    {
        if (onElementClickListener != null)
        {
            socialFriendListItemUserDTO.isSelected = !socialFriendListItemUserDTO.isSelected;
            actionCb.setBackgroundResource(socialFriendListItemUserDTO.isSelected ?
                    R.drawable.register_duihao : R.drawable.register_duihao_cancel);
            onElementClickListener.onCheckBoxClick(socialFriendListItemUserDTO.userFriendsDTO);
        }
    }

    public void setOnElementClickedListener(@Nullable OnElementClickListener onElementClickListener)
    {
        this.onElementClickListener = onElementClickListener;
    }

    @Override
    public void display(@NotNull SocialFriendListItemDTO dto)
    {
        if(dto instanceof SocialFriendListItemUserDTO)
        {
            this.socialFriendListItemUserDTO = (SocialFriendListItemUserDTO) dto;
            displayUserIcon();
            displayTitle();
            displayActionButton();
        }
    }

    private void displayUserIcon()
    {
        ImageLoader.getInstance().displayImage(socialFriendListItemUserDTO.userFriendsDTO.getProfilePictureURL(),
                friendLogo,
                UniversalImageLoader.getAvatarImageLoaderOptions(false));
    }


    private void displayTitle()
    {
        friendTitle.setText(socialFriendListItemUserDTO.userFriendsDTO.name);
    }

    private void displayActionButton()
    {
        if (socialFriendListItemUserDTO.userFriendsDTO.isTradeHeroUser())
        {
            actionBtn.setText(R.string.follow);
            actionBtn.setBackgroundResource(R.drawable.basic_green_selector_round_corner);
            actionBtn.setEnabled(true);
            actionBtn.setOnClickListener(new OnClickListener(

            ) {
                @Override
                public void onClick(View v) {
                    onActionButtonClick();
                }
            });
        }
        else
        {
            setWeiboCheckBox();
        }
    }

    private boolean isNeedCheckBoxShow()
    {
        return socialFriendListItemUserDTO.userFriendsDTO instanceof UserFriendsWeiboDTO
                && (!socialFriendListItemUserDTO.userFriendsDTO.isTradeHeroUser());
    }

    private void setWeiboCheckBox()
    {
        actionCb.setBackgroundResource(socialFriendListItemUserDTO.isSelected ?
                R.drawable.register_duihao : R.drawable.register_duihao_cancel);

        // TODO change to be another test
        if (isNeedCheckBoxShow())
        {
            actionBtn.setVisibility(View.GONE);
            actionCb.setVisibility(View.VISIBLE);
        }
        else
        {
            actionBtn.setVisibility(View.VISIBLE);
            actionCb.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (isNeedCheckBoxShow())
        {
            onActionCheckBoxClick();
        }
    }

    public interface OnElementClickListener
    {
        void onFollowButtonClick(@NotNull UserFriendsDTO userFriendsDTO);
        void onInviteButtonClick(@NotNull UserFriendsDTO userFriendsDTO);
        void onCheckBoxClick(@NotNull UserFriendsDTO userFriendsDTO);
    }
}
