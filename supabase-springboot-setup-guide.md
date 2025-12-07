# Supabase + Spring Boot Setup Guide  
**Use Supabase for Auth, Spring Boot for API + Database (Supabase Postgres)**  

This is a reusable checklist + boilerplate so you can spin this stack up fast next time.

---

## 0. Tech Stack Overview

- **Supabase**
  - Handles: **Authentication**, JWTs, email verification, password reset
  - Also provides: **Hosted Postgres** (we’ll use it as our app DB)
- **Spring Boot**
  - Handles: **REST API**, business logic, JPA entities, security
  - Uses: **Supabase Postgres** as its datasource
  - Validates: **Supabase JWTs** for all protected endpoints

Flow:

```text
[Client (web/mobile)] 
    → Supabase Auth (login/signup) → gets JWT access_token
    → Sends JWT to Spring Boot: Authorization: Bearer <token>
    → Spring validates token via Supabase JWKS
    → Accesses Supabase Postgres via JPA
```

---

## 1. Supabase Project Setup

### 1.1 Create a Supabase project

1. Go to Supabase and create a **new project**.
2. Note:
   - **Project URL** → looks like `https://<PROJECT_REF>.supabase.co`
   - **Anon public key** → used by frontend / Postman
   - **Service role key** (keep secret, usually not needed by Spring in this pattern)

You’ll find these under:

> **Settings → API**

---

### 1.2 Configure Database (for Spring Boot)

Still in your Supabase project, go to:

> **Settings → Database → Connection Info**

Copy your **Postgres connection string**, then convert to Spring-style values:

```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://db.<PROJECT_REF>.supabase.co:5432/postgres
SPRING_DATASOURCE_USERNAME=<supabase_db_user>
SPRING_DATASOURCE_PASSWORD=<supabase_db_password>
```

You’ll paste those into your `.env` file later.

---

### 1.3 Supabase Auth & JWT

Supabase issues JWTs via GoTrue. We’ll validate those in Spring using:

- **Issuer (iss):**  
  `https://<PROJECT_REF>.supabase.co/auth/v1`
- **JWKS URL (public keys):**  
  `https://<PROJECT_REF>.supabase.co/auth/v1/jwks`
- **Audience (aud):** usually `"authenticated"`

You don’t need the legacy HS256 secret unless you specifically want to verify tokens manually. Using **JWKS** (the new model) is cleaner.

---

### 1.4 (Optional) Email (SMTP) Setup in Supabase

If you want email confirmation/password reset from your own address:

1. Go to **Authentication → Email → SMTP settings** in Supabase.
2. For Gmail dev setup:

   ```text
   Host: smtp.gmail.com
   Port: 587
   Username: your_gmail_address@gmail.com
   Password: <Gmail App Password>
   Sender email: your_gmail_address@gmail.com
   Sender name: AimSense (or your app name)
   ```

3. You MUST use a **Gmail App Password** (not your regular password).

This is optional for the backend itself, but nice for real users.

---

## 2. Spring Boot Project Setup

### 2.1 Maven Dependencies (pom.xml)

Add these (or confirm they’re present):

```xml
<dependencies>
    <!-- Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- JPA + Postgres -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Security + JWT Resource Server -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
    </dependency>

    <!-- Optional: Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Optional: Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

---

### 2.2 `.env` File (for local dev)

Create a `.env` file in your project root:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://db.<PROJECT_REF>.supabase.co:5432/postgres
SPRING_DATASOURCE_USERNAME=<supabase_db_user>
SPRING_DATASOURCE_PASSWORD=<supabase_db_password>

SUPABASE_PROJECT_URL=https://<PROJECT_REF>.supabase.co
SUPABASE_JWT_AUDIENCE=authenticated
```

> These will be loaded into `application.properties` via `spring.config.import`.

---

### 2.3 `application.properties`

```properties
spring.application.name=AimSense

# Database configuration (Supabase Postgres)
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Supabase project + JWT config
supabase.project-url=${SUPABASE_PROJECT_URL}
supabase.jwt.audience=${SUPABASE_JWT_AUDIENCE}

# Optional: mail if you still need it from backend (not required if Supabase handles all auth mail)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${SUPPORT_EMAIL}
spring.mail.password=${APP_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Load local .env file
spring.config.import=optional:file:.env[.properties]
```

> For other environments (prod), you can inject these as actual environment variables instead of `.env`.

---

## 3. JWT Config (Supabase → Spring)

We’ll configure Spring to accept **Supabase JWTs** by validating:

- Issuer (`iss`)
- Audience (`aud`)
- Signature via Supabase JWKS

Create: `JwtConfig.java`

```java
package com.yourapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;

import java.util.List;
import java.util.function.Predicate;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    @Value("${supabase.project-url}")
    private String projectUrl;

    @Value("${supabase.jwt.audience}")
    private String audience;

    @Bean
    public JwtDecoder jwtDecoder() {
        String issuer = projectUrl + "/auth/v1";
        String jwkSetUri = issuer + "/jwks";

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);

        OAuth2TokenValidator<Jwt> audienceValidator =
                new JwtClaimValidator<List<String>>("aud", containsAudience(audience));

        OAuth2TokenValidator<Jwt> validator =
                new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

        decoder.setJwtValidator(validator);

        return decoder;
    }

    private Predicate<List<String>> containsAudience(String audience) {
        return audList -> audList != null && audList.contains(audience);
    }
}
```

This tells Spring:

- Only accept tokens issued by: `https://<PROJECT_REF>.supabase.co/auth/v1`
- Only accept tokens whose `aud` includes `"authenticated"`
- Validate signature using Supabase’s JWKS

---

## 4. Security Configuration

Create: `SecurityConfiguration.java`

