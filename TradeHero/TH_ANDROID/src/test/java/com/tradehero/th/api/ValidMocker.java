package com.ayondo.academy.api;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.achievement.key.AchievementCategoryId;
import com.ayondo.academy.api.achievement.key.QuestBonusId;
import com.ayondo.academy.api.achievement.key.UserAchievementId;
import com.ayondo.academy.api.alert.AlertId;
import com.ayondo.academy.api.competition.ProviderId;
import com.ayondo.academy.api.competition.key.BasicProviderSecurityListType;
import com.ayondo.academy.api.competition.key.CompetitionId;
import com.ayondo.academy.api.competition.key.HelpVideoListKey;
import com.ayondo.academy.api.competition.key.ProviderDisplayCellListKey;
import com.ayondo.academy.api.competition.key.ProviderSecurityListType;
import com.ayondo.academy.api.discussion.DiscussionType;
import com.ayondo.academy.api.discussion.VoteDirection;
import com.ayondo.academy.api.discussion.form.ReplyDiscussionFormDTO;
import com.ayondo.academy.api.discussion.key.CommentKey;
import com.ayondo.academy.api.discussion.key.DiscussionKey;
import com.ayondo.academy.api.discussion.key.DiscussionListKey;
import com.ayondo.academy.api.discussion.key.DiscussionVoteKey;
import com.ayondo.academy.api.discussion.key.MessageDiscussionListKey;
import com.ayondo.academy.api.discussion.key.MessageHeaderId;
import com.ayondo.academy.api.discussion.key.MessageListKey;
import com.ayondo.academy.api.discussion.key.RecipientTypedMessageListKey;
import com.ayondo.academy.api.education.VideoCategoryId;
import com.ayondo.academy.api.leaderboard.competition.CompetitionLeaderboardId;
import com.ayondo.academy.api.leaderboard.key.LeaderboardKey;
import com.ayondo.academy.api.leaderboard.position.LeaderboardMarkUserId;
import com.ayondo.academy.api.leaderboard.position.PerPagedLeaderboardMarkUserId;
import com.ayondo.academy.api.market.ExchangeIntegerId;
import com.ayondo.academy.api.news.key.NewsItemListKey;
import com.ayondo.academy.api.news.key.NewsItemListRegionalKey;
import com.ayondo.academy.api.news.key.NewsItemListSecurityKey;
import com.ayondo.academy.api.notification.NotificationKey;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import com.ayondo.academy.api.position.OwnedPositionId;
import com.ayondo.academy.api.position.PositionCompactId;
import com.ayondo.academy.api.security.SecurityId;
import com.ayondo.academy.api.security.SecurityIntegerId;
import com.ayondo.academy.api.security.key.SecurityListType;
import com.ayondo.academy.api.security.key.TrendingBasicSecurityListType;
import com.ayondo.academy.api.share.achievement.AchievementShareFormDTO;
import com.ayondo.academy.api.social.SocialNetworkEnum;
import com.ayondo.academy.api.social.key.FollowerHeroRelationId;
import com.ayondo.academy.api.social.key.FriendsListKey;
import com.ayondo.academy.api.timeline.key.TimelineItemDTOKey;
import com.ayondo.academy.api.trade.OwnedTradeId;
import com.ayondo.academy.api.users.SearchUserListType;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.users.UserListType;
import com.ayondo.academy.api.watchlist.WatchlistPositionDTO;
import java.util.Random;
import javax.inject.Inject;

import static org.mockito.Mockito.mock;

public class ValidMocker
{
    public static Random random = new Random();

    @Inject public ValidMocker()
    {
        super();
    }

