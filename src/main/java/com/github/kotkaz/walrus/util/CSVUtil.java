package com.github.kotkaz.walrus.util;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.github.kotkaz.walrus.model.City;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import lombok.val;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

public class CSVUtil {

    public static List<City> csvToCities(InputStream is) {
        val csvFormat = CSVFormat.DEFAULT.builder()
            .setHeader("id", "name", "photo")
            .setTrim(true)
            .setSkipHeaderRecord(true)
            .build();

        try (val fileReader = new BufferedReader(new InputStreamReader(is, UTF_8));
             val csvParser = new CSVParser(fileReader, csvFormat)) {

            return csvParser.getRecords()
                .stream()
                .map(csvRecord -> {
                    City city = new City();
                    city.setId(Integer.parseInt(csvRecord.get("id")));
                    city.setName(csvRecord.get("name"));
                    city.setPhoto(csvRecord.get("photo"));
                    return city;
                })
                .toList();

        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }
}
