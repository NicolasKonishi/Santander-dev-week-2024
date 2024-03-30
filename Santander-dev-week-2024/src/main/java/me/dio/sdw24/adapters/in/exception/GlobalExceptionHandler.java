package me.dio.sdw24.adapters.in.exception;

import me.dio.sdw24.domain.exception.ChampionNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(ChampionNotFoundException.class)
    public ResponseEntity<ApiError> handleDoMainException(ChampionNotFoundException domainError){
        return ResponseEntity
                .unprocessableEntity()
                .body(new ApiError(domainError.getMessage()));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleDoMainException(Exception unexpectedError){
        String message = "Ocorreu um erro inesperado! :(";
        logger.error(message, unexpectedError);
        return ResponseEntity
                .internalServerError()
                .body(new ApiError(message));
    }

    /*
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiError handleDoMainException(Exception domainError){
        return new ApiError("Ocorreu um erro inesperado! :(");
    }*/

    public record ApiError(String message){

    }
}
