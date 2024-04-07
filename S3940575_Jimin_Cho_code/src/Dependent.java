import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Dependent implements Serializable {
    private String id;
    private String fullName;
    private String policyOwnerId;
    private String relationship;
    private String insuranceCardId; // 추가된 필드: 인슈어런스 카드 ID
    private List<String> claimIds = new ArrayList<>();

    // 생성자
    public Dependent(String id, String fullName, String policyOwnerId, String relationship) {
        this.id = id;
        this.fullName = fullName;
        this.policyOwnerId = policyOwnerId;
        this.relationship = relationship;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPolicyOwnerId() {
        return policyOwnerId;
    }

    public void setPolicyOwnerId(String policyOwnerId) {
        this.policyOwnerId = policyOwnerId;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getInsuranceCardId() {
        return insuranceCardId;
    } // 인슈어런스 카드 ID에 대한 getter

    public void setInsuranceCardId(String insuranceCardId) {
        this.insuranceCardId = insuranceCardId;
    } // 인슈어런스 카드 ID에 대한 setter

    public List<String> getClaimIds() {
        return claimIds;
    }

    public void setClaimIds(List<String> claimIds) {
        this.claimIds = claimIds;
    }

    public void addClaimId(String claimId) {
        this.claimIds.add(claimId);
    }

    @Override
    public String toString() {
        return "Dependent{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", policyOwnerId='" + policyOwnerId + '\'' +
                ", relationship='" + relationship + '\'' +
                ", insuranceCardId='" + insuranceCardId + '\'' +
                ", claimIds=" + claimIds + // claimIds 리스트를 출력하도록 추가
                '}';
    }
}
