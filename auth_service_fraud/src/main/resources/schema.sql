-- OAuth2 Authorization Server Schema
-- This file creates the required tables for Spring Authorization Server with JDBC persistence

-- OAuth2 Registered Client
CREATE TABLE IF NOT EXISTS oauth2_registered_client (
    id varchar(100) PRIMARY KEY,
    client_id varchar(100) NOT NULL UNIQUE,
    client_id_issued_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    client_secret varchar(200),
    client_secret_expires_at TIMESTAMP,
    client_name varchar(200) NOT NULL,
    client_authentication_methods varchar(1000) NOT NULL,
    authorization_grant_types varchar(1000) NOT NULL,
    redirect_uris varchar(1000),
    post_logout_redirect_uris varchar(1000),
    scopes varchar(1000) NOT NULL,
    client_settings varchar(2000) NOT NULL,
    token_settings varchar(2000) NOT NULL
);

-- OAuth2 Authorization
CREATE TABLE IF NOT EXISTS oauth2_authorization (
    id varchar(100) PRIMARY KEY,
    registered_client_id varchar(100) NOT NULL,
    principal_name varchar(200) NOT NULL,
    authorization_grant_type varchar(100) NOT NULL,
    authorized_scopes varchar(1000),
    attributes varchar(4000) NOT NULL,
    state varchar(500),
    authorization_code_value varchar(4000),
    authorization_code_issued_at TIMESTAMP,
    authorization_code_expires_at TIMESTAMP,
    authorization_code_metadata varchar(2000),
    access_token_value varchar(4000),
    access_token_issued_at TIMESTAMP,
    access_token_expires_at TIMESTAMP,
    access_token_metadata varchar(2000),
    access_token_type varchar(100),
    access_token_scopes varchar(1000),
    oidc_id_token_value varchar(4000),
    oidc_id_token_issued_at TIMESTAMP,
    oidc_id_token_expires_at TIMESTAMP,
    oidc_id_token_metadata varchar(2000),
    refresh_token_value varchar(4000),
    refresh_token_issued_at TIMESTAMP,
    refresh_token_expires_at TIMESTAMP,
    refresh_token_metadata varchar(2000),
    user_code_value varchar(4000),
    user_code_issued_at TIMESTAMP,
    user_code_expires_at TIMESTAMP,
    user_code_metadata varchar(2000),
    device_code_value varchar(4000),
    device_code_issued_at TIMESTAMP,
    device_code_expires_at TIMESTAMP,
    device_code_metadata varchar(2000),
    CONSTRAINT oauth2_authorization_registered_client_id_fk 
        FOREIGN KEY (registered_client_id) 
        REFERENCES oauth2_registered_client(id) 
        ON DELETE CASCADE
);

-- OAuth2 Authorization Consent
CREATE TABLE IF NOT EXISTS oauth2_authorization_consent (
    registered_client_id varchar(100) NOT NULL,
    principal_name varchar(200) NOT NULL,
    authorities varchar(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name),
    CONSTRAINT oauth2_authorization_consent_registered_client_id_fk 
        FOREIGN KEY (registered_client_id) 
        REFERENCES oauth2_registered_client(id) 
        ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS oauth2_authorization_registered_client_id_idx 
    ON oauth2_authorization(registered_client_id);
CREATE INDEX IF NOT EXISTS oauth2_authorization_principal_name_idx 
    ON oauth2_authorization(principal_name);
CREATE INDEX IF NOT EXISTS oauth2_authorization_authorization_grant_type_idx 
    ON oauth2_authorization(authorization_grant_type);