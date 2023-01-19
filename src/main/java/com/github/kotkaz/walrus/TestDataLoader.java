package com.github.kotkaz.walrus;

import com.github.kotkaz.walrus.repository.CityRepository;
import com.github.kotkaz.walrus.util.CSVUtil;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TestDataLoader implements ApplicationRunner {

    private final CityRepository cityRepository;

    @Override
    public void run(ApplicationArguments args) {

        try (val inputStream = getClass().getClassLoader().getResourceAsStream("cities.csv")) {
            val cities = CSVUtil.csvToCities(inputStream);
            cityRepository.saveAll(cities);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
