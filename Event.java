public abstract class Event {
    private String eventId;
    private String title;
    private String description;
    private String date;
    private String venue;
    private int capacity;
    private int registeredCount;
    private String organizerId;

    public Event(String eventId, String title, String description, String date,
                 String venue, int capacity, String organizerId) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.venue = venue;
        this.capacity = capacity;
        this.registeredCount = 0;
        this.organizerId = organizerId;
    }

    public abstract String getEventType();
    public abstract double calculateCost();

    // Getters & Setters
    public String getEventId() { return eventId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getVenue() { return venue; }
    public int getCapacity() { return capacity; }
    public int getRegisteredCount() { return registeredCount; }
    public String getOrganizerId() { return organizerId; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDate(String date) { this.date = date; }
    public void setVenue(String venue) { this.venue = venue; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public boolean incrementRegistration() {
        if (registeredCount < capacity) {
            registeredCount++;
            return true;
        }
        return false;
    }

    public void decrementRegistration() {
        if (registeredCount > 0) {
            registeredCount--;
        }
    }
}