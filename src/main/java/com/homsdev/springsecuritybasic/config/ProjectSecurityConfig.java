package com.homsdev.springsecuritybasic.config;

import com.homsdev.springsecuritybasic.filter.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collections;

@Configuration
public class ProjectSecurityConfig {
    /**
     * Default Spring Security Configuration Bean authenticate by login page or http credentials
     *
     * @param http HttpSecurity Object
     * @return Custom SecurityFilterChain Bean
     * @throws Exception
     */
    //@Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests().anyRequest().authenticated();
        http.formLogin();
        http.httpBasic();
        return http.build();
    }

    /**
     * Custom SecurityFilterChain with http and Login auth and JSessionID cookie
     *
     * @param http
     * @return
     * @throws Exception
     */
    //@Bean
    SecurityFilterChain defaultCustomSecurityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        http
                .securityContext().requireExplicitSave(false)
                .and()
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .cors(Customizer.withDefaults())//by default uses a Bean by the name of corsConfigurationSource;
                .csrf(
                        csrf -> csrf.csrfTokenRequestHandler(requestHandler)
                                .ignoringRequestMatchers("/contact", "/user")
                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new RequestValidationBeforeFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new AuthoritiesLoggingAfterfilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests()
                .requestMatchers("/account").hasAuthority("VIEWACCOUNT")
                .requestMatchers("/balance").hasAnyAuthority("VIEWACCOUNT", "VIEWBALANCE")
                .requestMatchers("/loan").hasAuthority("VIEWLOANS")
                .requestMatchers("/card").hasAuthority("VIEWCARDS")
                .requestMatchers("/user").authenticated()
                .requestMatchers("/notice", "/contact", "/welcome", "/user").permitAll();
        http.formLogin();
        http.httpBasic();
        return http.build();
    }


    @Bean
    SecurityFilterChain defaultCustomSecurityFilterChainWithJWT(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors(Customizer.withDefaults())//by default uses a Bean by the name of corsConfigurationSource;
                .csrf(
                        csrf -> csrf.csrfTokenRequestHandler(requestHandler)
                                .ignoringRequestMatchers("/contact", "/user")
                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new RequestValidationBeforeFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new AuthoritiesLoggingAfterfilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new JWTTokenGeneratorFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new JWTTokenValidatorFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests()
                .requestMatchers("/account").hasRole("USER")
                .requestMatchers("/balance").hasAnyRole("USER","ADMIN")
                .requestMatchers("/loan").hasRole("USER")
                .requestMatchers("/card").hasRole("USER")
                .requestMatchers("/user").authenticated()
                .requestMatchers("/notice", "/contact", "/welcome", "/user").permitAll();
        http.formLogin();
        http.httpBasic();
        return http.build();
    }


    /**
     * Authentication provider for in memory user details without password encoder
     *
     * @return UserDetailsManager for in memory users
     */
    //@Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("12345")
                .authorities("admin")
                .build();

        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("12345")
                .authorities("read")
                .build();
        return new InMemoryUserDetailsManager(admin, user);
    }

    /**
     * Performs user authentication against a database.- Only implements loadUserByUsername
     *
     * @param dataSource
     * @return UserDetailsService Bean
     */
    //@Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    /**
     * Creates bean with no password encoder only for test purposes
     *
     * @return Password Encoder Bean
     */
    //@Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * Creates a new bean of BcrytpPasswordEncoder
     *
     * @return Bean of type Password Encoder
     */
    @Bean
    public PasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Sets cors configuration source bean for web application
     *
     * @return CorsConfigurationSource Implementation Bean
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));//Allows client to take token header from backend
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
