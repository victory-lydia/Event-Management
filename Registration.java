public class Registration {
    private String registrationId;
    private String userId;
    private String eventId;
    private String registrationDate;
    private String status;

    public Registration(String registrationId, String userId, String eventId,
                       String registrationDate, String status) {
        this.registrationId = registrationId;
        this.userId = userId;
        this.eventId = eventId;
        this.registrationDate = registrationDate;
        this.status = status;
    }

    // Getters & Setters
    public String getRegistrationId() { return registrationId; }
    public String getUserId() { return userId; }
    public String getEventId() { return eventId; }
    public String getRegistrationDate() { return registrationDate; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("RegID: %s, UserID: %s, EventID: %s, Date: %s, Status: %s",
                registrationId, userId, eventId, registrationDate, status);
    }
}