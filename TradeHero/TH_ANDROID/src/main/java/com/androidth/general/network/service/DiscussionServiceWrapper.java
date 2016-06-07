package com.androidth.general.network.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.discussion.DiscussionDTO;
import com.androidth.general.api.discussion.form.DiscussionFormDTO;
import com.androidth.general.api.discussion.form.ReplyDiscussionFormDTO;
import com.androidth.general.api.discussion.key.DiscussionKey;
import com.androidth.general.api.discussion.key.DiscussionKeyFactory;
import com.androidth.general.api.discussion.key.DiscussionListKey;
import com.androidth.general.api.discussion.key.DiscussionVoteKey;
import com.androidth.general.api.discussion.key.MessageDiscussionListKey;
import com.androidth.general.api.discussion.key.PaginatedDiscussionListKey;
import com.androidth.general.api.pagination.PaginatedDTO;
import com.androidth.general.api.timeline.TimelineItemShareRequestDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.models.discussion.DTOProcessorDiscussion;
import com.androidth.general.models.discussion.DTOProcessorDiscussionReply;
import com.androidth.general.network.DelayRetriesOrFailFunc1;
import com.androidth.general.persistence.discussion.DiscussionCacheRx;
import com.androidth.general.persistence.discussion.DiscussionListCacheRx;
import com.androidth.general.persistence.user.UserMessagingRelationshipCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton public class DiscussionServiceWrapper
{
    private static final int RETRY_COUNT = 3;
    private static final long RETRY_DELAY_MILLIS = 1000;

    @NonNull private final DiscussionServiceRx discussionServiceRx;
    @NonNull private final CurrentUserId currentUserId;

    // It has to be lazy to avoid infinite dependency
    @NonNull private final Lazy<DiscussionListCacheRx> discussionListCache;
    @NonNull private final Lazy<DiscussionCacheRx> discussionCache;
    @NonNull private final Lazy<UserMessagingRelationshipCacheRx> userMessagingRelationshipCache;

    //<editor-fold desc="Constructors">
    @Inject public DiscussionServiceWrapper(
            @NonNull DiscussionServiceRx discussionServiceRx,
            @NonNull CurrentUserId currentUserId,
            @NonNull Lazy<DiscussionListCacheRx> discussionListCache,
            @NonNull Lazy<DiscussionCacheRx> discussionCache,
            @NonNull Lazy<UserMessagingRelationshipCacheRx> userMessagingRelationshipCache)
    {
        this.discussionServiceRx = discussionServiceRx;
        this.currentUserId = currentUserId;
        this.discussionCache = discussionCache;
        this.discussionListCache = discussionListCache;
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
    }
    //</editor-fold>

    //<editor-fold desc="DTO Processors">
    @NonNull protected DTOProcessorDiscussionReply createDiscussionReplyProcessor(@NonNull DiscussionKey initiatingKey,
            @Nullable DiscussionKey stubKey)
    {
        return new DTOProcessorDiscussionReply(
                currentUserId,
                discussionCache.get(),
                userMessagingRelationshipCache.get(),
                stubKey,
                discussionListCache.get(),
                initiatingKey);
    }
    //</editor-fold>

    // TODO add providers in RetrofitModule and RetrofitProtectedModule
    // TODO add methods based on DiscussionServiceAsync and MiddleCallback implementations

    //<editor-fold desc="Get Comment">
    @NonNull public Observable<DiscussionDTO> getCommentRx(@NonNull DiscussionKey discussionKey)
    {
        return discussionServiceRx.getComment(discussionKey.id)
                .map(new DTOProcessorDiscussion());
    }
    //</editor-fold>

    //<editor-fold desc="Create Discussion">
    @NonNull public Observable<DiscussionDTO> createDiscussionRx(@NonNull DiscussionFormDTO discussionFormDTO)
    {
        if (discussionFormDTO instanceof ReplyDiscussionFormDTO)
        {
            return discussionServiceRx.createDiscussion(discussionFormDTO)
                    .retryWhen(new DelayRetriesOrFailFunc1(RETRY_COUNT, RETRY_DELAY_MILLIS))
                    .map(createDiscussionReplyProcessor(
                            ((ReplyDiscussionFormDTO) discussionFormDTO).getInitiatingDiscussionKey(),
                            discussionFormDTO.stubKey));
        }
        return postToTimelineRx(
                currentUserId.toUserBaseKey(),
                discussionFormDTO);
    }
    //</editor-fold>

    //<editor-fold desc="Get Discussions">
    @NonNull public Observable<PaginatedDTO<DiscussionDTO>> getDiscussionsRx(@NonNull PaginatedDiscussionListKey discussionsKey)
    {
        return discussionServiceRx.getDiscussions(
                discussionsKey.inReplyToType,
                discussionsKey.inReplyToId,
                discussionsKey.page,
                discussionsKey.perPage);
    }

    @NonNull public Observable<PaginatedDTO<DiscussionDTO>> getMessageThreadRx(@NonNull MessageDiscussionListKey discussionsKey)
    {
        return discussionServiceRx.getMessageThread(
                discussionsKey.inReplyToType,
                discussionsKey.inReplyToId,
                discussionsKey.toMap());
    }
    //</editor-fold>

    //<editor-fold desc="Vote">
    @NonNull public Observable<DiscussionDTO> voteRx(@NonNull DiscussionVoteKey discussionVoteKey)
    {
        return discussionServiceRx.vote(
                discussionVoteKey.inReplyToType,
                discussionVoteKey.inReplyToId,
                discussionVoteKey.voteDirection)
                .map(createDiscussionReplyProcessor(
                        DiscussionKeyFactory.create(discussionVoteKey.inReplyToType, discussionVoteKey.inReplyToId),
                        null));
    }
    //</editor-fold>

    //<editor-fold desc="Share">
    @NonNull public Observable<BaseResponseDTO> shareRx(
            @NonNull DiscussionListKey discussionKey,
            @NonNull TimelineItemShareRequestDTO timelineItemShareRequestDTO)
    {
        return discussionServiceRx.share(
                discussionKey.inReplyToType,
                discussionKey.inReplyToId,
                timelineItemShareRequestDTO);
    }
    //</editor-fold>

    //<editor-fold desc="Post to Timeline">
    @NonNull public Observable<DiscussionDTO> postToTimelineRx(
            @NonNull UserBaseKey userBaseKey,
            @NonNull DiscussionFormDTO discussionFormDTO)
    {
        return discussionServiceRx.postToTimeline(
                userBaseKey.key,
                discussionFormDTO)
                .retryWhen(new DelayRetriesOrFailFunc1(RETRY_COUNT, RETRY_DELAY_MILLIS))
                .map(new DTOProcessorDiscussion());
    }
    //</editor-fold>

    //@NonNull public Observable<NewsfeedDTOList> getNewsfeed(@NonNull NewsfeedPagedDTOKey key)
    //{
    //    NewsfeedDTOList list = new NewsfeedDTOList();
    //
    //    NewsfeedNewsDTO dto = new NewsfeedNewsDTO();
    //
    //    dto.id = 12496937;
    //    dto.title = "Apple's September 9 Event: Will There be a Surprise Product?";
    //    dto.displayName = "Seeking Alpha";
    //    dto.createdAtUTC = DateUtils.parseString("2015-09-03 14:20:00", "yyyy-MM-dd HH:mm:ss");
    //    dto.picture = "https://pbs.twimg.com/profile_images/534299535552421888/eHacq8EQ.png";
    //    dto.description =
    //            "The launch of a new iPhone is a big deal, I realize, so maybe there really are 7000 people going to San Francisco to see it. My sense is that there could be much more on the menu. Not that iPhone won't still be center stage. It will.";
    //    dto.imageUrl = "http://www.iclarified.com/images/news/32816/134572/134572-640.jpg";
    //    dto.url = "http://seekingalpha.com/article/3474496-apples-september-9-event-will-there-be-a-surprise-product";
    //
    //    NewsfeedStockTwitDTO stockTwitDTO = new NewsfeedStockTwitDTO();
    //    stockTwitDTO.id = 2;
    //    stockTwitDTO.message =
    //            "<$NASDAQ:AAPL,123$> It will be interesting to see if it dip tomorrow after all Tim Cook said today. {#ff0000|Bearish}";
    //    stockTwitDTO.displayName = "Moriuchi Taka";
    //    stockTwitDTO.createdAtUTC = DateUtils.parseString("2015-09-03 14:00:00", "yyyy-MM-dd HH:mm:ss");
    //    stockTwitDTO.picture = "https://tuneuplyrics.files.wordpress.com/2013/08/taka.jpg";
    //
    //    NewsfeedNewsDTO motley = new NewsfeedNewsDTO();
    //
    //    motley.id = 12496937;
    //    motley.title = "3 Things That Could Go Wrong for Apple";
    //    motley.displayName = "The Motley Fool";
    //    motley.createdAtUTC = DateUtils.parseString("2015-09-03 13:24:00", "yyyy-MM-dd HH:mm:ss");
    //    motley.picture = "https://lh6.googleusercontent.com/-_nnBHgQANeU/AAAAAAAAAAI/AAAAAAAAAAA/p0jEdCySwjw/s0-c-k-no-ns/photo.jpg";
    //    motley.description =
    //            "The iPhone is still posting amazing growth, but the end of subsidies and slowing innovation are among the things that could cause trouble down the road.";
    //    motley.url = "http://www.fool.com/investing/general/2015/08/30/3-things-that-could-go-wrong-for-apple.aspx";
    //
    //    NewsfeedStockTwitDTO chart = new NewsfeedStockTwitDTO();
    //    chart.id = 5;
    //    chart.message =
    //            "These Information Technology stocks are trending on [http://www.estimize.com/csco/fq1-2016?utm_content=Information+Technology&utm_medium=popular_stocks_by_sector&utm_source=stocktwits#chart=historical](http://www.estimize.com/csco/fq1-2016?utm_content=Information+Technology&utm_medium=popular_stocks_by_sector&utm_source=stocktwits#chart=historical)";
    //    chart.displayName = "Estimize";
    //    chart.createdAtUTC = DateUtils.parseString("2015-09-04 14:00:00", "yyyy-MM-dd HH:mm:ss");
    //    chart.picture = "http://avatars.stocktwits.com/production/74023/large-1381535783.png?1381535783";
    //
    //    StockTwitEntitiesDTO entitiesDTO = new StockTwitEntitiesDTO();
    //    StockTwitEntitiesDTO.ChartDTO chartDto = new StockTwitEntitiesDTO.ChartDTO();
    //    chartDto.original =
    //            "http://www.estimize.com/csco/fq1-2016?utm_content=Information+Technology&utm_medium=popular_stocks_by_sector&utm_source=stocktwits#chart=historical";
    //    chartDto.thumb = "http://charts.stocktwits.com/production/original_42300753.png?1441353922";
    //
    //    entitiesDTO.chart = chartDto;
    //    chart.entities = entitiesDTO;
    //
    //    NewsfeedStockTwitDTO youtube = new NewsfeedStockTwitDTO();
    //    youtube.id = 7;
    //    youtube.message =
    //            "Does anybody know what kind of \"Conflict of Interest\" does \"BOB\" have [https://www.youtube.com/watch?v=GDz5yuxfgg0&index=9&list=RDGftT_wzRJdc](https://www.youtube.com/watch?v=GDz5yuxfgg0&index=9&list=RDGftT_wzRJdc)";
    //    youtube.displayName = "simonsaysiblog";
    //    youtube.createdAtUTC = DateUtils.parseString("2015-09-04 14:00:00", "yyyy-MM-dd HH:mm:ss");
    //    youtube.picture = "http://avatars.stocktwits.com/images/default_avatar_large.jpg";
    //
    //    StockTwitEntitiesDTO entitiesDTO2 = new StockTwitEntitiesDTO();
    //    StockTwitEntitiesDTO.VideoDTO youtubeDto = new StockTwitEntitiesDTO.VideoDTO();
    //    youtubeDto.original =
    //            "https://www.youtube.com/watch?v=GDz5yuxfgg0&index=9&list=RDGftT_wzRJdc";
    //    youtubeDto.thumb = "https://i.ytimg.com/vi/DW0VxxoCrNo/hqdefault.jpg";
    //
    //    entitiesDTO2.video = youtubeDto;
    //    youtube.entities = entitiesDTO2;
    //
    //    list.add(dto);
    //    list.add(stockTwitDTO);
    //    list.add(motley);
    //    list.add(chart);
    //    list.add(youtube);
    //
    //    return Observable.just(list);
    //}
}