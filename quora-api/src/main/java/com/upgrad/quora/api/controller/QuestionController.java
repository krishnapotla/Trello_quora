package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDeleteResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * RestController annotation specifies that this class represents a REST API(equivalent of @Controller + @ResponseBody)
 * This Controller class help to perform question operations
 */
@RestController
@RequestMapping
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest, @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException {

        String token = getAccessToken(accessToken);
        // Create question entity
        final QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setDate(ZonedDateTime.now());

        // Return response with created question entity
        final QuestionEntity createdQuestionEntity = questionService.createQuestion(questionEntity, accessToken);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestionEntity.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    /**
     *
     * @param questionId Question Id to delete from Server
     * @param accessToken this variable helps to authenticate the user
     * @return ResponseEntity with QuestionDeleteResponse
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionId") final String questionId,
                                                             @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {
        String token = getAccessToken(accessToken);
        questionService.deleteQuestion(questionId, token);
        // Return response
        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id(questionId).status("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }

    /**
     *  User can give only Access token or Bearer <accesstoken> as input.
     * @param accessToken
     * @return token
     */
    private String getAccessToken(String accessToken) {
        // if header contain "Bearer " key then truncate it"
        if (accessToken.startsWith("Bearer ")) {
            return (accessToken.split("Bearer "))[1];
        }
        return accessToken;
    }
}
