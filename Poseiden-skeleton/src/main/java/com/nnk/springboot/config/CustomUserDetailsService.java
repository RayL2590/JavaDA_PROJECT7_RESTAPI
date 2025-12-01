package com.nnk.springboot.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;

/**
 * Service personnalisé pour charger les utilisateurs depuis la base de données.
 * Intègre l'authentification Spring Security avec le modèle utilisateur de l'application.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Charge un utilisateur par son nom d'utilisateur pour l'authentification.
     * 
     * @param username Le nom d'utilisateur
     * @return UserDetails contenant les informations d'authentification
     * @throws UsernameNotFoundException Si l'utilisateur n'existe pas
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Tentative de connexion pour l'utilisateur: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Utilisateur non trouvé: {}", username);
                    return new UsernameNotFoundException("Utilisateur non trouvé: " + username);
                });

        logger.debug("Utilisateur trouvé: {} (ID: {})", user.getUsername(), user.getId());

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            getGrantedAuthorities(user.getRole())
        );
    }

    /**
     * Convertit un rôle métier en GrantedAuthority pour Spring Security.
     * Ajoute automatiquement le préfixe "ROLE_" au rôle.
     * 
     * @param role Le rôle de l'utilisateur (ex: "ADMIN", "USER")
     * @return Collection de GrantedAuthority
     */
    private Collection<GrantedAuthority> getGrantedAuthorities(String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (role != null && !role.trim().isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        return authorities;
    }
}