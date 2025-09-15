package com.example.security;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    private static final int WINDOW_SECONDS = 10;
    private static final int MAX_REQUESTS = 50;

    static class Counter {
        AtomicInteger count = new AtomicInteger(0);
        volatile long windowStartSec = Instant.now().getEpochSecond();
    }

    private final Map<String, Counter> counters = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String ip = getClientIp(request);
        Counter c = counters.computeIfAbsent(ip, k -> new Counter());
        long now = Instant.now().getEpochSecond();

        synchronized (c) {
            if (now - c.windowStartSec >= WINDOW_SECONDS) {
                c.windowStartSec = now;
                c.count.set(0);
            }
            if (c.count.incrementAndGet() > MAX_REQUESTS) {
                response.setContentType("text/plain");
                response.getWriter().write("Too many requests. Slow down.");
                response.getWriter().flush();
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isEmpty()) {
            return xff.split(",")[0].trim();
        }
        String remoteAddr = req.getRemoteAddr();
        return remoteAddr != null ? remoteAddr : "unknown";
    }
}
