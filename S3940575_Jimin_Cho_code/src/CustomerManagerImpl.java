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
        // 고객을 추가할 때 isPolicyHolder 값을 항상 true로 설정합니다.
        customer.setIsPolicyHolder(true);

        customers.put(customerId, customer);
        saveCustomer(customer);

        // 인슈어런스 카드 생성 및 저장
        createAndSaveInsuranceCard(customerId, customerId, true); // Policy holder 자신을 위한 카드
        for (Dependent dependent : customer.getDependents()) {
            createAndSaveInsuranceCard(dependent.getId(), customerId, false); // 각 Dependent를 위한 카드
        }
        return true;
    }

    private void createAndSaveInsuranceCard(String cardHolderId, String policyOwnerId, boolean isPolicyHolder) {
        String cardNumber = generateRandomCardNumber();
        Date expirationDate = generateCardExpirationDate();
        InsuranceCard card = new InsuranceCard(cardNumber, cardHolderId, policyOwnerId, expirationDate);

        saveInsuranceCard(card);
    }

    private String generateRandomCardNumber() {
        return Long.toString(ThreadLocalRandom.current().nextLong(1000000000L, 10000000000L));
    }

    private Date generateCardExpirationDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1); // 1년 후 만료로 설정
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
            try {
                Files.deleteIfExists(Paths.get(CUSTOMER_DIR + customer.getId() + ".txt"));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        if (customers.containsKey(customer.getId())) {
            customers.put(customer.getId(), customer);
            saveCustomer(customer); // 변경된 고객 정보를 파일에 다시 저장
            return true;
        }
        return false;
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
