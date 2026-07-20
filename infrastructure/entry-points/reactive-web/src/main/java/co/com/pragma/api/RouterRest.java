package co.com.pragma.api;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
        @Bean
        @RouterOperations({
                        @RouterOperation(path = "/api/v1/technologies", method = {
                                        RequestMethod.POST }, beanClass = Handler.class, beanMethod = "listenRegisterTechnology"),
                        @RouterOperation(path = "/api/v1/technologies/existence-check", method = {
                                        RequestMethod.POST }, beanClass = Handler.class, beanMethod = "listenCheckTechnologiesExistence")
        })
        public RouterFunction<ServerResponse> technologyRouterFunction(Handler handler) {
                return route(POST("/api/v1/technologies"), handler::listenRegisterTechnology)
                                .andRoute(POST("/api/v1/technologies/existence-check"), handler::listenCheckTechnologiesExistence);
        }
}
