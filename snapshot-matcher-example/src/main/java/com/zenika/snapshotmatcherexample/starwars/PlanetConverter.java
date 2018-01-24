package com.zenika.snapshotmatcherexample.starwars;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenika.snapshotmatcherexample.starwars.dtos.FilmDTO;
import com.zenika.snapshotmatcherexample.starwars.dtos.PeopleDTO;
import com.zenika.snapshotmatcherexample.starwars.dtos.PlanetDTO;
import com.zenika.snapshotmatcherexample.starwars.entities.Planet;

@Component
public class PlanetConverter {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    public PlanetDTO convertPlanet(Planet planet) {
        PlanetDTO dto = objectMapper.convertValue(planet, PlanetDTO.class);

        dto.films = planet.filmsUrls.stream()
                .map(filmUrl -> restTemplate.getForObject(filmUrl, FilmDTO.class))
                .collect(Collectors.toList());

        dto.residents = planet.residentsUrls.stream()
                .map(filmUrl -> restTemplate.getForObject(filmUrl, PeopleDTO.class))
                .collect(Collectors.toList());

        return dto;
    }
}
