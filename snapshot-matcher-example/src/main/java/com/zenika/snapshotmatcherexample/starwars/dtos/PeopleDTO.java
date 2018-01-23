package com.zenika.snapshotmatcherexample.starwars.dtos;

import java.io.Serializable;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PeopleDTO implements Serializable {
    public String name;

    @JsonProperty("birth_year")
    public String birthYear;

    public String gender;

    @JsonProperty("hair_color")
    public String hairColor;

    public String height;

    public String mass;

    @JsonProperty("skin_color")
    public String skinColor;

    public String created;
    public String edited;
    public String url;
}
