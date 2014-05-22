package com.tradehero.common.persistence;

import java.util.Map;

public interface FetchAssistant<DTOKeyType, DTOType>
{
    void execute();
    void execute(boolean force);
    void clear();
    boolean isDataComplete();
    void setListener(OnInfoFetchedListener<DTOKeyType, DTOType> listener);

    public static interface OnInfoFetchedListener<DTOKeyType, DTOType>
    {
        void onInfoFetched(Map<DTOKeyType, DTOType> fetched, boolean isDataComplete);
    }
}
