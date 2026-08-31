package com.api.authservice.auth_service_fraud.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

@Configuration
public class AuthorizationServerSettingConfig {

    @Value("${auth.issuer:http://localhost:9001}")
    private String issuer;

    @Bean
    AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer(issuer)
                .authorizationEndpoint("/authorize")
                .tokenEndpoint("/token")
                .jwkSetEndpoint("/jwks")
                .tokenRevocationEndpoint("/revoke")
                .tokenIntrospectionEndpoint("/introspect")
                .oidcLogoutEndpoint("/logout")
                .build();
    }
}
