package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserBusinessService {

    @Autowired
    private AdminBusinessService adminBusinessService;

    @Autowired
    private UserDao userDao;

    // Service method for signup
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {
        return adminBusinessService.createUser(userEntity);
    }

    @Autowired
    private AuthenticationService authenticationService;

    // Service method for signout
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signout(final String authorizationToken) throws SignOutRestrictedException {
        return authenticationService.userLogout(authorizationToken);
    }

    // Service method to get the user details
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUser(final String userUuid,final String authorizationToken)  throws AuthorizationFailedException,
            UserNotFoundException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);

        // Check if the user is signed in or not
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // Check if the user has signed out
        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }

        UserEntity userEntity = userDao.getUser(userUuid);

        // Check if the user exists
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }

        return userEntity;
    }


}
