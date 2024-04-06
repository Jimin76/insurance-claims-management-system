import java.io.Serializable;
import java.util.Date;

public class InsuranceCard implements Serializable {
    private String cardNumber;
    private String cardHolderId;
    private String policyOwnerId;
    private Date expirationDate;
    private String cardHolderName; // 카드 소유자 이름을 저장할 필드 추가

    // 생성자 수정: cardHolderName 인자 추가
    public InsuranceCard(String cardNumber, String cardHolderId, String policyOwnerId, Date expirationDate, String cardHolderName) {
        this.cardNumber = cardNumber;
        this.cardHolderId = cardHolderId;
        this.policyOwnerId = policyOwnerId;
        this.expirationDate = expirationDate;
        this.cardHolderName = cardHolderName; // 인자 값을 클래스 변수에 할당
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
