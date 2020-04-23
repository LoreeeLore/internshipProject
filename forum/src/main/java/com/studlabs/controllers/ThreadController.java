package com.studlabs.controllers;

import com.studlabs.bll.exceptions.*;
import com.studlabs.bll.model.ApiErrors;
import com.studlabs.bll.model.ForumThread;
import com.studlabs.bll.services.ThreadService;
import com.studlabs.controllers.security.Access;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/threads")
public class ThreadController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ThreadController.class);

    @Autowired
    private ThreadService threadService;

    @Autowired
    private HttpServletRequest request;

    @ApiOperation(value = "Save and return the full thread object with auto-generated ID")
    @PostMapping(value = "/")
    public ResponseEntity<?> save(@Valid @RequestBody ForumThread forumThread,
                                  BindingResult bindingResult) throws ForumException {
        if (bindingResult.hasErrors()) {
            ApiErrors apiErrors = new ApiErrors(bindingResult);
            logger.info("Save thread not valid "
                    + apiErrors.getMessage());
            return new ResponseEntity<>(apiErrors, HttpStatus.BAD_REQUEST);
        }

        checkUserFromToken(request, forumThread.getUser(), null);

        logger.info("/save");
        ForumThread thread = null;
        try {
            thread = threadService.save(forumThread);
        } catch (BllException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        } catch (BadRequestException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, request);
        }

        return new ResponseEntity<>(thread, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get thread having provided id")
    public ResponseEntity<?> getById(@ApiParam(value = "Thread id")
                                     @PathVariable("id") int id) {
        logger.info("/getById#{}", id);
        Optional<ForumThread> thread = null;
        try {
            thread = threadService.findById(id);
        } catch (NotFoundException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, request);
        } catch (BllException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }

        return new ResponseEntity<>(thread.get(), HttpStatus.OK);
    }

    @GetMapping("")
    @ApiOperation(value = "Get threads")
    public ResponseEntity<?> getAll(@ApiParam(value = "Filter based on main category")
                                    @RequestParam(value = "maincategory", required = false) Optional<String> category,
                                    @ApiParam(value = "Filter based on tags")
                                    @RequestParam(value = "tag", required = false) Optional<List<String>> tags,
                                    @ApiParam(value = "Sorting field eg. title, description")
                                    @RequestParam Optional<String> sortBy,
                                    @ApiParam(value = "Sorting order fot threads. Must be \"asc\" or \"desc\"")
                                    @RequestParam Optional<String> order,
                                    @ApiParam(value = "Set limit for number of results")
                                    @RequestParam Optional<Integer> limit,
                                    @ApiParam(value = "Set offset for results eg. result starts from Xth elements")
                                    @RequestParam Optional<Integer> offset) {

        List<ForumThread> forumThreadList;
        logger.info("get all messages");

        try {
            forumThreadList = threadService.filterThreads(category, tags, sortBy, order, limit, offset);
        } catch (BadRequestException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, request);
        } catch (ForumException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }

        return new ResponseEntity<>(forumThreadList, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}")
    @ApiOperation(value = "Update message for given id")
    public ResponseEntity<?> update(@ApiParam(value = "New thread")
                                    @Valid @RequestBody ForumThread forumThread,
                                    BindingResult bindingResult,
                                    @ApiParam(value = "Thread id")
                                    @PathVariable int id) throws AccessDeniedException {
        if (bindingResult.hasErrors()) {
            ApiErrors apiErrors = new ApiErrors(bindingResult);
            logger.info("Update thread not valid "
                    + apiErrors.getMessage());
            return new ResponseEntity<>(apiErrors, HttpStatus.BAD_REQUEST);
        }

        if (forumThread.getId() == null) {
            logger.info("setting message_id in message to be {}", id);
            forumThread.setId(id);
        }

        if (forumThread.getId() != id) {
            logger.warn("Different thread id and update id {}!={}", forumThread.getId(), id);
            return createErrorResponse("Bad thread id", HttpStatus.BAD_REQUEST, request);
        }

        checkUserFromToken(request, forumThread.getUser(), null);

        logger.info("/put#{}", forumThread.getId());

        try {
            threadService.update(forumThread);
        } catch (NotFoundException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, request);
        } catch (BllException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Delete thread for given id")
    public ResponseEntity<?> remove(@ApiParam(value = "Thread id")
                                    @PathVariable("id") int id) throws AccessDeniedException {
        logger.info("/delete#" + id);

        checkUserFromToken(request, null,
                Arrays.asList(AccessRole.MENTOR));

        try {
            threadService.remove(id);
        } catch (NotFoundException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, request);
        } catch (BllException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/privacy")
    public ResponseEntity<?> getUsers(@PathVariable("id") int id) {
        logger.info("/getUsers#" + id);

        try {
            return new ResponseEntity<>(threadService.getUsers(id), HttpStatus.OK);
        } catch (BadRequestException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, request);
        } catch (NotFoundException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, request);
        } catch (BllException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @PostMapping(value = "/{id}/privacy")
    public ResponseEntity<?> addUsers(@PathVariable("id") int id,
                                      @RequestBody List<String> users) {
        logger.info("/addUsers#" + id);

        try {
            Optional<ForumThread> thread = threadService.findById(id);
            if (thread.isPresent()) {
                checkUserFromToken(request, thread.get().getUser(),
                        Arrays.asList(AccessRole.MENTOR));
            }

            threadService.addUsers(id, users);
        } catch (AccessDeniedException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED, request);
        } catch (NotFoundException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, request);
        } catch (BadRequestException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, request);
        } catch (BllException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}/privacy")
    public ResponseEntity<?> removeUsers(@PathVariable("id") int id,
                                         @RequestBody List<String> users) {
        logger.info("/removeUsers#" + id);

        try {
            Optional<ForumThread> thread = threadService.findById(id);
            if (thread.isPresent()) {
                checkUserFromToken(request, thread.get().getUser(),
                        Arrays.asList(AccessRole.MENTOR));
            }

            threadService.removeUsers(id, users);
        } catch (AccessDeniedException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED, request);
        } catch (NotFoundException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, request);
        } catch (BadRequestException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, request);
        } catch (BllException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "{id}/close")
    @Access(role = {AccessRole.MENTOR})
    public ResponseEntity<?> close(@PathVariable("id") int id) {
        logger.info("/close#" + id);

        try {
            threadService.close(id);
        } catch (NotFoundException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, request);
        } catch (BllException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "{id}/validate")
    @Access(role = {AccessRole.MENTOR})
    public ResponseEntity<?> validate(@PathVariable("id") int id) {
        logger.info("/validate#" + id);

        try {
            threadService.validate(id);
        } catch (NotFoundException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, request);
        } catch (BllException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/open")
    @Access(role = {AccessRole.MENTOR})
    public ResponseEntity<?> getAllOpen() {
        logger.info("get all open threads");

        try {
            return new ResponseEntity<>(threadService.findAllOpen(), HttpStatus.OK);
        } catch (ForumException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }
}
