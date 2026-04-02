package com.farma.treinamentos.security;

import com.farma.treinamentos.model.Utilizador;
import com.farma.treinamentos.repository.UtilizadorRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilizadorRepository utilizadorRepository;

    public CustomUserDetailsService(UtilizadorRepository utilizadorRepository) {
        this.utilizadorRepository = utilizadorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Utilizador utilizador = utilizadorRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Utilizador não encontrado"));

        return new User(
                utilizador.getId(),
                "",
                Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority(
                        "ROLE_" + utilizador.getNivelAcesso().name()
                ))
        );
    }
}