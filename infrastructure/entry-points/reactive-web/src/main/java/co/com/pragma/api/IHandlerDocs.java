package co.com.pragma.api;

import co.com.pragma.api.constants.PathVariableConstants;
import co.com.pragma.api.dto.CapabilityTechnologyLinkInDto;
import co.com.pragma.api.dto.CapabilityTechnologyLinkOutDto;
import co.com.pragma.api.dto.TechnologyExistenceInDto;
import co.com.pragma.api.dto.TechnologyExistenceOutDto;
import co.com.pragma.api.dto.TechnologyInDto;
import co.com.pragma.api.dto.TechnologyOutDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface IHandlerDocs {

    @Operation(
            operationId = "listenRegisterTechnology",
            summary = "Register a technology",
            description = "Creates a new technology. The name must be unique.",
            tags = { "Technologies" },
            requestBody = @RequestBody(
                    description = "Input data",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TechnologyInDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "name": "Java",
                                      "description": "Object-oriented programming language"
                                    }
                                    """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TechnologyOutDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "name": "Java",
                                      "description": "Object-oriented programming language"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "Business validation failed",
                                      "errors": [
                                        {
                                          "field": "name",
                                          "message": "Technology name is required"
                                        },
                                        {
                                          "field": "description",
                                          "message": "Technology description is required"
                                        }
                                      ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "Business validation failed",
                                      "errors": [
                                        {
                                          "field": "name",
                                          "message": "Technology name already exists"
                                        }
                                      ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "An unexpected error occurred. Please contact the administrator."
                                    }
                                    """)))
    })
    Mono<ServerResponse> listenRegisterTechnology(ServerRequest serverRequest);

    @Operation(
            operationId = "listenCheckTechnologiesExistence",
            summary = "Check technologies existence",
            description = "Given a list of technology ids, returns the ids that do not exist. An empty list means all of them exist.",
            tags = { "Technologies" },
            requestBody = @RequestBody(
                    description = "Input data",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TechnologyExistenceInDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "technologyIds": [1, 2, 3]
                                    }
                                    """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TechnologyExistenceOutDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "missingIds": [3]
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "Business validation failed",
                                      "errors": [
                                        {
                                          "field": "technologyIds",
                                          "message": "Technology ids list is required and must not be empty"
                                        }
                                      ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "An unexpected error occurred. Please contact the administrator."
                                    }
                                    """)))
    })
    Mono<ServerResponse> listenCheckTechnologiesExistence(ServerRequest serverRequest);

    @Operation(
            operationId = "listenLinkCapabilityTechnologies",
            summary = "Link technologies to a capability",
            description = "Persists the relation between a capability and its technologies. All technology ids must exist.",
            tags = { "Technologies" },
            requestBody = @RequestBody(
                    description = "Input data",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CapabilityTechnologyLinkInDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "capabilityId": 10,
                                      "technologyIds": [1, 2, 3]
                                    }
                                    """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CapabilityTechnologyLinkOutDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "capabilityId": 10,
                                      "technologyIds": [1, 2, 3]
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Invalid input", value = """
                                            {
                                              "message": "Business validation failed",
                                              "errors": [
                                                {
                                                  "field": "capabilityId",
                                                  "message": "Capability id is required"
                                                },
                                                {
                                                  "field": "technologyIds",
                                                  "message": "Technology ids list is required and must not be empty"
                                                }
                                              ]
                                            }
                                            """),
                                    @ExampleObject(name = "Technologies not found", value = """
                                            {
                                              "message": "Business validation failed",
                                              "errors": [
                                                {
                                                  "field": "technologyIds",
                                                  "message": "The following technology ids do not exist: [3]"
                                                }
                                              ]
                                            }
                                            """)
                            })),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "An unexpected error occurred. Please contact the administrator."
                                    }
                                    """)))
    })
    Mono<ServerResponse> listenLinkCapabilityTechnologies(ServerRequest serverRequest);

    @Operation(
            operationId = "listenDeleteCapabilityTechnologies",
            summary = "Delete all technology links for a capability",
            description = "Removes every capability-technology relation for the given capability id. "
                    + "Used to clean up an incomplete previous registration attempt before retrying.",
            tags = { "Technologies" },
            parameters = @Parameter(name = PathVariableConstants.CAPABILITY_ID, in = ParameterIn.PATH, required = true,
                    schema = @Schema(type = "integer", format = "int64")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "Business validation failed",
                                      "errors": [
                                        {
                                          "field": "capabilityId",
                                          "message": "Capability id must be numeric"
                                        }
                                      ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "An unexpected error occurred. Please contact the administrator."
                                    }
                                    """)))
    })
    Mono<ServerResponse> listenDeleteCapabilityTechnologies(ServerRequest serverRequest);
}
