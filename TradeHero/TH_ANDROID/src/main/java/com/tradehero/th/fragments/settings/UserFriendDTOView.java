package com.tradehero.th.fragments.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.RoundedShapeTransformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/22/14 Time: 11:53 AM Copyright (c) TradeHero
 */
public class UserFriendDTOView extends RelativeLayout
        implements DTOView<UserFriendsDTO>, Checkable
{
    private ImageView userFriendAvatar;
    private TextView userFriendName;
    private TextView userFriendSourceFb;
    private TextView userFriendSourceLi;
    private TextView userFriendSourceContact;
    private UserFriendsDTO userFriendDTO;

    @Inject protected Lazy<Picasso> picasso;

    //<editor-fold desc="Constructors">
    public UserFriendDTOView(Context context)
    {
        super(context);
    }

    public UserFriendDTOView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public UserFriendDTOView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        init();
    }

    private void init()
    {
        DaggerUtils.inject(this);

        userFriendAvatar = (ImageView) findViewById(R.id.user_friend_avatar);
        userFriendName = (TextView) findViewById(R.id.user_friend_name);

        userFriendSourceFb = (TextView) findViewById(R.id.user_friend_source_facebook);
        userFriendSourceLi = (TextView) findViewById(R.id.user_friend_source_linkedin);
        userFriendSourceContact = (TextView) findViewById(R.id.user_friend_source_contact);
    }

    @Override public void display(UserFriendsDTO dto)
    {
        linkWith(dto, true);
    }

    private void linkWith(UserFriendsDTO userFriendDTO, boolean andDisplay)
    {
        if (userFriendDTO != null)
        {
            this.userFriendDTO = userFriendDTO;
        }
        if (andDisplay && userFriendDTO != null)
        {
            displayFriendAvatar();
            displayFriendName();
            displayFriendSource();
            displaySelectionState();
        }
    }

    private void displaySelectionState()
    {
        setBackgroundColor(!isChecked() ? getResources().getColor(R.color.white) : getResources().getColor(R.color.gray_normal));
    }

    private void displayFriendSource()
    {
        resetVisibilityOfSourceButtons();

        if (userFriendDTO.fbId != null)
        {
            userFriendSourceFb.setVisibility(View.VISIBLE);
        }
        else if (userFriendDTO.liId != null)
        {
            userFriendSourceLi.setVisibility(View.VISIBLE);
        }
        else if (userFriendDTO.getEmail() != null)
        {
            userFriendSourceContact.setVisibility(View.VISIBLE);
            userFriendSourceContact.setText(userFriendDTO.getEmail());
        }
    }

    private void resetVisibilityOfSourceButtons()
    {
        userFriendSourceFb.setVisibility(View.INVISIBLE);
        userFriendSourceLi.setVisibility(View.INVISIBLE);
        userFriendSourceContact.setVisibility(View.INVISIBLE);
    }

    private void displayFriendName()
    {
        if (userFriendDTO.name != null)
        {
            userFriendName.setText(userFriendDTO.name);
        }
        else
        {
            userFriendName.setText("");
        }
    }

    private void displayFriendAvatar()
    {
        String avatarUrl = userFriendDTO.getProfilePictureURL();
        if (avatarUrl != null)
        {
            picasso.get().load(avatarUrl)
                    .transform(new RoundedShapeTransformation())
                    .into(userFriendAvatar);
        }
        else
        {
            picasso.get().load(R.drawable.superman_facebook)
                    .transform(new RoundedShapeTransformation())
                    .into(userFriendAvatar);
        }
    }

    //@Override public void invalidate()
    //{
    //    displaySelectionState();
    //    super.invalidate();
    //}

    @Override public void setChecked(boolean checked)
    {
        if (userFriendDTO != null && checked != isChecked())
        {
            userFriendDTO.setSelected(checked);
            displaySelectionState();
        }
    }

    @Override public boolean isChecked()
    {
        return userFriendDTO != null && userFriendDTO.isSelected();
    }

    @Override public void toggle()
    {
        setChecked(!isChecked());
    }
}
