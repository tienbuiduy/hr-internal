package com.hr.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(1)
public  class ApiLoginWebSecurityConfigurationAdapter extends
        WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] permitAll = {"/api/allocations/import-allocation",
                "/api/allocations/upload-allo-by-month",
                "/api/allocations/import-allocation-by-day",
                "/api/allocations/import-allocation-by-period",
                "/api/lessonlearns/import"
        };
        http
                .antMatcher("/api/**")
                .httpBasic()
                .disable()
                .authorizeRequests()
                .antMatchers(permitAll)
                .fullyAuthenticated()
                .and()
                .csrf().disable();
    }
}