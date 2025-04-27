package com.enmasse.Order_Service.dtos;


import lombok.Builder;

@Builder
public record StripeResponse<T>(
        String status,
        String message,
        Integer httpStatus,
        T data
) {
    public static <T> StripeResponse<T> of(String status, String message) {
        return new StripeResponse<>(status, message, null, null);
    }

    public static <T> StripeResponse<T> of(String status, String message, T data) {
        return new StripeResponse<>(status, message, null, data);
    }

    public static <T> StripeResponse<T> of(String status, String message, Integer httpStatus) {
        return new StripeResponse<>(status, message, httpStatus, null);
    }

    public static <T> StripeResponse<T> of(String status, String message, Integer httpStatus, T data) {
        return new StripeResponse<>(status, message, httpStatus, data);
    }
}

