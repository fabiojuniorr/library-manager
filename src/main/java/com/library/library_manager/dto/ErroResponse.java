package com.library.library_manager.dto;

import java.time.LocalDateTime;

public record ErroResponse(
		String codigo,
		String mensagem,
		LocalDateTime timestamp
) {
}
