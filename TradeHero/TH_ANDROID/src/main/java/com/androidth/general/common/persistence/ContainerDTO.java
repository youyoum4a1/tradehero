package com.androidth.general.common.persistence;

import java.util.List;

public interface ContainerDTO<
        DTOType extends DTO,
        DTOListType extends DTO & List<DTOType>>
{
    int size();
    DTOListType getList();
}
