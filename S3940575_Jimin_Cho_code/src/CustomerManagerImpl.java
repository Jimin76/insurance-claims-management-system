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
        // 고객 ID 생성 및 설정
        String customerId = "c-" + ThreadLocalRandom.current().nextInt(1000000, 10000000);
        customer.setId(customerId);

        // 고객 정보 저장
        customers.put(customerId, customer);
        saveCustomer(customer); // 고객 정보를 파일에 저장하는 메서드 구현 필요

        // 고객의 인슈어런스 카드 생성 및 저장
        String customerCardId = createAndSaveInsuranceCard(customerId, customerId, true, customer.getFullName());
        customer.addInsuranceCardId(customerCardId); // 여기서 고객의 인슈어런스 카드 ID를 추가

        // 디펜던트의 인슈어런스 카드 생성 및 저장
        for (Dependent dependent : customer.getDependents()) {
            String dependentCardId = createAndSaveInsuranceCard(dependent.getId(), customerId, false, dependent.getFullName());
            dependent.setInsuranceCardId(dependentCardId); // 디펜던트에게 고유한 카드 번호 설정
            dependent.setPolicyOwnerId(customerId); // 여기서 디펜던트의 policyOwnerId를 설정
        }

        saveCustomer(customer); // 변경된 고객 정보를 다시 저장합니다.

        return true; // 성공적으로 추가되었음을 의미
    }

    private String createAndSaveInsuranceCard(String cardHolderId, String policyOwnerId, boolean isPolicyHolder, String cardHolderName) {
        String cardNumber = generateRandomCardNumber();
        Date expirationDate = generateCardExpirationDate();
        InsuranceCard card = new InsuranceCard(cardNumber, cardHolderId, policyOwnerId, expirationDate, cardHolderName);
        saveInsuranceCard(card);
        return cardNumber; // 인슈어런스 카드 번호 반환
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
            // 인슈어런스 카드 삭제 로직
            deleteInsuranceCard(customer.getId());
            // 고객 삭제 로직
            try {
                Files.deleteIfExists(Paths.get(CUSTOMER_DIR + id + ".txt"));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void deleteInsuranceCard(String cardHolderId) {
        String filename = INSURANCE_CARD_DIR + cardHolderId + "_insurance_card.txt";
        try {
            Files.deleteIfExists(Paths.get(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        String customerId = customer.getId();
        if (customers.containsKey(customerId)) {
            customers.put(customerId, customer);
            saveCustomer(customer); // 변경된 고객 정보를 파일에 저장
            return true;
        } else {
            return false;
        }
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
