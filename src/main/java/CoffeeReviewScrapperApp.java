import crawler.GetCoffeeReviewDetails;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class CoffeeReviewScrapperApp {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Start Web Scrap? (Y/N)");
            var cmd = scanner.next();
            switch (cmd.toUpperCase()) {
               case "Y" -> {
                   int from = getStartPage(scanner);
                   int to = getEndPage(scanner, from);
                   String filePath  = getFileNameToSave(scanner);
                   GetCoffeeReviewDetails.start(filePath, from, to);
                   System.out.println("finished with success!");
                   break;
               }
               case "N" -> {
                   System.out.println("program exit");
                   System.exit(1);
               }
                default -> {
                    System.out.println("invalid response");
                }
            }
        }
    }

    private static String getFileNameToSave(Scanner scanner) {
        System.out.println("enter the file name to save:");
        while (true) {
            String filename = scanner.next();
            Path currentRelativePath = Paths.get("");
            String s = currentRelativePath.toAbsolutePath().toString() + "\\";
            return filename + ".json";
        }
    }

    private static int getEndPage(Scanner scanner, int from) {
        System.out.println("enter end page: (starts from " + from + " and less than or equal to 349)");
        while (true) {
            int toPage = scanner.nextInt();
            if (toPage < from) {
                System.out.println("cannot be less than from page");
            } else if (toPage > 349) {
                System.out.println("cannot exceed more than 349 pages!");
                System.out.println("As of December 2022, coffeereviews has only 6980 written reviews.");
            } else {
                return toPage;
            }

        }
    }

    private static int getStartPage(Scanner scanner) {
        System.out.println("enter start page: (less than or equal to 349)");
        while (true) {
            int fromPage = scanner.nextInt();
            if (fromPage < 0) {
                System.out.println("cannot be negative");
            } else if (fromPage > 349) {
                System.out.println("cannot be more than 349");
            } else {
                return fromPage;
            }
        }
    }
}