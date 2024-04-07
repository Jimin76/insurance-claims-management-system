import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
//import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author <Jimin Cho - s3940575>
 */

public class CustomerManagerImpl implements CustomerManager {
    private Map<String, Customer> customers = new HashMap<>();
    private static final String CUSTOMER_DIR = "./customers/";
    private static final String INSURANCE_CARD_DIR = "./insurance cards/";

    public CustomerManagerImpl() {
        new File(CUSTOMER_DIR).mkdirs();
        new File(INSURANCE_CARD_DIR).mkdirs();
        loadCustomers();
    }

    @Override
    public boolean addCustomer(Customer customer) throws IOException {
        // Generate and set customer ID
        String customerId = "c-" + ThreadLocalRandom.current().nextInt(1000000, 10000000);
        customer.setId(customerId);


        // Save customer information
        customers.put(customerId, customer);
        saveCustomer(customer);

        // Create and save the customer's insurance card
        String customerCardId = createAndSaveInsuranceCard(customerId, customerId, true, customer.getFullName());
        customer.addInsuranceCardId(customerCardId);

        // Create and save the insurance card for the dependent
        for (Dependent dependent : customer.getDependents()) {
            String dependentCardId = createAndSaveInsuranceCard(dependent.getId(), customerId, false, dependent.getFullName());
            dependent.setInsuranceCardId(dependentCardId);
            dependent.setPolicyOwnerId(customerId);
        }

        saveCustomer(customer);

        return true;
    }

    private String createAndSaveInsuranceCard(String cardHolderId, String policyOwnerId, boolean isPolicyHolder, String cardHolderName) {
        String cardNumber = generateRandomCardNumber();
        Date expirationDate = generateCardExpirationDate();
        InsuranceCard card = new InsuranceCard(cardNumber, cardHolderId, policyOwnerId, expirationDate, cardHolderName);
        saveInsuranceCard(card);
        return cardNumber;
    }

    private void saveInsuranceCard(InsuranceCard card, String cardHolderId) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(INSURANCE_CARD_DIR + cardHolderId + "_insurance_card.txt"))) {
            oos.writeObject(card);
        }
    }

    private String generateRandomCardNumber() {
        return Long.toString(ThreadLocalRandom.current().nextLong(1000000000L, 10000000000L));
    }

    private Date generateCardExpirationDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
        return calendar.getTime();
    }

    private void saveInsuranceCard(InsuranceCard card) {
        String filename = INSURANCE_CARD_DIR + card.getCardHolderId() + "_insurance_card.txt";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(card);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Customer getCustomerById(String id) {
        return customers.get(id);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    @Override
    public boolean deleteCustomer(String id) throws IOException {
        Customer customer = customers.remove(id);
        if (customer != null) {

            deleteInsuranceCard(customer.getId());

            try {
                Files.deleteIfExists(Paths.get(CUSTOMER_DIR + id + ".txt"));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void deleteInsuranceCards(Customer customer) throws IOException {
        Files.deleteIfExists(Paths.get(INSURANCE_CARD_DIR + customer.getId() + "_insurance_card.txt"));
        for (Dependent dependent : customer.getDependents()) {
            Files.deleteIfExists(Paths.get(INSURANCE_CARD_DIR + dependent.getId() + "_insurance_card.txt"));
        }
    }

    private void deleteInsuranceCard(String cardHolderId) {
        String filename = INSURANCE_CARD_DIR + cardHolderId + "_insurance_card.txt";
        try {
            Files.deleteIfExists(Paths.get(filename));
            System.out.println("Deleted insurance card for: " + cardHolderId);
        } catch (IOException e) {
            System.out.println("Failed to delete insurance card for: " + cardHolderId);
            e.printStackTrace();
        }
    }

    @Override
    public boolean updateCustomer(Customer customer) throws IOException {
        Customer existingCustomer = customers.get(customer.getId());
        if (existingCustomer == null) {
            System.out.println("Customer not found.");
            return false;
        }

        // Delete the existing insurance card for the dependent
        for (Dependent dependent : existingCustomer.getDependents()) {
            String dependentInsuranceCardFilename = INSURANCE_CARD_DIR + dependent.getInsuranceCardId() + "_insurance_card.txt";
            Files.deleteIfExists(Paths.get(dependentInsuranceCardFilename));
        }

        // update customer info
        existingCustomer.setFullName(customer.getFullName());
        existingCustomer.setDependents(customer.getDependents());

        // Create and save the insurance card for the new dependent
        for (Dependent newDependent : customer.getDependents()) {
            String dependentCardId = createAndSaveInsuranceCard(newDependent.getId(), customer.getId(), false, newDependent.getFullName());
            newDependent.setInsuranceCardId(dependentCardId);
        }

        saveCustomer(existingCustomer); // save updated customer info
        return true;
    }

    private void deleteInsuranceCardsForCustomer(Customer customer) throws IOException {
        Files.deleteIfExists(Paths.get(INSURANCE_CARD_DIR + customer.getId() + "_insurance_card.txt"));
        for (Dependent dependent : customer.getDependents()) {
            Files.deleteIfExists(Paths.get(INSURANCE_CARD_DIR + dependent.getId() + "_insurance_card.txt"));
        }
    }

    private void generateAndSaveInsuranceCards(Customer customer) throws IOException {
        createAndSaveInsuranceCard(customer.getId(), customer.getId(), true, customer.getFullName());
        for (Dependent dependent : customer.getDependents()) {
            createAndSaveInsuranceCard(dependent.getId(), customer.getId(), false, dependent.getFullName());
        }
    }



    @Override
    public void saveCustomers() {
        customers.values().forEach(customer -> {
            try {
                saveCustomer(customer);
            } catch (IOException e) {
                System.err.println("Error saving customer " + customer.getId() + ": " + e.getMessage());
            }
        });
    }

    @Override
    public void loadCustomers() {
        File folder = new File(CUSTOMER_DIR);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));
        for (File file : files) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Customer customer = (Customer) ois.readObject();
                customers.put(customer.getId(), customer);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveCustomer(Customer customer) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CUSTOMER_DIR + customer.getId() + ".txt"))) {
            oos.writeObject(customer);
        } catch (IOException e) {
            throw e;
        }
    }
}
