package com.hng.internship.profile_app.api;

import com.hng.internship.profile_app.exception.ApiErrorException;
import com.hng.internship.profile_app.exception.DataNotFoundException;
import com.hng.internship.profile_app.model.*;
import com.hng.internship.profile_app.repository.DataRepository;
import com.hng.internship.profile_app.response.ErrorResponse;
import com.hng.internship.profile_app.response.ProfileAlreadyExistsResponse;
import com.hng.internship.profile_app.response.ProfileResponse;
import com.hng.internship.profile_app.response.ResponseList;
import com.hng.internship.profile_app.services.AgifyApiService;
import com.hng.internship.profile_app.services.GenderizeApiService;
import com.hng.internship.profile_app.services.NationalizeApiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api", produces = "application/json")
@CrossOrigin
public class ProfileController {

    private final DataRepository dataRepository;
    private final GenderizeApiService genderizeApiService;
    private final AgifyApiService agifyApiService;
    private final NationalizeApiService nationalizeApiService;

    public ProfileController(DataRepository dataRepository, GenderizeApiService genderizeApiService, AgifyApiService agifyApiService, NationalizeApiService nationalizeApiService){
        this.dataRepository = dataRepository;
        this.genderizeApiService = genderizeApiService;
        this.agifyApiService = agifyApiService;
        this.nationalizeApiService = nationalizeApiService;
    }

    @PostMapping(value = "/profiles", consumes = "application/json")
    public ResponseEntity<?> createProfile(@RequestBody com.hng.internship.profile_app.model.RequestBody requestBody){
        if(requestBody == null || requestBody.name().trim().isEmpty())
            return ResponseEntity.badRequest().body(new ErrorResponse("error", "Missing or empty name"));
        if(isNumeric(requestBody.name()))
            throw  new IllegalArgumentException("Invalid type");

        Optional<Data> data = dataRepository.findByName(requestBody.name());
        if(data.isPresent())
            return ResponseEntity.ok().body(new ProfileAlreadyExistsResponse("success", "Profile already exists", data.get()));

        ResponseEntity<Genderize> genderizeEntity;

        try{
            genderizeEntity = genderizeApiService.getNameGenderizeInfo(requestBody.name());
        } catch (RuntimeException e) {
            return  serverErrorResponse("error", "Upstream or server failure");
        }
        ResponseEntity<Agify> agifyEntity ;
        try {
            agifyEntity = agifyApiService.getNameAgifyInfo(requestBody.name());
        } catch (RuntimeException e) {
            return serverErrorResponse("error", "Upstream or server failure");
        }
        ResponseEntity<Nationalize> nationalizeEntity;
        try {
            nationalizeEntity = nationalizeApiService.getNameNationalizeInfo(requestBody.name());
        } catch (RuntimeException e) {
            return   serverErrorResponse("error", "Upstream or server failure");
        }

        if(genderizeEntity.getStatusCode().is5xxServerError())
            return serverErrorResponse("502", "Genderize returned an invalid response");
        if(agifyEntity.getStatusCode().is5xxServerError())
            return serverErrorResponse("502", "Agify returned an invalid response");
        if (nationalizeEntity.getStatusCode().is5xxServerError())
            return serverErrorResponse("502", "Nationalize returned an invalid response");

        Genderize genderize1 = genderizeEntity.getBody();
        Agify agify1 = agifyEntity.getBody();
        Nationalize nationalize1 = nationalizeEntity.getBody();

        if(genderize1 == null || genderize1.gender() == null || genderize1.count() == 0)
            return ResponseEntity.internalServerError().body(new ErrorResponse("error", "do not store"));
        if(agify1 == null || agify1.age() == null)
            return ResponseEntity.internalServerError().body(new ErrorResponse("error", "do not store"));
        if(nationalize1 == null || nationalize1.country().isEmpty())
            return ResponseEntity.internalServerError().body(new ErrorResponse("error", "do not store"));

        Data savedData = dataRepository.save(toData(nationalize1, genderize1, agify1));
        return  ResponseEntity.status(201).body(new ProfileResponse("success", savedData));

    }

    @GetMapping("/profiles/{id}")
    public ResponseEntity<?> getProfile(@PathVariable("id")UUID uuid){
        Optional<Data> data = dataRepository.findById(uuid);

        if(data.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(new ProfileResponse("success", data.get()));
    }

    @GetMapping("/profiles")
    public ResponseEntity<ResponseList> getList(@RequestParam(required = false) String gender, @RequestParam(required = false) String country_id, @RequestParam(required = false) String age_group){

        List<Data> dataList = dataRepository.findByAny(gender, country_id, age_group);

        if(dataList.isEmpty())
            return ResponseEntity.ok().build();
        String staus = "success";
        int count = dataList.size();
       List<com.hng.internship.profile_app.response.Data> data = map(dataList);
        return ResponseEntity.ok().body(new ResponseList(staus, count, data));

    }

    @DeleteMapping("/profiles/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") UUID uuid){
        Optional<Data> data = dataRepository.findById(uuid);
        if(data.isEmpty())
            throw  new DataNotFoundException("id doesn't exist");
        dataRepository.delete(data.get());
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<?> illegalArgumentExceptionHandler(){
        return ResponseEntity.unprocessableContent().body(new ErrorResponse("error", "Invalid type"));


    }



    private Data toData( Nationalize nationalize,Genderize genderize, Agify agify){
        Data data = new Data();
        data.setName(genderize.name());
        data.setGender(genderize.gender());
        data.setGender_probability(genderize.probability()) ;
        data.setSample_size(genderize.count()) ;
        data.setAge(agify.age());
        data.setAge_group();
        Country country = nationalize.getCountryWithHighestProbability();
        data.setCountry_id(country.country_id());
        data.setCountry_probability(country.probability());
        data.setCreated_at(utc8601Formatter());

        return  data;
    }

    private ResponseEntity<ErrorResponse> serverErrorResponse(String err, String message){
        return  new ResponseEntity<ErrorResponse>(new ErrorResponse(err, message),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorResponse> dataNotFound(){
        return  new ResponseEntity<>(new ErrorResponse("error", "Profile not found"), HttpStatus.NOT_FOUND);
    }

    private List<com.hng.internship.profile_app.response.Data> map(List<Data> dataList){
        List<com.hng.internship.profile_app.response.Data> dataList1 = new ArrayList<>();
        int count = 0;
        for(Data d : dataList){
            ++count;
            String id = "id-" + count;
            String name = d.getName();
            String gender = d.getGender();
            int age = d.getAge();
            String age_group = d.getAge_group();
            String country_id = d.getCountry_id();
            com.hng.internship.profile_app.response.Data dd = new com.hng.internship.profile_app.response.Data(id, name, gender, age, age_group, country_id);
            dataList1.add(dd);
        }
        return  dataList1;
    }

    private String utc8601Formatter(){
        StringBuilder builder = new StringBuilder(Instant.now().toString());
        builder = builder.delete(21, 28);
        return  builder.toString();
    }

    private static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
