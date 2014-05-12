package com.tradehero.th.adapters;

import java.util.List;


public interface ExpandableListReporter
{
    List<Boolean> getExpandedStatesPerPosition();
    void setExpandedStatesPerPosition(List<Boolean> expandedStatesPerPosition);
    void setExpandedStatesPerPosition(boolean[] expandedStatesPerPosition);
}
