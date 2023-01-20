package com.github.kotkaz.walrus.controller;

import com.github.kotkaz.walrus.api.CityExplorerApi;
import com.github.kotkaz.walrus.dto.City;
import com.github.kotkaz.walrus.dto.CityPage;
import com.github.kotkaz.walrus.mapper.CityMapper;
import com.github.kotkaz.walrus.repository.CityRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CityExplorerApiImpl implements CityExplorerApi {

    private static final int DEFAULT_PAGE = 0;

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final CityRepository cityRepository;

    private final CityMapper cityMapper;


    // Just for dev purposes.
    // In real situation, this should be replaced for example with reverse proxy.
    @CrossOrigin(origins = "http://localhost:3000")
    @Override
    public ResponseEntity<CityPage> getCities(Optional<Integer> page,
                                              Optional<Integer> size,
                                              Optional<String> name) {
        val pageNr = page.orElse(DEFAULT_PAGE);
        val sizeNr = size.orElse(DEFAULT_PAGE_SIZE);

        val pageable = PageRequest.of(pageNr, sizeNr);
        val citiesPageEntity = name.isEmpty()
            ? cityRepository.findAll(pageable)
            : cityRepository.findAllByNameContainingIgnoreCase(name.get(), pageable);

        val cityPage = new CityPage();
        cityPage.setCities(cityMapper.entitiesToDtoList(citiesPageEntity.getContent()));
        cityPage.setTotalPages(citiesPageEntity.getTotalPages());
        cityPage.setSize(citiesPageEntity.getSize());

        val nextPage = Math.min(cityPage.getTotalPages() - 1, citiesPageEntity.getNumber() + 1);
        cityPage.setNextPage(nextPage);

        return ResponseEntity.ok(cityPage);
    }


    // Just for dev purposes.
    // In real situation, this should be replaced for example with reverse proxy.
    @CrossOrigin(origins = "http://localhost:3000")
    @Override
    public ResponseEntity<City> editCity(Integer id, City city) {
        val optionalCity = cityRepository.findById(id);
        if (optionalCity.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        val cityEntity = optionalCity.get();
        cityEntity.setName(city.getName());
        cityEntity.setPhoto(city.getPhoto());
        val updatedCityEntity = cityRepository.save(cityEntity);

        return ResponseEntity.ok(cityMapper.entityToDto(updatedCityEntity));
    }
}
