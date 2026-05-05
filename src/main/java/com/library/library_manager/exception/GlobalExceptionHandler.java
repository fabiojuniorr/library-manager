package com.library.library_manager.exception;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.library.library_manager.dto.ErroResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(NegocioException.class)
	public ResponseEntity<ErroResponse> handleNegocioException(NegocioException ex) {
		HttpStatus status = "LIVRO_NAO_ENCONTRADO".equals(ex.getCodigo()) ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
		ErroResponse body = new ErroResponse(ex.getCodigo(), ex.getMessage(), LocalDateTime.now());
		return ResponseEntity.status(status).body(body);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErroResponse> handleValidationException(MethodArgumentNotValidException ex) {
		String mensagem = ex.getBindingResult().getFieldErrors().stream()
				.map(FieldError::getDefaultMessage)
				.collect(Collectors.joining(", "));
		ErroResponse body = new ErroResponse("DADOS_INVALIDOS", mensagem, LocalDateTime.now());
		return ResponseEntity.badRequest().body(body);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErroResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
		if (ex.getCause() instanceof InvalidFormatException invalidFormatException
				&& invalidFormatException.getTargetType() != null
				&& invalidFormatException.getTargetType().isEnum()) {
			String campo = invalidFormatException.getPath().isEmpty()
					? "campo"
					: invalidFormatException.getPath().getLast().getFieldName();

			String valoresValidos = Arrays.stream(invalidFormatException.getTargetType().getEnumConstants())
					.map(Object::toString)
					.collect(Collectors.joining(", "));
			ErroResponse body = new ErroResponse(
					"DADOS_INVALIDOS",
					"Valor invalido para '" + campo + "'. Valores permitidos: " + valoresValidos + ".",
					LocalDateTime.now());
			return ResponseEntity.badRequest().body(body);
		}

		ErroResponse body = new ErroResponse("DADOS_INVALIDOS", "Corpo da requisicao invalido.", LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}
}
