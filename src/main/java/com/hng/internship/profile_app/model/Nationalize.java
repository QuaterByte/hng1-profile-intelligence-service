package com.hng.internship.profile_app.model;

import java.util.List;

public record Nationalize(int count, String name, List<Country> country) {
    public Country getCountryWithHighestProbability(){
        country.sort((a,b)->{
            return Double.compare(a.probability(), b.probability());
        });
        return country.getLast();
    }
}
