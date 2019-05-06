package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AdminBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * RestController annotation specifies that this class represents a REST API(equivalent of @Controller + @ResponseBody)
 * This Controller class help to perform admin operations
 */
@RestController
@RequestMapping
public class AdminController {

    @Autowired
    private AdminBusinessService adminBusinessService;

    /**
     * @param userUuid    UserId to delete from the request
     * @param accessToken this variable helps to authenticate the user
     * @return ResponseEntity is returned with Status OK.
     * @throws UserNotFoundException
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/admin/user/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> deleteUser(@PathVariable("userId") final String userUuid,
                                                         @RequestHeader("authorization") final String accessToken) throws UserNotFoundException, AuthorizationFailedException {
        String token = accessToken;
        // if header contain "Bearer " key then truncate it"
        if (accessToken.startsWith("Bearer ")) {
            token = (accessToken.split("Bearer "))[1];
        }
        final UserEntity userEntity = adminBusinessService.deleteUser(userUuid, token);
        UserDeleteResponse userDetailsResponse = new UserDeleteResponse().id(userEntity.getUuid()).status("USER SUCCESSFULLY DELETED");
        return new ResponseEntity<UserDeleteResponse>(userDetailsResponse, HttpStatus.OK);
    }
}
