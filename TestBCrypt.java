import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBCrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "senha123";
        String hashFromDB = "$2a$10$N9qo8uLOickgx2ZMRZoMye7VFnqnQ1iR.6VWjRQE5f26J7StNZM.6";
        
        System.out.println("Raw password: " + rawPassword);
        System.out.println("Hash from DB: " + hashFromDB);
        System.out.println("Matches: " + encoder.matches(rawPassword, hashFromDB));
        
        String newHash = encoder.encode(rawPassword);
        System.out.println("New hash: " + newHash);
        System.out.println("New hash matches: " + encoder.matches(rawPassword, newHash));
    }
}
