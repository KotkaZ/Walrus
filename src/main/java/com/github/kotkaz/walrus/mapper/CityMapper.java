package com.github.kotkaz.walrus.mapper;

import com.github.kotkaz.walrus.dto.City;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CityMapper {

    City entityToDto(com.github.kotkaz.walrus.model.City entity);

    List<City> entitiesToDtoList(List<com.github.kotkaz.walrus.model.City> entity);
}
