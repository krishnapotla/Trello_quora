package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * RestController annotation specifies that this class represents a REST API(equivalent of @Controller + @ResponseBody)
 * This Controller class help to perform answer operations
 */
@RestController
@RequestMapping
public class AnswerController {
    @Autowired
    AnswerBusinessService answerBusinessService;

    /**
     * create new Answer for a question
     *
     * @param answerRequest
     * @param questionId
     * @param accessToken
     * @return ResponseEntity
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */
    @PostMapping(path = "/question/{questionId}/answer/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest,
                                                       @PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String accessToken)
            throws AuthorizationFailedException, InvalidQuestionException {
        String token = getAccessToken(accessToken);

        // Create answer entity
        final AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAnswer(answerRequest.getAnswer());

        // Return response with created answer entity
        final AnswerEntity createdAnswerEntity =
                answerBusinessService.createAnswer(answerEntity, questionId, token);
        AnswerResponse answerResponse = new AnswerResponse().id(createdAnswerEntity.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    /**
     * edit Answer Content
     *
     * @param answerEditRequest
     * @param answerId
     * @param accessToken
     * @return ResponseEntity
     * @throws AuthorizationFailedException
     * @throws AnswerNotFoundException
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(final AnswerEditRequest answerEditRequest,
                                                                @PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String accessToken)
            throws AuthorizationFailedException, AnswerNotFoundException {

        String token = getAccessToken(accessToken);
        // Created answer entity for further update
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAnswer(answerEditRequest.getContent());
        answerEntity.setUuid(answerId);

        // Return response with updated answer entity
        AnswerEntity updatedAnswerEntity = answerBusinessService.editAnswerContent(answerEntity, token);
        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(updatedAnswerEntity.getUuid()).status("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }

    /**
     * delete Answer
     *
     * @param answerId
     * @param accessToken
     * @return ResponseEntity
     * @throws AuthorizationFailedException
     * @throws AnswerNotFoundException
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") final String answerId,
                                                             @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, AnswerNotFoundException {
        String token = getAccessToken(accessToken);

        // Delete requested answer
        answerBusinessService.deleteAnswer(answerId, token);

        // Return response
        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(answerId).status("ANSWER DELETED");
        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
    }

    /**
     * get all answers to a question
     *
     * @param questionId
     * @param accessToken
     * @return RequestMapping
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */
    @RequestMapping(method = RequestMethod.GET, path = "/answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(@PathVariable("questionId") final String questionId,
                                                                               @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {
        String token = getAccessToken(accessToken);

        // Get all answers for requested question
        List<AnswerEntity> allAnswers = answerBusinessService.getAllAnswersToQuestion(questionId, token);

        // Create response
        List<AnswerDetailsResponse> allAnswersResponse = new ArrayList<AnswerDetailsResponse>();

        allAnswers.forEach(answers -> {
            AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse()
                    .answerContent(answers.getAnswer())
                    .questionContent(answers.getQuestion().getContent())
                    .id(answers.getUuid());
            allAnswersResponse.add(answerDetailsResponse);
        });

        // Return response
        return new ResponseEntity<List<AnswerDetailsResponse>>(allAnswersResponse, HttpStatus.OK);
    }

    /**
     * User can give only Access token or Bearer <accesstoken> as input.
     *
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
