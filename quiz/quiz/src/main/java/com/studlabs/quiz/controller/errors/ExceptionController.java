package com.studlabs.quiz.controller.errors;

import com.studlabs.quiz.exception.*;
import org.apache.logging.log4j.*;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.*;

@ControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LogManager.getLogger(ExceptionController.class);

    @ExceptionHandler(ConvertBlobException.class)
    public final ResponseEntity<Void> handleConvertBlobException(ConvertBlobException ex) {
        LOGGER.error("ConvertBlob exception occurred ", ex);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public final ResponseEntity<Void> handleConvertBlobException(AuthenticationCredentialsNotFoundException ex) {
        LOGGER.error("Authentication credentials not found ", ex);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<Void> handleAccessDeniedException(AccessDeniedException ex) {
        LOGGER.error("Access denied! ", ex);
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ParserJwtException.class)
    public final ResponseEntity<Void> handleParserJwtException(ParserJwtException ex) {
        LOGGER.error("ParserJwt exception occurred ", ex);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataTransactionException.class)
    public final ResponseEntity<Void> handleDataTransactionException(DataTransactionException ex) {
        LOGGER.error("Data transaction exception occurred ", ex);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidQuizException.class)
    public final ResponseEntity<Void> handleInvalidQuizException(InvalidQuizException ex) {
        LOGGER.error("Invalid quiz exception occurred ", ex);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidQuestionsException.class)
    public final ResponseEntity<Void> handleInvalidQuestionsException(InvalidQuestionsException ex) {
        LOGGER.error("Invalid questions exception occurred ", ex);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
