# Spring Boot OAuth2 Authorization Code Flow Example

This project demonstrates the OAuth2 Authorization Code Flow using Spring Boot 3. It consists of two applications:

1. **Authorization Server** - Authenticates users and issues access tokens
2. **Resource Server** - Protects resources with OAuth2 and handles token verification

## Project Structure

```
authorization-code-flow-spring-boot/
├── authorization-server-spring-boot/   # OAuth2 Authorization Server
└── resource-server-spring-boot/        # OAuth2 Resource Server
```

## Prerequisites

- Java 17 or higher
- Maven 3.9.9 or higher (project uses Maven wrapper)

## Getting Started

### Running the Authorization Server

```bash
cd authorization-server-spring-boot
./mvnw spring-boot:run
```

The Authorization Server will start on port 9090: http://localhost:9090/authorization-server

### Running the Resource Server

```bash
cd resource-server-spring-boot
./mvnw spring-boot:run
```

The Resource Server will start on port 8080: http://localhost:8080/resource-server

## How It Works

## Understanding the OAuth2 Authorization Code Flow

The Authorization Code Flow is designed for web applications that can securely store client credentials. This project demonstrates the complete flow with both the authorization server and resource server components.

### Complete Flow Diagram

```
┌─────────────┐                                     ┌──────────────────┐                                     ┌──────────────────┐
│             │                                     │                  │                                     │                  │
│             │  1. Authorization Request           │                  │                                     │                  │
│    User     │─────────────────────────────────────▶  Authorization   │                                     │                  │
│   Browser   │                                     │     Server       │                                     │  Resource Server │
│             │  2. User Login & Consent            │                  │                                     │                  │
│             │◀─────────────────────────────────────                  │                                     │                  │
│             │                                     │                  │                                     │                  │
│             │  3. Redirect with Auth Code         │                  │                                     │                  │
│             │◀─────────────────────────────────────                  │                                     │                  │
│             │                                     └──────────────────┘                                     │                  │
│             │                                                                                              │                  │
│             │  4. Forward Auth Code to Resource Server                                                     │                  │
│             │─────────────────────────────────────────────────────────────────────────────────────────────▶                  │
│             │                                                                                              │                  │
│             │                                                                                              │                  │
│             │                                     ┌──────────────────┐                                     │                  │
│             │                                     │                  │  5. Backend Token Request           │                  │
│             │                                     │                  │◀─────────────────────────────────────                  │
│             │                                     │  Authorization   │                                     │                  │
│             │                                     │     Server       │  6. Issue Access Token              │                  │
│             │                                     │                  │─────────────────────────────────────▶                  │
│             │                                     │                  │                                     │                  │
│             │                                     └──────────────────┘                                     └──────────────────┘
│             │                                                                                              
│             │  7. Display Token to User                                                                   
│             │◀─────────────────────────────────────────────────────────────────────────────────────────────                  
│             │                                                                                              
└─────────────┘                                                                                              
```

### Detailed Flow Steps

1. **User Initiates Flow**: The user visits an application that requires access to protected resources.

2. **Authorization Request**: The application redirects the user's browser to the authorization server with these parameters:
   ```
   http://localhost:9090/authorization-server/oauth2/authorize?response_type=code&client_id=custom-client-id&scope=test_resources&redirect_uri=http://localhost:8080/resource-server/auth-confirmation
   ```
    - `response_type=code`: Specifies the authorization code flow
    - `client_id`: Identifies the client application
    - `scope`: Specifies the permissions requested
    - `redirect_uri`: Where to send the code after authorization

3. **User Authentication**: The authorization server presents a login form to the user:
    - Username: `amdose`
    - Password: `amdose`

4. **Consent Screen**: After login, the user sees a consent screen showing which permissions (scopes) the application is requesting.

5. **Authorization Code Grant**: After the user approves, the authorization server generates a one-time authorization code and redirects the user's browser back to the application's redirect URI with the code as a parameter:
   ```
   http://localhost:8080/resource-server/auth-confirmation?code=ABC123XYZ
   ```

6. **Token Exchange (Backend)**: The resource server:
    - Receives the authorization code
    - Displays it to the user and asks for confirmation
    - When confirmed, makes a secure backend request to the authorization server's token endpoint
    - Includes client credentials for authentication (client ID and secret)
    - Sends the authorization code, redirect URI, and grant type

7. **Token Issuance**: The authorization server:
    - Validates the client credentials
    - Verifies the authorization code hasn't expired and was issued to this client
    - Issues an access token (and optionally a refresh token)
    - Returns these tokens to the resource server

8. **Token Display**: The resource server displays the access token to the user.

9. **Access Protected Resources**: With the token, the client can access protected resources:
   ```
   GET http://localhost:8080/resource-server/test/worked
   Authorization: Bearer <access_token>
   ```

### Testing the Flow

1. Start both servers (authorization and resource)
2. In your browser, navigate to:
   ```
   http://localhost:9090/authorization-server/oauth2/authorize?response_type=code&client_id=custom-client-id&scope=test_resources&redirect_uri=http://localhost:8080/resource-server/auth-confirmation
   ```
3. Log in with username `amdose` and password `amdose`
4. Approve the requested permissions
5. On the confirmation page, click "Exchange for Token"
6. Use the token to access protected resources

## Component Details

### Authorization Server

The authorization server is configured with:
- In-memory user details service with a single user
- In-memory client registration
- OAuth2 authorization endpoints
- Basic security configuration

Key configuration classes:
- `AuthServerConfig`: Configures OAuth2 authorization server components

### Resource Server

The resource server is configured with:
- JWT token validation
- Route protection based on OAuth2 scopes
- Token exchange controller for authorization code flow
- Simple UI for authorization code confirmation

Key configuration classes:
- `ResourceServerConfig`: Configures OAuth2 resource server components
- `OAuth2AuthorizationFlowController`: Handles authorization code exchange
- `TestController`: Provides a protected endpoint for testing