```java
package com.yourapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                    // Public endpoints (health, docs, maybe a public status)
                    .requestMatchers("/health", "/public/**").permitAll()
                    // Everything else requires a valid Supabase JWT
                    .anyRequest().authenticated()
            )
            // Use Spring's built-in JWT support (backed by JwtDecoder from JwtConfig)
            .oauth2ResourceServer(oauth2 -> oauth2.jwt());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5173",
                "https://your-frontend-domain.com"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

This config:

- Disables CSRF (for a pure API)
- Enables CORS for your frontends
- Makes the app stateless (good for JWT)
- Requires **auth** for everything except explicitly allowed paths
- Uses Supabase JWTs via the `JwtDecoder` bean

No custom `JwtAuthenticationFilter` needed; let Spring do the heavy lifting.

---

## 5. Example: Authenticated Controller Using Supabase JWT

Create: `AuthTestController.java`

```java
package com.yourapp.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthTestController {

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "sub", jwt.getSubject(),          // Supabase user ID
                "email", jwt.getClaim("email"),   // email claim (if present)
                "claims", jwt.getClaims()         // all claims for debugging
        );
    }
}
```

If the token is valid, the `Jwt` will be injected and your `/api/me` endpoint will return info about the user.

---

## 6. Example: Per-User Entity (Note)

This shows how to tie data to the Supabase user (`sub`). You can adapt it to `BallisticProfile`, `ShotSession`, etc.

### 6.1 Entity

```java
package com.yourapp.note;

import jakarta.persistence.*;

@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ownerId; // Supabase user id (JWT "sub")

    private String title;

    @Column(length = 2000)
    private String content;

    // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
```

### 6.2 Repository

```java
package com.yourapp.note;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByOwnerId(String ownerId);
}
```

### 6.3 DTO

```java
package com.yourapp.note;

public record CreateNoteRequest(
        String title,
        String content
) {}
```

### 6.4 Controller

```java
package com.yourapp.note;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteRepository noteRepository;

    @PostMapping
    public Note createNote(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateNoteRequest request
    ) {
        String userId = jwt.getSubject();

        Note note = new Note();
        note.setOwnerId(userId);
        note.setTitle(request.title());
        note.setContent(request.content());

        return noteRepository.save(note);
    }

    @GetMapping
    public List<Note> myNotes(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return noteRepository.findByOwnerId(userId);
    }
}
```

Because of `SecurityConfiguration`, `/api/notes/**` endpoints are **protected**: they require a valid Supabase JWT.

---

## 7. How to Test the Flow with Postman

### 7.1 Sign up (via Supabase)

Either use:

- Supabase Dashboard → **Authentication → Users → Add User**, or
- POST request to Supabase `/auth/v1/signup` from your frontend / Postman.

Make sure the user is **verified** (either via email or by disabling “Confirm email” in Supabase settings for dev).

---

### 7.2 Get an Access Token from Supabase

**POST**

```http
https://<PROJECT_REF>.supabase.co/auth/v1/token?grant_type=password
```

**Headers:**

```text
apikey: <YOUR_ANON_PUBLIC_KEY>
Content-Type: application/json
```

**Body (JSON):**

```json
{
  "email": "your_user_email@example.com",
  "password": "your_password"
}
```

Response will include:

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "bearer",
  "expires_in": 3600,
  "refresh_token": "...",
  "user": { ... }
}
```

Copy the `access_token`.

---

### 7.3 Call Spring Boot `/api/me`

In Postman:

- **GET** `http://localhost:8080/api/me`
- Auth → Type: **Bearer Token**
- Token: `<PASTE_ACCESS_TOKEN>`

If configured correctly, you’ll get JSON with `sub`, `email`, and all claims.

If you remove the token → you should get **401 Unauthorized**.

---

### 7.4 Call a protected resource (`/api/notes`)

**Create Note:**

- **POST** `http://localhost:8080/api/notes`
- Auth → Bearer Token → same token as above
- Body (JSON):

```json
{
  "title": "Zero range session",
  "content": "Testing Supabase + Spring Boot + DB"
}
```

Response example:

```json
{
  "id": 1,
  "ownerId": "1e39d2e9-5240-449f-8f19-3afe4b9a3ef2",
  "title": "Zero range session",
  "content": "Testing Supabase + Spring Boot + DB"
}
```

**List Notes:**

- **GET** `http://localhost:8080/api/notes`
- Same Bearer token

You’ll get all notes for this Supabase user (`ownerId = sub`).

---

## 8. Summary Checklist (Copy-Paste For Future Projects)

1. **Create Supabase project**
   - Grab Project URL + anon key
2. **Configure DB in Supabase**
   - Get Postgres connection info
3. **Create Spring Boot project**
   - Add Web, Security, OAuth2 Resource Server, JPA, Postgres deps
4. **Create `.env`**
   - `SPRING_DATASOURCE_URL`, username, password
   - `SUPABASE_PROJECT_URL`, `SUPABASE_JWT_AUDIENCE`
5. **Configure `application.properties`**
   - Point datasource to Supabase Postgres
   - Load `.env`
6. **Create `JwtConfig`**
   - Use `project-url/auth/v1/jwks`
   - Validate `iss` and `aud`
7. **Create `SecurityConfiguration`**
   - Stateless, CORS, JWT resource server
   - Protect `/api/**`
8. **Create test controller `/api/me`**
   - Inject `Jwt` with `@AuthenticationPrincipal`
9. **Create an entity with `ownerId = jwt.getSubject()`**
   - Hook it up with JPA
10. **Test with Postman**
    - Get Supabase access token with password grant
    - Call your Spring Boot endpoints with `Authorization: Bearer <token>`

Once this is all working, you can replace the `Note` example with your real domain models (`BallisticProfile`, `ShotSession`, etc.) and you’re production-ready.
