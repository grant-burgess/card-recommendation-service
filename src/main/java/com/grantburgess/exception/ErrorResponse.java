package com.grantburgess.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    // alternately a list of errors could be returned
    InternalErrorResponse error;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InternalErrorResponse {
        private String message;
        private String type;
        private int code;
    }
}