package com.hng.internship.profile_app.services;

import com.hng.internship.profile_app.model.Agify;
import org.springframework.http.ResponseEntity;

public interface AgifyApiService {

    public ResponseEntity<Agify> getNameAgifyInfo(String name);
}
