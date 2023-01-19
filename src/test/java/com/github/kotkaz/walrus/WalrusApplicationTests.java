package com.github.kotkaz.walrus;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.kotkaz.walrus.api.CityExplorerApi;
import com.github.kotkaz.walrus.mapper.CityMapper;
import com.github.kotkaz.walrus.repository.CityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WalrusApplicationTests {

    @Autowired
    private CityExplorerApi cityExplorerApi;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CityMapper cityMapper;

    @Test
    void contextLoads() {
        assertNotNull(cityExplorerApi);
        assertNotNull(cityRepository);
        assertNotNull(cityMapper);
    }

}
