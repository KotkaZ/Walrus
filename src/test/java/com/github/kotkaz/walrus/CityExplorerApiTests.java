package com.github.kotkaz.walrus;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.kotkaz.walrus.api.CityExplorerApi;
import com.github.kotkaz.walrus.dto.City;
import com.github.kotkaz.walrus.repository.CityRepository;
import java.util.Optional;
import javax.validation.ConstraintViolationException;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

@SpringBootTest
public class CityExplorerApiTests {

    @Autowired
    private CityExplorerApi cityExplorerApi;

    @Autowired
    private CityRepository cityRepository;


    @Test
    public void getCitiesWithoutPaginationUsesFallback() {
        val responseEntity =
            cityExplorerApi.getCities(Optional.empty(), Optional.empty(), Optional.empty());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        val cityPage = responseEntity.getBody();
        assertNotNull(cityPage);
        assertEquals(50, cityPage.getTotalPages());
        assertEquals(20, cityPage.getSize());
        assertEquals(1, cityPage.getNextPage());

        val cities = cityPage.getCities();
        assertEquals(20, cities.size());
    }


    @Test
    public void getCitiesWithPagination() {
        val responseEntity = cityExplorerApi
            .getCities(Optional.of(10), Optional.of(50), Optional.empty());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        val cityPage = responseEntity.getBody();
        assertNotNull(cityPage);
        assertEquals(20, cityPage.getTotalPages());
        assertEquals(50, cityPage.getSize());
        assertEquals(11, cityPage.getNextPage());

        val cities = cityPage.getCities();
        assertEquals(50, cities.size());
    }

    @Test
    public void getCitiesWithTooLargeSizeThrowsAnError() {
        assertThrows(
            ConstraintViolationException.class,
            () -> cityExplorerApi.getCities(Optional.of(0), Optional.of(1000), Optional.empty())
        );
    }

    @Test
    public void getCitiesWithTooLargePageNumberReturnsEmptyList() {
        val responseEntity = cityExplorerApi
            .getCities(Optional.of(1000), Optional.of(100), Optional.empty());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        val cityPage = responseEntity.getBody();
        assertNotNull(cityPage);
        assertEquals(10, cityPage.getTotalPages());
        assertEquals(100, cityPage.getSize());
        assertEquals(9, cityPage.getNextPage());

        val cities = cityPage.getCities();
        assertEquals(0, cities.size());
    }

    @Test
    public void getCitiesFiltersBySpecificName() {
        val responseEntity = cityExplorerApi
            .getCities(Optional.empty(), Optional.empty(), Optional.of("Tallinn"));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        val cityPage = responseEntity.getBody();
        assertNotNull(cityPage);
        assertEquals(1, cityPage.getTotalPages());
        assertEquals(20, cityPage.getSize());
        assertEquals(0, cityPage.getNextPage());

        val cities = cityPage.getCities();
        assertEquals(1, cities.size());
        assertTrue(cities.stream().allMatch(city -> "Tallinn".equals(city.getName())));
    }

    @Test
    public void getCitiesFiltersByPartialName() {
        val responseEntity = cityExplorerApi
            .getCities(Optional.empty(), Optional.empty(), Optional.of("a"));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        val cityPage = responseEntity.getBody();
        assertNotNull(cityPage);
        assertEquals(36, cityPage.getTotalPages());
        assertEquals(20, cityPage.getSize());
        assertEquals(1, cityPage.getNextPage());

        val cities = cityPage.getCities();
        assertEquals(20, cities.size());
        assertTrue(cities.stream().allMatch(city -> city.getName().toLowerCase().contains("a")));
    }

    @Test
    public void getCitiesFiltersByPartialNameWithPagination() {
        val responseEntity = cityExplorerApi
            .getCities(Optional.of(3), Optional.of(50), Optional.of("a"));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        val cityPage = responseEntity.getBody();
        assertNotNull(cityPage);
        assertEquals(15, cityPage.getTotalPages());
        assertEquals(50, cityPage.getSize());
        assertEquals(4, cityPage.getNextPage());

        val cities = cityPage.getCities();
        assertEquals(50, cities.size());
        assertTrue(cities.stream().allMatch(city -> city.getName().toLowerCase().contains("a")));
    }

    @Test
    public void editCityWithUnknownIdReturns404() {
        val city = new City()
            .name("Unicorn City")
            .photo("https://m.media-amazon.com/images/I/51b2z+hWV9L._AC_SY1000_.jpg");

        val responseEntity = cityExplorerApi.editCity(99999, city);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void editCityUpdatesDatabaseEntity() {
        val name = "Unicorn City";
        val photo = "https://m.media-amazon.com/images/I/51b2z+hWV9L._AC_SY1000_.jpg";
        val city = new City().name(name).photo(photo);

        val responseEntity = assertDoesNotThrow(() -> cityExplorerApi.editCity(10, city));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        val updatedCity = responseEntity.getBody();
        assertNotNull(updatedCity);
        assertEquals(10, updatedCity.getId());
        assertEquals(name, updatedCity.getName());
        assertEquals(photo, updatedCity.getPhoto());

        val updatedCityEntity = assertDoesNotThrow(() -> cityRepository.findById(10).orElseThrow());
        assertNotNull(updatedCityEntity);
        assertEquals(10, updatedCityEntity.getId());
        assertEquals(name, updatedCityEntity.getName());
        assertEquals(photo, updatedCityEntity.getPhoto());
    }

}
