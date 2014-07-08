package com.tradehero.th.api.notification;

public enum NotificationType
{
    None(0),
    HeroAction(1),
    LowBalance(2),
    SubscriptionExpired(3),
    ReferralSucceeded(4),
    TimelineEcho(5),
    FriendStartedFollowing(7),
    PositionClosed(8),
    TradeOfTheWeek(9),
    StockAlert(10),
    GeneralAnnouncement(11),
    FreeCash(12),
    CompetitionInvite(13),
    ResetPortfolio(14),
    TradeInCompetition(16),
    NotifyOriginator(19),
    NotifyContributors(23),
    PrivateMessage(24),
    BroadcastMessage(25), ;

    private final int typeId;

    NotificationType(int typeId)
    {
        this.typeId = typeId;
    }

    public static NotificationType fromType(int typeId)
    {
        for (NotificationType notificationType: values())
        {
            if (notificationType.typeId == typeId)
            {
                return notificationType;
            }
        }

        return None;
    }

    public int getTypeId()
    {
        return typeId;
    }
}
