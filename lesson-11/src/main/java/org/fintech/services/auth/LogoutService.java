package org.fintech.services.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class LogoutService {
    private final Set<String> invalidatedTokens = ConcurrentHashMap.newKeySet();
    private final JwtService jwtService;

    public void invalidateToken(String token) {
        invalidatedTokens.add(token);
    }
    public boolean isTokenInvalidated(String token) {
        return invalidatedTokens.contains(token);
    }

    @Scheduled(fixedRate = 86400)
    private void cleanOldTokens() {
        Iterator<String> iterator = invalidatedTokens.iterator();
        while (iterator.hasNext()) {
            String token = iterator.next();
            if (jwtService.isTokenExpired(token)) {
                iterator.remove();
            }
        }
    }
}
