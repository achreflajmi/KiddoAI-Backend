
package com.example.kiddoai.Config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoConfig {

    @Bean
    public MongoTemplate mongoTemplate() {
        Dotenv dotenv = Dotenv.load();
        String uri = dotenv.get("MONGODB_URI");
        return new MongoTemplate(MongoClients.create(uri), "KiddoAI");
    }
}
