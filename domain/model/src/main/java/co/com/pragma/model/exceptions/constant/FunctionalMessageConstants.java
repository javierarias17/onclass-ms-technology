package co.com.pragma.model.exceptions.constant;

public final class FunctionalMessageConstants {

    private FunctionalMessageConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String BUSINESS_VALIDATION_FAILED = "Business validation failed";
    public static final String TECHNOLOGY_ALREADY_EXISTS = "Technology name already exists";
}
