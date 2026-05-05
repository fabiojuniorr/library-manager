package com.library.library_manager.repository;

import com.library.library_manager.domain.GeneroEnum;
import com.library.library_manager.domain.Livro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LivroRepository extends MongoRepository<Livro, String> {

	boolean existsByIsbn(String isbn);

	boolean existsByIsbnAndIdNot(String isbn, String id);

	Page<Livro> findByGenero(GeneroEnum genero, Pageable pageable);
}
