public class Organizer extends Person {
    public Organizer(String id, String name, String email, String phone) {
        super(id, name, email, phone);
    }

    @Override
    public String getRole() { return "ORGANIZER"; }

    @Override
    public String[] getPermissions() {
        return new String[]{"CREATE_EVENT", "MANAGE_OWN_EVENTS", "VIEW_ATTENDEES"};
    }
}