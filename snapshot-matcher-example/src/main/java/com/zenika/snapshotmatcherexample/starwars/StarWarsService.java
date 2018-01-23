package com.zenika.snapshotmatcherexample.starwars;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.zenika.snapshotmatcherexample.starwars.dtos.PlanetDTO;
import com.zenika.snapshotmatcherexample.starwars.entities.Planet;

@Service
public class StarWarsService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PlanetConverter planetConverter;

    @Value("${endpoints.planets}")
    private String endpoint;

    public PlanetDTO getPlanet(Long id){
        return planetConverter.convertPlanet(restTemplate.getForObject(endpoint + id, Planet.class));
    }
}
