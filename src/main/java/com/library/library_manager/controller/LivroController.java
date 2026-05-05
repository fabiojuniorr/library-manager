package com.library.library_manager.controller;

import com.library.library_manager.domain.GeneroEnum;
import com.library.library_manager.dto.ErroResponse;
import com.library.library_manager.dto.LivroRequest;
import com.library.library_manager.dto.LivroResponse;
import com.library.library_manager.service.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/livros")
@RequiredArgsConstructor
@Tag(name = "Livros", description = "Recursos de gerenciamento de livros")
public class LivroController {

	private final LivroService livroService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Cria um novo livro")
	@ApiResponse(responseCode = "201", description = "Livro criado com sucesso")
	@ApiResponse(responseCode = "400", description = "Dados invalidos", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	public LivroResponse criar(@Valid @RequestBody LivroRequest request) {
		return livroService.criar(request);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Busca livro por id")
	@ApiResponse(responseCode = "200", description = "Livro encontrado")
	@ApiResponse(responseCode = "404", description = "Livro nao encontrado", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	public LivroResponse buscarPorId(@PathVariable String id) {
		return livroService.buscarPorId(id);
	}

	@GetMapping
	@Operation(summary = "Lista livros com paginacao e filtro opcional de genero")
	@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
	public Page<LivroResponse> listar(
			@PageableDefault Pageable pageable,
			@RequestParam(required = false) GeneroEnum genero) {
		return livroService.listar(pageable, genero);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Atualiza um livro existente")
	@ApiResponse(responseCode = "200", description = "Livro atualizado com sucesso")
	@ApiResponse(responseCode = "400", description = "Dados invalidos", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	@ApiResponse(responseCode = "404", description = "Livro nao encontrado", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	public LivroResponse atualizar(@PathVariable String id, @Valid @RequestBody LivroRequest request) {
		return livroService.atualizar(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Remove livro por id")
	@ApiResponse(responseCode = "204", description = "Livro removido com sucesso")
	@ApiResponse(responseCode = "404", description = "Livro nao encontrado", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
	public void remover(@PathVariable String id) {
		livroService.remover(id);
	}
}
