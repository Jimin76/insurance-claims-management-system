import java.io.Serializable;
import java.util.UUID;

public class Customer implements Serializable {
    private String id;
    private String fullName;

    public Customer(String fullName) {
        // UUID를 사용하여 랜덤하고 고유한 ID 생성
        this.id = UUID.randomUUID().toString();
        this.fullName = fullName;
    }

    // fullName 속성을 업데이트하는 메소드 추가
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public String toString() {
        return "Customer{" + "id='" + id + '\'' + ", fullName='" + fullName + '\'' + '}';
    }
}
