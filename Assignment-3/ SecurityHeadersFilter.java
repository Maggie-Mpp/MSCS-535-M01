// SecurityHeadersFilter.java
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecurityHeadersFilter implements Filter {
  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    HttpServletResponse resp = (HttpServletResponse) res;

    // Enforce HTTPS (HSTS) – only enable after you serve HTTPS correctly
    resp.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
    // Prevent MIME sniffing
    resp.setHeader("X-Content-Type-Options", "nosniff");
    // Clickjacking protection
    resp.setHeader("X-Frame-Options", "DENY");
    // Basic XSS protection via CSP (adjust to your app’s needs)
    resp.setHeader("Content-Security-Policy",
        "default-src 'self'; object-src 'none'; base-uri 'self'; frame-ancestors 'none'; " +
        "img-src 'self' data:; script-src 'self'; style-src 'self' 'unsafe-inline'");
    // Limit referrer info
    resp.setHeader("Referrer-Policy", "no-referrer");
    // Reduce exposed browser features
    resp.setHeader("Permissions-Policy",
        "geolocation=(), microphone=(), camera=(), usb=(), payment=()");

    chain.doFilter(req, res);
  }
}
