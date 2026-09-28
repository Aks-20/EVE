package com.eve.eve.common;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data
) {
}