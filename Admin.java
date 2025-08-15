public class Admin extends Person {
    public Admin(String id, String name, String email, String phone) {
        super(id, name, email, phone);
    }

    @Override
    public String getRole() { return "ADMIN"; }

    @Override
    public String[] getPermissions() {
        return new String[]{"CREATE_EVENT", "DELETE_EVENT", "MANAGE_USERS", "VIEW_REPORTS"};
    }
}