package dev.xvision.demo.securitydemo;

import dev.xvision.demo.securitydemo.security.JWTAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.sql.DataSource;

@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authz) -> {
            try {
                authz.mvcMatchers("/").permitAll()
                        .mvcMatchers("/users/create").permitAll()
                        .anyRequest().authenticated()
                        .and().formLogin()
                        .successHandler(jwtAuthenticationSuccessHandler())
                        .and().csrf().disable();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        return http.build();
    }

    @Bean
    public UserDetailsManager users(DataSource dataSource){
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public AuthenticationSuccessHandler jwtAuthenticationSuccessHandler(){
        return new JWTAuthenticationSuccessHandler();
    }

}
