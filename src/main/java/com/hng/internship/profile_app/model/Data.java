package com.hng.internship.profile_app.model;

import com.hng.internship.profile_app.helper.GeneratedUuidV7;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;
@Entity

public class Data {

    @Id
    @GeneratedUuidV7
    private UUID id;
    @Column(unique = true)
    private String name;
    private String gender;
    private double gender_probability;
    private int sample_size;
    private int age;
    private String age_group;
    private String country_id;
    private double country_probability;
    private String created_at;

    public Data(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setSample_size(int sample_size) {
        this.sample_size = sample_size;
    }

    public int getSample_size() {
        return sample_size;
    }

    public double getCountry_probability() {
        return country_probability;
    }

    public int getAge() {
        return age;
    }

    public double getGender_probability() {
        return gender_probability;
    }

    public UUID getId() {
        return id;
    }

    public String getAge_group() {
        return age_group;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setAge_group() {
        if(age >= 0 && age <= 12)
            age_group = "child";
        else if (age >= 13 && age <= 19)
           age_group = "teenager";
        else if(age >= 20 && age <= 59)
            age_group = "adult";
        else
            age_group = "senior";

    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public void setCountry_probability(double country_probability) {
        this.country_probability = country_probability;
    }

    public void setGender_probability(double gender_probability) {
        this.gender_probability = gender_probability;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

}
