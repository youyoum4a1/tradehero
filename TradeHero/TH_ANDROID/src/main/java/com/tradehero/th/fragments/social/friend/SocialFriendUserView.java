package com.tradehero.th.fragments.social.friend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.social.UserFriendsWeiboDTO;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SocialFriendUserView extends SocialFriendItemView
{
    @InjectView(R.id.social_item_logo) ImageView friendLogo;
    @InjectView(R.id.social_item_title) TextView friendTitle;
    @InjectView(R.id.social_item_action_btn) TextView actionBtn;
    @InjectView(R.id.social_friend_item_ll) LinearLayout socialFriendItem;
    @InjectView(R.id.social_item_action_cb) ImageView actionCb;
    @Inject Picasso picasso;
    @Inject @ForUserPhoto Transformation peopleIconTransformation;

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
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @OnClick(R.id.social_item_action_btn)
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

    @OnClick(R.id.social_item_action_cb)
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

    @OnClick(R.id.social_friend_item_ll)
    public void onActionItemViewClick()
    {
        if (isNeedCheckBoxShow())
        {
            actionCb.performClick();
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
            setItemViewClickable();
        }
    }

    private void setItemViewClickable()
    {
        socialFriendItem.setClickable(isNeedCheckBoxShow());
    }

    private void displayUserIcon()
    {
        picasso.load(socialFriendListItemUserDTO.userFriendsDTO.getProfilePictureURL())
                .placeholder(R.drawable.superman_facebook)
                .error(R.drawable.superman_facebook)
                .into(friendLogo, new Callback()
                {
                    @Override public void onSuccess()
                    {
                    }

                    @Override public void onError()
                    {
                        displayDefaultUserIcon();
                    }
                });
    }

    private void displayDefaultUserIcon()
    {
        picasso.load(R.drawable.superman_facebook)
                .into(friendLogo);
    }

    private void displayTitle()
    {
        friendTitle.setText(socialFriendListItemUserDTO.userFriendsDTO.name);
    }

    private void displayActionButton()
    {
        int pL = actionBtn.getPaddingLeft();
        int pR = actionBtn.getPaddingRight();
        int pT = actionBtn.getPaddingTop();
        int pB = actionBtn.getPaddingBottom();

        if (socialFriendListItemUserDTO.userFriendsDTO.isTradeHeroUser())
        {
            actionBtn.setText(R.string.follow);
            actionBtn.setBackgroundResource(R.drawable.basic_green_selector_round_corner);
            actionBtn.setEnabled(true);
        }
        else
        {
            actionBtn.setText(R.string.invite);
            actionBtn.setBackgroundResource(R.drawable.yellow_rounded_button_selector);
            actionBtn.setEnabled(!socialFriendListItemUserDTO.userFriendsDTO.alreadyInvited);
            setWeiboCheckBox();
        }
        actionBtn.setPadding(pL, pT, pR, pB);
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

    public static interface OnElementClickListener
    {
        void onFollowButtonClick(@NotNull UserFriendsDTO userFriendsDTO);
        void onInviteButtonClick(@NotNull UserFriendsDTO userFriendsDTO);
        void onCheckBoxClick(@NotNull UserFriendsDTO userFriendsDTO);
    }
}
