package com.juicelabs.icd;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "code not found")
public class CodeNotFoundException extends RuntimeException {
}
