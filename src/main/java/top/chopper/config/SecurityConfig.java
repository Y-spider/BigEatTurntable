package top.chopper.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import top.chopper.filter.JWTAuthenticationFilter;


/*
   @Author:ROBOT
   @DateTime:2024/4/15 19:48
   @Version:1.0.0
   @Description: Security配置类
   */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JWTAuthenticationFilter jwtAuthenticationFilter;
    // 配置默认配置
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 显式配置权限规则：所有未明确放行的接口都需要认证
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/user/admin/login",
                                "/user/client/login",
                                // ↓↓↓ 追加 Swagger / Knife4j 相关路径 ↓↓↓
                                "/v2/**",
                                "/v3/**",
                                "/swagger-resources/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/doc.html",          // 如果用 knife4j
                                "/webjars/**"
                        )
                        .permitAll() // 公开接口
                        .anyRequest() // 其他所有接口
                        .authenticated() // 必须认证
                )
                // 2. 关闭表单登录和HTTP Basic认证
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
//                // 3. 强制无状态会话（JWT必须的配置）
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
                // 4. 添加JWT过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 5. 跨域配置
                .cors(cors -> cors.configurationSource(getConfigurationSource()))
                // 6. 关闭CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // 7. 自定义认证失败处理
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new AuthenticationEntryPointImpl())
                );

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return new DBUserDetailsManager();
    }

    private CorsConfigurationSource getConfigurationSource(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedOriginPattern("*");
        corsConfiguration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**",corsConfiguration);
        return urlBasedCorsConfigurationSource;
    }

}
