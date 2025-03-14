package com.amdose.server.resource.controllers;

import com.amdose.server.resource.models.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

/**
 * @author Amdose Team
 */
@Controller
public class OAuth2AuthorizationFlowController {

    @Value("${spring.security.oauth2.resource-server.jwt.issuer-uri}")
    private String authorizationServerBaeUrl;

    @Value("${oauth2.authorization-server.endpoint}")
    private String tokenEndpoint;

    @Value("${oauth2.resource-server.client.id}")
    private String clientId;

    @Value("${oauth2.resource-server.client.secret}")
    private String clientSecret;

    @Value("${oauth2.resource-server.client.redirect.uri}")
    private String redirectUri;

    @GetMapping("/auth-confirmation")
    public String showAuthConfirmation(@RequestParam(required = false) String code, Model model) {
        if (code == null || code.isEmpty()) {
            model.addAttribute("error", "No authorization code provided");
        } else {
            model.addAttribute("authorizationCode", code);
            // Optional: Calculate and add time remaining
            model.addAttribute("timeRemaining", "10 minutes");
        }
        return "authorization-confirmation"; // name of the template file without extension
    }

    @PostMapping("/exchange-token")
    public String exchangeToken(@RequestParam String authorizationCode, Model model) {
        try {
            // Exchange code for token
            String token = this.exchangeCodeForToken(authorizationCode);

            // Add token to model
            model.addAttribute("token", token);

            // Return the token-success template directly
            return "token-success";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to exchange token: " + e.getMessage());
            model.addAttribute("authorizationCode", authorizationCode);
            return "authorization-confirmation";
        }
    }

    @GetMapping("/cancel")
    public String cancelExchange() {
        // Handle cancellation logic
        return "redirect:/"; // Redirect to home or wherever appropriate
    }


    private String exchangeCodeForToken(String authorizationCode) {
        // Implementation to exchange code for token
        // Typically using RestTemplate or WebClient to call OAuth token endpoint

        // Example with RestTemplate:
        RestTemplate restTemplate = new RestTemplate();

        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Basic " + encodedCredentials);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("code", authorizationCode);
        requestBody.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                authorizationServerBaeUrl + tokenEndpoint, requestEntity, TokenResponse.class);

        return response.getBody().getAccessToken();
    }
}
