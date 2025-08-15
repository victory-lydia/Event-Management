public class Workshop extends Event {
    private int duration; // in hours

    public Workshop(String eventId, String title, String description, String date,
                   String venue, int capacity, String organizerId, int duration) {
        super(eventId, title, description, date, venue, capacity, organizerId);
        this.duration = duration;
    }

    @Override
    public String getEventType() { return "WORKSHOP"; }

    @Override
    public double calculateCost() {
        return 200.0 + (duration * 50.0);
    }

    public int getDuration() { return duration; }
}