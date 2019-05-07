package com.upgrad.quora.service.common;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonBusinessService {

    @Autowired
    private UserDao userDao;
    public UserEntity getUser(final String userUuid){
        return userDao.getUser(userUuid);
    }
}
