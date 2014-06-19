package com.tradehero.th.api.market;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SecuritySuperCompactDTO
{
    @JsonProperty("i")
    public int id;
    @JsonProperty("n")
    public String name;
    @JsonProperty("s")
    public String symbol;
    @JsonProperty("m")
    public double marketCap;
    @JsonProperty("b")
    public String blobRef;
}
