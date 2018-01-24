package com.zenika.snapshotmatcherexample.starwars.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Planet implements Serializable {
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

    @JsonProperty("residents")
    public List<String> residentsUrls;

    @JsonProperty("films")
    public List<String> filmsUrls;
}
