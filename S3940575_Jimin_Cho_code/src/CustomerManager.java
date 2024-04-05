import java.util.List;

public interface CustomerManager {
    void addCustomer(Customer customer);
    Customer updateCustomer(String id, String newFullName);
    boolean deleteCustomer(String id);
    Customer getCustomerById(String id);
    List<Customer> getAllCustomers();
    void saveCustomers(); // 고객 정보를 파일에 저장
    void loadCustomers(); // 파일에서 고객 정보를 불러옴
}
