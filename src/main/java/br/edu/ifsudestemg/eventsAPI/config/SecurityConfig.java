package br.edu.ifsudestemg.eventsAPI.config;

import br.edu.ifsudestemg.eventsAPI.security.JwtAuthFilter;
import br.edu.ifsudestemg.eventsAPI.security.JwtService;
import br.edu.ifsudestemg.eventsAPI.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableWebSecurity
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
public class SecurityConfig  {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtService jwtService;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OncePerRequestFilter jwtFilter(){
        return new JwtAuthFilter(jwtService, usuarioService);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.GET, "/api/v1/atividades/**")
                        .hasAnyRole("ADMINISTRADOR","ORGANIZADOR","MEDIADOR","PARTICIPANTE")
                        .requestMatchers("/api/v1/atividades/**")
                        .hasAnyRole("ADMINISTRADOR","ORGANIZADOR","MEDIADOR")

                        .requestMatchers(HttpMethod.GET, "/api/v1/eventos/**")
                        .hasAnyRole("ADMINISTRADOR","ORGANIZADOR","MEDIADOR","PARTICIPANTE")
                        .requestMatchers("/api/v1/eventos/**")
                        .hasAnyRole("ADMINISTRADOR","ORGANIZADOR","MEDIADOR")

                        .requestMatchers(HttpMethod.GET, "/api/v1/ingressos/**")
                        .hasAnyRole("ADMINISTRADOR", "ORGANIZADOR", "MEDIADOR", "PARTICIPANTE")
                        .requestMatchers("/api/v1/ingressos/**")
                        .hasAnyRole("ADMINISTRADOR", "PARTICIPANTE")

                        .requestMatchers(HttpMethod.GET, "/api/v1/porteEvento/**")
                        .hasAnyRole("ADMINISTRADOR","ORGANIZADOR","MEDIADOR")
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
                        .hasAnyRole("ADMINISTRADOR","ORGANIZADOR","MEDIADOR")
                        .requestMatchers("/api/v1/tipoEvento/**")
                        .hasAnyRole("ADMINISTRADOR")

                        .requestMatchers(HttpMethod.GET, "/api/v1/usuario/**")
                        .hasAnyRole("ADMINISTRADOR","ORGANIZADOR","MEDIADOR","PARTICIPANTE")
                        //.requestMatchers("/api/v1/usuario/**")
                        //.hasAnyRole("ADMINISTRADOR","ORGANIZADOR","MEDIADOR")

                        .anyRequest()
                        .authenticated()
                )
                .addFilterBefore(
                        jwtFilter(),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /*
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(usuarioService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().disable()
                .csrf().disable()
                .authorizeRequests()

                .antMatchers(HttpMethod.GET, "/api/v1/atividades/**")
                .hasAnyRole("ADMINISTRADOR", "ORGANIZADOR", "MEDIADOR", "PARTICIPANTE")
                .antMatchers("/api/v1/atividades/**")
                .hasAnyRole("ADMINISTRADOR", "ORGANIZADOR", "MEDIADOR")

                .antMatchers(HttpMethod.GET, "/api/v1/eventos/**")
                .hasAnyRole("ADMINISTRADOR", "ORGANIZADOR", "MEDIADOR", "PARTICIPANTE")
                .antMatchers("/api/v1/eventos/**")
                .hasAnyRole("ADMINISTRADOR", "ORGANIZADOR", "MEDIADOR")

                .antMatchers(HttpMethod.GET, "/api/v1/ingressos/**")
                .hasAnyRole("ADMINISTRADOR", "ORGANIZADOR", "MEDIADOR", "PARTICIPANTE")
                .antMatchers("/api/v1/ingressos/**")
                .hasAnyRole("ADMINISTRADOR", "PARTICIPANTE")

                .antMatchers(HttpMethod.GET, "/api/v1/porteEvento/**")
                .hasAnyRole("ADMINISTRADOR", "ORGANIZADOR", "MEDIADOR")
                .antMatchers("/api/v1/porteEvento/**")
                .hasRole("ADMINISTRADOR")

                //.antMatchers(HttpMethod.GET, "/api/v1/presencas/**")
                .antMatchers("/api/v1/presencas/**")
                .hasAnyRole("ADMINISTRADOR", "ORGANIZADOR", "MEDIADOR", "PARTICIPANTE")
                //.antMatchers("/api/v1/presencas/**")
                //.hasRole("ADMINISTRADOR", "ORGANIZADOR", "MEDIADOR")

                .antMatchers(HttpMethod.GET, "/api/v1/tipoAtividade/**")
                .hasAnyRole("ADMINISTRADOR", "ORGANIZADOR", "MEDIADOR")
                .antMatchers("/api/v1/tipoAtividade/**")
                .hasRole("ADMINISTRADOR")

                .antMatchers(HttpMethod.GET, "/api/v1/tipoEvento/**")
                .hasAnyRole("ADMINISTRADOR", "ORGANIZADOR", "MEDIADOR")
                .antMatchers("/api/v1/tipoEvento/**")
                .hasRole("ADMINISTRADOR")

                .antMatchers("/api/v1/usuario/**")
                .hasAnyRole("ADMINISTRADOR", "ORGANIZADOR", "MEDIADOR", "PARTICIPANTE")

                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
        ;
    }*/

    /*
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**");
    }*/
}