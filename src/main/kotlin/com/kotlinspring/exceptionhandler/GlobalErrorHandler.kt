package com.kotlinspring.exceptionhandler

import com.kotlinspring.exception.InstructorNotValidException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
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

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception, request: WebRequest): ResponseEntity<Any> {
        logger.error("Exception : ${ex.message}", ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.message)
    }

    @ExceptionHandler(InstructorNotValidException::class)
    fun handleInstructorNotValidExceptions(ex: InstructorNotValidException, request: WebRequest): ResponseEntity<Any> {
        logger.error("Exception : ${ex.message}", ex)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.message)
    }
}