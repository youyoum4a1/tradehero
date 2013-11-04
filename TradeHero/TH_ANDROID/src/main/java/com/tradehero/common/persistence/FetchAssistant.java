package com.tradehero.common.persistence;

import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/1/13 Time: 4:29 PM To change this template use File | Settings | File Templates. */
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
