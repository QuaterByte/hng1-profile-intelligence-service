package com.hng.internship.profile_app.services;

import com.hng.internship.profile_app.model.Genderize;
import org.springframework.http.ResponseEntity;

public interface GenderizeApiService {
    public ResponseEntity<Genderize> getNameGenderizeInfo(String name);
}
