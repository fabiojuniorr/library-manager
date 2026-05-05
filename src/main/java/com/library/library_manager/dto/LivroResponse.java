package com.library.library_manager.dto;

import java.time.LocalDateTime;

import com.library.library_manager.domain.GeneroEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LivroResponse {

    private String id;
    private String titulo;
    private String autor;
    private String isbn;
    private Integer anoPublicacao;
    private GeneroEnum genero;
    private Boolean disponivel;
    private LocalDateTime dataInclusao;
    private LocalDateTime dataAtualizacao;
}