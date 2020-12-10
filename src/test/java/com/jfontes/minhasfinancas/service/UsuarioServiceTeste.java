package com.jfontes.minhasfinancas.service;


import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.jfontes.minhasfinancas.exception.ErroAutenticacao;
import com.jfontes.minhasfinancas.exception.RegraNegocioException;
import com.jfontes.minhasfinancas.model.entity.Usuario;
import com.jfontes.minhasfinancas.model.repository.UsuarioRepository;
import com.jfontes.minhasfinancas.service.impl.UsuarioServiceImpl;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTeste {
 
	@SpyBean
	UsuarioServiceImpl service;
	
	@MockBean
	UsuarioRepository repository;
	
	@Test
	public void deveSalvarUmUsuario() {
		//cenario
       Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
       Usuario usuario = Usuario.builder()
			.id(1l)
			.nome("nome")
			.email("email@email.com")
			.senha("senha").build();
	
       Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
	
       //acao
       Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
		
       //verificacao
       Assertions.assertThat(usuarioSalvo).isNotNull();
       Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
       Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
       Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
       Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
       		
	}
	
	@Test(expected = RegraNegocioException.class)
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		//cenario
		String email = "email@email.com";
		Usuario usuario = Usuario.builder().email(email).build();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
	    
		//acao
		service.salvarUsuario(usuario);
		
		//varificacao
		Mockito.verify(repository, Mockito.never()).save(usuario);
	
	}
	
	
	@Test(expected = Test.None.class)
	public void deveAutenticarUmUsuarioComSucesso() {
		//cenario
		String email = "email@email.com";
		String senha = "senha";
		
		Usuario usuario = Usuario.builder().email(email).senha(senha).build();
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		//acao
		Usuario result = service.autenticar(email, senha);
		
		//verificacao
		Assertions.assertThat(result).isNotNull();
	}
	
	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
		//cenario
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		//acao
		Throwable exception = Assertions.catchThrowable(()-> service.autenticar("email@email.com", "senha"));
		
		//verificacao
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuário não encontrado para o email informado.");
	}
	
	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
		//cenario
		String senha = "senha";
		Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
	    Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
	    
		//acao
		Throwable exception = Assertions.catchThrowable( ()-> service.autenticar("email@email.com", "123"));
		
		//verificacao
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha Inválida.");
		
	}
	
	
	@Test(expected = Test.None.class)
	public void deveValidarEmail() {
		// cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		
		//acao
		service.validarEmail("email@email.com");
		
	}
	
	@Test(expected = RegraNegocioException.class)
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		//cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		//acao
		service.validarEmail("email@email.com");
	}

}
