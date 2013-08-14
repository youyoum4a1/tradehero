package com.tradehero.th.models;

public class TradeOfWeek
{

    private String id;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getCreatedAtUtc()
    {
        return createdAtUtc;
    }

    public void setCreatedAtUtc(String createdAtUtc)
    {
        this.createdAtUtc = createdAtUtc;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Medias getMedias()
    {
        return medias;
    }

    public void setMedias(Medias medias)
    {
        this.medias = medias;
    }

    public String getPushTypeId()
    {
        return pushTypeId;
    }

    public void setPushTypeId(String pushTypeId)
    {
        this.pushTypeId = pushTypeId;
    }

    private String createdAtUtc;
    private String userId;
    private String text;
    private String type;
    private Medias medias;
    private String pushTypeId;
}
