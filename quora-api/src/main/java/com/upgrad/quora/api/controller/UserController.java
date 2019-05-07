package org.upgrad.controllers;
import org.upgrad.models.User;
import org.upgrad.models.UserProfile;
import org.upgrad.services.NotificationService;
import org.upgrad.services.UserService;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import javax.servlet.http.HttpSession;
import java.util.Date;

@Controller
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    NotificationService notificationService;

    @PostMapping("/api/user/signup")
    public ResponseEntity<?> userSignUp(@RequestParam(value = "firstName") String firstName,
                                        @RequestParam(value = "lastName", defaultValue = "null") String lastName,
                                        @RequestParam(value = "userName") String username,
                                        @RequestParam(value = "email") String email,
                                        @RequestParam(value = "password") String password,
                                        @RequestParam(value = "country") String country,
                                        @RequestParam(value = "aboutMe", defaultValue = "null") String aboutMe,
                                        @RequestParam(value = "dob") String dob,
                                        @RequestParam(value = "contactNumber", defaultValue = "null") String phoneNumber) {
        if (userService.findUserByUsername(username) != null) {
            return new ResponseEntity<>("Try any other Username, " +
                    "this Username has already been taken.", HttpStatus.FORBIDDEN);
        }
        if (userService.findUserByEmail(email) != null) {
            return new ResponseEntity<>("This user has already been registered, " +
                    "try with any other emailId.", HttpStatus.FORBIDDEN);
        }
        String sha256hex = Hashing.sha256()
                .hashString(password, Charsets.US_ASCII)
                .toString();
        userService.addUser(username, sha256hex, email, firstName, lastName, aboutMe, dob, phoneNumber, country);
        return new ResponseEntity<>(username + " successfully registered", HttpStatus.OK);
    }
    
    @PostMapping("/api/user/login")
    public ResponseEntity<?> userSignIn(@RequestParam(value = "userName") String username,
                                        @RequestParam(value = "password") String password,
                                        HttpSession httpSession) {
        String sha256hex = Hashing.sha256()
                .hashString(password, Charsets.US_ASCII)
                .toString();
        String passwordFromDatabase = userService.getPasswordByUsername(username);
        if (!passwordFromDatabase.equalsIgnoreCase(sha256hex)) {
            return new ResponseEntity<>("Invalid Credentials", HttpStatus.UNAUTHORIZED);
        } else if (userService.getRoleByUsername(username).equalsIgnoreCase("admin")) {
            User user = userService.findUserByUsername(username);
            httpSession.setAttribute("currUser", user);
            return new ResponseEntity<>("You have logged in as admin!", HttpStatus.OK);
        } else {
            User user = userService.findUserByUsername(username);
            httpSession.setAttribute("currUser", user);
            return new ResponseEntity<>("You have logged in successfully!", HttpStatus.OK);
        }
    }
