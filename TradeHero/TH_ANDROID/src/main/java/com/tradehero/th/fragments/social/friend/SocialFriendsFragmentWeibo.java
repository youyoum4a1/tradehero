package com.tradehero.th.fragments.social.friend;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.UserFriendsDTO;
import java.util.List;
import timber.log.Timber;

public class SocialFriendsFragmentWeibo extends SocialFriendsFragment
{
    private AlertDialog mWeiboInviteDialog;

    @Override
    protected SocialNetworkEnum getSocialNetwork()
    {
        return SocialNetworkEnum.WB;
    }

    @Override
    protected String getTitle()
    {
        return getString(R.string.invite_social_friend, getString(R.string.sina_weibo));
    }

    @Override
    protected boolean canInviteAll()
    {
        return true;
    }

    @Override
    protected boolean canInvite()
    {
        return true;
    }

    @Override protected void bindNormalData()
    {
        int countOfUnFollowed = getCountOfUnFollowed();
        int countOfUnInvited = getCountOfUnInvited();

        if (countOfUnFollowed != 0)
        {
            listedSocialItems.add(0, new SocialFriendListItemHeaderDTO(getString(R.string.friends_can_be_follow, countOfUnFollowed)));
        }
        if (countOfUnInvited != 0)
        {
            listedSocialItems.add(countOfUnFollowed + (countOfUnFollowed == 0 ? 0 : 1),
                    new SocialFriendListItemHeaderDTO(getString(R.string.friends_can_be_invite, countOfUnInvited)));
        }

        super.bindNormalData();
    }

    @Override protected void inviteAll()
    {
        inviteAllSelected();
    }

    private void showWeiboInviteDialog(final List<UserFriendsDTO> usersToInvite)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.weibo_friends_invite_dialog, null);
        edtMessageInvite = (EditText) view.findViewById(R.id.edtInviteMessage);
        tvMessageCount = (TextView) view.findViewById(R.id.tvMessageCount);
        btnMessageCancel = (Button) view.findViewById(R.id.btnCancle);
        btnMessageComfirm = (Button) view.findViewById(R.id.btnComfirm);

        btnMessageCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view)
            {
                Timber.d("WeiboInviteDialog Canceled !");
                mWeiboInviteDialog.dismiss();
            }
        });

        btnMessageComfirm.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view)
            {
                Timber.d("WeiboInviteDialog Comfirmed");
                if (checkMessageLengthLimit())
                {
                    dissmissWeiboInviteDialog();
                    inviteFriends(getWeiboInviteMessage(), usersToInvite);
                }
                else
                {
                    THToast.show(R.string.weibo_message_length_error);
                }
            }
        });

        edtMessageInvite.setText(getString(R.string.weibo_friends_invite) + getStrMessageOfAtList(usersToInvite));
        setMessageTextLength();
        builder.setView(view);
        builder.setCancelable(true);
        addMessageTextListener();
        mWeiboInviteDialog = builder.create();
        mWeiboInviteDialog.show();
    }

    @Override protected void handleInviteUsers(List<UserFriendsDTO> usersToInvite)
    {
        showWeiboInviteDialog(usersToInvite);
    }

    private void dissmissWeiboInviteDialog()
    {
        if (mWeiboInviteDialog != null)
        {
            mWeiboInviteDialog.dismiss();
        }
    }

    protected void inviteFriends(String msg, List<UserFriendsDTO> usersToInvite)
    {
        List<UserFriendsDTO> usersUnInvited = usersToInvite;
        if (usersUnInvited == null || usersUnInvited.size() == 0)
        {
            THToast.show(R.string.social_no_friend_to_invite);
            return;
        }
        handleInviteUsers(msg, usersUnInvited);
    }

    protected void handleInviteUsers(String msg, List<UserFriendsDTO> usersToInvite)
    {
        createFriendHandler();
        socialFriendHandler.inviteWeiboFriends(msg, currentUserId.toUserBaseKey(), usersToInvite, createInviteCallback(usersToInvite));
    }

    @Override protected void handleInviteSuccess(List<UserFriendsDTO> usersToInvite)
    {
        super.handleInviteSuccess(usersToInvite);
        dissmissWeiboInviteDialog();
        clearWeiboInviteStatus();
    }

    private void clearWeiboInviteStatus()
    {
        if (friendDTOList != null)
        {
            for (UserFriendsDTO userdto : friendDTOList)
            {
                userdto.selected = false;
            }
            if (socialFriendsListAdapter != null)
            {
                socialFriendsListAdapter.notifyDataSetChanged();
            }
        }
    }
}
