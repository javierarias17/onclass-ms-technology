package co.com.pragma.model.common;

public final class ValidationMessageConstants {

    private ValidationMessageConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String MSG_NAME_REQUIRED = "Technology name is required";
    public static final String MSG_NAME_MAX_LENGTH = "Technology name must not exceed %d characters";
    public static final String MSG_DESCRIPTION_REQUIRED = "Technology description is required";
    public static final String MSG_DESCRIPTION_MAX_LENGTH = "Technology description must not exceed %d characters";
    public static final String MSG_TECHNOLOGY_IDS_REQUIRED = "Technology ids list is required and must not be empty";
    public static final String MSG_CAPABILITY_ID_REQUIRED = "Capability id is required";
}
