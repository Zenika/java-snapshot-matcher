package com.zenika.snapshotmatcherexample.starwars;

import static com.zenika.snapshotmatcher.SnapshotMatcher.matchesSnapshot;
import static com.zenika.snapshotmatcher.utils.TestUtils.fromJson;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.zenika.snapshotmatcherexample.starwars.dtos.FilmDTO;
import com.zenika.snapshotmatcherexample.starwars.dtos.PeopleDTO;
import com.zenika.snapshotmatcherexample.starwars.dtos.PlanetDTO;
import com.zenika.snapshotmatcherexample.starwars.entities.Planet;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PlanetConverterTest {

    @Mock
    private RestTemplate restTemplate;

    @Autowired
    @InjectMocks
    private PlanetConverter converter;

    private PeopleDTO lukeSkywalker = fromJson("luke-skywalker", PeopleDTO.class);
    private FilmDTO attackOfTheClones = fromJson("attack-of-the-clones", FilmDTO.class);
    private Planet tatooine = fromJson("tatooine", Planet.class);

    @Before
    public void setUp() {
        when(restTemplate.getForObject("https://swapi.co/api/people/1/", PeopleDTO.class))
                .thenReturn(lukeSkywalker);
        when(restTemplate.getForObject("https://swapi.co/api/films/5/", FilmDTO.class))
                .thenReturn(attackOfTheClones);
    }

    @Test
    public void converterTest() {
        PlanetDTO planet = converter.convertPlanet(tatooine);

        assertThat(planet, matchesSnapshot());
    }
}