import java.io.Serializable;
import java.util.Date;

public class Claim implements Serializable {
    private String id;
    private Date claimDate;
    private String insuredPerson;
    private double claimAmount;

    public Claim(String id, Date claimDate, String insuredPerson, double claimAmount) {
        this.id = id;
        this.claimDate = claimDate;
        this.insuredPerson = insuredPerson;
        this.claimAmount = claimAmount;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Date getClaimDate() { return claimDate; }
    public void setClaimDate(Date claimDate) { this.claimDate = claimDate; }

    public String getInsuredPerson() { return insuredPerson; }
    public void setInsuredPerson(String insuredPerson) { this.insuredPerson = insuredPerson; }

    public double getClaimAmount() { return claimAmount; }
    public void setClaimAmount(double claimAmount) { this.claimAmount = claimAmount; }

    @Override
    public String toString() {
        return "Claim{" +
                "id='" + id + '\'' +
                ", claimDate=" + claimDate +
                ", insuredPerson='" + insuredPerson + '\'' +
                ", claimAmount=" + claimAmount +
                '}';
    }
}
