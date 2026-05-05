package com.library.library_manager.service;

import java.time.Year;

import com.library.library_manager.domain.GeneroEnum;
import com.library.library_manager.domain.Livro;
import com.library.library_manager.dto.LivroRequest;
import com.library.library_manager.dto.LivroResponse;
import com.library.library_manager.exception.NegocioException;
import com.library.library_manager.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LivroServiceImpl implements LivroService {

	private final LivroRepository livroRepository;
	private final ModelMapper modelMapper;

	@Override
	public LivroResponse criar(LivroRequest request) {
		validarAnoPublicacao(request.getAnoPublicacao());
		validarIsbnUnico(request.getIsbn());

		Livro livro = modelMapper.map(request, Livro.class);

		Livro salvo = livroRepository.save(livro);
		log.info("Livro criado com sucesso. id={}, isbn={}", salvo.getId(), salvo.getIsbn());
		return modelMapper.map(salvo, LivroResponse.class);
	}

	@Override
	@Cacheable(value = "livros", key = "#id")
	public LivroResponse buscarPorId(String id) {
		Livro livro = livroRepository.findById(id)
				.orElseThrow(() -> new NegocioException("LIVRO_NAO_ENCONTRADO", "Livro com id '" + id + "' nao encontrado."));
		log.debug("Livro encontrado. id={}, isbn={}", livro.getId(), livro.getIsbn());
		return modelMapper.map(livro, LivroResponse.class);
	}

	@Override
	public Page<LivroResponse> listar(Pageable pageable, GeneroEnum genero) {
		Page<Livro> page = genero == null
				? livroRepository.findAll(pageable)
				: livroRepository.findByGenero(genero, pageable);
		return page.map(livro -> modelMapper.map(livro, LivroResponse.class));
	}

	@Override
	@CacheEvict(value = "livros", key = "#id")
	public LivroResponse atualizar(String id, LivroRequest request) {
		Livro livro = livroRepository.findById(id)
				.orElseThrow(() -> new NegocioException("LIVRO_NAO_ENCONTRADO", "Livro com id '" + id + "' nao encontrado."));

		validarAnoPublicacao(request.getAnoPublicacao());
		validarIsbnUnicoAtualizacao(request.getIsbn(), id);

		livro.setTitulo(request.getTitulo());
		livro.setAutor(request.getAutor());
		livro.setIsbn(request.getIsbn());
		livro.setAnoPublicacao(request.getAnoPublicacao());
		livro.setGenero(request.getGenero());
		livro.setDisponivel(request.getDisponivel());

		Livro salvo = livroRepository.save(livro);
		log.info("Livro atualizado com sucesso. id={}, isbn={}", salvo.getId(), salvo.getIsbn());
		return modelMapper.map(salvo, LivroResponse.class);
	}

	@Override
	@CacheEvict(value = "livros", key = "#id")
	public void remover(String id) {
		if (!livroRepository.existsById(id)) {
			throw new NegocioException("LIVRO_NAO_ENCONTRADO", "Livro com id '" + id + "' nao encontrado.");
		}
		livroRepository.deleteById(id);
		log.info("Livro removido com sucesso. id={}", id);
	}

	private void validarIsbnUnico(String isbn) {
		if (livroRepository.existsByIsbn(isbn)) {
			log.warn("Falha de validacao: ISBN duplicado na criacao. isbn={}", isbn);
			throw new NegocioException("ISBN_DUPLICADO", "Ja existe livro cadastrado com o ISBN informado.");
		}
	}

	private void validarIsbnUnicoAtualizacao(String isbn, String id) {
		if (livroRepository.existsByIsbnAndIdNot(isbn, id)) {
			log.warn("Falha de validacao: ISBN duplicado na atualizacao. id={}, isbn={}", id, isbn);
			throw new NegocioException("ISBN_DUPLICADO", "Ja existe livro cadastrado com o ISBN informado.");
		}
	}

	private void validarAnoPublicacao(Integer anoPublicacao) {
		int anoAtual = Year.now().getValue();
		if (anoPublicacao == null || anoPublicacao <= 1000 || anoPublicacao > anoAtual) {
			log.warn("Falha de validacao: anoPublicacao invalido. anoPublicacao={}, anoAtual={}", anoPublicacao, anoAtual);
			throw new NegocioException("ANO_PUBLICACAO_INVALIDO", "anoPublicacao deve ser maior que 1000 e menor ou igual ao ano atual.");
		}
	}
}
