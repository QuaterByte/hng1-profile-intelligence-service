package com.hng.internship.profile_app.repository;

import com.hng.internship.profile_app.model.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DataRepository extends JpaRepository<Data, UUID> {

    public Optional<Data> findByName(String name);
    @Query("Select distinct d from Data d where LOWER(d.gender) = LOWER(:gender) OR LOWER(d.country_id) = LOWER(:country_id) OR LOWER(d.age_group) = LOWER(:age_group)")
    public List<Data>  findByAny(@Param("gender") String gender, @Param("country_id") String country_id, @Param("age_group") String age_group);
}
