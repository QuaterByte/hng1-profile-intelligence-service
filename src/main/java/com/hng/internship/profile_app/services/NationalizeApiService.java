package com.hng.internship.profile_app.services;

import com.hng.internship.profile_app.model.Nationalize;
import org.springframework.http.ResponseEntity;

public interface NationalizeApiService {

    public ResponseEntity<Nationalize> getNameNationalizeInfo(String name);
}
