import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final CustomerManager customerManager = new CustomerManagerImpl();

    public static void main(String[] args) {
        boolean exit = false;

        while (!exit) {
            System.out.println("\n--- Customer Management System ---");
            System.out.println("1. Add Customer");
            System.out.println("2. View Customer");
            System.out.println("3. Delete Customer");
            System.out.println("4. List All Customers");
            System.out.println("5. Exit");
            System.out.println("6. Update Customer");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addCustomer();
                    break;
                case 2:
                    viewCustomer();
                    break;
                case 3:
                    deleteCustomer();
                    break;
                case 4:
                    listAllCustomers();
                    break;
                case 5:
                    exit = true;
                    System.out.println("Exiting...");
                    break;
                case 6:
                    updateCustomer();
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1-5.");
            }
        }
    }

    private static void addCustomer() {
        System.out.print("Enter customer's full name: ");
        String fullName = scanner.nextLine();
        Customer customer = new Customer(fullName, true);

        System.out.print("Does this customer have dependents? (yes/no): ");
        String hasDependents = scanner.nextLine();

        if ("yes".equalsIgnoreCase(hasDependents)) {
            do {
                System.out.print("Enter dependent's full name: ");
                String depFullName = scanner.nextLine();
                System.out.print("Enter dependent's relationship: ");
                String relationship = scanner.nextLine();

                // 고유 ID 생성
                String depId = "c-" + ThreadLocalRandom.current().nextInt(1000000, 10000000);

                Dependent dependent = new Dependent(depId, depFullName, customer.getId(), relationship);
                customer.addDependent(dependent);

                System.out.print("Would you like to add another dependent? (yes/no): ");
            } while ("yes".equalsIgnoreCase(scanner.nextLine()));
        }

        if (customerManager.addCustomer(customer)) {
            System.out.println("Customer added successfully.");
        } else {
            System.out.println("Failed to add customer.");
        }
    }

    private static void viewCustomer() {
        System.out.print("Enter customer ID: ");
        String id = scanner.nextLine();
        Customer customer = customerManager.getCustomerById(id);
        if (customer != null) {
            System.out.println(customer);
        } else {
            System.out.println("Customer not found.");
        }
    }

    private static void deleteCustomer() {
        System.out.print("Enter customer ID to delete: ");
        String id = scanner.nextLine();
        if (customerManager.deleteCustomer(id)) {
            System.out.println("Customer deleted successfully.");
        } else {
            System.out.println("Failed to delete customer.");
        }
    }

    private static void listAllCustomers() {
        System.out.println("Listing all customers:");
        customerManager.getAllCustomers().forEach(System.out::println);
    }

    private static void updateCustomer() {
        System.out.print("Enter the ID of the customer to update: ");
        String id = scanner.nextLine();
        Customer customer = customerManager.getCustomerById(id);

        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }

        System.out.print("Enter new full name of the customer: ");
        String fullName = scanner.nextLine();
        customer.setFullName(fullName);

        System.out.print("Does this customer have dependents? (yes/no): ");
        String hasDependents = scanner.nextLine();

        customer.getDependents().clear(); // 기존 디펜던트 정보를 삭제
        if ("yes".equalsIgnoreCase(hasDependents)) {
            do {
                System.out.print("Enter dependent's full name: ");
                String depFullName = scanner.nextLine();
                System.out.print("Enter dependent's relationship: ");
                String relationship = scanner.nextLine();

                // 새로운 고유 ID 생성
                String depId = "d-" + ThreadLocalRandom.current().nextInt(1000000, 10000000);

                // policyOwnerId는 고객의 ID를 사용
                String policyOwnerId = customer.getId();

                Dependent dependent = new Dependent(depId, depFullName, policyOwnerId, relationship);
                customer.addDependent(dependent);

                System.out.print("Would you like to add another dependent? (yes/no): ");
            } while ("yes".equalsIgnoreCase(scanner.nextLine()));
        }

        if (customerManager.updateCustomer(customer)) {
            System.out.println("Customer updated successfully.");
        } else {
            System.out.println("Failed to update customer.");
        }
    }
}



