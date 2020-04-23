package com.studlabs.controllers;

import com.studlabs.bll.exceptions.BllException;
import com.studlabs.bll.model.Tag;
import com.studlabs.bll.services.TagService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tags")
public class TagController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(TagController.class);

    @Autowired
    private TagService tagService;

    @Autowired
    private HttpServletRequest request;

    @GetMapping(value = "")
    @ApiOperation(value = "Get all existing tags")
    public ResponseEntity<?> getAll() {
        List<Tag> tags;
        logger.info("Get all tags");

        try {
            tags = tagService.getAll();
        } catch (BllException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }

        return new ResponseEntity<>(tags, HttpStatus.OK);
    }

    @PostMapping(value = "")
    @ApiOperation(value = "Add new tags")
    public ResponseEntity<?> save(@RequestBody Map<String, List<String>> tags) {
        logger.info("Save tag list {}", tags.get("tags"));

        if (!tags.containsKey("tags")) {
            logger.info("Tags cannot be found");
            return createErrorResponse("Tags cannot be found", HttpStatus.BAD_REQUEST, request);
        }

        try {
            tagService.saveTagList(tags.get("tags"));
        } catch (BllException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "")
    @ApiOperation(value = "Delete tags")
    public ResponseEntity<?> delete(@RequestBody Map<String, List<String>> tags) {
        logger.info("Delete tag list {}", tags);

        if (!tags.containsKey("tags")) {
            logger.info("Tags cannot be found");
            return createErrorResponse("Tags cannot be found", HttpStatus.BAD_REQUEST, request);
        }

        try {
            tagService.deleteTagList(tags.get("tags"));
        } catch (BllException e) {
            logger.warn(e.getMessage());
            return createErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
