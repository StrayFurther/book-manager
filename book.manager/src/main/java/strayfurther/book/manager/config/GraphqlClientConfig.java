package strayfurther.book.manager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GraphqlClientConfig {

    @Value("${graphql.client.url:}")
    private String graphqlUrl;

    @Value("${graphql.client.admin-secret:}")
    private String adminSecret;

    @Bean
    public WebClient graphqlWebClient() {
        WebClient.Builder builder = WebClient.builder();
        if (graphqlUrl != null && !graphqlUrl.isBlank()) {
            builder = builder.baseUrl(graphqlUrl);
        }
        if (adminSecret != null && !adminSecret.isBlank()) {
            builder = builder.defaultHeader("x-hasura-admin-secret", adminSecret);
        }
        return builder.build();
    }
}