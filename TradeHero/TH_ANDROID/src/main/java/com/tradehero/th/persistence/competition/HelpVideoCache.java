package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.competition.HelpVideoDTO;
import com.tradehero.th.api.competition.HelpVideoDTOList;
import com.tradehero.th.api.competition.key.HelpVideoId;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class HelpVideoCache extends StraightDTOCacheNew<HelpVideoId, HelpVideoDTO>
{
    public static final int DEFAULT_MAX_SIZE = 200;

    //<editor-fold desc="Constructors">
    @Inject public HelpVideoCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override @NotNull public HelpVideoDTO fetch(@NotNull HelpVideoId key) throws Throwable
    {
        throw new RuntimeException();
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public HelpVideoDTOList put(@Nullable List<HelpVideoDTO> videoDTOList)
    {
        if (videoDTOList == null)
        {
            return null;
        }
        HelpVideoDTOList previous = new HelpVideoDTOList();
        for (HelpVideoDTO videoDTO : videoDTOList)
        {
            previous.add(put(videoDTO.getHelpVideoId(), videoDTO));
        }
        return previous;
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public HelpVideoDTOList get(@Nullable List<HelpVideoId> videoIds)
    {
        if (videoIds == null)
        {
            return null;
        }
        HelpVideoDTOList videoDTOs = new HelpVideoDTOList();
        for (HelpVideoId helpVideoId : videoIds)
        {
            videoDTOs.add(get(helpVideoId));
        }
        return videoDTOs;
    }
}
