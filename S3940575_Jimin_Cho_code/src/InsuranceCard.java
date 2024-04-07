import java.io.Serializable;
import java.util.Date;
import java.lang.Comparable;
/**
 * @author <Jimin Cho - s3940575>
 */

public class InsuranceCard implements Serializable, Comparable<InsuranceCard> {
    private String cardNumber;
    private String cardHolderId;
    private String policyOwnerId;
    private Date expirationDate;
    private String cardHolderName;


    public InsuranceCard(String cardNumber, String cardHolderId, String policyOwnerId, Date expirationDate, String cardHolderName) {
        this.cardNumber = cardNumber;
        this.cardHolderId = cardHolderId;
        this.policyOwnerId = policyOwnerId;
        this.expirationDate = expirationDate;
        this.cardHolderName = cardHolderName;
    }

    // Getters and Setters

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }
    public String getCardNumber() { return cardNumber; }
    public String getCardHolderId() { return cardHolderId; }
    public String getPolicyOwnerId() { return policyOwnerId; }
    public Date getExpirationDate() { return expirationDate; }

    @Override
    public int compareTo(InsuranceCard other) {
        return this.cardHolderName.compareTo(other.cardHolderName);
    }
    @Override
    public String toString() {
        return "InsuranceCard{" +
                "cardNumber='" + cardNumber + '\'' +
                ", cardHolderId='" + cardHolderId + '\'' +
                ", policyOwnerId='" + policyOwnerId + '\'' +
                ", expirationDate=" + expirationDate +
                ", cardHolderName='" + cardHolderName + '\'' +
                '}';
    }
}
