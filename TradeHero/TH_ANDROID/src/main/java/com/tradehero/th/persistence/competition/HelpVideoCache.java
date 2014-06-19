package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.competition.HelpVideoDTO;
import com.tradehero.th.api.competition.key.HelpVideoId;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class HelpVideoCache extends StraightDTOCache<HelpVideoId, HelpVideoDTO>
{
    public static final int DEFAULT_MAX_SIZE = 200;

    //<editor-fold desc="Constructors">
    @Inject public HelpVideoCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected HelpVideoDTO fetch(HelpVideoId key) throws Throwable
    {
        throw new RuntimeException();
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public List<HelpVideoDTO> getOrFetch(@Nullable List<HelpVideoId> helpVideoIds) throws Throwable
    {
        if (helpVideoIds == null)
        {
            return null;
        }

        List<HelpVideoDTO> helpDTOList = new ArrayList<>();
        for (@NotNull HelpVideoId helpVideoId : helpVideoIds)
        {
            helpDTOList.add(getOrFetch(helpVideoId, false));
        }
        return helpDTOList;
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public List<HelpVideoDTO> get(@Nullable List<HelpVideoId> helpVideoIds)
    {
        if (helpVideoIds == null)
        {
            return null;
        }

        List<HelpVideoDTO> fleshedValues = new ArrayList<>();

        for (@NotNull HelpVideoId helpVideoId: helpVideoIds)
        {
            fleshedValues.add(get(helpVideoId));
        }

        return fleshedValues;
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public List<HelpVideoDTO> put(@Nullable List<HelpVideoDTO> values)
    {
        if (values == null)
        {
            return null;
        }

        List<HelpVideoDTO> previousValues = new ArrayList<>();

        for (@NotNull HelpVideoDTO helpVideoDTO: values)
        {
            previousValues.add(put(helpVideoDTO.getHelpVideoId(), helpVideoDTO));
        }

        return previousValues;
    }
}
