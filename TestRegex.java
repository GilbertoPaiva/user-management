public class TestRegex {
    public static void main(String[] args) {
        String pattern = ".*[@#$%^&+=!?*()_\\-\\[\\]{}|;:,.<>\"'`~\\\\/].*";
        String test1 = "Password123\\!";
        String test2 = "Password123!";
        
        System.out.println("Testing: " + test1);
        System.out.println("Matches: " + test1.matches(pattern));
        
        System.out.println("Testing: " + test2);
        System.out.println("Matches: " + test2.matches(pattern));
        
        String[] specialChars = {"!", "\\", "\\!"};
        for (String ch : specialChars) {
            System.out.println("Char '" + ch + "' matches: " + ch.matches(".*[@#$%^&+=!?*()_\\-\\[\\]{}|;:,.<>\"'`~\\\\/].*"));
        }
    }
}
