package com.ayondo.academy.fragments.social.friend;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.tradehero.common.utils.THToast;
import com.ayondo.academy.R;
import com.ayondo.academy.api.BaseResponseDTO;
import com.ayondo.academy.api.social.SocialNetworkEnum;
import com.ayondo.academy.api.social.UserFriendsDTO;
import com.ayondo.academy.api.social.UserFriendsDTOList;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class SocialFriendsFragmentWeibo extends SocialFriendsFragment
{
    private final int MAX_TEXT_LENGTH = 140;

    @Inject UserProfileCacheRx userProfileCache;
    @Inject Provider<SocialFriendHandlerWeibo> weiboSocialFriendHandlerProvider;
    private AlertDialog mWeiboInviteDialog;
    protected EditText edtMessageInvite;
    protected TextView tvMessageCount;
    protected Button btnMessageCancel;
    protected Button btnMessageComfirm;

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

    @Override @NonNull
    protected SocialFriendHandlerWeibo createFriendHandler()
    {
        return weiboSocialFriendHandlerProvider.get();
    }

    @Override @NonNull protected SocialFriendListItemDTOList createListedItems(@NonNull UserFriendsDTOList value)
    {
        SocialFriendListItemDTOList created = super.createListedItems(value);

        int countOfUnFollowed = getCountFollowable();
        int countOfUnInvited = getCountInvitable();

        if (countOfUnFollowed != 0)
        {
            created.add(0, new SocialFriendListItemHeaderDTO(getString(R.string.friends_can_be_follow, countOfUnFollowed)));
        }
        if (countOfUnInvited != 0)
        {
            created.add(countOfUnFollowed + (countOfUnFollowed == 0 ? 0 : 1),
                    new SocialFriendListItemHeaderDTO(getString(R.string.friends_can_be_invite, countOfUnInvited)));
        }

        return created;
    }

    protected int getCountFollowable()
    {
        return followableFriends == null ? 0 : followableFriends.size();
    }

    protected int getCountInvitable()
    {
        return invitableFriends == null ? 0 : invitableFriends.size();
    }

    @Override protected void inviteAll(View view)
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
        btnMessageCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnMessageComfirm = (Button) view.findViewById(R.id.btn_comfirm);

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

        UserProfileDTO userProfileDTO = userProfileCache.getCachedValue(currentUserId.toUserBaseKey());
        if (userProfileDTO != null)
        {
            edtMessageInvite.setText(getString(R.string.weibo_friends_invite, userProfileDTO.referralCode) + getStrMessageOfAtList(usersToInvite));
            setMessageTextLength();
            builder.setView(view);
            builder.setCancelable(true);
            addMessageTextListener();
            mWeiboInviteDialog = builder.create();
            mWeiboInviteDialog.show();
        }
    }

    protected void addMessageTextListener()
    {
        if (edtMessageInvite != null)
        {
            edtMessageInvite.addTextChangedListener(new TextWatcher()
            {
                @Override public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
                {
                }

                @Override public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
                {
                    setMessageTextLength();
                }

                @Override public void afterTextChanged(Editable editable)
                {
                }
            });
        }
    }

    protected void setMessageTextLength()
    {
        int length = edtMessageInvite.getText().toString().length();
        tvMessageCount.setText(getString(R.string.weibo_message_text_limit, length));
    }

    protected boolean checkMessageLengthLimit()
    {
        return edtMessageInvite.getText().toString().length() <= MAX_TEXT_LENGTH;
    }

    protected String getStrMessageOfAtList(List<UserFriendsDTO> usersToInvite)
    {
        if (usersToInvite == null)
        {
            return "";
        }
        else
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < usersToInvite.size(); i++)
            {
                sb.append(" @").append(usersToInvite.get(i).name);
            }
            return sb.toString();
        }
    }

    protected String getWeiboInviteMessage()
    {
        if (edtMessageInvite != null)
        {
            return edtMessageInvite.getText().toString();
        }
        return null;
    }

    @Override protected void handleInviteUsers(@NonNull List<UserFriendsDTO> usersToInvite)
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
        if (usersToInvite == null || usersToInvite.size() == 0)
        {
            THToast.show(R.string.social_no_friend_to_invite);
            return;
        }
        handleInviteUsers(msg, usersToInvite);
    }

    protected void handleInviteUsers(String msg, List<UserFriendsDTO> usersToInvite)
    {
        createFriendHandler();
        RequestObserver<BaseResponseDTO> observer = createInviteObserver(usersToInvite);
        observer.onRequestStart();
        ((SocialFriendHandlerWeibo) socialFriendHandler).inviteWeiboFriends(msg, currentUserId.toUserBaseKey()) /*, usersToInvite*/
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override protected void handleInviteSuccess(List<UserFriendsDTO> usersToInvite)
    {
        super.handleInviteSuccess(usersToInvite);
        dissmissWeiboInviteDialog();
        clearWeiboInviteStatus();
        setInviteAllViewCountText(getCountOfCheckBoxInvited());
    }

    private void clearWeiboInviteStatus()
    {
        if (listedSocialItems != null)
        {
            for (SocialFriendListItemDTO o : listedSocialItems)
            {
                if (o instanceof SocialFriendListItemUserDTO)
                {
                    ((SocialFriendListItemUserDTO) o).isSelected = false;
                }
            }
            if (socialFriendsListAdapter != null)
            {
                socialFriendsListAdapter.notifyDataSetChanged();
            }
        }
    }
}
