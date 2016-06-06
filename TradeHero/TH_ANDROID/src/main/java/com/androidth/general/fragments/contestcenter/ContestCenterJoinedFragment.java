package com.androidth.general.fragments.contestcenter;

import android.content.Context;
import android.support.annotation.NonNull;
import com.androidth.general.R;
import com.androidth.general.api.competition.ProviderDTO;
import javax.inject.Inject;

public class ContestCenterJoinedFragment extends ContestCenterBaseFragment
{
    @Inject Context doNotRemoveOrItFails;

    @Override public void recreateAdapter()
    {
        setContestCenterScreen(android.R.id.list);
        contestListAdapter = createAdapter();
        if (providerDTOs != null)
        {
            for (ProviderDTO providerDTO : providerDTOs)
            {
                if (providerDTO.isUserEnrolled)
                {
                    contestListAdapter.add(new ProviderContestPageDTO(providerDTO));
                }
            }
        }

        contestListView.setAdapter(contestListAdapter);

        if (isNotJoinedContest())
        {
            setContestCenterScreen(R.id.contest_no_joined);
        }
    }

    @Override @NonNull public ContestCenterTabType getCCTabType()
    {
        return ContestCenterTabType.JOINED;
    }

    public boolean isNotJoinedContest()
    {
        if (providerDTOs != null)
        {
            for (ProviderDTO providerDTO : providerDTOs)
            {
                if (providerDTO.isUserEnrolled)
                {
                    return false;
                }
            }
        }
        return true;
    }
}
