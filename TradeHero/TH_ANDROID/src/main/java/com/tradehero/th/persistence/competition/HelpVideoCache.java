package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.competition.HelpVideoDTO;
import com.tradehero.th.api.competition.key.HelpVideoId;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton public class HelpVideoCache extends StraightDTOCache<HelpVideoId, HelpVideoDTO>
{
    public static final String TAG = HelpVideoCache.class.getSimpleName();
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

    public List<HelpVideoDTO> getOrFetch(List<HelpVideoId> helpVideoIds) throws Throwable
    {
        if (helpVideoIds == null)
        {
            return null;
        }

        List<HelpVideoDTO> helpDTOList = new ArrayList<>();
        for (HelpVideoId helpVideoId : helpVideoIds)
        {
            helpDTOList.add(getOrFetch(helpVideoId, false));
        }
        return helpDTOList;
    }

    public List<HelpVideoDTO> get(List<HelpVideoId> helpVideoIds)
    {
        if (helpVideoIds == null)
        {
            return null;
        }

        List<HelpVideoDTO> fleshedValues = new ArrayList<>();

        for (HelpVideoId helpVideoId: helpVideoIds)
        {
            fleshedValues.add(get(helpVideoId));
        }

        return fleshedValues;
    }

    public List<HelpVideoDTO> put(List<HelpVideoDTO> values)
    {
        if (values == null)
        {
            return null;
        }

        List<HelpVideoDTO> previousValues = new ArrayList<>();

        for (HelpVideoDTO helpVideoDTO: values)
        {
            previousValues.add(put(helpVideoDTO.getHelpVideoId(), helpVideoDTO));
        }

        return previousValues;
    }
}
