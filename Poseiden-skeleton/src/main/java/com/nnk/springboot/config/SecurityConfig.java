package com.nnk.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security est le module qui gère l'authentification (connexion) et l'autorisation (droits d'accès) dans l'application.
 * 1. Spring Security intercepte toutes les requêtes HTTP pour vérifier si 
 *    l'utilisateur est authentifié et s'il a le droit d'accéder à la ressource.
 * 2. Pour cela, il a besoin de :
 *    - Un moyen de charger les utilisateurs depuis la base (CustomUserDetailsService)
 *    - Un moyen de vérifier les mots de passe (PasswordEncoder)
 *    - Des règles pour savoir qui peut accéder à quoi (SecurityFilterChain)
 * 3. Le rôle de chaque morceau de code :
 *    - CustomUserDetailsService : Permet à Spring Security de retrouver un utilisateur
 *      à partir de son nom d'utilisateur (loadUserByUsername). C'est obligatoire pour
 *      que Spring Security puisse authentifier les utilisateurs stockés en base.
 *    - PasswordEncoder : Sert à comparer le mot de passe saisi avec celui stocké en base.
 *      Spring Security utilise automatiquement cet encodeur lors de la connexion.
 *      Il faut donc encoder les mots de passe lors de la création ou modification d'un utilisateur.
 *    - SecurityFilterChain : Définit les règles d'accès (qui peut accéder à quoi), la page de login,
 *      la page de logout, etc.
 *    - AuthenticationManager : Utilisé en interne par Spring Security pour gérer l'authentification.
 * 4. Fonctionnement global :
 *    - Lorsqu'un utilisateur tente de se connecter, Spring Security appelle loadUserByUsername()
 *      pour charger l'utilisateur et son mot de passe.
 *    - Il compare ensuite le mot de passe saisi (après encodage) avec celui stocké en base.
 *    - Si tout est bon, il crée une session sécurisée et applique les règles d'accès définies.
 * 5. Gestion des erreurs HTTP :
 *    - Si l'utilisateur fournit de mauvais identifiants (login ou mot de passe incorrect), Spring Security renvoie une erreur HTTP 401 (Unauthorized).
 *    - Si l'utilisateur est bien connecté mais n'a pas les droits d'accès à une ressource (ex : rôle insuffisant), Spring Security renvoie une erreur HTTP 403 (Forbidden).
 *    - Ces erreurs peuvent être personnalisées, mais par défaut elles sont gérées automatiquement par Spring Security.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * Configure les règles de sécurité pour les URLs, le login et le logout.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers("/", "/login", "/error", "/css/**", "/js/**", "/images/**").permitAll();
                auth.requestMatchers("/user/**").hasRole("ADMIN");
                auth.requestMatchers("/bidList/**", "/curvePoint/**").hasAnyRole("ADMIN", "USER");
                auth.anyRequest().hasRole("ADMIN");
            })
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/bidList/list", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            )
            .build();
    }

    /**
     * Configure l'encodeur de mots de passe avec BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure le gestionnaire d'authentification avec le service utilisateur personnalisé.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
            .userDetailsService(customUserDetailsService)
            .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }
}