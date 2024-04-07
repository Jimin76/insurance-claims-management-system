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
import java.util.ArrayList;
import java.util.UUID;
import java.text.SimpleDateFormat;
import java.text.ParseException;
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
                System.out.println("3. Claim Management System"); // "Under development" 제거
                System.out.println("4. Exit");
                System.out.print("Enter choice: ");

                int mainChoice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (mainChoice) {
                    case 1:
                        manageCustomers();
                        break;
                    case 2:
                        viewAllInsuranceCards();
                        break;
                    case 3:
                        manageClaims(); // 클레임 관리 시스템 호출
                        break;
                    case 4:
                        exit = true;
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter 1-4.");
                }
            } catch (InputMismatchException ime) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // 입력 스트림을 지우고 다시 사용자 입력을 받을 준비를 함
            }
        }
    }

    private static void manageCustomers() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Customer Management System ---");
            System.out.println("1. Add Customer");
            System.out.println("2. View Customer");
            System.out.println("3. Update Customer");
            System.out.println("4. Delete Customer");
            System.out.println("5. List All Customers");
            System.out.println("6. Back");
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
                    updateCustomer();
                    break;
                case 4:
                    deleteCustomer();
                    break;
                case 5:
                    listAllCustomers();
                    break;
                case 6:
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

    private static void manageClaims() {
        while (true) {
            System.out.println("\n--- Claim Management System ---");
            System.out.println("1. Add Claim");
            System.out.println("2. View Claim");
            System.out.println("3. Update Claim");
            System.out.println("4. Delete Claim");
            System.out.println("5. List All Claims");
            System.out.println("6. Back");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

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
                    return; // Back to main menu
                default:
                    System.out.println("Invalid choice. Please enter 1-6.");
            }
        }
    }

    public static void addClaim() {
        System.out.println("Enter the details for the new claim.");

        System.out.print("Enter customer ID: ");
        String customerId = scanner.nextLine();

        System.out.print("Enter claim date (yyyy-MM-dd): ");
        String claimDateStr = scanner.nextLine();
        Date claimDate = parseDate(claimDateStr); // 날짜 파싱 메소드 사용

        System.out.print("Enter exam date (yyyy-MM-dd): ");
        String examDateStr = scanner.nextLine();
        Date examDate = parseDate(examDateStr); // 날짜 파싱 메소드 사용

        System.out.print("Enter claim amount: ");
        double claimAmount = scanner.nextDouble();
        scanner.nextLine(); // 숫자 입력 후 newline 문자 처리

        System.out.print("Enter receiver banking info (Bank – Name – Number): ");
        String receiverBankingInfo = scanner.nextLine();

        // 새로운 Claim 객체 생성 및 설정
        Claim newClaim = new Claim(UUID.randomUUID().toString());
        newClaim.setInsuredPersonId(customerId);
        newClaim.setClaimDate(claimDate);
        newClaim.setExamDate(examDate);
        newClaim.setClaimAmount(claimAmount);
        newClaim.setReceiverBankingInfo(receiverBankingInfo);
        newClaim.setStatus("New"); // 상태 초기 설정

        // ClaimProcessManager를 통해 새 Claim 객체 추가
        claimProcessManager.addClaim(newClaim);
        System.out.println("Claim added successfully with ID: " + newClaim.getId());
    }

    private static Date parseDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            return null; // 또는 적절한 예외 처리
        }
    }
    private static void viewClaim() {
        System.out.print("Enter claim ID: ");
        String claimId = scanner.nextLine();

        Claim claim = claimProcessManager.getClaimById(claimId);
        if (claim != null) {
            System.out.println(claim);
        } else {
            System.out.println("Claim not found.");
        }
    }

    private static void updateClaim() {
        System.out.print("Enter claim ID: ");
        String claimId = scanner.nextLine();

        Claim claim = claimProcessManager.getClaimById(claimId);
        if (claim == null) {
            System.out.println("Claim not found.");
            return;
        }

        System.out.print("Enter new status (New, Processing, Done): ");
        String newStatus = scanner.nextLine();
        claim.setStatus(newStatus);

        claimProcessManager.updateClaim(claimId, claim);
        System.out.println("Claim updated successfully.");
    }

    private static void deleteClaim() {
        System.out.print("Enter claim ID: ");
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




