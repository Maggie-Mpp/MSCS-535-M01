// RateLimitingFilter.java
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Very simple sliding-window rate limiter: N requests per IP per WINDOW_SECONDS.
 * For production, consider Bucket4j or Redis-backed rate limits.
 */
public class RateLimitingFilter implements Filter {
  private static final int WINDOW_SECONDS = 10;
  private static final int MAX_REQUESTS = 50;

  static class Counter {
    AtomicInteger count = new AtomicInteger(0);
    volatile long windowStartSec = Instant.now().getEpochSecond();
  }

  private final Map<String, Counter> counters = new ConcurrentHashMap<>();

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    String ip = getClientIp(req);

    Counter c = counters.computeIfAbsent(ip, k -> new Counter());
    long now = Instant.now().getEpochSecond();

    synchronized (c) {
      if (now - c.windowStartSec >= WINDOW_SECONDS) {
        c.windowStartSec = now;
        c.count.set(0);
      }
      if (c.count.incrementAndGet() > MAX_REQUESTS) {
        // 429 Too Many Requests
        response.setContentType("text/plain");
        response.getWriter().write("Too many requests. Slow down.");
        response.getWriter().flush();
        return;
      }
    }

    chain.doFilter(request, response);
  }

  private String getClientIp(HttpServletRequest req) {
    String xff = req.getHeader("X-Forwarded-For");
    if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();
    String xri = req.getHeader("X-Real-IP");
    if (xri != null && !xri.isBlank()) return xri;
    return req.getRemoteAddr();
  }
}
