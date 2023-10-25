package config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Configuration
@Component
@PropertySource("application.yml")
public class BotConfig {
    @Value("${bot.username}")
    String botName;
    @Value("${bot.token}")
    String token;
}
