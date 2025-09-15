# Assignment-3

## Application Layer Security Demo (Spring Boot)

This project demonstrates protection of the Application Layer (Layer 7) in the OSI model using Java and Spring Boot. It includes:
- Security headers (see `SecurityHeadersFilter.java`)
- Rate limiting (see `RateLimitingFilter.java`)
- Safe controller (`HelloController.java`)

### How to Run

1. Make sure you have Maven and Java 17+ installed.
2. Build and run the Spring Boot app:
   ```sh
   mvn clean package
   mvn spring-boot:run
   ```
3. Open [http://localhost:8080/](http://localhost:8080/) in your browser. You should see:
   `Hello, secure world!`
4. Inspect response headers in Chrome DevTools → Network → Headers. You should see:
   - `X-Content-Type-Options: nosniff`
   - `X-Frame-Options: DENY`
   - `X-XSS-Protection: 1; mode=block`
   - `Strict-Transport-Security: max-age=31536000; includeSubDomains`
   - `Content-Security-Policy: default-src 'self'`

### Test Rate Limiting
Run this in your terminal:
```sh
for i in {1..60}; do curl -s http://localhost:8080/; done
```
After ~50 requests in 10 seconds, you should see:
`Too many requests. Slow down.`

---

## One-Time Pad Encryption

The `OneTimePad.java` file demonstrates one-time pad encryption for the text `MY NAME IS UNKNOWN`.

### How to Run

1. Compile and run:
   ```sh
   javac Assignment-3/OneTimePad.java
   java -cp Assignment-3 OneTimePad
   ```
2. Output will show:
   - Plaintext
   - Random key
   - Ciphertext
   - Decrypted text

---

## Files
- `Assignment-3/src/com/example/security/HelloController.java`: Main endpoint
- `Assignment-3/src/com/example/security/SecurityHeadersFilter.java`: Security headers filter
- `Assignment-3/src/com/example/security/RateLimitingFilter.java`: Rate limiting filter
- `Assignment-3/src/com/example/security/SecurityDemoApplication.java`: Spring Boot main class
- `Assignment-3/OneTimePad.java`: One-time pad encryption demo
