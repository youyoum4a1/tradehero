package com.tradehero.chinabuild.data;

import com.tradehero.common.persistence.DTO;
import java.io.Serializable;

/**
 * Created by huhaiping on 14-9-10.
 */
public class ExchangeDTO implements DTO, Serializable
{
    private static final long serialVersionUID = 1L;
    public int id;
    public String desc;
    public String name;
}
