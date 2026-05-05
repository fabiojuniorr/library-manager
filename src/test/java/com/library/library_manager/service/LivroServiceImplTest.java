package com.library.library_manager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import com.library.library_manager.config.ModelMapperConfig;
import com.library.library_manager.domain.GeneroEnum;
import com.library.library_manager.domain.Livro;
import com.library.library_manager.dto.LivroRequest;
import com.library.library_manager.dto.LivroResponse;
import com.library.library_manager.exception.NegocioException;
import com.library.library_manager.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LivroServiceImplTest {

	@Mock
	private LivroRepository livroRepository;

	@InjectMocks
	private LivroServiceImpl livroService;

	@BeforeEach
	void setup() {
		ModelMapper modelMapper = new ModelMapperConfig().modelMapper();
		livroService = new LivroServiceImpl(livroRepository, modelMapper);
	}

	@Test
	void deveCriarLivroComSucesso() {
		LivroRequest request = new LivroRequest("Clean Code", "Robert Martin", "123", 2008, GeneroEnum.TECNOLOGIA, true);
		Livro salvo = Livro.builder()
				.id("1")
				.titulo(request.getTitulo())
				.autor(request.getAutor())
				.isbn(request.getIsbn())
				.anoPublicacao(request.getAnoPublicacao())
				.genero(request.getGenero())
				.disponivel(request.getDisponivel())
				.dataInclusao(LocalDateTime.now())
				.dataAtualizacao(LocalDateTime.now())
				.build();

		when(livroRepository.existsByIsbn(request.getIsbn())).thenReturn(false);
		when(livroRepository.save(any(Livro.class))).thenReturn(salvo);

		LivroResponse response = livroService.criar(request);

		assertEquals("1", response.getId());
		assertEquals("Clean Code", response.getTitulo());
		verify(livroRepository).save(any(Livro.class));
	}

	@Test
	void deveLancarErroQuandoIsbnDuplicadoNaCriacao() {
		LivroRequest request = new LivroRequest("Livro", "Autor", "123", 2020, GeneroEnum.HISTORIA, true);

		when(livroRepository.existsByIsbn(request.getIsbn())).thenReturn(true);

		NegocioException ex = assertThrows(NegocioException.class, () -> livroService.criar(request));

		assertEquals("ISBN_DUPLICADO", ex.getCodigo());
	}

	@Test
	void deveBuscarLivroPorIdComSucesso() {
		Livro livro = Livro.builder()
				.id("1")
				.titulo("Livro")
				.autor("Autor")
				.isbn("123")
				.anoPublicacao(2020)
				.genero(GeneroEnum.FANTASIA)
				.disponivel(true)
				.build();

		when(livroRepository.findById("1")).thenReturn(Optional.of(livro));

		LivroResponse response = livroService.buscarPorId("1");

		assertEquals("1", response.getId());
	}

	@Test
	void deveLancarErroQuandoLivroNaoEncontrado() {
		when(livroRepository.findById("999")).thenReturn(Optional.empty());

		NegocioException ex = assertThrows(NegocioException.class, () -> livroService.buscarPorId("999"));

		assertEquals("LIVRO_NAO_ENCONTRADO", ex.getCodigo());
	}

	@Test
	void deveRemoverLivroComSucesso() {
		when(livroRepository.existsById("1")).thenReturn(true);

		livroService.remover("1");

		verify(livroRepository).deleteById("1");
	}
}
