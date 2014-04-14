package com.tradehero.th.api.alert;

public class AlertPlanDTO
{
    public int id;
    public int costCCPerMonth;
    public int numberOfAlerts;
    public String productIdentifier;

    @Override public String toString()
    {
        return String.format("id=%d numberOfAlerts=%d produciIdentifier%s", id, numberOfAlerts,
                productIdentifier);
    }
}