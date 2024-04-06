import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CustomerManagerImpl implements CustomerManager {
    private Map<String, Customer> customers = new HashMap<>();
    private static final String CUSTOMER_DIR = "./customers/";
    private static final String INSURANCE_CARD_DIR = "./insurance cards/";

    public CustomerManagerImpl() {
        new File(CUSTOMER_DIR).mkdirs();
        new File(INSURANCE_CARD_DIR).mkdirs(); // 인슈어런스 카드 디렉토리 생성
        loadCustomers();
    }

    @Override
    public boolean addCustomer(Customer customer) {
        String customerId = "c-" + ThreadLocalRandom.current().nextInt(1000000, 10000000);
        customer.setId(customerId);
        customer.setIsPolicyHolder(true);

        customers.put(customerId, customer);
        saveCustomer(customer);

        createAndSaveInsuranceCard(customerId, customerId, true, customer.getFullName()); // Policy holder 자신을 위한 카드
        for (Dependent dependent : customer.getDependents()) {
            // Dependent의 fullName을 cardHolderName으로 전달합니다.
            createAndSaveInsuranceCard(dependent.getId(), customerId, false, dependent.getFullName()); // 각 Dependent를 위한 카드
        }
        return true;
    }

    private void createAndSaveInsuranceCard(String cardHolderId, String policyOwnerId, boolean isPolicyHolder, String cardHolderName) {
        String cardNumber = generateRandomCardNumber();
        Date expirationDate = generateCardExpirationDate();
        // cardHolderName 파라미터를 생성자 호출에 추가합니다.
        InsuranceCard card = new InsuranceCard(cardNumber, cardHolderId, policyOwnerId, expirationDate, cardHolderName);
        saveInsuranceCard(card);
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
    public boolean deleteCustomer(String id) {
        Customer customer = customers.remove(id);
        if (customer != null) {
            deleteInsuranceCard(customer.getId()); // 인슈어런스 카드 삭제 로직 추가
            for (Dependent dependent : customer.getDependents()) {
                deleteInsuranceCard(dependent.getId()); // 디펜던트의 인슈어런스 카드 삭제
            }
            try {
                Files.deleteIfExists(Paths.get(CUSTOMER_DIR + customer.getId() + ".txt"));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void deleteInsuranceCard(String cardHolderId) {
        String filename = cardHolderId + "_insurance_card.txt";
        Path path = Paths.get(INSURANCE_CARD_DIR, filename);
        System.out.println("Attempting to delete insurance card: " + path); // Debugging log
        try {
            boolean deleted = Files.deleteIfExists(path);
            System.out.println("Insurance card " + (deleted ? "successfully deleted." : "not found or already deleted.")); // Debugging log
        } catch (IOException e) {
            System.err.println("Error deleting insurance card: " + path);
            e.printStackTrace();
        }
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        if (customers.containsKey(customer.getId())) {
            Customer existingCustomer = customers.get(customer.getId());

            // 기존 인슈어런스 카드 삭제
            deleteInsuranceCard(existingCustomer.getId()); // 고객 자신의 인슈어런스 카드 삭제
            for (Dependent dependent : existingCustomer.getDependents()) {
                deleteInsuranceCard(dependent.getId()); // 기존 디펜던트의 인슈어런스 카드 삭제
            }

            // 고객 정보 업데이트
            customers.put(customer.getId(), customer);
            saveCustomer(customer); // 변경된 고객 정보를 파일에 다시 저장

            // 새로운 인슈어런스 카드 생성
            createAndSaveInsuranceCard(customer.getId(), customer.getId(), true, customer.getFullName()); // 고객 자신을 위한 새 인슈어런스 카드
            for (Dependent dependent : customer.getDependents()) {
                // 여기서 cardHolderName을 포함하여 메서드를 호출해야 합니다.
                createAndSaveInsuranceCard(dependent.getId(), customer.getId(), false, dependent.getFullName()); // 각 Dependent를 위한 새 인슈어런스 카드
            }

            return true;
        }
        return false;
    }

    private void updateInsuranceCard(String cardHolderId, String policyOwnerId) {
        // cardHolderName을 찾기 위해 customers 맵에서 Customer 객체를 가져옵니다.
        Customer cardHolder = customers.get(cardHolderId);
        String cardHolderName = ""; // 초기화
        if (cardHolder != null) {
            cardHolderName = cardHolder.getFullName(); // 고객 이름을 가져옵니다.
        } else {
            // cardHolderId로 직접 Customer 객체를 찾을 수 없는 경우,
            // 다른 방법으로 이름을 조회하거나 오류 처리를 해야 할 수 있습니다.
            System.out.println("Error: No customer found with ID " + cardHolderId);
            return; // 이름을 찾을 수 없으므로 작업을 중단합니다.
        }

        // 기존 카드 삭제
        deleteInsuranceCard(cardHolderId);

        // 새 카드 생성 및 저장 - 이제 cardHolderName을 포함하여 호출합니다.
        createAndSaveInsuranceCard(cardHolderId, policyOwnerId, cardHolderId.equals(policyOwnerId), cardHolderName);
    }

    @Override
    public void saveCustomers() {
        customers.values().forEach(this::saveCustomer);
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

    private void saveCustomer(Customer customer) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CUSTOMER_DIR + customer.getId() + ".txt"))) {
            oos.writeObject(customer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
