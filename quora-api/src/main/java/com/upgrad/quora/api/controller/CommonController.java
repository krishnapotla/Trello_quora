package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.common.CommonBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    private CommonBusinessService commonBusinessService;

    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)

    public ResponseEntity<UserDetailsResponse> getUser(@PathVariable("id") final String userUuid) {
        final UserEntity userEntity = commonBusinessService.getUser(userUuid);
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse().firstName(userEntity.getFirstName()).lastName(userEntity.getLastName()).userName(userEntity.getUserName()).emailAddress(userEntity.getEmail()).country(userEntity.getCountry()).aboutMe(userEntity.getAboutme()).dob(userEntity.getDob()).contactNumber(userEntity.getContactNumber());

        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
    }
}
