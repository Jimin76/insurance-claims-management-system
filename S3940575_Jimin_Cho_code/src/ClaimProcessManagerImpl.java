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

/**
 * @author <Jimin Cho - s3940575>
 */
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
        System.out.println("Claim added to the system: " + claim.getId());
        try {
            saveClaimToFile(claim);
            System.out.println("Claim saved to file: " + claim.getId());
            Customer customer = customerManager.getCustomerById(claim.getInsuredPersonId());
            if (customer != null) {
                System.out.println("Customer found for the claim: " + customer.getId());
                customer.addClaimId(claim.getId());
                System.out.println("Claim ID added to the customer: " + claim.getId());
                try {
                    customerManager.updateCustomer(customer);
                } catch (IOException e) {
                    System.err.println("Failed to update customer: " + e.getMessage());
                }
            } else {
                System.out.println("No customer found with ID: " + claim.getInsuredPersonId());
            }
        } catch (IOException e) {
            System.err.println("Failed to save the claim to file: " + e.getMessage());
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
        scanner.nextLine();

        System.out.print("Enter receiver banking info (Bank_Name_Number): ");
        String receiverBankingInfo = scanner.nextLine();

        Claim claim = new Claim(generateUniqueClaimId());
        claim.setClaimDate(new Date());
        claim.setInsuredPerson(insuranceCard.getCardHolderName());
        claim.setCardNumber(insuranceCard.getCardNumber());
        claim.setExamDate(examDate);
        claim.setClaimAmount(claimAmount);
        claim.setStatus("New");
        claim.setReceiverBankingInfo(receiverBankingInfo);
        claim.setInsuredPersonId(customerId);

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
            try {
                if (customer.getId().equals(cardHolderId)) {
                    customer.addClaimId(claimId);
                    customerManager.updateCustomer(customer);
                    System.out.println("Claim ID added to the customer: " + claimId);
                    return;
                }


                for (Dependent dependent : customer.getDependents()) {
                    if (dependent.getId().equals(cardHolderId)) {
                        dependent.addClaimId(claimId);
                        customerManager.updateCustomer(customer);
                        System.out.println("Claim ID added to the dependent: " + claimId);
                        return;
                    }
                }
            } catch (IOException e) {
                System.err.println("Failed to update customer or dependent with new claim ID: " + e.getMessage());
            }
        }
        System.out.println("No customer or dependent found with ID: " + cardHolderId);
    }



//    private void addClaimToCustomer(String claimId, String customerId) {
//        Customer customer = customerManager.getCustomerById(customerId);
//        if (customer != null) {
//            customer.addClaimId(claimId);
//            try {
//                customerManager.updateCustomer(customer);
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
    public void updateClaim(String claimId) throws IOException, ParseException {
        Claim claim = claims.get(claimId);
        if (claim == null) {
            System.out.println("Claim not found: " + claimId);
            return;
        }

        // Receive new claim information from the user
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter exam date (yyyy-MM-dd): ");
        Date examDate = new SimpleDateFormat("yyyy-MM-dd").parse(scanner.nextLine());

        System.out.print("Enter claim amount: ");
        double claimAmount = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Enter receiver banking info (Bank_Name_Number): ");
        String receiverBankingInfo = scanner.nextLine();

        // Update the claim object
        claim.setExamDate(examDate);
        claim.setClaimAmount(claimAmount);
        claim.setReceiverBankingInfo(receiverBankingInfo);
        claim.setClaimDate(new Date());

        // Save the updated claim object to a file
        saveClaimToFile(claim);
        System.out.println("Claim updated successfully with ID: " + claim.getId());
    }

    @Override
    public void changeStatus(String claimId, String newStatus) throws IOException {
        Claim claim = claims.get(claimId);
        if (claim == null) {
            System.out.println("Claim not found.");
            return;
        }
        claim.setStatus(newStatus);
        saveClaimToFile(claim);
        System.out.println("Claim status updated successfully to " + newStatus + ".");
    }

    @Override
    public void deleteClaim(String claimId) {
        Claim claim = claims.remove(claimId);
        if (claim != null) {
            // If a claim is deleted, remove the corresponding claim ID from the customer information
            for (Customer customer : customerManager.getAllCustomers()) {
                if (customer.getClaimIds().remove(claimId)) {
                    try {
                        customerManager.updateCustomer(customer); // update customer info
                    } catch (IOException e) {
                        System.err.println("An error occurred while updating customer data: " + e.getMessage());

                    }
                }
                for (Dependent dependent : customer.getDependents()) {
                    if (dependent.getClaimIds().remove(claimId)) {
                        try {
                            customerManager.updateCustomer(customer); // save changes
                        } catch (IOException e) {
                            System.err.println("An error occurred while updating customer data: " + e.getMessage());

                        }
                        break; // If the claim ID is found in the respective dependency, stop searching further
                    }
                }
            }
            deleteClaimFile(claimId);
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
        // Find and return the object containing the claimId among customers and dependencies
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
        // Logic to generate a 10-digit number starting with f
        long number = ThreadLocalRandom.current().nextLong(1000000000L, 10000000000L);
        return "f-" + number;
    }
}
