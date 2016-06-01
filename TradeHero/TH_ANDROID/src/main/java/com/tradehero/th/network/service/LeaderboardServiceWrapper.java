package com.ayondo.academy.network.service;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.ayondo.academy.api.leaderboard.LeaderboardDTO;
import com.ayondo.academy.api.leaderboard.def.FriendLeaderboardDefDTO;
import com.ayondo.academy.api.leaderboard.def.LeaderboardDefDTO;
import com.ayondo.academy.api.leaderboard.def.LeaderboardDefDTOList;
import com.ayondo.academy.api.leaderboard.key.FriendsPerPagedLeaderboardKey;
import com.ayondo.academy.api.leaderboard.key.LeaderboardDefKey;
import com.ayondo.academy.api.leaderboard.key.LeaderboardKey;
import com.ayondo.academy.api.leaderboard.key.PagedLeaderboardKey;
import com.ayondo.academy.api.leaderboard.key.PerPagedFilteredLeaderboardKey;
import com.ayondo.academy.api.leaderboard.key.PerPagedLeaderboardKey;
import com.ayondo.academy.api.leaderboard.key.SortedPerPagedLeaderboardKey;
import com.ayondo.academy.api.leaderboard.key.UserOnLeaderboardKey;
import com.ayondo.academy.api.leaderboard.position.LeaderboardFriendsDTO;
import com.ayondo.academy.api.leaderboard.position.LeaderboardFriendsKey;
import com.ayondo.academy.api.leaderboard.position.LeaderboardMarkUserId;
import com.ayondo.academy.api.leaderboard.position.PagedLeaderboardMarkUserId;
import com.ayondo.academy.api.leaderboard.position.PerPagedLeaderboardMarkUserId;
import com.ayondo.academy.api.position.GetPositionsDTO;
import com.ayondo.academy.models.position.DTOProcessorGetPositions;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Func1;

@Singleton public class LeaderboardServiceWrapper
{
    @NonNull private final Resources resources;
    @NonNull private final LeaderboardServiceRx leaderboardServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardServiceWrapper(
            @NonNull Context context,
            @NonNull LeaderboardServiceRx leaderboardServiceRx)
    {
        super();
        this.resources = context.getResources();
        this.leaderboardServiceRx = leaderboardServiceRx;
    }
    //</editor-fold>

    //<editor-fold desc="Get Leaderboard Definitions">
    @NonNull public Observable<LeaderboardDefDTOList> getLeaderboardDefinitionsRx()
    {
        return leaderboardServiceRx.getLeaderboardDefinitions()
                .map(new Func1<LeaderboardDefDTOList, LeaderboardDefDTOList>()
                {
                    @Override public LeaderboardDefDTOList call(LeaderboardDefDTOList leaderboardDefDTOs)
                    {
                        leaderboardDefDTOs.add(new FriendLeaderboardDefDTO(resources));
                        return leaderboardDefDTOs;
                    }
                });
    }
    //</editor-fold>

    //<editor-fold desc="Get Leaderboard Definition">
    @NonNull public Observable<LeaderboardDefDTO> getLeaderboardDef(
            @NonNull LeaderboardDefKey leaderboardDefKey)
    {
        return leaderboardServiceRx.getLeaderboardDef(leaderboardDefKey.key);
    }
    //</editor-fold>

