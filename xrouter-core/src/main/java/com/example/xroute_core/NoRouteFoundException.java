package com.example.xroute_core;

public class NoRouteFoundException extends RuntimeException {
    public NoRouteFoundException(String detail) {
        super(detail);
    }
}
