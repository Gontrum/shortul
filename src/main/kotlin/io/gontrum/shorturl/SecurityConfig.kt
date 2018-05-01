package io.gontrum.shorturl

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User


val VIEW_ALL_TOP_STATS = "VIEW_ALL_TOP_STATS"
val VIEW_TOP_STATS = "VIEW_TOP_STATS"
val VIEW_ALL_STATISTICS = "VIEW_ALL_STATISTICS"
val VIEW_STATISTICS = "VIEW_STATISTICS"

@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {

    override fun configure(httpSecurity: HttpSecurity) {
        httpSecurity
                .csrf().disable()
                .logout().disable()
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .anonymous()
                .and()
                .authorizeRequests()
                .antMatchers("/statistics/**").authenticated()
                .antMatchers("/**").permitAll()
                //.anyRequest().permitAll()
                .and()
                .httpBasic()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        val user = User.withDefaultPasswordEncoder().username("user").password("user").authorities(VIEW_TOP_STATS, VIEW_STATISTICS).build()
        val admin = User.withDefaultPasswordEncoder().username("admin").password("admin").authorities(VIEW_ALL_TOP_STATS, VIEW_ALL_STATISTICS).build()
        auth.inMemoryAuthentication()
                .withUser(user)
                .withUser(admin)
    }
}
