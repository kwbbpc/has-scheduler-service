package com.broadway.has.httpexceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Json in request was invalid")
public class InvalidJsonRequestError extends RuntimeException{
}
