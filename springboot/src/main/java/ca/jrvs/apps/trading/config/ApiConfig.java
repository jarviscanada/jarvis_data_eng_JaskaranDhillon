package ca.jrvs.apps.trading.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiConfig {
    @Value("${API_KEY}") // Use a default value if not found
    private String eodApiKey;

    public String getMyEnvVariable() {
        return eodApiKey;
    }
}
