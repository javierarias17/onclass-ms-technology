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

        private static final String TECHNOLOGIES_PATH = "/api/v1/technologies";
        private static final String TECHNOLOGIES_EXISTENCE_CHECK_PATH = TECHNOLOGIES_PATH + "/existence-check";
        private static final String CAPABILITY_TECHNOLOGIES_PATH = "/api/v1/capability-technologies";
        private static final String CAPABILITY_TECHNOLOGIES_BY_ID_PATH = CAPABILITY_TECHNOLOGIES_PATH
                        + "/{" + PathVariableConstants.CAPABILITY_ID + "}";
        private static final String CAPABILITY_TECHNOLOGIES_BY_CAPABILITY_IDS_PATH = CAPABILITY_TECHNOLOGIES_PATH
                        + "/by-capability-ids";

        @Bean
        @RouterOperations({
                        @RouterOperation(path = TECHNOLOGIES_PATH, method = {
                                        RequestMethod.POST }, beanClass = Handler.class, beanMethod = "listenRegisterTechnology"),
                        @RouterOperation(path = TECHNOLOGIES_EXISTENCE_CHECK_PATH, method = {
                                        RequestMethod.POST }, beanClass = Handler.class, beanMethod = "listenCheckTechnologiesExistence"),
                        @RouterOperation(path = CAPABILITY_TECHNOLOGIES_PATH, method = {
                                        RequestMethod.POST }, beanClass = Handler.class, beanMethod = "listenLinkCapabilityTechnologies"),
                        @RouterOperation(path = CAPABILITY_TECHNOLOGIES_BY_ID_PATH, method = {
                                        RequestMethod.DELETE }, beanClass = Handler.class, beanMethod = "listenDeleteCapabilityTechnologies"),
                        @RouterOperation(path = CAPABILITY_TECHNOLOGIES_BY_CAPABILITY_IDS_PATH, method = {
                                        RequestMethod.POST }, beanClass = Handler.class, beanMethod = "listenFindTechnologiesByCapabilityIds")
        })
        public RouterFunction<ServerResponse> technologyRouterFunction(Handler handler) {
                return route(POST(TECHNOLOGIES_PATH), handler::listenRegisterTechnology)
                                .andRoute(POST(TECHNOLOGIES_EXISTENCE_CHECK_PATH),
                                                handler::listenCheckTechnologiesExistence)
                                .andRoute(POST(CAPABILITY_TECHNOLOGIES_PATH), handler::listenLinkCapabilityTechnologies)
                                .andRoute(DELETE(CAPABILITY_TECHNOLOGIES_BY_ID_PATH),
                                                handler::listenDeleteCapabilityTechnologies)
                                .andRoute(POST(CAPABILITY_TECHNOLOGIES_BY_CAPABILITY_IDS_PATH),
                                                handler::listenFindTechnologiesByCapabilityIds);
        }
}
