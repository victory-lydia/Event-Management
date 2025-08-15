public class Conference extends Event {
    private int numberOfSpeakers;

    public Conference(String eventId, String title, String description, String date,
                     String venue, int capacity, String organizerId, int numberOfSpeakers) {
        super(eventId, title, description, date, venue, capacity, organizerId);
        this.numberOfSpeakers = numberOfSpeakers;
    }

    @Override
    public String getEventType() { return "CONFERENCE"; }

    @Override
    public double calculateCost() {
        return 500.0 + (numberOfSpeakers * 100.0);
    }

    public int getNumberOfSpeakers() { return numberOfSpeakers; }
}