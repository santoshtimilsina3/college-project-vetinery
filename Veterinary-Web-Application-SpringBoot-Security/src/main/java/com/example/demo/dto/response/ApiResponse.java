package com.example.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Map;

@JsonPropertyOrder({"httpHeaders", "httpStatusCode", "message", "data", "otherParams"})
@Getter
public class ApiResponse<T> {

    private final HttpHeaders httpHeaders;
    private final int httpStatusCode;
    private final String message;
    private final T data;
    private final Map<String, Object> otherParams;

    private ApiResponse(ApiResponseBuilder builder) {
        this.httpHeaders = builder.httpHeaders;
        this.httpStatusCode = builder.httpStatusCode;
        this.message = builder.message;
        this.data = (T) builder.data;
        this.otherParams = builder.otherParams;
    }

    public static class ApiResponseBuilder<T> {

        private HttpHeaders httpHeaders = new HttpHeaders();
        private final int httpStatusCode;
        private final String message;
        private T data;
        private Map<String, Object> otherParams = Collections.emptyMap();

        public ApiResponseBuilder(int httpStatusCode, String message) {
            this.httpStatusCode = httpStatusCode;
            this.message = message;
        }

        public ApiResponseBuilder<T> withHttpHeaders(HttpHeaders httpHeader) {
            this.httpHeaders = httpHeader;
            return this;
        }

        public ApiResponseBuilder<T> withData(T data) {
            this.data = data;
            return this;
        }

        public ApiResponseBuilder<T> withOtherParams(Map<String, Object> otherParams) {
            this.otherParams = otherParams;
            return this;
        }

        public ResponseEntity<ApiResponse> build() {
            ApiResponse<T> apiResponse = new ApiResponse<>(this);
            return ResponseEntity.status(apiResponse.getHttpStatusCode())
                    .headers(apiResponse.getHttpHeaders())
                    .body(apiResponse);
        }
    }
}
