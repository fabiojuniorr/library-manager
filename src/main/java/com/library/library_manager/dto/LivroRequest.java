package com.library.library_manager.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.library.library_manager.domain.GeneroEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LivroRequest {

    @NotBlank(message = "titulo é obrigatório")
    private String titulo;

    @NotBlank(message = "autor é obrigatório")
    private String autor;

    @NotBlank(message = "isbn é obrigatório")
    private String isbn;

    @NotNull(message = "ano publicação é obrigatório")
    @Min(value = 1001, message = "ano publicação deve ser maior que 1000")
    @Max(value = 3000, message = "ano publicação inválido")
    private Integer anoPublicacao;

    @NotNull(message = "gênero é obrigatório")
    private GeneroEnum genero;

    @NotNull(message = "disponível é obrigatório")
    private Boolean disponivel;
}