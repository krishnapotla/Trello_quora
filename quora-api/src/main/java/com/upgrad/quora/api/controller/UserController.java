package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.SignupBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/")

public class UserController {

    @Autowired
    private SignupBusinessService signupBusinessService;

    @RequestMapping(method = RequestMethod.POST,path = "/user/signup",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest)
    {
        final UserEntity userEntity = new UserEntity();
        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutme(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setContactNumber(signupUserRequest.getContactNumber());
        userEntity.setSalt("1234abc");

        final UserEntity createdUserEntity = signupBusinessService.signup(userEntity);
        SignupUserResponse signupUserResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("REGISTERED");
        return new ResponseEntity<SignupUserResponse>(signupUserResponse,HttpStatus.CREATED);
    }
}




//package org.upgrad.controllers;
//import org.upgrad.models.User;
//import org.upgrad.models.UserProfile;
//import org.upgrad.services.NotificationService;
//import org.upgrad.services.UserService;
//import com.google.common.base.Charsets;
//import com.google.common.hash.Hashing;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//
//import javax.servlet.http.HttpSession;
//import java.util.Date;
//
//@Controller
//public class UserController {
//    @Autowired
//    UserService userService;
//    @Autowired
//    NotificationService notificationService;
//
//    @PostMapping("/api/user/signup")
//    public ResponseEntity<?> userSignUp(@RequestParam(value = "firstName") String firstName,
//                                        @RequestParam(value = "lastName", defaultValue = "null") String lastName,
//                                        @RequestParam(value = "userName") String username,
//                                        @RequestParam(value = "email") String email,
//                                        @RequestParam(value = "password") String password,
//                                        @RequestParam(value = "country") String country,
//                                        @RequestParam(value = "aboutMe", defaultValue = "null") String aboutMe,
//                                        @RequestParam(value = "dob") String dob,
//                                        @RequestParam(value = "contactNumber", defaultValue = "null") String phoneNumber) {
//        if (userService.findUserByUsername(username) != null) {
//            return new ResponseEntity<>("Try any other Username, " +
//                    "this Username has already been taken.", HttpStatus.FORBIDDEN);
//        }
//        if (userService.findUserByEmail(email) != null) {
//            return new ResponseEntity<>("This user has already been registered, " +
//                    "try with any other emailId.", HttpStatus.FORBIDDEN);
//        }
//        String sha256hex = Hashing.sha256()
//                .hashString(password, Charsets.US_ASCII)
//                .toString();
//        userService.addUser(username, sha256hex, email, firstName, lastName, aboutMe, dob, phoneNumber, country);
//        return new ResponseEntity<>(username + " successfully registered", HttpStatus.OK);
//    }
//
//    @PostMapping("/api/user/login")
//    public ResponseEntity<?> userSignIn(@RequestParam(value = "userName") String username,
//                                        @RequestParam(value = "password") String password,
//                                        HttpSession httpSession) {
//        String sha256hex = Hashing.sha256()
//                .hashString(password, Charsets.US_ASCII)
//                .toString();
//        String passwordFromDatabase = userService.getPasswordByUsername(username);
//        if (!passwordFromDatabase.equalsIgnoreCase(sha256hex)) {
//            return new ResponseEntity<>("Invalid Credentials", HttpStatus.UNAUTHORIZED);
//        } else if (userService.getRoleByUsername(username).equalsIgnoreCase("admin")) {
//            User user = userService.findUserByUsername(username);
//            httpSession.setAttribute("currUser", user);
//            return new ResponseEntity<>("You have logged in as admin!", HttpStatus.OK);
//        } else {
//            User user = userService.findUserByUsername(username);
//            httpSession.setAttribute("currUser", user);
//            return new ResponseEntity<>("You have logged in successfully!", HttpStatus.OK);
//        }
//    }
//
//    @PostMapping("/api/user/logout")
//    public ResponseEntity<?> userSignOut(HttpSession httpSession) {
//        if (httpSession.getAttribute("currUser") == null) {
//            return new ResponseEntity<>("You are currently not logged in", HttpStatus.UNAUTHORIZED);
//        } else {
//            httpSession.removeAttribute("currUser");
//            return new ResponseEntity<>("You have logged out successfully!", HttpStatus.OK);
//        }
//    }
//    @GetMapping("/api/user/userProfile/{userId}")
//    public ResponseEntity<?> getUserProfile(@PathVariable(value = "userId") int userId, HttpSession httpSession) {
//        if (httpSession.getAttribute("currUser") == null) {
//            return new ResponseEntity<>("Please Login first to access this endpoint", HttpStatus.UNAUTHORIZED);
//        } else {
//            User user = (User) httpSession.getAttribute("currUser");
//            UserProfile userProfile = userService.getUserProfile(user.getId());
//            if (userProfile == null) {
//                return new ResponseEntity<>("User Profile not found!", HttpStatus.NOT_FOUND);
//            } else {
//                return new ResponseEntity<>(userProfile, HttpStatus.OK);
//            }
//        }
//    }
//        @GetMapping("/api/user/notification/new")
//    public ResponseEntity<?> getNewNotifications(HttpSession httpSession) {
//        if (httpSession.getAttribute("currUser") == null) {
//            return new ResponseEntity<>("Please Login first to access this endpoint!", HttpStatus.UNAUTHORIZED);
//        } else {
//            User user = (User) httpSession.getAttribute("currUser");
//            return new ResponseEntity<>(notificationService.getNewNotifications(user.getId()), HttpStatus.OK);
//        }
//    }
//
//    @GetMapping("/api/user/notification/all")
//    public ResponseEntity<?> getAllNotifications(HttpSession httpSession) {
//        if (httpSession.getAttribute("currUser") == null) {
//            return new ResponseEntity<>("Please Login first to access this endpoint!", HttpStatus.UNAUTHORIZED);
//        } else {
//            User user = (User) httpSession.getAttribute("currUser");
//            return new ResponseEntity<>(notificationService.getAllNotifications(user.getId()), HttpStatus.OK);
//        }
//    }
//}
//
//
