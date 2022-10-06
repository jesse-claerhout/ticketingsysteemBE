package be.optis.opticketapi.models;

public enum Role {
    USER("ROLE_USER"),
    HANDYMAN("ROLE_HANDYMAN");

    private final String springSecurityRole;

    Role(String key) {
        this.springSecurityRole = key;
    }

    public String getSpringSecurityRole() { return springSecurityRole; }
}
