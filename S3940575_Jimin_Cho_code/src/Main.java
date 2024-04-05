import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static CustomerManager customerManager = new CustomerManagerImpl();

    public static void main(String[] args) {
        System.out.println("Welcome to the Insurance Claims Management System");
        boolean exit = false;
        while (!exit) {
            System.out.println("\n[1] Manage Customers");
            System.out.println("[2] Exit");
            System.out.print("Please select an option: ");
            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    manageCustomers();
                    break;
                case 2:
                    System.out.println("Exiting program.");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void manageCustomers() {
        boolean back = false;
        while (!back) {
            System.out.println("\n[1] Add Customer");
            System.out.println("[2] Update Customer");
            System.out.println("[3] Delete Customer");
            System.out.println("[4] View Customer");
            System.out.println("[5] View All Customers");
            System.out.println("[6] Back");
            System.out.print("Please select an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (option) {
                case 1:
                    addCustomer();
                    break;
                case 2:
                    updateCustomer();
                    break;
                case 3:
                    deleteCustomer();
                    break;
                case 4:
                    viewCustomer();
                    break;
                case 5:
                    viewAllCustomers();
                    break;
                case 6:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void addCustomer() {
        System.out.print("Enter customer full name: ");
        String fullName = scanner.nextLine();
        Customer customer = new Customer(fullName);
        customerManager.addCustomer(customer);
        System.out.println("Customer added successfully.");
    }

    private static void updateCustomer() {
        System.out.print("Enter customer ID to update: ");
        String id = scanner.nextLine();
        System.out.print("Enter new full name: ");
        String newName = scanner.nextLine();

        Customer updatedCustomer = customerManager.updateCustomer(id, newName); // 수정됨
        if (updatedCustomer != null) {
            System.out.println("Customer updated successfully.");
        } else {
            System.out.println("Customer not found.");
        }
    }

    private static void deleteCustomer() {
        System.out.print("Enter customer ID to delete: ");
        String id = scanner.nextLine();
        customerManager.deleteCustomer(id);
        System.out.println("Customer deleted successfully.");
    }

    private static void viewCustomer() {
        System.out.print("Enter customer ID to view: ");
        String id = scanner.nextLine();
        Customer customer = customerManager.getCustomerById(id);
        if (customer != null) {
            System.out.println("Customer Details: " + customer);
        } else {
            System.out.println("Customer not found.");
        }
    }

    private static void viewAllCustomers() {
        List<Customer> customers = customerManager.getAllCustomers();
        if (!customers.isEmpty()) {
            System.out.println("All Customers:");
            for (Customer customer : customers) {
                System.out.println(customer);
            }
        } else {
            System.out.println("No customers found.");
        }
    }
}
