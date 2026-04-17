package com.hng.internship.profile_app.services.internal;

import com.hng.internship.profile_app.exception.ApiErrorException;
import com.hng.internship.profile_app.model.Genderize;
import com.hng.internship.profile_app.services.GenderizeApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GenderizeRestClientApiService implements GenderizeApiService {
    private RestClient restClient;
    private String url = "https://api.genderize.io?name={myname}";

    public GenderizeRestClientApiService(RestClient restClient){
        this.restClient = restClient;
    }
    @Override
    public ResponseEntity<Genderize> getNameGenderizeInfo(String name){
        try{
            return   restClient.get()
                    .uri(url, name)
                    .retrieve()
                    .toEntity(Genderize.class);
        } catch (RuntimeException e) {
           throw new ApiErrorException("Genderize returned an invalid response");
        }
    }
}
