package com.zenika.snapshotmatcherexample.starwars;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zenika.snapshotmatcherexample.starwars.dtos.PlanetDTO;

@RestController
@RequestMapping("planets")
public class StarWarsRest {

    @Autowired
    private StarWarsService starWarsService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public PlanetDTO getPlanet(@PathVariable Long id){
        return starWarsService.getPlanet(id);
    }
}
