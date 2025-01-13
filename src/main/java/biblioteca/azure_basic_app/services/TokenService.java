package biblioteca.azure_basic_app.services;


import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

@Service
public class TokenService {
    private Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    public void invalidateToken(String token) {
        blacklistedTokens.add(token);
    }

    public boolean isTokenInvalidated(String token) {
        return blacklistedTokens.contains(token);
    }
}
