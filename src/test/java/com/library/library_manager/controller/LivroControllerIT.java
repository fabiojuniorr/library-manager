package com.library.library_manager.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.library_manager.TestcontainersConfiguration;
import com.library.library_manager.domain.GeneroEnum;
import com.library.library_manager.domain.Livro;
import com.library.library_manager.dto.LivroRequest;
import com.library.library_manager.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class LivroControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private LivroRepository livroRepository;

	@BeforeEach
	void clean() {
		livroRepository.deleteAll();
	}

	@Test
	void deveCriarEBuscarLivro() throws Exception {
		LivroRequest request = new LivroRequest("Duna", "Frank Herbert", "isbn-01", 1965, GeneroEnum.FICCAO_CIENTIFICA, true);

		String response = mockMvc.perform(post("/livros")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andReturn()
				.getResponse()
				.getContentAsString();

		Livro criado = objectMapper.readValue(response, Livro.class);

		mockMvc.perform(get("/livros/{id}", criado.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.titulo").value("Duna"));
	}

	@Test
	void deveAtualizarLivro() throws Exception {
		Livro salvo = livroRepository.save(Livro.builder()
				.titulo("Livro A")
				.autor("Autor A")
				.isbn("isbn-02")
				.anoPublicacao(2010)
				.genero(GeneroEnum.HISTORIA)
				.disponivel(true)
				.build());

		LivroRequest request = new LivroRequest("Livro B", "Autor B", "isbn-02", 2011, GeneroEnum.ROMANCE, false);

		mockMvc.perform(put("/livros/{id}", salvo.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.titulo").value("Livro B"))
				.andExpect(jsonPath("$.disponivel").value(false));
	}

	@Test
	void deveDeletarLivro() throws Exception {
		Livro salvo = livroRepository.save(Livro.builder()
				.titulo("Livro A")
				.autor("Autor A")
				.isbn("isbn-03")
				.anoPublicacao(2010)
				.genero(GeneroEnum.HISTORIA)
				.disponivel(true)
				.build());

		mockMvc.perform(delete("/livros/{id}", salvo.getId()))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/livros/{id}", salvo.getId()))
				.andExpect(status().isNotFound());
	}

	@Test
	void deveRetornar400QuandoPayloadInvalido() throws Exception {
		String payload = """
				{
				  "titulo": "",
				  "autor": "",
				  "isbn": "",
				  "anoPublicacao": 900,
				  "genero": null,
				  "disponivel": null
				}
				""";

		mockMvc.perform(post("/livros")
						.contentType(MediaType.APPLICATION_JSON)
						.content(payload))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.codigo").value("DADOS_INVALIDOS"));
	}

	@Test
	void deveRetornar404QuandoLivroNaoExiste() throws Exception {
		mockMvc.perform(get("/livros/{id}", "nao-existe"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.codigo").value("LIVRO_NAO_ENCONTRADO"));
	}
}
