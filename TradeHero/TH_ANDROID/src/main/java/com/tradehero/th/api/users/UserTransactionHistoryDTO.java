package com.tradehero.th.api.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTO;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: nia
 * Date: 22/10/13
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserTransactionHistoryDTO implements DTO
{
    public static final String TAG = UserTransactionHistoryDTO.class.getSimpleName();

    public int id;
    public Date createdAtUtc;
    public Double balance;
    public Double value;
    public String comment;

    public UserTransactionHistoryDTO()
    {
        super();
    }

    @JsonIgnore
    public UserTransactionHistoryId getUserTransactionHistoryId()
    {
        return new UserTransactionHistoryId(id);
    }
}
