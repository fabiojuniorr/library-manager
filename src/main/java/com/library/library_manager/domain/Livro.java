package com.library.library_manager.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "livros")
public class Livro {

	@Id
	private String id;

	private String titulo;
	private String autor;

	@Indexed(unique = true)
	private String isbn;

	private Integer anoPublicacao;
	private GeneroEnum genero;
	private Boolean disponivel;

	@CreatedDate
	private LocalDateTime dataInclusao;

	@LastModifiedDate
	private LocalDateTime dataAtualizacao;
}
