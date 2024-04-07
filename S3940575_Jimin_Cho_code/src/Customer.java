import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <Jimin Cho - s3940575>
 */
public class Customer implements Serializable {
    private String id;
    private String fullName;
    private boolean isPolicyHolder;
    private List<Dependent> dependents = new ArrayList<>();
    private List<String> insuranceCardIds = new ArrayList<>();
    private List<String> claimIds = new ArrayList<>();


    public Customer(String fullName, boolean isPolicyHolder) {
        this.fullName = fullName;
        this.isPolicyHolder = isPolicyHolder;
        this.dependents = new ArrayList<>();
        this.insuranceCardIds = new ArrayList<>();
        this.claimIds = new ArrayList<>();
    }


    public void addDependent(Dependent dependent) {
        this.dependents.add(dependent);
    }



    public void addInsuranceCardId(String cardId) {
        this.insuranceCardIds.add(cardId);
    }

    public void addClaimId(String claimId) {
        this.claimIds.add(claimId);
    }

    public void removeClaimId(String claimId) {
        this.claimIds.remove(claimId);
    }

    // Getters
    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public boolean isPolicyHolder() { return isPolicyHolder; }
    public List<Dependent> getDependents() { return dependents; }
    public List<String> getInsuranceCardIds() { return insuranceCardIds; }
    public List<String> getClaimIds() { return claimIds; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setIsPolicyHolder(boolean isPolicyHolder) {
        this.isPolicyHolder = isPolicyHolder;
    }
    public void setPolicyHolder(boolean policyHolder) { isPolicyHolder = policyHolder; }
    public void setDependents(List<Dependent> dependents) { this.dependents = dependents; }
    public void setInsuranceCardIds(List<String> insuranceCardIds) { this.insuranceCardIds = insuranceCardIds; }
    public void setClaimIds(List<String> claimIds) { this.claimIds = claimIds; }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", isPolicyHolder=" + isPolicyHolder +
                ", dependents=" + dependents +
                ", insuranceCardIds=" + insuranceCardIds +
                ", claimIds=" + claimIds +
                '}';
    }
}
