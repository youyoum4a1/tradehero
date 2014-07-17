package com.tradehero.th.utils.metrics.events;

public class TypeEvent extends SingleAttributeEvent
{
    public TypeEvent(String name, String attributeValue)
    {
        super(name, "type", attributeValue);
    }
}
