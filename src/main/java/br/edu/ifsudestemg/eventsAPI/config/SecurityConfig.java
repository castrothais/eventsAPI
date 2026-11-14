package br.edu.ifsudestemg.eventsAPI.config;

import br.edu.ifsudestemg.eventsAPI.security.JwtAuthFilter;
import br.edu.ifsudestemg.eventsAPI.security.JwtService;
import br.edu.ifsudestemg.eventsAPI.service.UsuarioService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.config.Customizer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {
    private final UsuarioService usuarioService;
    private JwtService jwtService;
    private PasswordEncoder passwordEncoder;

    public SecurityConfig(UsuarioService usuarioService, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public OncePerRequestFilter jwtFilter(){
        return new JwtAuthFilter(jwtService, usuarioService);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(usuarioService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/v1/atividades/**")
                        .hasAnyRole("ADMINISTRADOR","ORGANIZADOR","MEDIADOR","PARTICIPANTE")
                        .requestMatchers("/api/v1/atividades/**")
                        .hasAnyRole("ADMINISTRADOR","ORGANIZADOR","MEDIADOR")

                        .requestMatchers(HttpMethod.GET, "/api/v1/eventos/**")
                        .hasAnyRole("ADMINISTRADOR", "ORGANIZADOR", "MEDIADOR", "PARTICIPANTE")

                        .requestMatchers(HttpMethod.POST, "/api/v1/eventos")
                        .hasAnyRole("ADMINISTRADOR", "ORGANIZADOR", "MEDIADOR", "PARTICIPANTE")

                        .requestMatchers(HttpMethod.PUT, "/api/v1/eventos/**")
                        .hasAnyRole("ADMINISTRADOR", "ORGANIZADOR", "MEDIADOR", "PARTICIPANTE")

                        .requestMatchers(HttpMethod.DELETE, "/api/v1/eventos/**")
                        .hasAnyRole("ADMINISTRADOR", "ORGANIZADOR", "MEDIADOR", "PARTICIPANTE")

                        .requestMatchers(HttpMethod.POST, "/api/v1/eventos/*/upload")
                        .hasAnyRole("ADMINISTRADOR", "ORGANIZADOR", "MEDIADOR", "PARTICIPANTE")

                        .requestMatchers("/api/v1/eventos/**")
                        .hasAnyRole("ADMINISTRADOR", "ORGANIZADOR", "MEDIADOR")

                        .requestMatchers(HttpMethod.GET, "/api/v1/ingressos/**")
                        .hasAnyRole("ADMINISTRADOR", "ORGANIZADOR", "MEDIADOR", "PARTICIPANTE")
                        .requestMatchers("/api/v1/ingressos/**")
                        .hasAnyRole("ADMINISTRADOR", "PARTICIPANTE")

                        .requestMatchers(HttpMethod.GET, "/api/v1/porteEvento/**")
                        .hasAnyRole("ADMINISTRADOR","ORGANIZADOR","MEDIADOR", "PARTICIPANTE")
                        .requestMatchers("/api/v1/porteEvento/**")
                        .hasAnyRole("ADMINISTRADOR")

                        .requestMatchers(HttpMethod.GET, "/api/v1/presencas/**")
                        .hasAnyRole("ADMINISTRADOR","ORGANIZADOR","MEDIADOR","PARTICIPANTE")
                        //.requestMatchers("/api/v1/presencas/**")
                        //.hasAnyRole("ADMINISTRADOR")

                        .requestMatchers(HttpMethod.GET, "/api/v1/tipoAtividade/**")
                        .hasAnyRole("ADMINISTRADOR","ORGANIZADOR","MEDIADOR")
                        .requestMatchers("/api/v1/tipoAtividade/**")
                        .hasAnyRole("ADMINISTRADOR")

                        .requestMatchers(HttpMethod.GET, "/api/v1/tipoEvento/**")
                        .hasAnyRole("ADMINISTRADOR","ORGANIZADOR","MEDIADOR", "PARTICIPANTE")
                        .requestMatchers("/api/v1/tipoEvento/**")
                        .hasAnyRole("ADMINISTRADOR")

                        .requestMatchers(HttpMethod.GET, "/api/v1/usuario/me")
                        .authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/v1/usuario")
                        .hasRole("ADMINISTRADOR")

                        .requestMatchers(HttpMethod.GET, "/api/v1/usuario/{id}")
                        .authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/usuario/**")
                        .permitAll()
                        //.requestMatchers("/api/v1/usuario/**")
                        //.hasAnyRole("ADMINISTRADOR","ORGANIZADOR","MEDIADOR")

                        .requestMatchers("/api/v1/usuario/auth").permitAll()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/webjars/**"
        );
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}

//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .cors(cors -> cors.disable())
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//
//                        .requestMatchers(HttpMethod.GET, "/api/v1/atividades/**")
//                        .hasAnyRole("ADMINISTRADOR","ORGANIZADOR","MEDIADOR","PARTICIPANTE")
//                        .requestMatchers("/api/v1/atividades/**")
//                        .hasAnyRole("ADMINISTRADOR","ORGANIZADOR","MEDIADOR")
//
//                        .requestMatchers(HttpMethod.GET, "/api/v1/eventos/**")
//                        .hasAnyRole("ADMINISTRADOR","ORGANIZADOR","MEDIADOR","PARTICIPANTE")
//                        .requestMatchers("/api/v1/eventos/**")
//                        .hasAnyRole("ADMINISTRADOR","ORGANIZADOR","MEDIADOR")
//
//                        .requestMatchers(HttpMethod.GET, "/api/v1/ingressos/**")
//                        .hasAnyRole("ADMINISTRADOR", "ORGANIZADOR", "MEDIADOR", "PARTICIPANTE")
//                        .requestMatchers("/api/v1/ingressos/**")
//                        .hasAnyRole("ADMINISTRADOR", "PARTICIPANTE")
//
//                        .requestMatchers(HttpMethod.GET, "/api/v1/porteEvento/**")
//                        .hasAnyRole("ADMINISTRADOR","ORGANIZADOR","MEDIADOR")
//                        .requestMatchers("/api/v1/porteEvento/**")
//                        .hasAnyRole("ADMINISTRADOR")
//
//                        .requestMatchers(HttpMethod.GET, "/api/v1/presencas/**")
//                        .hasAnyRole("ADMINISTRADOR","ORGANIZADOR","MEDIADOR","PARTICIPANTE")
//                        //.requestMatchers("/api/v1/presencas/**")
//                        //.hasAnyRole("ADMINISTRADOR")
//
//                        .requestMatchers(HttpMethod.GET, "/api/v1/tipoAtividade/**")
//                        .hasAnyRole("ADMINISTRADOR","ORGANIZADOR","MEDIADOR")
//                        .requestMatchers("/api/v1/tipoAtividade/**")
//                        .hasAnyRole("ADMINISTRADOR")
//
//                        .requestMatchers(HttpMethod.GET, "/api/v1/tipoEvento/**")
//                        .hasAnyRole("ADMINISTRADOR","ORGANIZADOR","MEDIADOR")
//                        .requestMatchers("/api/v1/tipoEvento/**")
//                        .hasAnyRole("ADMINISTRADOR")
//
//                        .requestMatchers(HttpMethod.GET, "/api/v1/usuario/**")
//                        .hasAnyRole("ADMINISTRADOR","ORGANIZADOR","MEDIADOR","PARTICIPANTE")
//                        //.requestMatchers("/api/v1/usuario/**")
//                        //.hasAnyRole("ADMINISTRADOR","ORGANIZADOR","MEDIADOR")
//
//                        .anyRequest()
//                        .authenticated()
//                )
//                .addFilterBefore(
//                        jwtFilter(),
//                        UsernamePasswordAuthenticationFilter.class
//                );
//
//        return http.build();
//    }