package com.androidth.general.fragments.leaderboard;

import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import com.androidth.general.common.api.PagedDTOKey;
import com.androidth.general.common.persistence.ContainerDTO;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.common.rx.PairGetSecond;
import com.androidth.general.R;
import com.androidth.general.api.leaderboard.key.LeaderboardDefKey;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.fragments.BasePagedRecyclerRxFragment;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import com.androidth.general.rx.TimberAndToastOnErrorAction1;
import java.util.List;
import javax.inject.Inject;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

abstract public class BaseLeaderboardPagedRecyclerRxFragment<
        PagedDTOKeyType extends PagedDTOKey,
        DTOType extends DTO,
        DTOListType extends DTO & List<DTOType>,
        ContainerDTOType extends DTO & ContainerDTO<DTOType, DTOListType>>
        extends BasePagedRecyclerRxFragment<
        PagedDTOKeyType,
        DTOType,
        DTOListType,
        ContainerDTOType>
{
    private static final String BUNDLE_KEY_LEADERBOARD_ID = BaseLeaderboardPagedRecyclerRxFragment.class.getName() + ".leaderboardId";

    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;

    @NonNull protected LeaderboardDefKey leaderboardDefKey;

    protected UserProfileDTO currentUserProfileDTO;

    public static void putLeaderboardDefKey(@NonNull Bundle args, @NonNull LeaderboardDefKey leaderboardDefKey)
    {
        args.putInt(BUNDLE_KEY_LEADERBOARD_ID, leaderboardDefKey.key);
    }

    @NonNull public static LeaderboardDefKey getLeadboardDefKey(@NonNull Bundle args)
    {
        return new LeaderboardDefKey(args.getInt(BUNDLE_KEY_LEADERBOARD_ID));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        leaderboardDefKey = getLeadboardDefKey(getArguments());
        super.onCreate(savedInstanceState);
    }

    @Override public void onStart()
    {
        super.onStart();
        if (currentUserProfileDTO == null)
        {
            fetchCurrentUserProfile();
        }
        else
        {
            setCurrentUserProfileDTO(currentUserProfileDTO);
        }
    }

    @Override public void onDestroy()
    {
        currentUserProfileDTO = null;
        super.onDestroy();
    }

    protected void fetchCurrentUserProfile()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                userProfileCache.get(currentUserId.toUserBaseKey())
                        .map(new PairGetSecond<UserBaseKey, UserProfileDTO>()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<UserProfileDTO>()
                        {
                            @Override public void call(UserProfileDTO userProfileDTO)
                            {
                                BaseLeaderboardPagedRecyclerRxFragment.this.setCurrentUserProfileDTO(userProfileDTO);
                            }
                        },
                        new TimberAndToastOnErrorAction1(
                                getString(R.string.error_fetch_your_user_profile),
                                "Failed to download current UserProfile")));
    }

    protected void setCurrentUserProfileDTO(@NonNull UserProfileDTO currentUserProfileDTO)
    {
        this.currentUserProfileDTO = currentUserProfileDTO;
    }

    @MenuRes protected int getMenuResource()
    {
        return R.menu.empty_menu;
    }

    public abstract void updateRow(LeaderboardItemDisplayDTO dto);
}
