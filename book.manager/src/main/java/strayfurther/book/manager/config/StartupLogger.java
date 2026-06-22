package strayfurther.book.manager.config;

import org.springframework.context.ApplicationListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class StartupLogger implements ApplicationListener<ApplicationReadyEvent> {

    private final Environment env;

    public StartupLogger(Environment env) {
        this.env = env;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        System.out.println("Effective spring.graphql.path=" + env.getProperty("spring.graphql.path"));
        System.out.println("Effective spring.mvc.servlet.path=" + env.getProperty("spring.mvc.servlet.path"));
        System.out.println("ENV SPRING_GRAPHQL_PATH=" + System.getenv("SPRING_GRAPHQL_PATH"));
        System.out.println("ENV SPRING_MVC_SERVLET_PATH=" + System.getenv("SPRING_MVC_SERVLET_PATH"));
    }
}