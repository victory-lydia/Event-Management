public class Attendee extends Person {
    public Attendee(String id, String name, String email, String phone) {
        super(id, name, email, phone);
    }

    @Override
    public String getRole() { return "ATTENDEE"; }

    @Override
    public String[] getPermissions() {
        return new String[]{"REGISTER_EVENT", "VIEW_EVENTS", "CANCEL_REGISTRATION"};
    }
}