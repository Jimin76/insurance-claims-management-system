import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.InputMismatchException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.lang.ClassNotFoundException;
import java.util.Date;
import java.util.List;
//import java.util.ArrayList;
//import java.util.UUID;
import java.text.SimpleDateFormat;
import java.text.ParseException;


/**
 * @author <Jimin Cho - s3940575>
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final CustomerManager customerManager = new CustomerManagerImpl();
    private static final ClaimProcessManager claimProcessManager = new ClaimProcessManagerImpl(customerManager); // 클레임 관리자 인스턴스 추가
    private static final String INSURANCE_CARD_DIR = "./insurance cards/";

    public static void main(String[] args) {
        boolean exit = false;
        while (!exit) {
            try {
                System.out.println("\nWelcome to Insurance Claims Management System");
                System.out.println("Please select options above:");
                System.out.println("1. Customer Management System");
                System.out.println("2. View all Insurance Cards");
                System.out.println("3. Claim Management System");
                System.out.println("0. Exit");
                System.out.print("Enter choice: ");

                int mainChoice = scanner.nextInt();
                scanner.nextLine();

                switch (mainChoice) {
                    case 1:
                        manageCustomers();
                        break;
                    case 2:
                        viewAllInsuranceCards();
                        break;
                    case 3:
                        manageClaims();
                        break;
                    case 0:
                        exit = true;
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter 1-4.");
                }
            } catch (InputMismatchException ime) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    private static void manageCustomers() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Customer Management System ---");
            System.out.println("1. Add Customer");
            System.out.println("2. View Customer(getOne)");
            System.out.println("3. Update Customer");
            System.out.println("4. Delete Customer");
            System.out.println("5. List All Customers(getAll)");
            System.out.println("0. Back");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addCustomer();
                    break;
                case 2:
                    viewCustomer();
                    break;
                case 3:
                    updateCustomer();
                    break;
                case 4:
                    deleteCustomer();
                    break;
                case 5:
                    listAllCustomers();
                    break;
                case 0:
                    back = true;
                    break;

                default:
                    System.out.println("Invalid choice. Please enter 1-6.");
            }
        }
    }

    private static void viewAllInsuranceCards() {
        File folder = new File(INSURANCE_CARD_DIR);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    System.out.println("\nCard Details:");
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                        InsuranceCard card = (InsuranceCard) ois.readObject();
                        System.out.println(card);
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.out.println("No insurance cards found.");
        }
    }

    private static void addCustomer() {
        try {
            System.out.print("Enter customer's full name: ");
            String fullName = scanner.nextLine();
            Customer customer = new Customer(fullName, true);

            System.out.print("Does this customer have dependents? (yes/no): ");
            String hasDependents = scanner.nextLine();

            if ("yes".equalsIgnoreCase(hasDependents)) {
                do {
                    System.out.print("Enter dependent's full name: ");
                    String depFullName = scanner.nextLine();
                    System.out.print("Enter dependent's relationship (example: Husband): ");
                    String relationship = scanner.nextLine();

                    // Generate a unique ID
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
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }


    private static void viewCustomer() {
        System.out.print("Enter customer ID (c-numbers; 7 numbers): ");
        String id = scanner.nextLine();
        Customer customer = customerManager.getCustomerById(id);
        if (customer != null) {
            System.out.println(customer);
        } else {
            System.out.println("Customer not found.");
        }
    }

    private static void deleteCustomer() {
        try {
            System.out.print("Enter customer ID to delete (c-numbers; 7 numbers): ");
            String id = scanner.nextLine();
            if (customerManager.deleteCustomer(id)) {
                System.out.println("Customer deleted successfully.");
            } else {
                System.out.println("Failed to delete customer.");
            }
        } catch (Exception e) {
            System.err.println("An error occurred while attempting to delete customer: " + e.getMessage());
        }
    }


    private static void listAllCustomers() {
        System.out.println("Listing all customers:");
        customerManager.getAllCustomers().forEach(System.out::println);
    }

    private static void updateCustomer() {
        System.out.print("Enter the ID of the customer to update (c-numbers; 7 numbers): ");
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

        customer.getDependents().clear(); // delete existing dependent info
        if ("yes".equalsIgnoreCase(hasDependents)) {
            do {
                System.out.print("Enter dependent's full name: ");
                String depFullName = scanner.nextLine();
                System.out.print("Enter dependent's relationship (example: Husband): ");
                String relationship = scanner.nextLine();

                // generate new ID
                String depId = "d-" + ThreadLocalRandom.current().nextInt(1000000, 10000000);

                // policyOwnerId is dependent owner or custumers id
                String policyOwnerId = customer.getId();

                Dependent dependent = new Dependent(depId, depFullName, policyOwnerId, relationship);
                customer.addDependent(dependent);

                System.out.print("Would you like to add another dependent? (yes/no): ");
            } while ("yes".equalsIgnoreCase(scanner.nextLine()));
        }

        try {
            if (customerManager.updateCustomer(customer)) {
                System.out.println("Customer updated successfully.");
            } else {
                System.out.println("Failed to update customer.");
            }
        } catch (Exception e) {
            System.err.println("An error occurred while updating the customer: " + e.getMessage());
        }
    }


    private static void manageClaims() {
        while (true) {
            System.out.println("\n--- Claim Management System ---");
            System.out.println("1. Add Claim");
            System.out.println("2. View Claim(getOne)");
            System.out.println("3. Update Claim");
            System.out.println("4. Delete Claim");
            System.out.println("5. List All Claims(getAll)");
            System.out.println("6. Change Status");
            System.out.println("0. Back");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addClaim();
                    break;
                case 2:
                    viewClaim();
                    break;
                case 3:
                    updateClaim();
                    break;
                case 4:
                    deleteClaim();
                    break;
                case 5:
                    listAllClaims();
                    break;
                case 6:
                    System.out.print("Enter claim ID for status change (f-numbers; 10 numbers): ");
                    String claimIdForStatus = scanner.nextLine();
                    System.out.print("Enter new status (New, Processing, Done): ");
                    String newStatus = scanner.nextLine();
                    try {
                        claimProcessManager.changeStatus(claimIdForStatus, newStatus);
                    } catch (IOException e) {
                        System.out.println("Error changing claim status.");
                        e.printStackTrace();
                    }
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Please enter 1-6.");
            }
        }
    }

    public static void addClaim() {
        System.out.print("Enter customer ID (c-numbers; 7 numbers): ");
        String customerId = scanner.nextLine();

        try {
            claimProcessManager.addClaimWithCustomerID(customerId);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("An error occurred while adding the claim.");
            e.printStackTrace();
        }
    }

    private static Date parseDate(String dateString) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            return null;
        }
    }
    private static void viewClaim() {
        System.out.print("Enter claim ID (f-numbers; 10 numbers): ");
        String claimId = scanner.nextLine();

        Claim claim = claimProcessManager.getClaimById(claimId);
        if (claim != null) {
            System.out.println(claim);
        } else {
            System.out.println("Claim not found.");
        }
    }

    private static void updateClaim() {
        System.out.print("Enter claim ID (f-numbers; 10 numbers): ");
        String claimId = scanner.nextLine();

        try {
            claimProcessManager.updateClaim(claimId);
        } catch (IOException | ParseException e) {
            System.out.println("An error occurred while updating the claim.");
            e.printStackTrace();
        }
    }

    private static void deleteClaim() {
        System.out.print("Enter claim ID (f-numbers; 10 numbers): ");
        String claimId = scanner.nextLine();

        claimProcessManager.deleteClaim(claimId);
        System.out.println("Claim deleted successfully.");
    }

    private static void listAllClaims() {
        List<Claim> claims = claimProcessManager.getAllClaims();
        for (Claim claim : claims) {
            System.out.println(claim);
        }
    }


}




