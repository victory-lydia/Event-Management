import java.io.*;
import java.util.ArrayList;

public class FileHandler {
    private static final String USERS_FILE = "users.txt";
    private static final String EVENTS_FILE = "events.txt";
    private static final String REGISTRATIONS_FILE = "registrations.txt";

    public static void saveUsers(ArrayList<Person> users) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (Person user : users) {
                writer.println(user.getId() + "," + user.getName() + "," +
                        user.getEmail() + "," + user.getPhone() + "," + user.getRole());
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    public static ArrayList<Person> loadUsers() {
        ArrayList<Person> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String id = parts[0], name = parts[1], email = parts[2], phone = parts[3], role = parts[4];
                    switch (role) {
                        case "ADMIN":
                            users.add(new Admin(id, name, email, phone));
                            break;
                        case "ORGANIZER":
                            users.add(new Organizer(id, name, email, phone));
                            break;
                        case "ATTENDEE":
                            users.add(new Attendee(id, name, email, phone));
                            break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Users file not found. Starting with empty database.");
        }
        return users;
    }

    public static void saveEvents(ArrayList<Event> events) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(EVENTS_FILE))) {
            for (Event event : events) {
                writer.println(event.getEventId() + "," + event.getTitle() + "," +
                        event.getDescription() + "," + event.getDate() + "," +
                        event.getVenue() + "," + event.getCapacity() + "," +
                        event.getRegisteredCount() + "," + event.getOrganizerId() + "," +
                        event.getEventType());
            }
        } catch (IOException e) {
            System.err.println("Error saving events: " + e.getMessage());
        }
    }

    public static ArrayList<Event> loadEvents() {
        ArrayList<Event> events = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(EVENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 9) {
                    String eventId = parts[0], title = parts[1], description = parts[2],
                            date = parts[3], venue = parts[4];
                    int capacity = Integer.parseInt(parts[5]);
                    int registeredCount = Integer.parseInt(parts[6]);
                    String organizerId = parts[7], eventType = parts[8];

                    Event event = null;
                    switch (eventType) {
                        case "CONFERENCE":
                            event = new Conference(eventId, title, description, date, venue,
                                    capacity, organizerId, 5); // Default speakers
                            break;
                        case "WORKSHOP":
                            event = new Workshop(eventId, title, description, date, venue,
                                    capacity, organizerId, 8); // Default duration
                            break;
                        case "CONCERT":
                            event = new Concert(eventId, title, description, date, venue,
                                    capacity, organizerId, "Unknown Artist");
                            break;
                    }

                    if (event != null) {
                        for (int i = 0; i < registeredCount; i++) {
                            event.incrementRegistration();
                        }
                        events.add(event);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Events file not found. Starting with empty database.");
        }
        return events;
    }

    public static void saveRegistrations(ArrayList<Registration> registrations) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(REGISTRATIONS_FILE))) {
            for (Registration reg : registrations) {
                writer.println(reg.getRegistrationId() + "," + reg.getUserId() + "," +
                        reg.getEventId() + "," + reg.getRegistrationDate() + "," +
                        reg.getStatus());
            }
        } catch (IOException e) {
            System.err.println("Error saving registrations: " + e.getMessage());
        }
    }

    public static ArrayList<Registration> loadRegistrations() {
        ArrayList<Registration> registrations = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(REGISTRATIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    registrations.add(new Registration(parts[0], parts[1], parts[2], parts[3], parts[4]));
                }
            }
        } catch (IOException e) {
            System.out.println("Registrations file not found. Starting with empty database.");
        }
        return registrations;
    }
}