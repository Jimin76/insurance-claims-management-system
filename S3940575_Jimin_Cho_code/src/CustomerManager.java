import java.util.List;

public interface CustomerManager {
    boolean addCustomer(Customer customer);
    Customer getCustomerById(String id);
    List<Customer> getAllCustomers();
    boolean deleteCustomer(String id);
    void saveCustomers();
    void loadCustomers();
    boolean updateCustomer(Customer customer); // 업데이트 메서드 추가
}
