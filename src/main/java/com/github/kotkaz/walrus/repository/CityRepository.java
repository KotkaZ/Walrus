package com.github.kotkaz.walrus.repository;

import com.github.kotkaz.walrus.model.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Integer> {

    Page<City> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

}
