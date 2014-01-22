package com.tradehero.th.fragments.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
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
public class UserFriendDTOView extends RelativeLayout implements DTOView<UserFriendsDTO>
{
    private ImageView userFriendAvatar;
    private ImageView userFriendSourceImage;
    private TextView userFriendName;
    private TextView userFriendSourceName;
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
        userFriendSourceName = (TextView) findViewById(R.id.user_friend_source_name);
        userFriendSourceImage = (ImageView) findViewById(R.id.user_friend_source_picture);
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
        }
    }

    private void displayFriendSource()
    {
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
            userFriendAvatar.setImageResource(R.drawable.avatar);
        }
    }
}
