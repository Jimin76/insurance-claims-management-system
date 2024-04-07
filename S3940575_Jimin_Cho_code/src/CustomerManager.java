import java.util.List;
import java.io.IOException;

/**
 * @author <Jimin Cho - s3940575>
 */
public interface CustomerManager {
    boolean addCustomer(Customer customer)throws IOException;
    Customer getCustomerById(String id);
    List<Customer> getAllCustomers();
    boolean deleteCustomer(String id)throws IOException;
    void saveCustomers();
    void loadCustomers();
    boolean updateCustomer(Customer customer) throws IOException;
}
