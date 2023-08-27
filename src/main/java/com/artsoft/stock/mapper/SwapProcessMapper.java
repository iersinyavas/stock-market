package com.artsoft.stock.mapper;

import com.artsoft.stock.dto.SwapProcessDTO;
import com.artsoft.stock.entity.SwapProcess;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SwapProcessMapper {
    SwapProcessMapper INSTANCE = Mappers.getMapper(SwapProcessMapper.class);

    SwapProcessDTO entityToDTO(SwapProcess swapProcess);
}
