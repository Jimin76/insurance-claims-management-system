import java.io.Serializable;
import java.util.Date;

public class InsuranceCard implements Serializable {
    private String cardNumber;
    private String policyOwner;
    private Date expirationDate;

    public InsuranceCard(String cardNumber, String policyOwner, Date expirationDate) {
        this.cardNumber = cardNumber;
        this.policyOwner = policyOwner;
        this.expirationDate = expirationDate;
    }

    // Getters and Setters
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getPolicyOwner() { return policyOwner; }
    public void setPolicyOwner(String policyOwner) { this.policyOwner = policyOwner; }

    public Date getExpirationDate() { return expirationDate; }
    public void setExpirationDate(Date expirationDate) { this.expirationDate = expirationDate; }

    @Override
    public String toString() {
        return "InsuranceCard{" +
                "cardNumber='" + cardNumber + '\'' +
                ", policyOwner='" + policyOwner + '\'' +
                ", expirationDate=" + expirationDate +
                '}';
    }
}
