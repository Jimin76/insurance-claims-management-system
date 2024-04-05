import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Customer implements Serializable {
    private String id;
    private String fullName;
    private boolean isPolicyHolder;
    private List<Dependent> dependents;

    public Customer(String fullName, boolean isPolicyHolder) {
        this.fullName = fullName;
        this.isPolicyHolder = isPolicyHolder;
        this.dependents = new ArrayList<>();
    }

    public void addDependent(Dependent dependent) {
        this.dependents.add(dependent);
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public boolean getIsPolicyHolder() {
        return isPolicyHolder;
    }

    public List<Dependent> getDependents() {
        return dependents;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setIsPolicyHolder(boolean isPolicyHolder) {
        this.isPolicyHolder = isPolicyHolder;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", isPolicyHolder=" + isPolicyHolder +
                ", dependents=" + dependents +
                '}';
    }
}
