import exceptions.DuplicateRegistrationException;
import exceptions.EventCapacityExceededException;
import exceptions.InvalidDateException;
import exceptions.UserNotFoundException;
import java.time.LocalDate;
import java.util.*;

public class EventManagementSystem {
    private ArrayList<Person> users;
    private ArrayList<Event> events;
    private ArrayList<Registration> registrations;
    private HashMap<String, Person> userMap;
    private HashMap<String, Event> eventMap;
    private Scanner scanner;
    private Person currentUser;

    public EventManagementSystem() {
        users = FileHandler.loadUsers();
        events = FileHandler.loadEvents();
        registrations = FileHandler.loadRegistrations();
        userMap = new HashMap<>();
        eventMap = new HashMap<>();
        scanner = new Scanner(System.in);

        // Populate hash maps
        for (Person user : users) {
            userMap.put(user.getId(), user);
        }
        for (Event event : events) {
            eventMap.put(event.getEventId(), event);
        }
    }

    public static String generateId(String prefix) {
        return prefix + System.currentTimeMillis();
    }

    public static boolean validateEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    public static boolean validateDate(String date) {
        if (date == null || date.length() != 10) return false;
        String[] parts = date.split("-");
        if (parts.length != 3) return false;
        try {
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);
            return day >= 1 && day <= 31 && month >= 1 && month <= 12 && year >= 2023;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public Person searchUserById(String userId) throws UserNotFoundException {
        Person user = userMap.get(userId);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + userId + " not found.");
        }
        return user;
    }

    public Event[] searchEventsByDate(String date) {
        ArrayList<Event> matchingEvents = new ArrayList<>();
        for (Event event : events) {
            if (event.getDate().equals(date)) {
                matchingEvents.add(event);
            }
        }
        return matchingEvents.toArray(new Event[0]);
    }

    public void sortEventsByDate() {
        events.sort(Comparator.comparing(Event::getDate));
    }

    public void start() {
        System.out.println("=== EVENT MANAGEMENT SYSTEM ===");
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showLoginMenu() {
        System.out.println("\n1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Enter choice: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    register();
                    break;
                case 3:
                    saveAllData();
                    System.out.println("Thank you for using Event Management System!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } catch (InputMismatchException e) {
            System.out.println("Please enter a valid number!");
            scanner.nextLine(); // Consume invalid input
        }
    }

    private void showMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("Welcome, " + currentUser.getName() + " (" + currentUser.getRole() + ")");

        String[] permissions = currentUser.getPermissions();

        System.out.println("1. View All Events");
        System.out.println("2. Search Events");
        if (Arrays.asList(permissions).contains("REGISTER_EVENT")) {
            System.out.println("3. Register for Event");
            System.out.println("4. View My Registrations");
        }
        if (Arrays.asList(permissions).contains("CREATE_EVENT")) {
            System.out.println("5. Create Event");
            System.out.println("6. Manage My Events");
        }
        if (Arrays.asList(permissions).contains("MANAGE_USERS")) {
            System.out.println("7. Manage Users");
            System.out.println("8. View Reports");
        }
        System.out.println("9. Logout");
        System.out.print("Enter choice: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine();
            handleMenuChoice(choice);
        } catch (InputMismatchException e) {
            System.out.println("Please enter a valid number!");
            scanner.nextLine();
        }
    }

    private void handleMenuChoice(int choice) {
        try {
            switch (choice) {
                case 1:
                    viewAllEvents();
                    break;
                case 2:
                    searchEvents();
                    break;
                case 3:
                    if (currentUser.getRole().equals("ATTENDEE")) {
                        registerForEvent();
                    }
                    break;
                case 4:
                    if (currentUser.getRole().equals("ATTENDEE")) {
                        viewMyRegistrations();
                    }
                    break;
                case 5:
                    if (currentUser.getRole().equals("ORGANIZER") || currentUser.getRole().equals("ADMIN")) {
                        createEvent();
                    }
                    break;
                case 6:
                    if (currentUser.getRole().equals("ORGANIZER") || currentUser.getRole().equals("ADMIN")) {
                        manageMyEvents();
                    }
                    break;
                case 7:
                    if (currentUser.getRole().equals("ADMIN")) {
                        manageUsers();
                    }
                    break;
                case 8:
                    if (currentUser.getRole().equals("ADMIN")) {
                        viewReports();
                    }
                    break;
                case 9:
                    logout();
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void login() {
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine();

        try {
            currentUser = searchUserById(userId);
            System.out.println("Login successful! Welcome " + currentUser.getName());
        } catch (UserNotFoundException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void register() {
        try {
            System.out.print("Enter Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter Email: ");
            String email = scanner.nextLine();
            if (!validateEmail(email)) {
                throw new IllegalArgumentException("Invalid email format!");
            }

            System.out.print("Enter Phone: ");
            String phone = scanner.nextLine();

            System.out.println("Select Role:");
            System.out.println("1. Attendee");
            System.out.println("2. Organizer");
            System.out.print("Enter choice: ");

            int roleChoice = scanner.nextInt();
            scanner.nextLine();

            String userId = generateId("U");
            Person newUser;

            switch (roleChoice) {
                case 1:
                    newUser = new Attendee(userId, name, email, phone);
                    break;
                case 2:
                    newUser = new Organizer(userId, name, email, phone);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid role selection!");
            }

            users.add(newUser);
            userMap.put(userId, newUser);
            FileHandler.saveUsers(users);

            System.out.println("Registration successful! Your User ID is: " + userId);
        } catch (Exception e) {
            System.err.println("Registration failed: " + e.getMessage());
        }
    }

    private void logout() {
        saveAllData();
        currentUser = null;
        System.out.println("Logged out successfully!");
    }

    private void saveAllData() {
        FileHandler.saveUsers(users);
        FileHandler.saveEvents(events);
        FileHandler.saveRegistrations(registrations);
    }

    private void viewAllEvents() {
        if (events.isEmpty()) {
            System.out.println("No events available.");
            return;
        }

        System.out.println("\n=== ALL EVENTS ===");
        for (Event event : events) {
            displayEventDetails(event);
        }
    }

    private void displayEventDetails(Event event) {
        System.out.println("----------------------------------------");
        System.out.println("Event ID: " + event.getEventId());
        System.out.println("Title: " + event.getTitle());
        System.out.println("Type: " + event.getEventType());
        System.out.println("Description: " + event.getDescription());
        System.out.println("Date: " + event.getDate());
        System.out.println("Venue: " + event.getVenue());
        System.out.println("Capacity: " + event.getCapacity());
        System.out.println("Available Slots: " + (event.getCapacity() - event.getRegisteredCount()));
        System.out.println("Cost: $" + event.calculateCost());
        System.out.println("----------------------------------------");
    }

    private void searchEvents() {
        System.out.print("Enter date to search (dd-mm-yyyy): ");
        String date = scanner.nextLine();

        Event[] foundEvents = searchEventsByDate(date);

        if (foundEvents.length == 0) {
            System.out.println("No events found for date: " + date);
        } else {
            System.out.println("\n=== EVENTS ON " + date + " ===");
            for (Event event : foundEvents) {
                displayEventDetails(event);
            }
        }
    }


    private void registerForEvent() {
        try {
            System.out.print("Enter Event ID to register: ");
            String eventId = scanner.nextLine();

            Event event = eventMap.get(eventId);
            if (event == null) {
                throw new UserNotFoundException("Event not found!");
            }

            for (Registration reg : registrations) {
                if (reg.getUserId().equals(currentUser.getId()) &&
                        reg.getEventId().equals(eventId) &&
                        reg.getStatus().equals("ACTIVE")) {
                    throw new DuplicateRegistrationException("Already registered for this event!");
                }
            }

            if (event.getRegisteredCount() >= event.getCapacity()) {
                throw new EventCapacityExceededException("Event is full!");
            }

            String regId = generateId("R");
            String regDate = LocalDate.now().toString();
            Registration registration = new Registration(regId, currentUser.getId(), eventId, regDate, "ACTIVE");

            registrations.add(registration);
            event.incrementRegistration();
            FileHandler.saveRegistrations(registrations);
            FileHandler.saveEvents(events);

            System.out.println("Registration successful! Registration ID: " + regId);
        } catch (Exception e) {
            System.err.println("Registration failed: " + e.getMessage());
        }
    }

    private void viewMyRegistrations() {
        ArrayList<Registration> myRegistrations = new ArrayList<>();
        for (Registration reg : registrations) {
            if (reg.getUserId().equals(currentUser.getId())) {
                myRegistrations.add(reg);
            }
        }

        if (myRegistrations.isEmpty()) {
            System.out.println("You have no registrations.");
            return;
        }

        System.out.println("\n=== MY REGISTRATIONS ===");
        for (Registration reg : myRegistrations) {
            Event event = eventMap.get(reg.getEventId());
            if (event != null) {
                System.out.println("Registration ID: " + reg.getRegistrationId());
                System.out.println("Event: " + event.getTitle());
                System.out.println("Date: " + event.getDate());
                System.out.println("Status: " + reg.getStatus());
                System.out.println("Registration Date: " + reg.getRegistrationDate());
                System.out.println("----------------------------------------");
            }
        }
    }

    private void createEvent() {
        try {
            System.out.print("Enter Event Title: ");
            String title = scanner.nextLine();

            System.out.print("Enter Description: ");
            String description = scanner.nextLine();

            System.out.print("Enter Date (dd-mm-yyyy): ");
            String date = scanner.nextLine();
            if (!validateDate(date)) {
                throw new InvalidDateException("Invalid date format or past date!");
            }

            System.out.print("Enter Venue: ");
            String venue = scanner.nextLine();

            System.out.print("Enter Capacity: ");
            int capacity = scanner.nextInt();
            scanner.nextLine();
            if (capacity <= 0) {
                throw new IllegalArgumentException("Capacity must be positive!");
            }

            System.out.println("Select Event Type:");
            System.out.println("1. Conference");
            System.out.println("2. Workshop");
            System.out.println("3. Concert");
            System.out.print("Enter choice: ");

            int typeChoice = scanner.nextInt();
            scanner.nextLine();

            String eventId = generateId("E");
            Event newEvent;

            switch (typeChoice) {
                case 1:
                    System.out.print("Enter number of speakers: ");
                    int speakers = scanner.nextInt();
                    scanner.nextLine();
                    newEvent = new Conference(eventId, title, description, date, venue, capacity, currentUser.getId(), speakers);
                    break;
                case 2:
                    System.out.print("Enter duration (hours): ");
                    int duration = scanner.nextInt();
                    scanner.nextLine();
                    newEvent = new Workshop(eventId, title, description, date, venue, capacity, currentUser.getId(), duration);
                    break;
                case 3:
                    System.out.print("Enter artist name: ");
                    String artist = scanner.nextLine();
                    newEvent = new Concert(eventId, title, description, date, venue, capacity, currentUser.getId(), artist);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid event type!");
            }

            events.add(newEvent);
            eventMap.put(eventId, newEvent);
            FileHandler.saveEvents(events);

            System.out.println("Event created successfully! Event ID: " + eventId);
        } catch (Exception e) {
            System.err.println("Event creation failed: " + e.getMessage());
        }
    }

    private void manageMyEvents() {
        ArrayList<Event> myEvents = new ArrayList<>();
        for (Event event : events) {
            if (event.getOrganizerId().equals(currentUser.getId())) {
                myEvents.add(event);
            }
        }

        if (myEvents.isEmpty()) {
            System.out.println("You have no events.");
            return;
        }

        System.out.println("\n=== MY EVENTS ===");
        for (int i = 0; i < myEvents.size(); i++) {
            System.out.println((i + 1) + ". " + myEvents.get(i).getTitle() +
                    " (ID: " + myEvents.get(i).getEventId() + ")");
        }

        System.out.print("Select event to manage (0 to go back): ");
        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice > 0 && choice <= myEvents.size()) {
                Event selectedEvent = myEvents.get(choice - 1);
                manageEvent(selectedEvent);
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input!");
            scanner.nextLine();
        }
    }

    private void manageEvent(Event event) {
        System.out.println("\nManaging Event: " + event.getTitle());
        System.out.println("1. View Event Details");
        System.out.println("2. View Attendees");
        System.out.println("3. Update Event");
        System.out.println("4. Delete Event");
        System.out.print("Enter choice: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    displayEventDetails(event);
                    break;
                case 2:
                    viewEventAttendees(event);
                    break;
                case 3:
                    updateEvent(event);
                    break;
                case 4:
                    deleteEvent(event);
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input!");
            scanner.nextLine();
        }
    }

    private void viewEventAttendees(Event event) {
        System.out.println("\n=== ATTENDEES FOR " + event.getTitle() + " ===");

        boolean hasAttendees = false;
        for (Registration reg : registrations) {
            if (reg.getEventId().equals(event.getEventId()) &&
                    reg.getStatus().equals("ACTIVE")) {
                Person attendee = userMap.get(reg.getUserId());
                if (attendee != null) {
                    System.out.println("Name: " + attendee.getName());
                    System.out.println("Email: " + attendee.getEmail());
                    System.out.println("Registration Date: " + reg.getRegistrationDate());
                    System.out.println("----------------------------------------");
                    hasAttendees = true;
                }
            }
        }

        if (!hasAttendees) {
            System.out.println("No attendees registered for this event.");
        }
    }

    private void updateEvent(Event event) {
        System.out.println("Update Event Information:");
        System.out.print("New Title (current: " + event.getTitle() + "): ");
        String newTitle = scanner.nextLine();
        if (!newTitle.trim().isEmpty()) {
            event.setTitle(newTitle);
        }

        System.out.print("New Description (current: " + event.getDescription() + "): ");
        String newDescription = scanner.nextLine();
        if (!newDescription.trim().isEmpty()) {
            event.setDescription(newDescription);
        }

        System.out.print("New Venue (current: " + event.getVenue() + "): ");
        String newVenue = scanner.nextLine();
        if (!newVenue.trim().isEmpty()) {
            event.setVenue(newVenue);
        }

        FileHandler.saveEvents(events);
        System.out.println("Event updated successfully!");
    }

    private void deleteEvent(Event event) {
        System.out.print("Are you sure you want to delete this event? (yes/no): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("yes")) {
            events.remove(event);
            eventMap.remove(event.getEventId());

            registrations.removeIf(reg -> reg.getEventId().equals(event.getEventId()));
            FileHandler.saveEvents(events);
            FileHandler.saveRegistrations(registrations);

            System.out.println("Event deleted successfully!");
        } else {
            System.out.println("Event deletion cancelled.");
        }
    }

    private void manageUsers() {
        System.out.println("\n=== USER MANAGEMENT ===");
        System.out.println("1. View All Users");
        System.out.println("2. Create Admin User");
        System.out.println("3. Delete User");
        System.out.print("Enter choice: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewAllUsers();
                    break;
                case 2:
                    createAdminUser();
                    break;
                case 3:
                    deleteUser();
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input!");
            scanner.nextLine();
        }
    }

    private void viewAllUsers() {
        if (users.isEmpty()) {
            System.out.println("No users in the system.");
            return;
        }

        System.out.println("\n=== ALL USERS ===");
        for (Person user : users) {
            System.out.println("ID: " + user.getId());
            System.out.println("Name: " + user.getName());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Role: " + user.getRole());
            System.out.println("----------------------------------------");
        }
    }

    private void createAdminUser() {
        try {
            System.out.print("Enter Admin Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter Admin Email: ");
            String email = scanner.nextLine();
            if (!validateEmail(email)) {
                throw new IllegalArgumentException("Invalid email format!");
            }

            System.out.print("Enter Admin Phone: ");
            String phone = scanner.nextLine();

            String userId = generateId("A");
            Admin newAdmin = new Admin(userId, name, email, phone);

            users.add(newAdmin);
            userMap.put(userId, newAdmin);
            FileHandler.saveUsers(users);

            System.out.println("Admin user created successfully! User ID: " + userId);
        } catch (Exception e) {
            System.err.println("Admin creation failed: " + e.getMessage());
        }
    }

    private void deleteUser() {
        try {
            System.out.print("Enter User ID to delete: ");
            String userId = scanner.nextLine();

            Person userToDelete = userMap.get(userId);
            if (userToDelete == null) {
                throw new UserNotFoundException("User not found!");
            }

            if (userToDelete.getId().equals(currentUser.getId())) {
                System.out.println("Cannot delete currently logged-in user!");
                return;
            }

            System.out.print("Are you sure you want to delete user " + userToDelete.getName() + "? (yes/no): ");
            String confirmation = scanner.nextLine();

            if (confirmation.equalsIgnoreCase("yes")) {
                users.remove(userToDelete);
                userMap.remove(userId);

                registrations.removeIf(reg -> reg.getUserId().equals(userId));
                FileHandler.saveUsers(users);
                FileHandler.saveRegistrations(registrations);

                System.out.println("User deleted successfully!");
            } else {
                System.out.println("User deletion cancelled.");
            }
        } catch (Exception e) {
            System.err.println("User deletion failed: " + e.getMessage());
        }
    }

    private void viewReports() {
        System.out.println("\n=== SYSTEM REPORTS ===");
        System.out.println("Total Users: " + users.size());
        System.out.println("Total Events: " + events.size());
        System.out.println("Total Registrations: " + registrations.size());

        int admins = 0, organizers = 0, attendees = 0;
        for (Person user : users) {
            switch (user.getRole()) {
                case "ADMIN": admins++; break;
                case "ORGANIZER": organizers++; break;
                case "ATTENDEE": attendees++; break;
            }
        }

        System.out.println("\nUsers by Role:");
        System.out.println("- Admins: " + admins);
        System.out.println("- Organizers: " + organizers);
        System.out.println("- Attendees: " + attendees);

        int conferences = 0, workshops = 0, concerts = 0;
        for (Event event : events) {
            switch (event.getEventType()) {
                case "CONFERENCE": conferences++; break;
                case "WORKSHOP": workshops++; break;
                case "CONCERT": concerts++; break;
            }
        }

        System.out.println("\nEvents by Type:");
        System.out.println("- Conferences: " + conferences);
        System.out.println("- Workshops: " + workshops);
        System.out.println("- Concerts: " + concerts);
    }

    public static void main(String[] args) {
        EventManagementSystem system = new EventManagementSystem();
        system.start();
    }
}