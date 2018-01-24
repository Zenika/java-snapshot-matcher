package com.zenika.snapshotmatcherexample.starwars.dtos;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlanetDTO implements Serializable {
    public String name;
    public String diameter;
    public String gravity;
    public String population;
    public String climate;
    public String terrain;
    public String created;
    public String edited;
    public String url;

    @JsonProperty("rotation_period")
    public String rotationPeriod;

    @JsonProperty("orbital_period")
    public String orbitalPeriod;

    @JsonProperty("surface_water")
    public String surfaceWater;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public List<PeopleDTO> residents;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public List<FilmDTO> films;
}
