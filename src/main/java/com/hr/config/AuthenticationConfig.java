package com.hr.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class AuthenticationConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .oauth2Login().permitAll();
        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/logoutSuccess")
//                .deleteCookies("JSESSIONID", "jsessionid")
                .invalidateHttpSession(true);
        http.sessionManagement().maximumSessions(240).maxSessionsPreventsLogin(true);
    }
    @Override
    public void configure(WebSecurity web) throws Exception{
        web.ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**")
                .antMatchers("/api/**")
                .antMatchers(HttpMethod.POST, "/api/employees")
                .antMatchers(HttpMethod.POST, "/api/customers/**")
                .antMatchers(HttpMethod.POST, "/api/projects/**")
                .antMatchers(HttpMethod.POST, "/api/opportunities/**")
                .antMatchers(HttpMethod.POST, "/api/functions/**")
                .antMatchers(HttpMethod.POST, "/api/allocations/**");}
}
