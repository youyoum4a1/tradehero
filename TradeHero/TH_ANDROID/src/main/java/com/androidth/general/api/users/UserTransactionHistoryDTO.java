package com.androidth.general.api.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.androidth.general.common.persistence.DTO;
import java.util.Date;

public class UserTransactionHistoryDTO implements DTO
{
    public int id;
    public Date createdAtUtc;
    public Double balance;
    public Double value;
    public String comment;

    @JsonIgnore
    public UserTransactionHistoryId getUserTransactionHistoryId()
    {
        return new UserTransactionHistoryId(id);
    }
}
