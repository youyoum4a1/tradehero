package com.tradehero.th.adapters;

import java.util.List;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/1/13 Time: 11:04 AM To change this template use File | Settings | File Templates. */
public interface ExpandableListReporter
{
    List<Boolean> getExpandedStatesPerPosition();
    void setExpandedStatesPerPosition(List<Boolean> expandedStatesPerPosition);
    void setExpandedStatesPerPosition(boolean[] expandedStatesPerPosition);
}
