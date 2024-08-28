package com.example.demo.dto.response;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ResponseBuilder {

    public ResponseEntity<ApiResponse> buildResponse(
            HttpHeaders httpHeaders,
            int httpStatusCode,
            String message,
            Object data,
            Map<String, Object> otherParams) {
        return new ApiResponse.ApiResponseBuilder<>(httpStatusCode, message)
                .withHttpHeaders(httpHeaders)
                .withData(data)
                .withOtherParams(otherParams)
                .build();
    }

    public ResponseEntity<ApiResponse> buildResponse(
            int httpStatusCode, String message, Object data, Map<String, Object> otherParams) {
        return new ApiResponse.ApiResponseBuilder<>(httpStatusCode, message)
                .withData(data)
                .withOtherParams(otherParams)
                .build();
    }

    public ResponseEntity<ApiResponse> buildResponse(
            int httpStatusCode, String message, Map<String, Object> otherParams) {
        return new ApiResponse.ApiResponseBuilder<>(httpStatusCode, message)
                .withOtherParams(otherParams)
                .build();
    }

    public ResponseEntity<ApiResponse> buildResponse(
            HttpHeaders httpHeaders, int httpStatusCode, String message, Object data) {
        return new ApiResponse.ApiResponseBuilder<>(httpStatusCode, message)
                .withHttpHeaders(httpHeaders)
                .withData(data)
                .build();
    }

    public ResponseEntity<ApiResponse> buildResponse(
            HttpHeaders httpHeaders,
            int httpStatusCode,
            String message,
            Map<String, Object> otherParams) {
        return new ApiResponse.ApiResponseBuilder<>(httpStatusCode, message)
                .withHttpHeaders(httpHeaders)
                .withOtherParams(otherParams)
                .build();
    }

    public ResponseEntity<ApiResponse> buildResponse(
            HttpHeaders httpHeaders, int httpStatusCode, String message) {
        return new ApiResponse.ApiResponseBuilder<>(httpStatusCode, message)
                .withHttpHeaders(httpHeaders)
                .build();
    }

    public ResponseEntity<ApiResponse> buildResponse(
            int httpStatusCode, String message, Object data) {
        return new ApiResponse.ApiResponseBuilder<>(httpStatusCode, message).withData(data).build();
    }
}
