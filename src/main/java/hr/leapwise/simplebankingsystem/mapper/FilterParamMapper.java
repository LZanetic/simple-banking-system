package hr.leapwise.simplebankingsystem.mapper;

import hr.leapwise.simplebankingsystem.model.dto.FilterParamDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface FilterParamMapper {

    FilterParamMapper INSTANCE = Mappers.getMapper(FilterParamMapper.class);

    @Mapping(target = "dateBefore", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "dateAfter", dateFormat = "yyyy-MM-dd HH:mm:ss")
    FilterParamDTO mapToFilterParamDTO(Map<String, String> filterParam);
}
