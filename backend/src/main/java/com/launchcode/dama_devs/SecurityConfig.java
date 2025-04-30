package com.launchcode.dama_devs;

import com.launchcode.dama_devs.configs.OAuth2LoginSuccessHandler;
import com.launchcode.dama_devs.models.AppRole;
import com.launchcode.dama_devs.models.Role;
import com.launchcode.dama_devs.models.User;
import com.launchcode.dama_devs.models.data.RoleRepository;
import com.launchcode.dama_devs.models.data.UserRepository;
import com.launchcode.dama_devs.security.jwt.AuthEntryPointJwt;
import com.launchcode.dama_devs.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    @Lazy
    OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter(){
        return new AuthTokenFilter();
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http.cors(Customizer.withDefaults())
                .csrf(csrf ->

                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        /*here we are making use of ignoring request matches method
                          here all the apis that match the pattern will be ignored for
                          csrf protection
                        */
                        .ignoringRequestMatchers("/api/auth/public/**"));
        http.authorizeHttpRequests(requests ->
                requests
                        //here giving permissions to everyone to access the endpoint of contact
                        .requestMatchers("/contact").permitAll()
                        .requestMatchers("/plant_images/**").permitAll()
                        .requestMatchers("/api/photo/upload/**").authenticated()
                        .requestMatchers("/api/photo/user/**").authenticated()
                        .requestMatchers("/api/garden/user/**").authenticated()
                        .requestMatchers("/api/comment/user/**").authenticated()
                        .requestMatchers("/api/plant-details/user/**").authenticated()
                        .requestMatchers("/api/weather/**").permitAll()
                        .requestMatchers("/api/nurseries/**").permitAll()
                        //here restricted to admin to access the end point of hello
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/auth/public/**").permitAll()
                        .requestMatchers("/api/csrf-token").permitAll()
                        .requestMatchers("/oauth2/**").permitAll()

                        //here other than the above requests to access you need to authenticate
                        .anyRequest().authenticated())
                        .oauth2Login(oauth2->{
                            oauth2.successHandler(oAuth2LoginSuccessHandler);
                        });
        http.exceptionHandling(exception
                -> exception.authenticationEntryPoint(unauthorizedHandler));
        http.addFilterBefore(authenticationJwtTokenFilter(),
                UsernamePasswordAuthenticationFilter.class);
        http.formLogin(Customizer.withDefaults());
        http.httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository,
                                      UserRepository userRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_USER)));

            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_ADMIN)));

            if (!userRepository.existsByUsername("user1")) {
                User user1 = new User("user1", "user1@example.com",
                        passwordEncoder.encode("password1"));
                user1.setAccountNonLocked(false);
                user1.setAccountNonExpired(true);
                user1.setCredentialsNonExpired(true);
                user1.setEnabled(true);
                user1.setSignUpMethod("email");
                user1.setRole(userRole);
                userRepository.save(user1);
            }

            if (!userRepository.existsByUsername("admin")) {
                User admin = new User("admin", "admin@example.com",
                        passwordEncoder.encode("adminPass"));
                admin.setAccountNonLocked(true);
                admin.setAccountNonExpired(true);
                admin.setCredentialsNonExpired(true);
                admin.setEnabled(true);
                admin.setSignUpMethod("email");
                admin.setRole(adminRole);
                userRepository.save(admin);
            }
        };
    }
}
