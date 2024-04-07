import java.util.List;
import java.io.IOException;
import java.text.ParseException;

/**
 * @author <Jimin Cho - s3940575>
 */
public interface ClaimProcessManager {
    void addClaim(Claim claim);
    void changeStatus(String claimId, String newStatus) throws IOException;
    void updateClaim(String claimId) throws IOException, ParseException;
    void deleteClaim(String claimId);
    Claim getClaimById(String claimId);
    List<Claim> getAllClaims();


    void addClaimWithCustomerID(String customerId) throws IOException, ClassNotFoundException;
}
