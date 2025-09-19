package Hello.Sugang.web;

public class ThreadCheck {
    public static void main(String[] args) {
        try {
            Process process = Runtime.getRuntime().exec("wmic cpu get NumberOfCores");
            process.getInputStream().transferTo(System.out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


