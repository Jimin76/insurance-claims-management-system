import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerManagerImpl implements CustomerManager {
    private List<Customer> customers = new ArrayList<>();
    private static final String DATA_FILE = "customers.dat";

    public CustomerManagerImpl() {
        loadCustomers();
    }

    @Override
    public void addCustomer(Customer customer) {
        customers.add(customer);
        saveCustomers();
    }

    @Override
    public Customer updateCustomer(String id, String newFullName) {
        for (Customer customer : customers) {
            if (customer.getId().equals(id)) {
                customer.setFullName(newFullName);
                saveCustomers();
                return customer;
            }
        }
        return null;
    }

    @Override
    public boolean deleteCustomer(String id) {
        boolean removed = customers.removeIf(customer -> customer.getId().equals(id));
        if (removed) {
            saveCustomers();
        }
        return removed;
    }

    @Override
    public Customer getCustomerById(String id) {
        return customers.stream().filter(customer -> customer.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }

    @Override
    public void saveCustomers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(customers);
        } catch (IOException e) {
            System.err.println("Error saving customers: " + e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadCustomers() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                customers = (List<Customer>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading customers: " + e.getMessage());
            }
        }
    }
}

