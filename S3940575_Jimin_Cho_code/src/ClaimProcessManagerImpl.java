import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.List;

public class ClaimProcessManagerImpl implements ClaimProcessManager {
    private Map<String, Claim> claims = new HashMap<>();
    private static final String CLAIMS_DIR = "./claims/";

    public ClaimProcessManagerImpl() {
        new File(CLAIMS_DIR).mkdirs();
        loadAllClaims();
    }

    @Override
    public void addClaim(Claim claim) {
        String claimId = generateUniqueClaimId();
        claim.setId(claimId);
        claims.put(claimId, claim);
        saveClaimToFile(claim);
    }

    @Override
    public void updateClaim(String claimId, Claim claim) {
        if (claims.containsKey(claimId)) {
            claims.put(claimId, claim);
            saveClaimToFile(claim);
            // 반환문 제거
        }
        // null 반환 제거, 반환 타입이 void이므로 반환값이 없습니다.
    }

    @Override
    public void deleteClaim(String claimId) {
        claims.remove(claimId);
        deleteClaimFile(claimId);
    }

    @Override
    public Claim getClaimById(String claimId) {
        return claims.get(claimId);
    }

    @Override
    public List<Claim> getAllClaims() {
        return new ArrayList<>(claims.values());
    }

    private void saveClaimToFile(Claim claim) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CLAIMS_DIR + claim.getId() + ".txt"))) {
            oos.writeObject(claim);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAllClaims() {
        File folder = new File(CLAIMS_DIR);
        FilenameFilter txtFileFilter = (dir, name) -> name.endsWith(".txt");

        File[] files = folder.listFiles(txtFileFilter);
        for (File file : files) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Claim claim = (Claim) ois.readObject();
                claims.put(claim.getId(), claim);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteClaimFile(String claimId) {
        try {
            Files.deleteIfExists(Paths.get(CLAIMS_DIR + claimId + ".txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateUniqueClaimId() {
        String id;
        do {
            id = "f-" + ThreadLocalRandom.current().nextLong(1000000000L, 10000000000L);
        } while (claims.containsKey(id));
        return id;
    }
}
