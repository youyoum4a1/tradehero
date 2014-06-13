package com.tradehero.th.models.portfolio;

import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

public class DisplayablePortfolioFetchAssistant
{
    private final PortfolioCompactListCache portfolioListCache;
    private final PortfolioCache portfolioCache;
    private final UserProfileCache userProfileCache;

    private final Map<UserBaseKey, FlaggedDisplayablePortfolioDTOList> displayPortfolios;
    private OnFetchedListener fetchedListener;

    @Inject public DisplayablePortfolioFetchAssistant(
            PortfolioCompactListCache portfolioListCache,
            PortfolioCache portfolioCache,
            UserProfileCache userProfileCache)
    {
        super();
        this.portfolioListCache = portfolioListCache;
        this.portfolioCache = portfolioCache;
        this.userProfileCache = userProfileCache;
        displayPortfolios = new HashMap<>();
    }

    public void onDestroy()
    {
    }

    public void setFetchedListener(OnFetchedListener fetchedListener)
    {
        this.fetchedListener = fetchedListener;
    }

    public void fetch(List<UserBaseKey> userBaseKeys)
    {
        displayPortfolios.clear();
        for (UserBaseKey userBaseKey : userBaseKeys)
        {
            if (!displayPortfolios.containsKey(userBaseKey))
            {
                displayPortfolios.put(userBaseKey, new FlaggedDisplayablePortfolioDTOList());
            }
        }
        populate();
    }

    protected void populate()
    {
        for (Map.Entry<UserBaseKey, FlaggedDisplayablePortfolioDTOList> entry : displayPortfolios.entrySet())
        {
            if (entry.getValue().size() == 0 && !entry.getValue().fetchingIds)
            {
                entry.getValue().fetchingIds = true;
                portfolioListCache.register(entry.getKey(), createOwnedPortfolioIdListListener());
                portfolioListCache.getOrFetchAsync(entry.getKey());
            }
            else
            {
                for (FlaggedDisplayablePortfolioDTO displayablePortfolioDTO : entry.getValue())
                {
                    if (displayablePortfolioDTO.userBaseDTO == null && !displayablePortfolioDTO.fetchingUser)
                    {
                        displayablePortfolioDTO.fetchingUser = true;
                        userProfileCache.register(entry.getKey(), createUserProfileDTOListener());
                        userProfileCache.getOrFetchAsync(entry.getKey());
                    }
                    if (displayablePortfolioDTO.portfolioDTO == null && !displayablePortfolioDTO.fetchingPortfolio)
                    {
                        displayablePortfolioDTO.fetchingPortfolio = true;
                        portfolioCache.getOrFetch(displayablePortfolioDTO.ownedPortfolioId, createPortfolioDTOListener()).execute();
                    }
                }
            }
        }
    }

    private DTOCacheNew.Listener<UserBaseKey, OwnedPortfolioIdList> createOwnedPortfolioIdListListener()
    {
        return new DTOCacheNew.Listener<UserBaseKey, OwnedPortfolioIdList>()
        {
            @Override public void onDTOReceived(UserBaseKey key, OwnedPortfolioIdList value)
            {
                Timber.d("Received id list for %s: %s", key, value);
                FlaggedDisplayablePortfolioDTOList valueList = displayPortfolios.get(key);
                if (valueList != null)
                {
                    valueList.fetchingIds = false;
                    for (OwnedPortfolioId ownedPortfolioId : value)
                    {
                        valueList.add(new FlaggedDisplayablePortfolioDTO(ownedPortfolioId));
                    }
                    populate();
                }
            }

            @Override public void onErrorThrown(UserBaseKey key, Throwable error)
            {
                THToast.show(R.string.error_fetch_portfolio_list_info);
                notifyListener();
            }
        };
    }

    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileDTOListener()
    {
        return new DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>()
        {
            @Override public void onDTOReceived(UserBaseKey key, UserProfileDTO value)
            {
                Timber.d("Received UserProfileDTO %s", key);
                FlaggedDisplayablePortfolioDTOList valueList = displayPortfolios.get(key);
                if (valueList != null)
                {
                    for (FlaggedDisplayablePortfolioDTO displayablePortfolioDTO : valueList)
                    {
                        displayablePortfolioDTO.fetchingUser = false;
                        displayablePortfolioDTO.userBaseDTO = value;
                    }
                    conditionalNotifyListener();
                }
            }

            @Override public void onErrorThrown(UserBaseKey key, Throwable error)
            {
                THToast.show(R.string.error_fetch_user_profile);
            }
        };
    }

    private DTOCache.Listener<OwnedPortfolioId, PortfolioDTO> createPortfolioDTOListener()
    {
        return new DTOCache.Listener<OwnedPortfolioId, PortfolioDTO>()
        {
            @Override public void onDTOReceived(OwnedPortfolioId key, PortfolioDTO value, boolean fromCache)
            {
                Timber.d("Received PortfolioDTO for %s: %s", key, value);
                FlaggedDisplayablePortfolioDTOList valueList = displayPortfolios.get(key.getUserBaseKey());
                if (valueList != null)
                {
                    for (FlaggedDisplayablePortfolioDTO displayablePortfolioDTO : valueList)
                    {
                        if (displayablePortfolioDTO.ownedPortfolioId.equals(key))
                        {
                            displayablePortfolioDTO.fetchingPortfolio = false;
                            displayablePortfolioDTO.portfolioDTO = value;
                        }
                    }
                    conditionalNotifyListener();
                }
            }

            @Override public void onErrorThrown(OwnedPortfolioId key, Throwable error)
            {
                THToast.show(R.string.error_fetch_portfolio_info);
            }
        };
    }
    
    protected boolean isFetching()
    {
        for (Map.Entry<UserBaseKey, FlaggedDisplayablePortfolioDTOList> entry : displayPortfolios.entrySet())
        {
            if (entry.getValue().size() == 0 || entry.getValue().fetchingIds)
            {
                return true;
            }
            for (FlaggedDisplayablePortfolioDTO displayablePortfolioDTO : entry.getValue())
            {
                if (displayablePortfolioDTO.fetchingUser || displayablePortfolioDTO.fetchingPortfolio)
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    protected void conditionalNotifyListener()
    {
        if (!isFetching())
        {
            notifyListener();
        }
    }

    protected void notifyListener()
    {
        if (fetchedListener != null)
        {
            fetchedListener.onFetched();
        }
    }

    public Map<UserBaseKey, List<DisplayablePortfolioDTO>> getMapDisplayablePortfolios()
    {
        Map<UserBaseKey, List<DisplayablePortfolioDTO>> snapshot = new HashMap<>();
        for (Map.Entry<UserBaseKey, FlaggedDisplayablePortfolioDTOList> entry : displayPortfolios.entrySet())
        {
            snapshot.put(entry.getKey(), new ArrayList<DisplayablePortfolioDTO>(entry.getValue()));
        }
        return snapshot;
    }

    public List<DisplayablePortfolioDTO> getDisplayablePortfolios()
    {
        List<DisplayablePortfolioDTO> snapshot = new ArrayList<>();
        for (Map.Entry<UserBaseKey, FlaggedDisplayablePortfolioDTOList> entry : displayPortfolios.entrySet())
        {
            snapshot.addAll(entry.getValue());
        }
        return snapshot;
    }

    public static interface OnFetchedListener
    {
        void onFetched();
    }
}
