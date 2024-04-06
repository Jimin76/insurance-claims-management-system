
import java.util.List;
import java.io.IOException;

public interface ClaimProcessManager {
    void addClaim(Claim claim);
    void updateClaim(String claimId, Claim claim);
    void deleteClaim(String claimId);
    Claim getClaimById(String claimId);
    List<Claim> getAllClaims();

    // 새로운 메서드 정의
    void addClaimWithCustomerID(String customerId) throws IOException, ClassNotFoundException;
}
