package io.gontrum.shorturl.exception

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.Errors
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {
    override fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException, headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any> {
        val error = fromBindingErrors(ex.getBindingResult())
        return handleExceptionInternal(ex, error,
                HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request)
    }

    fun fromBindingErrors(errors: Errors?): ErrorResponse {
        return ErrorResponse("Validation failed. " + errors?.errorCount + " error(s)", errors?.allErrors?.map {
            it.defaultMessage ?: "no specific"
        })
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(
        val message: String,
        val validationErrors: List<String>? = null
)