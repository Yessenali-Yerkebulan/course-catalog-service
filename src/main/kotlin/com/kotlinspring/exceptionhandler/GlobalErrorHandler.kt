package com.kotlinspring.exceptionhandler

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.net.http.HttpHeaders

@Component
@ControllerAdvice
class GlobalErrorHandler : ResponseEntityExceptionHandler() {
    companion object KLogging {}

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: org.springframework.http.HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        logger.error("MethodArgumentNotValidException : ${ex.message}", ex)

        val errors = ex.bindingResult.allErrors
            .map{error ->error.defaultMessage!!}
            .sorted()

        logger.info("errors : $errors")

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.joinToString(", ") { it })
    }
}