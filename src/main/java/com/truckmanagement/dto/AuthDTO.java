package com.truckmanagement.dto;

public class AuthDTO {

    public static class LoginRequest {
        private String username;
        private String password;
        public String getUsername() { return username; }
        public void setUsername(String v) { this.username = v; }
        public String getPassword() { return password; }
        public void setPassword(String v) { this.password = v; }
    }

    public static class TokenResponse {
        private String accessToken;
        private String tokenType = "Bearer";
        private String username;

        public TokenResponse() {}
        public TokenResponse(String accessToken, String tokenType, String username) {
            this.accessToken = accessToken;
            this.tokenType = tokenType;
            this.username = username;
        }
        public String getAccessToken() { return accessToken; }
        public String getTokenType() { return tokenType; }
        public String getUsername() { return username; }
    }

    public static class RegisterRequest {
        private String username;
        private String password;
        public String getUsername() { return username; }
        public void setUsername(String v) { this.username = v; }
        public String getPassword() { return password; }
        public void setPassword(String v) { this.password = v; }
    }
}