    //<editor-fold desc="Get Leaderboard">
    @NonNull public Observable<LeaderboardDTO> getLeaderboardRx(@NonNull LeaderboardKey leaderboardKey)
    {
        Integer lbType =
                leaderboardKey.getAssetClass() != null? leaderboardKey.getAssetClass().getValue() : null;
        if (leaderboardKey instanceof UserOnLeaderboardKey)
        {
            return leaderboardServiceRx.getUserOnLeaderboard(
                    leaderboardKey.id,
                    lbType,
                    ((UserOnLeaderboardKey) leaderboardKey).userBaseKey.key,
                    null);
        }
        else if (leaderboardKey instanceof SortedPerPagedLeaderboardKey)
        {
            SortedPerPagedLeaderboardKey sortedPerPagedLeaderboardKey = (SortedPerPagedLeaderboardKey) leaderboardKey;
            return leaderboardServiceRx.getLeaderboard(
                    sortedPerPagedLeaderboardKey.id,
                    sortedPerPagedLeaderboardKey.page,
                    sortedPerPagedLeaderboardKey.perPage,
                    sortedPerPagedLeaderboardKey.sortType);
        }
        else if (leaderboardKey instanceof PerPagedFilteredLeaderboardKey)
        {
            PerPagedFilteredLeaderboardKey perPagedFilteredLeaderboardKey = (PerPagedFilteredLeaderboardKey) leaderboardKey;
            return leaderboardServiceRx.getFilteredLeaderboard(perPagedFilteredLeaderboardKey.id,
                    lbType,
                    perPagedFilteredLeaderboardKey.winRatio,
                    perPagedFilteredLeaderboardKey.averageMonthlyTradeCount,

                    // HACK https://www.pivotaltracker.com/story/show/73042972
                    Math.max(1, perPagedFilteredLeaderboardKey.averageHoldingDays),

                    perPagedFilteredLeaderboardKey.minSharpeRatio,
                    perPagedFilteredLeaderboardKey.minConsistency == null ? null : 1 / perPagedFilteredLeaderboardKey.minConsistency,
                    perPagedFilteredLeaderboardKey.page,
                    perPagedFilteredLeaderboardKey.perPage);
        }
        else if (leaderboardKey instanceof FriendsPerPagedLeaderboardKey)
        {
            return leaderboardServiceRx.getNewFriendsLeaderboard()
                    .map(new Func1<LeaderboardFriendsDTO, LeaderboardDTO>()
                    {
                        @Override public LeaderboardDTO call(LeaderboardFriendsDTO leaderboardFriendsDTO)
                        {
                            return leaderboardFriendsDTO.leaderboard;
                        }
                    });
        }
        else if (leaderboardKey instanceof PerPagedLeaderboardKey)
        {
            PerPagedLeaderboardKey perPagedLeaderboardKey = (PerPagedLeaderboardKey) leaderboardKey;
            return leaderboardServiceRx.getLeaderboard(
                    perPagedLeaderboardKey.id,
                    lbType,
                    perPagedLeaderboardKey.page,
                    perPagedLeaderboardKey.perPage);
        }
        else if (leaderboardKey instanceof PagedLeaderboardKey)
        {
            PagedLeaderboardKey pagedLeaderboardKey = (PagedLeaderboardKey) leaderboardKey;
            return leaderboardServiceRx.getLeaderboard(
                    pagedLeaderboardKey.id,
                    lbType,
                    pagedLeaderboardKey.page,
                    null);
        }
        return leaderboardServiceRx.getLeaderboard(leaderboardKey.id, lbType, null, null);
    }

    @NonNull public Observable<LeaderboardFriendsDTO> getNewFriendsLeaderboardRx(@NonNull LeaderboardFriendsKey key)
    {
        if (!key.page.equals(1))
        {
            return Observable.just(new LeaderboardFriendsDTO());
        }
        return leaderboardServiceRx.getNewFriendsLeaderboard();
    }
    //</editor-fold>

    //<editor-fold desc="Get Positions For Leaderboard Mark User">
    @NonNull public Observable<GetPositionsDTO> getPositionsForLeaderboardMarkUserRx(
            @NonNull LeaderboardMarkUserId key)
    {
        Observable<GetPositionsDTO> received;
        if (key instanceof PerPagedLeaderboardMarkUserId)
        {
            PerPagedLeaderboardMarkUserId perPagedLeaderboardMarkUserId = (PerPagedLeaderboardMarkUserId) key;
            received = leaderboardServiceRx.getPositionsForLeaderboardMarkUser(
                    perPagedLeaderboardMarkUserId.key,
                    perPagedLeaderboardMarkUserId.page,
                    perPagedLeaderboardMarkUserId.perPage);
        }
        else if (key instanceof PagedLeaderboardMarkUserId)
        {
            PagedLeaderboardMarkUserId pagedLeaderboardMarkUserId = (PagedLeaderboardMarkUserId) key;
            received = leaderboardServiceRx.getPositionsForLeaderboardMarkUser(
                    pagedLeaderboardMarkUserId.key,
                    pagedLeaderboardMarkUserId.page,
                    null);
        }
        else
        {
            received = leaderboardServiceRx.getPositionsForLeaderboardMarkUser(
                    key.key,
                    null,
                    null);
        }
        return received.map(new DTOProcessorGetPositions(key));
    }
    //</editor-fold>
}
