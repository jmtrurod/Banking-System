package com.ironhack.BankSystem.security;


import com.ironhack.BankSystem.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled=true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private UserDetailsServiceImpl userDetailsService;
  
  @Bean
  public PasswordEncoder passwordEncoder () {
    return new BCryptPasswordEncoder();
  }
  
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
      .userDetailsService(userDetailsService)
      .passwordEncoder(passwordEncoder);
  }

  @Override
  public void configure(HttpSecurity httpSecurity) throws Exception {

    httpSecurity.httpBasic();
    httpSecurity.authorizeRequests()
            .antMatchers(HttpMethod.POST, "/account").hasAuthority("ROLE_ADMIN")
            .antMatchers(HttpMethod.POST, "/account/unfreeze/{id}").hasAuthority("ROLE_ADMIN")
            .antMatchers(HttpMethod.POST, "/transfer/accountFrom/{idFrom}/accountTo/{idTo}").hasAuthority("ROLE_HOLDER")
            .antMatchers(HttpMethod.POST, "/account/{id}").hasAuthority("ROLE_HOLDER")
            .antMatchers(HttpMethod.POST, "/credit/{id}").hasAuthority("ROLE_ADMIN")
            .antMatchers(HttpMethod.POST, "/debit/{id}").hasAuthority("ROLE_ADMIN")
            .antMatchers(HttpMethod.POST, "/third-party").hasAuthority("ROLE_ADMIN")
            .antMatchers(HttpMethod.POST, "/third-party/credit/{hashkey}").hasAuthority("ROLE_THIRDPARTY")
            .antMatchers(HttpMethod.POST, "/third-party/debit/{hashkey}").hasAuthority("ROLE_THIRDPARTY")
            .and().logout().deleteCookies("JSESSIONID");

    httpSecurity.csrf().disable();
  }
}
