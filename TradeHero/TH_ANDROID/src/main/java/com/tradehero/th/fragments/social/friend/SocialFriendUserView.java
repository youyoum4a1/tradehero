package com.tradehero.th.fragments.social.friend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
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
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.graphics.ForUserPhoto;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class SocialFriendUserView extends SocialFriendItemView
{
    @InjectView(R.id.social_item_logo) ImageView friendLogo;
    @InjectView(R.id.social_item_title) TextView friendTitle;
    @InjectView(R.id.social_item_action_btn) TextView actionBtn;
    @InjectView(R.id.social_friend_item_ll) LinearLayout socialFriendItem;
    @InjectView(R.id.social_item_action_cb) CheckBox actionCb;
    @Inject Picasso picasso;
    @Inject @ForUserPhoto Transformation peopleIconTransformation;

    private SocialFriendListItemUserDTO socialFriendListItemUserDTO;
    @Nullable private OnElementClickListener onElementClickListener;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public SocialFriendUserView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
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
        HierarchyInjector.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        picasso.cancelRequest(friendLogo);
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
            socialFriendListItemUserDTO.isSelected = actionCb.isChecked();
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
        displayDefaultUserIcon();
        picasso.load(socialFriendListItemUserDTO.userFriendsDTO.getProfilePictureURL())
                .placeholder(friendLogo.getDrawable())
                .transform(peopleIconTransformation)
                .error(R.drawable.superman_facebook)
                .into(friendLogo, new Callback()
                {
                    @Override public void onSuccess()
                    {
                        Timber.d("windy display User Icon success!");
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
                .transform(peopleIconTransformation)
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
            actionBtn.setBackgroundResource(R.drawable.leaderboard_user_item_follow_action_button);
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
        actionCb.setChecked(socialFriendListItemUserDTO.isSelected);

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
