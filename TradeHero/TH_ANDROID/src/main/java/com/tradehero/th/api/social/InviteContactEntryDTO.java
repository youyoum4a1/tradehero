package com.tradehero.th.api.social;

public class InviteContactEntryDTO implements InviteDTO
{
    public String email;

    //<editor-fold desc="Constructors">
    public InviteContactEntryDTO()
    {
        super();
    }

    public InviteContactEntryDTO(String email)
    {
        this.email = email;
    }
    //</editor-fold>
}
