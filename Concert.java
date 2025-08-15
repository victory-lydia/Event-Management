public class Concert extends Event {
    private String artist;

    public Concert(String eventId, String title, String description, String date,
                  String venue, int capacity, String organizerId, String artist) {
        super(eventId, title, description, date, venue, capacity, organizerId);
        this.artist = artist;
    }

    @Override
    public String getEventType() { return "CONCERT"; }

    @Override
    public double calculateCost() {
        return 1000.0;
    }

    public String getArtist() { return artist; }
}