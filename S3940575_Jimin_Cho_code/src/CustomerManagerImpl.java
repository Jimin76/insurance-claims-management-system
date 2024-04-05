import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class CustomerManagerImpl implements CustomerManager {
    private Map<String, Customer> customers = new HashMap<>();
    private static final String CUSTOMER_DIR = "./customers/";

    public CustomerManagerImpl() {
        new File(CUSTOMER_DIR).mkdirs();
        loadCustomers();
    }

    @Override
    public boolean addCustomer(Customer customer) {
        String customerId = "c-" + ThreadLocalRandom.current().nextInt(1000000, 10000000);
        customer.setId(customerId);
        if (customer.getDependents().isEmpty()) {
            customer.setIsPolicyHolder(true);
        } else {
            for (Dependent dependent : customer.getDependents()) {
                dependent.setPolicyOwnerId(customerId);
            }
            customer.setIsPolicyHolder(false);
        }
        customers.put(customerId, customer);
        saveCustomer(customer);
        return true;
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
