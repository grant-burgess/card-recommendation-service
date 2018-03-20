package com.grantburgess.api.v1;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.grantburgess.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/v1/creditcards")
public class CreditCardControllerV1 {
    private final CreditCardServiceV1 creditCardService;

    public CreditCardControllerV1(CreditCardServiceV1 creditCardService) {
        this.creditCardService = creditCardService;
    }

    @PostMapping
    public ResponseEntity<List<CreditCardResponse>> getRecommendedCards(@Validated @RequestBody CreditCardRequest creditCardRequest) {
        return ResponseEntity.ok(creditCardService.processRequest(creditCardRequest));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(MethodArgumentNotValidException exception) {
        String errorMsg = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> String.format("'%s' %s", fieldError.getField(), fieldError.getDefaultMessage()))
                .findFirst()
                .orElse(exception.getMessage());

        return toErrorResponse(errorMsg);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(HttpMessageNotReadableException exception) {
        String errorMsg = exception.getMessage();
        if (exception.getCause() instanceof InvalidFormatException && exception.getCause().getCause() instanceof DateTimeParseException) {
            errorMsg = "Invalid date format. Date should be in the format yyyy-MM-dd";
        }

        return toErrorResponse(errorMsg);
    }

    private ErrorResponse toErrorResponse(String errorMsg) {
        return ErrorResponse
                .builder()
                .error(new ErrorResponse.InternalErrorResponse(errorMsg, "MethodArgumentNotValid", 15000))
                .build();
    }
}