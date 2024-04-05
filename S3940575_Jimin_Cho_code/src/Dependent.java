import java.io.Serializable;

public class Dependent implements Serializable {
    private String id;
    private String fullName;
    private String policyOwnerId;
    private String relationship;

    public Dependent(String id, String fullName, String policyOwnerId, String relationship) {
        this.id = id;
        this.fullName = fullName;
        this.policyOwnerId = policyOwnerId;
        this.relationship = relationship;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPolicyOwnerId() { return policyOwnerId; }
    public void setPolicyOwnerId(String policyOwnerId) { this.policyOwnerId = policyOwnerId; }
    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }

    @Override
    public String toString() {
        return "Dependent{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", policyOwnerId='" + policyOwnerId + '\'' +
                ", relationship='" + relationship + '\'' +
                '}';
    }
}
