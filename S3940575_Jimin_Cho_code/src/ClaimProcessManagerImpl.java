import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

public class ClaimProcessManagerImpl implements ClaimProcessManager {
    private Map<String, Claim> claims = new HashMap<>();
    private CustomerManager customerManager; // CustomerManagerImpl의 인스턴스를 저장하기 위한 필드
    private static final String CLAIMS_DIR = "./claims/";
    private static final String INSURANCE_CARDS_DIR = "./insurance cards/";

    public ClaimProcessManagerImpl(CustomerManager customerManager) {
        this.customerManager = customerManager;
        new File(CLAIMS_DIR).mkdirs();
        loadAllClaims();
    }

    @Override
    public void addClaim(Claim claim) {
        claims.put(claim.getId(), claim);
        try {
            saveClaimToFile(claim);
            // 추가된 로직: 고객의 claimIds 업데이트
            Customer customer = customerManager.getCustomerById(claim.getInsuredPersonId()); // 정정: customers.get 대신 customerManager 사용
            if (customer != null) {
                customer.getClaimIds().add(claim.getId());
                customerManager.updateCustomer(customer); // 고객 정보 업데이트
            }
        } catch (IOException e) {
            System.out.println("Failed to save the claim to file.");
            e.printStackTrace();
        }
    }


    public void addClaimWithCustomerID(String customerId) throws IOException, ClassNotFoundException {
        System.out.println("Looking for file at: " + INSURANCE_CARDS_DIR + customerId + "_insurance_card.txt"); // 로그 추가
        File insuranceCardFile = new File(INSURANCE_CARDS_DIR + customerId + "_insurance_card.txt");
        if (!insuranceCardFile.exists()) {
            System.out.println("No insurance card found for the provided customer ID.");
            return;
        }

        InsuranceCard insuranceCard;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(insuranceCardFile))) {
            insuranceCard = (InsuranceCard) ois.readObject();
        }

        // 날짜 파싱과 관련된 부분은 ParseException 처리가 필요합니다.
        Date claimDate = new Date();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter exam date (yyyy-MM-dd): ");
        Date examDate; // 이 변수는 여기에서 한 번만 선언합니다.
        try {
            examDate = new SimpleDateFormat("yyyy-MM-dd").parse(scanner.nextLine());
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please try again using the yyyy-MM-dd format.");
            return; // 예외 발생 시 메서드 실행 종료
        }

        System.out.print("Enter claim amount: ");
        double claimAmount = scanner.nextDouble();
        scanner.nextLine(); // 숫자 입력 후 줄 바꿈 문자 처리

        System.out.print("Enter receiver banking info (Bank – Name – Number): ");
        String receiverBankingInfo = scanner.nextLine();

        // Claim 객체 생성
        Claim claim = new Claim(generateUniqueClaimId());
        claim.setInsuredPerson(insuranceCard.getCardHolderName());
        claim.setCardNumber(insuranceCard.getCardNumber());
        claim.setClaimDate(claimDate);
        claim.setExamDate(examDate);
        claim.setClaimAmount(claimAmount);
        claim.setStatus("New");
        claim.setReceiverBankingInfo(receiverBankingInfo);

        // 클레임 저장 로직
        saveClaimToFile(claim);
        System.out.println("Claim added successfully.");
    }

    @Override
    public void updateClaim(String claimId, Claim claim) {
        if (claims.containsKey(claimId)) {
            claims.put(claimId, claim);
            try {
                saveClaimToFile(claim);
            } catch (IOException e) {
                System.out.println("Unable to save the claim due to an IO error.");
                e.printStackTrace();
            }
        }
    }


    @Override
    public void deleteClaim(String claimId) {
        Claim claim = claims.remove(claimId);
        deleteClaimFile(claimId);
        if (claim != null) {
            // 삭제된 클레임에 해당하는 커스터머를 찾아 클레임 ID 목록에서 제거합니다.
            Customer customer = customerManager.getCustomerById(claim.getInsuredPersonId());
            if (customer != null) {
                customer.removeClaimId(claimId);
                customerManager.updateCustomer(customer); // 커스터머 정보를 업데이트하는 메소드 (구현 필요)
            }
        }
    }

    @Override
    public Claim getClaimById(String claimId) {
        return claims.get(claimId);
    }

    @Override
    public List<Claim> getAllClaims() {
        return new ArrayList<>(claims.values());
    }

    private void saveClaimToFile(Claim claim) throws IOException {
        File claimFile = new File(CLAIMS_DIR + claim.getId() + ".txt");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(claimFile))) {
            oos.writeObject(claim);
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
        // f-10자리 숫자를 생성하는 로직
        long number = ThreadLocalRandom.current().nextLong(1000000000L, 10000000000L);
        return "f-" + number;
    }
}
