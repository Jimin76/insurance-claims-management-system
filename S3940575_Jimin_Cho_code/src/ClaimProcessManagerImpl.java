import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import java.nio.file.Path;

public class ClaimProcessManagerImpl implements ClaimProcessManager {
    private Map<String, Claim> claims = new HashMap<>();
    private CustomerManager customerManager;
    private static final String CLAIMS_DIR = "./claims/";
    private static final String INSURANCE_CARDS_DIR = "./insurance cards/";

    public ClaimProcessManagerImpl(CustomerManager customerManager) {
        this.customerManager = customerManager;
        new File(CLAIMS_DIR).mkdirs();
        loadAllClaims();
    }

    public void addClaim(Claim claim) {
        claims.put(claim.getId(), claim);
        System.out.println("Claim added to the system: " + claim.getId()); // 로그 추가
        try {
            saveClaimToFile(claim);
            System.out.println("Claim saved to file: " + claim.getId()); // 로그 추가
            Customer customer = customerManager.getCustomerById(claim.getInsuredPersonId());
            if (customer != null) {
                System.out.println("Customer found for the claim: " + customer.getId()); // 로그 추가
                customer.addClaimId(claim.getId());
                System.out.println("Claim ID added to the customer: " + claim.getId()); // 로그 추가
                // 변경된 고객 정보를 업데이트하고 저장합니다.
                customerManager.updateCustomer(customer);
            } else {
                System.out.println("No customer found with ID: " + claim.getInsuredPersonId()); // 로그 추가
            }
        } catch (IOException e) {
            System.out.println("Failed to save the claim to file: " + claim.getId());
            e.printStackTrace();
        }
    }


    @Override
    public void addClaimWithCustomerID(String customerId) throws IOException {
        InsuranceCard insuranceCard = findInsuranceCardByCustomerId(customerId);
        if (insuranceCard == null) {
            System.out.println("No insurance card found for the provided customer ID.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter exam date (yyyy-MM-dd): ");
        Date examDate;
        try {
            examDate = new SimpleDateFormat("yyyy-MM-dd").parse(scanner.nextLine());
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please try again using the yyyy-MM-dd format.");
            return;
        }

        System.out.print("Enter claim amount: ");
        double claimAmount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter receiver banking info (Bank_Name_Number): ");
        String receiverBankingInfo = scanner.nextLine();

        Claim claim = new Claim(generateUniqueClaimId());
        claim.setClaimDate(new Date()); // 현재 날짜 및 시간
        claim.setInsuredPerson(insuranceCard.getCardHolderName());
        claim.setCardNumber(insuranceCard.getCardNumber());
        claim.setExamDate(examDate);
        claim.setClaimAmount(claimAmount);
        claim.setStatus("New");
        claim.setReceiverBankingInfo(receiverBankingInfo);
        claim.setInsuredPersonId(customerId); // 고객 ID 설정

        saveClaimToFile(claim);
        updateCustomerClaimIds(customerId, claim.getId());

        System.out.println("Claim added successfully with ID: " + claim.getId());
    }

    private InsuranceCard findInsuranceCardByCustomerId(String customerId) {
        String filename = INSURANCE_CARDS_DIR + customerId + "_insurance_card.txt";
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("No insurance card found for customer ID: " + customerId);
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            InsuranceCard insuranceCard = (InsuranceCard) ois.readObject();
            return insuranceCard;
        } catch (FileNotFoundException e) {
            System.out.println("Insurance card file not found for customer ID: " + customerId);
        } catch (IOException e) {
            System.out.println("Error reading insurance card file for customer ID: " + customerId);
        } catch (ClassNotFoundException e) {
            System.out.println("Error casting to InsuranceCard for customer ID: " + customerId);
        }
        return null;
    }

    private void updateCustomerClaimIds(String cardHolderId, String claimId) {
        for (Customer customer : customerManager.getAllCustomers()) {
            // 고객 자신의 클레임인 경우
            if (customer.getId().equals(cardHolderId)) {
                customer.addClaimId(claimId);
                customerManager.updateCustomer(customer);
                System.out.println("Claim ID added to the customer: " + claimId);
                return;
            }

            // 의존 대상의 클레임인 경우
            for (Dependent dependent : customer.getDependents()) {
                if (dependent.getId().equals(cardHolderId)) {
                    dependent.addClaimId(claimId);
                    customerManager.updateCustomer(customer); // 의존 대상을 포함하는 고객 정보 업데이트
                    System.out.println("Claim ID added to the dependent: " + claimId);
                    return;
                }
            }
        }
        System.out.println("No customer or dependent found with ID: " + cardHolderId);
    }


//    private void addClaimToCustomer(String claimId, String customerId) {
//        Customer customer = customerManager.getCustomerById(customerId);
//        if (customer != null) {
//            customer.addClaimId(claimId);
//            try {
//                customerManager.updateCustomer(customer); // 고객 정보 업데이트
//                System.out.println("Claim ID added to the customer: " + customerId);
//            } catch (Exception e) {
//                System.out.println("Failed to update customer with new claim ID.");
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println("Customer not found with ID: " + customerId);
//        }
//    }

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
        if (claim != null) {
            // 클레임이 삭제되면 고객 정보에서도 해당 클레임 ID를 제거
            for (Customer customer : customerManager.getAllCustomers()) {
                if (customer.getClaimIds().remove(claimId)) {
                    customerManager.updateCustomer(customer); // 고객 정보 업데이트
                }
                for (Dependent dependent : customer.getDependents()) {
                    if (dependent.getClaimIds().remove(claimId)) {
                        customerManager.updateCustomer(customer); // 변경 사항 저장
                        break; // 해당 의존 대상에서 클레임 ID를 찾았으면 더 이상 찾지 않음
                    }
                }
            }
            deleteClaimFile(claimId); // 클레임 파일 삭제
        } else {
            System.out.println("Claim not found: " + claimId);
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

    private Customer findCustomerByClaimId(String customerId, String claimId) {
        // 고객과 의존 대상 중에서 claimId를 포함하는 객체를 찾아 반환
        for (Customer customer : customerManager.getAllCustomers()) {
            if (customer.getClaimIds().contains(claimId) || customer.getDependents().stream().anyMatch(d -> d.getClaimIds().contains(claimId))) {
                return customer;
            }
        }
        return null;
    }

    private void deleteClaimFile(String claimId) {
        Path path = Paths.get(CLAIMS_DIR + claimId + ".txt");
        try {
            Files.deleteIfExists(path);
            System.out.println("Successfully deleted claim file: " + path);
        } catch (IOException e) {
            System.out.println("Failed to delete claim file: " + path);
            e.printStackTrace();
        }
    }

    private String generateUniqueClaimId() {
        // f-10자리 숫자를 생성하는 로직
        long number = ThreadLocalRandom.current().nextLong(1000000000L, 10000000000L);
        return "f-" + number;
    }
}
