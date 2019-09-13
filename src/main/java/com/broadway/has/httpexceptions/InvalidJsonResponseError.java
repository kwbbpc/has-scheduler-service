package com.broadway.has.httpexceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Invalid json was returned in the response.")
public class InvalidJsonResponseError extends RuntimeException {

}
