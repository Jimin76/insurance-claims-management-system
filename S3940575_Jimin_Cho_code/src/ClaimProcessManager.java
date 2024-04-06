
import java.util.List;

public interface ClaimProcessManager {
    void addClaim(Claim claim);
    void updateClaim(String claimId, Claim claim); // 반환 타입이 void
    void deleteClaim(String claimId);
    Claim getClaimById(String claimId);
    List<Claim> getAllClaims();
}
