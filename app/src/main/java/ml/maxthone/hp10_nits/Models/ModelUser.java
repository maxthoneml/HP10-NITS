package ml.maxthone.hp10_nits.Models;

public class ModelUser {
    private String uid, email, name, phone, role, profileImage;
    private boolean active;

    public ModelUser() {
    }

    public ModelUser(String uid, String email, String name, String phone, String role, String profileImage, boolean active) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.profileImage = profileImage;
        this.active = active;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
