package com.studlabs.controllers;

import com.studlabs.bll.exceptions.*;
import com.studlabs.bll.model.ApiErrors;
import com.studlabs.bll.model.ForumThread;
import com.studlabs.bll.model.Message;
import com.studlabs.bll.model.Rating;
import com.studlabs.bll.services.MessageService;
import com.studlabs.bll.services.RatingServiceImpl;
import com.studlabs.bll.services.ThreadService;
import com.studlabs.controllers.security.AccessRole;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;


@RestController
@RequestMapping(value = "/threads/{threadId}/messages")
@ControllerAdvice(assignableTypes = MessageController.class)
public class MessageController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private RatingServiceImpl ratingService;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @GetMapping("/{id}")
    @ApiOperation(value = "Get message based on given id")
    public ResponseEntity<?> getById(@ApiParam(value = "Message id")
                                     @PathVariable("id") int id) {
        logger.info("Getting message with id: {}", id);
        Optional<Message> message = null;
        try {
            message = messageService.findById(id);
        } catch (ForumException e) {
            logger.warn("get message with id: {}, msg:{}", id, e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }

        if (message.isPresent()) {
            return new ResponseEntity<>(message.get(), HttpStatus.OK);
        }

        return createErrorResponse("Message cannot be found with id ", HttpStatus.NOT_FOUND, request);
    }

    @GetMapping("")
    @ApiOperation(value = "Get messages from a thread with provided id")
    public ResponseEntity<?> getAll(@ApiParam(value = "Thread id")
                                    @PathVariable int threadId,
                                    @ApiParam(value = "Sorting field eg. upvote, downvotes, text, date, user")
                                    @RequestParam Optional<String> sortBy,
                                    @ApiParam(value = "Sorting order. Must be \"asc\" or \"desc\"")
                                    @RequestParam Optional<String> order,
                                    @RequestParam Optional<Integer> limit,
                                    @RequestParam Optional<Integer> offset) {
        logger.info("Getting all messages from thread {}", threadId);
        List<Message> allSorted = null;
        try {
            allSorted = messageService.findAllSorted(threadId, sortBy, order, limit, offset);
        } catch (BadRequestException e) {
            logger.warn("getAll messages with sortBy: {}, order {}, msg:{}",
                    sortBy.orElse(null), order.orElse(null),
                    e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, request);
        } catch (ForumException e) {
            logger.warn("getAll messages with sortBy: {}, order {}, msg:{}",
                    sortBy.orElse(null), order.orElse(null),
                    e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }

        return new ResponseEntity<>(allSorted, HttpStatus.OK);
    }

    @PostMapping(value = "/")
    @ApiOperation(value = "Save message to a thread and return full message with auto-generated id")
    public ResponseEntity<?> save(@Valid @RequestBody Message message,
                                  BindingResult bindingResult,
                                  @ApiParam(value = "Thread id for message")
                                  @PathVariable Integer threadId) throws AccessDeniedException {

        if (bindingResult.hasErrors()) {
            ApiErrors apiErrors = new ApiErrors(bindingResult);
            logger.info("save message not valid "
                    + apiErrors.getMessage());
            return new ResponseEntity<>(apiErrors, HttpStatus.BAD_REQUEST);
        }

        if (!badThreadIdInMessage(message, threadId)) {
            return createErrorResponse("Bad thread id in message", HttpStatus.BAD_REQUEST, request);
        }

        checkUserFromToken(request, message.getUser(), null);

        logger.info("Saving message for thread with id: {}", message.getThreadId());

        Message savedMessage = null;
        try {
            savedMessage = messageService.save(message);
        } catch (BadRequestException e) {
            logger.warn("save message with text: {}, msg:{}", message.getText(), e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, request);
        } catch (ForumException e) {
            logger.warn("save message with text: {}, msg:{}", message.getText(), e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }

        return new ResponseEntity<>(savedMessage, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}")
    @ApiOperation(value = "Update a message with given id")
    public ResponseEntity<?> update(@Valid @RequestBody Message message,
                                    BindingResult bindingResult,
                                    @ApiParam(value = "Message id")
                                    @PathVariable Integer id) throws AccessDeniedException {
        if (bindingResult.hasErrors()) {
            ApiErrors apiErrors = new ApiErrors(bindingResult);
            logger.info("Update message not valid "
                    + apiErrors.getMessage());
            return new ResponseEntity<>(apiErrors, HttpStatus.BAD_REQUEST);
        }

        checkUserFromToken(request, message.getUser(), null);

        if (message.getId() == null) {
            logger.info("setting message_id in message to be {}", id);
            message.setId(id);
        } else if (!message.getId().equals(id)) {
            logger.info("Bad message_id in message {}, from path {}", message.getId(), id);
            return createErrorResponse("Bad message_id in message", HttpStatus.BAD_REQUEST, request);
        }

        logger.info(
                "Update message with id: {}", message.getId());
        try {
            messageService.update(message);
        } catch (NotFoundException e) {
            logger.warn("update message with id: {}, msg:{}", id, e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, request);
        } catch (BadRequestException e) {
            logger.warn("update message with id: {}, msg:{}", id, e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, request);
        } catch (ForumException e) {
            logger.warn("update message with id: {}, msg:{}", id, e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Delete a message with provided id")
    public ResponseEntity<?> remove(@ApiParam(value = "Message id")
                                    @PathVariable("id") int id) {
        logger.info("Delete message with id: {}", id);


        try {
            //only same user can delete his own message
            Optional<Message> message = messageService.findById(id);
            if (message.isPresent()) {
                checkUserFromToken(request, message.get().getUser(),
                        Arrays.asList(AccessRole.MENTOR));
            }

            messageService.remove(id);
        } catch (AccessDeniedException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED, request);
        } catch (NotFoundException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, request);
        } catch (ForumException e) {
            logger.warn("Delete message with id: {}, msg:{}", id, e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    //for rating
    @DeleteMapping(value = "/{id}/rating/{user}")
    @ApiOperation(value = "Delete rating from message given by provided user")
    public ResponseEntity<?> removeRating(@ApiParam(value = "Message id")
                                          @PathVariable("id") int id,
                                          @ApiParam(value = "User")
                                          @PathVariable("user") String user) {
        logger.info("Delete rating with messageId: {} and user: {}", id, user);
        try {
            ratingService.remove(id, user);
        } catch (NotFoundException e) {
            logger.warn("remove rating: msgId: {} user:{}, msg{}", id, user, e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, request);
        } catch (ForumException e) {
            logger.warn("remove rating: msgId: {} user:{}, msg{}", id, user, e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/rating/{user}")
    @ApiOperation(value = "Update rating from a provided message given by a provided user")
    public ResponseEntity<?> updateRating(@Valid @RequestBody Rating rating,
                                          BindingResult bindingResult,
                                          @ApiParam(value = "Message id")
                                          @PathVariable("id") Integer messageId,
                                          @ApiParam(value = "User")
                                          @PathVariable String user) throws AccessDeniedException {
        if (bindingResult.hasErrors()) {
            ApiErrors apiErrors = new ApiErrors(bindingResult);
            logger.info("update rating not valid "
                    + apiErrors.getMessage());
            return new ResponseEntity<>(apiErrors, HttpStatus.BAD_REQUEST);
        }

        if (badUserInRating(rating, user)) {
            return createErrorResponse("Bad user in rating", HttpStatus.BAD_REQUEST, request);
        }

        checkUserFromToken(request, rating.getUser(), null);

        if (badMessageIdInRating(rating, messageId)) {
            return createErrorResponse("Bad message_id in rating", HttpStatus.BAD_REQUEST, request);
        }

        logger.info("Update rating with messageId: {}, user: {}", messageId, user);
        try {
            ratingService.update(rating);
        } catch (NotFoundException e) {
            logger.warn("update rating: msgId: {} user:{}, msg{}", messageId, user, e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, request);
        } catch (ForumException e) {
            logger.warn("update rating: msgId: {} user:{}, msg{}", messageId, user, e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @PostMapping(value = "{id}/rating/")
    @ApiOperation(value = "Save a rating to a message")
    public ResponseEntity<?> saveRating(@Valid @RequestBody Rating rating,
                                        BindingResult bindingResult,
                                        @ApiParam(value = "Message id")
                                        @PathVariable("id") Integer messageId) throws Exception {
        if (bindingResult.hasErrors()) {
            ApiErrors apiErrors = new ApiErrors(bindingResult);
            logger.info("save rating not valid "
                    + apiErrors.getMessage());
            return new ResponseEntity<>(apiErrors, HttpStatus.BAD_REQUEST);
        }

        if (badMessageIdInRating(rating, messageId)) {
            return createErrorResponse("Bad message_id in rating", HttpStatus.BAD_REQUEST, request);
        }

        checkUserFromToken(request, rating.getUser(), null);

        try {
            rating = ratingService.save(rating);
        } catch (BadRequestException e) {
            logger.warn("save rating: msgId: {} user:{}, msg{}", rating.getMessageId(), rating.getUser(), e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, request);
        } catch (ForumException e) {
            logger.warn("save rating: msgId: {} user:{}, msg{}", rating.getMessageId(), rating.getUser(), e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }

        logger.info("Saving rating for message with id: {}", messageId);
        return new ResponseEntity<>(rating, HttpStatus.OK);
    }

    @GetMapping("/{id}/rating/{user}")
    @ApiOperation("Get rating given by a user")
    public ResponseEntity<?> getRatingById(@ApiParam(value = "Message id")
                                           @PathVariable("id") int id,
                                           @ApiParam(value = "User")
                                           @PathVariable("user") String user) {
        logger.info("Getting rating with messageId: {} and user: {}", id, user);

        try {
            Optional<Rating> ratingOpt = ratingService.findById(id, user);
            if (ratingOpt.isPresent()) {
                return new ResponseEntity<>(ratingOpt.get(), HttpStatus.OK);
            } else {
                logger.info("Rating cannot be found with id {}", id);
                return createErrorResponse("Rating cannot be found", HttpStatus.BAD_REQUEST, request);
            }
        } catch (ForumException e) {
            logger.warn("get rating: msgId: {} user:{}, msg{}", id, user, e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    private boolean badThreadIdInMessage(Message message, Integer threadId) {
        if (message.getThreadId() == null) {
            logger.info("setting message thread_id: {}",
                    threadId);
            message.setThreadId(threadId);
        } else if (!message.getThreadId().equals(threadId)) {
            logger.info("bad thread_id in message: {}, from path: {}", message.getThreadId(),
                    threadId);
            return false;
        }
        return true;
    }

    private boolean badUserInRating(Rating rating, String user) {
        if (rating.getUser() == null) {
            logger.info("setting user in rating to be {}", user);
            rating.setUser(user);
        } else if (!rating.getUser().equals(user)) {
            logger.info("bad user in rating {}, from path {}", rating.getUser(), user);
            return true;
        }

        return false;
    }

    private boolean badMessageIdInRating(Rating rating, Integer messageId) {
        if (rating.getMessageId() == null) {
            logger.info("setting user in rating to be {}", messageId);
            rating.setMessageId(messageId);
        } else if (!rating.getMessageId().equals(messageId)) {
            logger.info("bad message_id in rating {}, from path {}", rating.getUser(), messageId);
            return true;
        }
        return false;
    }

    @ModelAttribute
    private void checkThread(@PathVariable int threadId,
                             @PathVariable Optional<Integer> id,
                             @PathVariable Optional<String> user) throws Exception {

        //first check if thread exists
        Optional<ForumThread> thread = null;

        thread = threadService.findById(threadId);

        if (!thread.isPresent()) {
            throw new NoThreadException("Thread with id " + threadId + " does not exist");
        }


        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        HandlerExecutionChain handler = requestMappingHandlerMapping.getHandler(request);
        if (Objects.nonNull(handler)) {
            if (((HandlerMethod) handler.getHandler()).getMethod().getName().equals("save")) {

                logger.info("Entered pre-method - save");
                if (thread.get().getState().equals("closed")) {
                    logger.warn("cannot add message to closed thread");
                    throw new BadRequestException("cannot add message to closed thread");
                }
            }
        }
        //check if message exists in thread
        if (id.isPresent()) {
            messageService.checkIfMessageExists(threadId, id.get());
        }

        //for rating
        //check user in token
        if (user.isPresent()) {
            checkUserFromToken(request, user.get(), null);
        }
    }
}