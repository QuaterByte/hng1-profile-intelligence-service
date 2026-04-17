package com.hng.internship.profile_app.services.internal;

import com.hng.internship.profile_app.exception.ApiErrorException;
import com.hng.internship.profile_app.model.Nationalize;
import com.hng.internship.profile_app.services.NationalizeApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class NationalizeRestClientApiServiceImpl implements NationalizeApiService {

   private  RestClient restClient;
   private String url = "https://api.nationalize.io/?name={myname}";

   public NationalizeRestClientApiServiceImpl(RestClient restClient){
       this.restClient = restClient;

   }
    @Override
    public ResponseEntity<Nationalize> getNameNationalizeInfo(String name) {

            try {
                return restClient
                        .get()
                        .uri(url, name)
                        .retrieve()
                        .toEntity(Nationalize.class);
            } catch (RuntimeException e) {
                throw new ApiErrorException("Nationalize returned an invalid response");
            }
    }
}
