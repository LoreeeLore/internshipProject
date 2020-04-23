package com.studlabs.controllers;

import com.studlabs.bll.exceptions.BllException;
import com.studlabs.bll.exceptions.ForumException;
import com.studlabs.bll.model.Message;
import com.studlabs.bll.services.MessageServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/messages")
@ControllerAdvice(assignableTypes = MessageElasticSearchController.class)

public class MessageElasticSearchController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageServiceImpl messageService;

    @Autowired
    private HttpServletRequest request;

    @GetMapping("/search")
    public ResponseEntity<?> fullTextSearch(@RequestParam("q") String key) throws IOException {

        List<Message> finalAnswer = new ArrayList<>();
        logger.info("Getting message/s with the free text key: {}", key);
        List<String> answer = new ArrayList<>(messageService.fullTextSearch(key));
        for (String entity : answer) {
            Optional<Message> byId = null;
            try {
                byId = messageService.findById(Integer.parseInt(entity));
            } catch (BllException e) {
                e.printStackTrace();
            }
            if (byId.isPresent()) {
                finalAnswer.add(byId.get());
            }

        }
        return new ResponseEntity<>(finalAnswer, HttpStatus.OK);
    }

    //elastic search part
    @PutMapping(value = "/indexAll")
    public ResponseEntity<?> indexAllMessages(@PathVariable int threadId) {
        logger.info("Indexing all messages from thread {}", threadId);
        try {
            messageService.indexAllMessages();
        } catch (ForumException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}