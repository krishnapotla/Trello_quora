package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionBusinessService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity, String accessToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthEntity = userDao.getUserAuthToken(accessToken);
        authorizeUser(userAuthEntity);

        questionEntity.setUser(userAuthEntity.getUser());
        return questionDao.createQuestion(questionEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteQuestion(final String questionId, final String authorization) throws InvalidQuestionException, AuthorizationFailedException {
        UserAuthTokenEntity userAuthEntity = userDao.getUserAuthToken(authorization);

        authorizeUser(userAuthEntity);
        QuestionEntity questionEntity = questionDao.getQuestionById(questionId);
        // Validate if current user is the owner of requested question or the role of user is admin
        if (userAuthEntity.getUser().getRole().equals("nonadmin") && !userAuthEntity.getUser().getUuid().equals(questionEntity.getUser().getUuid())) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }
        // Validate if requested question exist or not
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        questionDao.deleteQuestion(questionEntity);
    }

    private void authorizeUser(UserAuthTokenEntity userAuthEntity) throws AuthorizationFailedException {
        // Validate if user is signed in or not
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // Validate if user has signed out
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
        }
    }

    public List<QuestionEntity> getAllQuestions(String token) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthEntity = userDao.getUserAuthToken(token);
        authorizeUser(userAuthEntity);
        return questionDao.getAllQuestions();
    }
}
