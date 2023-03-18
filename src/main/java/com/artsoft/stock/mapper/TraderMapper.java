package com.artsoft.stock.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TraderMapper {

    TraderMapper INSTANCE = Mappers.getMapper(TraderMapper.class);

}
