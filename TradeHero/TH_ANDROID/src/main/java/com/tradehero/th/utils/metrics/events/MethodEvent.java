package com.tradehero.th.utils.metrics.events;

/**
 *  by tho: Ok, I'm not sure about what is the meaning of "method" string here, according to @alex, it is requested by marketing team (?).
 *  the meaning of this class should be clarify. We should name it correctly and more verbose
 */
public class MethodEvent extends SingleAttributeEvent
{
    public MethodEvent(String name, String attributeValue)
    {
        super(name, "method", attributeValue);
    }
}
