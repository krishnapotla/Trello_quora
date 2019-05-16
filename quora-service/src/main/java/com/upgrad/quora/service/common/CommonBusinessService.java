//package com.upgrad.quora.service.common;
//
//import com.upgrad.quora.service.dao.UserDao;
//import com.upgrad.quora.service.entity.UserAuthTokenEntity;
//import com.upgrad.quora.service.entity.UserEntity;
//import com.upgrad.quora.service.exception.AuthorizationFailedException;
//import com.upgrad.quora.service.exception.UserNotFoundException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//public class CommonBusinessService {
//
//    @Autowired
//    private UserDao userDao;
//
//    @Transactional(propagation = Propagation.REQUIRED)
//    public UserEntity getUser(final String userUuid,final String authorizationToken)  throws AuthorizationFailedException,
//            UserNotFoundException {
//
//        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
//
//        if (userAuthTokenEntity == null) {
//            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
//        }
//
//        if (userAuthTokenEntity.getLogoutAt() != null) {
//            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
//        }
//
//        UserEntity userEntity = userDao.getUser(userUuid);
//
//        if (userEntity == null) {
//            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
//        }
//
//        return userEntity;
//    }
//
//
//}
