package com.hng.internship.profile_app.services.internal;

import com.hng.internship.profile_app.exception.ApiErrorException;
import com.hng.internship.profile_app.model.Agify;
import com.hng.internship.profile_app.model.Genderize;
import com.hng.internship.profile_app.services.AgifyApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
@Service
public class AgifyRestClientApiServiceImpl implements AgifyApiService {

    private  RestClient restClient;
    private String url = "https://api.agify.io?name={myname}";

    public AgifyRestClientApiServiceImpl(RestClient restClient){
        this.restClient = restClient;
    }
    @Override
    public ResponseEntity<Agify> getNameAgifyInfo(String name) {
        try{
            return   restClient.get()
                    .uri(url, name)
                    .retrieve()
                    .toEntity(Agify.class);
        } catch (RuntimeException e) {
            throw new ApiErrorException("Agify returned an invalid response");
        }
    }
}
