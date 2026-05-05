package com.library.library_manager.service;

import com.library.library_manager.domain.GeneroEnum;
import com.library.library_manager.dto.LivroRequest;
import com.library.library_manager.dto.LivroResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LivroService {

	LivroResponse criar(LivroRequest request);

	LivroResponse buscarPorId(String id);

	Page<LivroResponse> listar(Pageable pageable, GeneroEnum genero);

	LivroResponse atualizar(String id, LivroRequest request);

	void remover(String id);
}
