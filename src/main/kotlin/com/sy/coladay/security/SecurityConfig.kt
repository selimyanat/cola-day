package com.sy.coladay.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import javax.sql.DataSource

/**
 * Simple security configuration to secure the application. The mechanism used in here like http
 * basic, no password encoder are used to keep the demo simple. They are are not intended for usage
 * in real life scenario.
 *
 * @author selim
 */
@Configuration
@EnableWebSecurity
open class SecurityConfig : WebSecurityConfigurerAdapter() {
    @Autowired
    private val userDetailsService: UserDetailsServiceImpl? = null

    @Autowired
    private val dataSource: DataSource? = null

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and() // In real life scenario a stronger authentication mechanism must be use to protect the
            // user credentials.
            .httpBasic()
            .and()
            .authorizeRequests()
            .antMatchers("/actuator/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .csrf()
            .disable()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailsService)
        authProvider.setPasswordEncoder(encoder())
        auth
            .authenticationProvider(authProvider)
    }

    @Bean
    open fun encoder(): PasswordEncoder {
        // For the sake of simplicity we use a simple password encoder. In real life scenario, the
        // password must be at the very least hashed.
        return NoOpPasswordEncoder.getInstance()
    }
}