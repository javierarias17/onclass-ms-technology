package co.com.pragma.api;

import co.com.pragma.api.constants.PathVariableConstants;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
        @Bean
        @RouterOperations({
                        @RouterOperation(path = "/api/v1/technologies", method = {
                                        RequestMethod.POST }, beanClass = Handler.class, beanMethod = "listenRegisterTechnology"),
                        @RouterOperation(path = "/api/v1/technologies/existence-check", method = {
                                        RequestMethod.POST }, beanClass = Handler.class, beanMethod = "listenCheckTechnologiesExistence"),
                        @RouterOperation(path = "/api/v1/capability-technologies", method = {
                                        RequestMethod.POST }, beanClass = Handler.class, beanMethod = "listenLinkCapabilityTechnologies"),
                        @RouterOperation(path = "/api/v1/capability-technologies/{" + PathVariableConstants.CAPABILITY_ID + "}", method = {
                                        RequestMethod.DELETE }, beanClass = Handler.class, beanMethod = "listenDeleteCapabilityTechnologies"),
                        @RouterOperation(path = "/api/v1/capability-technologies/by-capability-ids", method = {
                                        RequestMethod.POST }, beanClass = Handler.class, beanMethod = "listenFindTechnologiesByCapabilityIds")
        })
        public RouterFunction<ServerResponse> technologyRouterFunction(Handler handler) {
                return route(POST("/api/v1/technologies"), handler::listenRegisterTechnology)
                                .andRoute(POST("/api/v1/technologies/existence-check"), handler::listenCheckTechnologiesExistence)
                                .andRoute(POST("/api/v1/capability-technologies"), handler::listenLinkCapabilityTechnologies)
                                .andRoute(DELETE("/api/v1/capability-technologies/{" + PathVariableConstants.CAPABILITY_ID + "}"), handler::listenDeleteCapabilityTechnologies)
                                .andRoute(POST("/api/v1/capability-technologies/by-capability-ids"), handler::listenFindTechnologiesByCapabilityIds);
        }
}