    //<editor-fold desc="Create valid parameters">
    public Object mockValidParameter(@NonNull Class<?> type)
    {
        if (type.equals(UserBaseKey.class))
        {
            return new UserBaseKey(1);
        }
        if (type.equals(UserListType.class))
        {
            Integer perPage = random.nextBoolean() ? 2 : null;
            return new SearchUserListType(
                    "a",
                    perPage != null || random.nextBoolean() ? 1 : null,
                    perPage);
        }
        if (type.equals(FriendsListKey.class))
        {
            return new FriendsListKey(
                    (UserBaseKey) mockValidParameter(UserBaseKey.class),
                    random.nextBoolean() ? SocialNetworkEnum.FB : null,
                    random.nextBoolean() ? "a" : null);
        }
        if (type.equals(SocialNetworkEnum.class))
        {
            return SocialNetworkEnum.FB;
        }

        if (type.equals(AlertId.class))
        {
            return new AlertId(1, 2);
        }
        if (type.equals(ProviderId.class))
        {
            return new ProviderId(1);
        }
        if (type.equals(CompetitionId.class))
        {
            return new CompetitionId(1);
        }
        if (type.equals(CompetitionLeaderboardId.class))
        {
            Integer perPage = random.nextBoolean() ? 4 : null;
            return new CompetitionLeaderboardId(
                    1,
                    2,
                    perPage != null || random.nextBoolean() ? 3 : null,
                    perPage);
        }
        if (type.equals(SecurityIntegerId.class))
        {
            return new SecurityIntegerId(1);
        }

        if (type.equals(FollowerHeroRelationId.class))
        {
            return new FollowerHeroRelationId(1, 2);
        }
        if (type.equals(LeaderboardKey.class))
        {
            return new LeaderboardKey(1);
        }
        if (type.equals(LeaderboardMarkUserId.class))
        {
            Integer perPage = random.nextBoolean() ? 3 : null;
            return new PerPagedLeaderboardMarkUserId(
                    1,
                    perPage != null || random.nextBoolean() ? 2 : null,
                    perPage);
        }

        if (type.equals(ExchangeIntegerId.class))
        {
            return new ExchangeIntegerId(1);
        }

        if (type.equals(TimelineItemDTOKey.class))
        {
            return new TimelineItemDTOKey(1);
        }
        if (type.equals(DiscussionKey.class))
        {
            return new CommentKey(1);
        }
        if (type.equals(ReplyDiscussionFormDTO.class))
        {
            return new ReplyDiscussionFormDTO()
            {
                @Override public DiscussionType getInReplyToType()
                {
                    return DiscussionType.BROADCAST_MESSAGE;
                }

                @Override public DiscussionKey getInitiatingDiscussionKey()
                {
                    return (DiscussionKey) mockValidParameter(DiscussionKey.class);
                }
            };
        }
        if (type.equals(MessageDiscussionListKey.class))
        {
            return new MessageDiscussionListKey(
                    DiscussionType.COMMENT,
                    1,
                    (UserBaseKey) mockValidParameter(UserBaseKey.class),
                    (UserBaseKey) mockValidParameter(UserBaseKey.class),
                    random.nextBoolean() ? 2 : null,
                    random.nextBoolean() ? 3 : null,
                    random.nextBoolean() ? 4 : null);
        }
        if (DiscussionListKey.class.isAssignableFrom(type))
        {
            return new DiscussionVoteKey(DiscussionType.COMMENT, 1, VoteDirection.DownVote);
        }
        if (MessageListKey.class.isAssignableFrom(type))
        {
            Integer perPage = random.nextBoolean() ? 2 : null;
            return new RecipientTypedMessageListKey(
                    perPage != null || random.nextBoolean() ? (Integer) 1 : null,
                    perPage,
                    DiscussionType.COMMENT,
                    (UserBaseKey) mockValidParameter(UserBaseKey.class));
        }
        if (type.equals(MessageHeaderId.class))
        {
            return new MessageHeaderId(1);
        }
        if (type.equals(NewsItemListRegionalKey.class))
        {
            Integer perPage = random.nextBoolean() ? 2 : null;
            return new NewsItemListRegionalKey(
                    "a",
                    "b",
                    perPage != null || random.nextBoolean() ? 1 : null,
                    perPage);
        }
        if (type.equals(NewsItemListSecurityKey.class))
        {
            Integer perPage = random.nextBoolean() ? 2 : null;
            return new NewsItemListSecurityKey(
                    (SecurityIntegerId) mockValidParameter(SecurityIntegerId.class),
                    perPage != null || random.nextBoolean() ? 1 : null,
                    perPage);
        }
        if (type.equals(NewsItemListKey.class))
        {
            return mockValidParameter(NewsItemListRegionalKey.class);
        }
        if (type.equals(NotificationKey.class))
        {
            return new NotificationKey(1);
        }

        if (type.equals(OwnedTradeId.class))
        {
            return new OwnedTradeId(1, 2, 3, 4);
        }
        if (type.equals(OwnedPositionId.class))
        {
            return new OwnedPositionId(1, 2, 3);
        }
        if (type.equals(OwnedPortfolioId.class))
        {
            return new OwnedPortfolioId(1, 2);
        }
        if (type.equals(WatchlistPositionDTO.class))
        {
            WatchlistPositionDTO value = new WatchlistPositionDTO();
            value.id = 1;
            return value;
        }
        if (type.equals(PositionCompactId.class))
        {
            return new PositionCompactId(1);
        }

        if (type.equals(ProviderSecurityListType.class))
        {
            Integer perPage = random.nextBoolean() ? 2 : null;
            return new BasicProviderSecurityListType(
                    (ProviderId) mockValidParameter(ProviderId.class),
                    perPage != null || random.nextBoolean() ? 1 : null,
                    perPage);
        }
        if (type.equals(HelpVideoListKey.class))
        {
            return new HelpVideoListKey((ProviderId) mockValidParameter(ProviderId.class));
        }
        if (type.equals(ProviderDisplayCellListKey.class))
        {
            return new ProviderDisplayCellListKey((ProviderId) mockValidParameter(ProviderId.class));
        }

        if (type.equals(SecurityId.class))
        {
            return new SecurityId("a", "b");
        }
        if (type.equals(SecurityListType.class))
        {
            Integer perPage = random.nextBoolean() ? 2 : null;
            // TODO randomly pick another one?
            return new TrendingBasicSecurityListType(
                    random.nextBoolean() ? "a" : null,
                    perPage != null || random.nextBoolean() ? 1 : null,
                    perPage
            );
        }
        if (type.equals(VideoCategoryId.class))
        {
            return new VideoCategoryId(1);
        }

        if (type.equals(AchievementCategoryId.class))
        {
            return new AchievementCategoryId(
                    (UserBaseKey) mockValidParameter(UserBaseKey.class),
                    2);
        }

        if (type.equals(UserAchievementId.class))
        {
            return new UserAchievementId(1);
        }

        if (type.equals(AchievementShareFormDTO.class))
        {
            return new AchievementShareFormDTO(
                    (UserAchievementId) mockValidParameter(UserAchievementId.class),
                    null);
        }

        if (type.equals(QuestBonusId.class))
        {
            return new QuestBonusId(1);
        }

        if (type.equals(Boolean.class) || type.equals(boolean.class))
        {
            return random.nextBoolean();
        }
        if (type.equals(Integer.class))
        {
            return 1;
        }
        if (type.equals(String.class))
        {
            return "a";
        }
        return mock(type);
    }
    //</editor-fold>
}
