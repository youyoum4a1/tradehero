package com.tradehero.th.models.staff;

public class StaffDTO
{
    private final String staffName;
    private final String staffTitle;

    public StaffDTO(String staffName, String staffTitle)
    {
        this.staffName = staffName;
        this.staffTitle = staffTitle;
    }

    public String getStaffName()
    {
        return staffName;
    }

    public String getStaffTitle()
    {
        return staffTitle;
    }
}
