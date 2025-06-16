package com.autobots.automanager.controles;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Credencial;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.modelos.Perfil;
import com.autobots.automanager.repositorios.RepositorioUsuario;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Controle de Usuários", description = "Gerencia usuários do sistema, permitindo cadastro, atualização, exclusão e consulta de usuários com diferentes perfis de acesso.")
@RestController
@SecurityRequirement(name = "BearerAuth") // <-- isso ativa o cadeado para esse controller
public class ControleUsuario {

	@Autowired
	private RepositorioUsuario repositorio;

	
	/**
	 * Endpoint para cadastrar um novo usuário.
	 * Apenas usuários com perfil ADMIN ou GERENTE podem acessar este endpoint.
	 * 
	 * @param usuario O usuário a ser cadastrado.
	 * @return ResponseEntity com status HTTP 201 (Created) se o cadastro for bem-sucedido,
	 *         ou 400 (Bad Request) se houver algum erro.
	 */	
	@PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
	@PostMapping("/cadastrar-usuario")
	public ResponseEntity<?> cadastrarUsuario(@RequestBody Usuario usuario) {
	    BCryptPasswordEncoder codificador = new BCryptPasswordEncoder();
	    try {
	        // Obtém o usuário autenticado
	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        String nomeUsuarioLogado = auth.getName();
	        Usuario usuarioLogado = repositorio.findByCredencialNomeUsuario(nomeUsuarioLogado);

	        boolean criandoAdmin = usuario.getPerfis().contains(Perfil.ROLE_ADMIN);
	        boolean logadoEhAdmin = usuarioLogado.getPerfis().contains(Perfil.ROLE_ADMIN);

	        if (criandoAdmin && !logadoEhAdmin) {
	            return new ResponseEntity<>("Você não tem permissão para criar usuários ADMIN.", HttpStatus.FORBIDDEN);
	        }

	        Credencial credencial = new Credencial();
	        credencial.setNomeUsuario(usuario.getCredencial().getNomeUsuario());
	        String senha = codificador.encode(usuario.getCredencial().getSenha());
	        credencial.setSenha(senha);
	        usuario.setCredencial(credencial);
	        repositorio.save(usuario);
	        return new ResponseEntity<>(HttpStatus.CREATED);
	    } catch (Exception e) {
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
	}

	
	/**
	 * Endpoint para obter todos os usuários cadastrados.
	 * Apenas usuários com perfil ADMIN ou GERENTE podem acessar este endpoint.
	 * 
	 * @return ResponseEntity com a lista de usuários e status HTTP 200 (OK) se bem-sucedido,
	 *         ou 404 (Not Found) se não houver usuários.
	 */
	@PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
	@GetMapping("/obter-usuarios")
	public ResponseEntity<List<Usuario>> obterUsuarios() {
		List<Usuario> usuarios = repositorio.findAll();
		return new ResponseEntity<List<Usuario>>(usuarios, HttpStatus.FOUND);
	}
	
	/**
	 * Endpoint para obter um usuário específico pelo ID.
	 * Apenas usuários com perfil ADMIN ou GERENTE podem acessar este endpoint.
	 * 
	 * @param id O ID do usuário a ser obtido.
	 * @return ResponseEntity com o usuário encontrado e status HTTP 200 (OK),
	 *         ou 404 (Not Found) se o usuário não for encontrado.
	 */
	@PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
	@GetMapping("/usuario/{id}")
	public ResponseEntity<Usuario> obterUsuario(
	    @Parameter(description = "ID do usuário a ser buscado", example = "1") 
	    @PathVariable Long id
	) {
	    Usuario usuario = repositorio.findById(id).orElse(null);
	    if (usuario != null) {
	        return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
	    } else {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	}
	
	/**
	 * Endpoint para atualizar um usuário existente.
	 * Apenas usuários com perfil ADMIN ou GERENTE podem acessar este endpoint.
	 * 
	 * @param id O ID do usuário a ser atualizado.
	 * @param novosDados Os novos dados do usuário.
	 * @return ResponseEntity com status HTTP 200 (OK) se a atualização for bem-sucedida,
	 *         ou 404 (Not Found) se o usuário não for encontrado, ou 403 (Forbidden) se houver
	 *         tentativa de modificar um usuário ADMIN por um GERENTE.
	 */	
	@PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
	@PutMapping("/atualizar-usuario/{id}")
	public ResponseEntity<?> atualizarUsuario(
	    @Parameter(description = "ID do usuário a ser atualizado", example = "2")
	    @PathVariable Long id,
	    @RequestBody Usuario novosDados,
	    Authentication auth
	) {
	    Optional<Usuario> existente = repositorio.findById(id);
	    if (existente.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

	    Usuario usuario = existente.get();

	    boolean isAdminTarget = usuario.getPerfis().contains(Perfil.ROLE_ADMIN);
	    boolean isAdminAuth = auth.getAuthorities().stream()
	        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

	    if (isAdminTarget && !isAdminAuth) {
	        return new ResponseEntity<>("Você não tem permissão para modificar um ADMIN.", HttpStatus.FORBIDDEN);
	    }

	    usuario.setNome(novosDados.getNome());
	    usuario.setPerfis(novosDados.getPerfis());
	    repositorio.save(usuario);
	    return new ResponseEntity<>(HttpStatus.OK);
	}

	
	/**
	 * Endpoint para excluir um usuário pelo ID.
	 * Apenas usuários com perfil ADMIN ou GERENTE podem acessar este endpoint.
	 * 
	 * @param id O ID do usuário a ser excluído.
	 * @return ResponseEntity com status HTTP 204 (No Content) se a exclusão for bem-sucedida,
	 *         ou 404 (Not Found) se o usuário não for encontrado, ou 403 (Forbidden) se houver
	 *         tentativa de excluir um usuário ADMIN por um GERENTE.
	 */
	
	@PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
	@DeleteMapping("/excluir-usuario/{id}")
	public ResponseEntity<?> excluirUsuario(
	    @Parameter(description = "ID do usuário a ser excluído", example = "3")
	    @PathVariable Long id,
	    Authentication auth
	) {
	    Optional<Usuario> usuario = repositorio.findById(id);
	    if (usuario.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

	    boolean isAdminTarget = usuario.get().getPerfis().contains(Perfil.ROLE_ADMIN);
	    boolean isAdminAuth = auth.getAuthorities().stream()
	        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

	    if (isAdminTarget && !isAdminAuth) {
	        return new ResponseEntity<>("Você não tem permissão para excluir um ADMIN.", HttpStatus.FORBIDDEN);
	    }

	    repositorio.delete(usuario.get());
	    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	
}