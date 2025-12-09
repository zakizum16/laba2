import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Main {
    private static final String QUIT = "quit";
    private static final Scanner scanner = new Scanner(System.in);
    private static final FileParser parser = new FileParser();
    private static final Analyzer analyzer = new Analyzer();

    public static void main(String[] args) {
        System.out.println("Добро пожаловать в анализатор адресов!");
        System.out.println("Введите путь к файлу или '" + QUIT + "' для выхода.");

        while (true) {
            System.out.print("\n> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase(QUIT)) {
                System.out.println("Завершение работы программы...");
                break;
            }

            File file = new File(input);
            if (!file.exists() || !file.isFile()) {
                System.out.println("Ошибка: Файл не существует.");
                continue;
            }

            try {
                Instant start = Instant.now();
                List<AddressRecord> records = parser.parse(file);
                Map<String, Object> stats = analyzer.analyze(records);
                Instant end = Instant.now();

                printStats(stats, Duration.between(start, end).toMillis());
            } catch (Exception e) {
                System.err.println("Ошибка: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static void printStats(Map<String, Object> stats, long durationMs) {
        System.out.println("\n=== СТАТИСТИКА ===");
        System.out.println("Обработано записей: " + stats.get("total"));
        System.out.println("Время обработки: " + durationMs + " мс");


        Map<AddressRecord, Long> duplicates = (Map<AddressRecord, Long>) stats.get("duplicates");
        System.out.println("\n1) Дублирующиеся записи:");
        if (duplicates.isEmpty()) {
            System.out.println("  Нет дубликатов.");
        } else {
            for (Map.Entry<AddressRecord, Long> entry : duplicates.entrySet()) {
                System.out.println("  " + entry.getKey() + " (повторений: " + entry.getValue() + ")");
            }
        }


        Map<String, Map<Integer, Long>> cityFloorStats = (Map<String, Map<Integer, Long>>) stats.get("cityFloorStats");
        System.out.println("\n2) Количество зданий по этажам в каждом городе:");
        for (Map.Entry<String, Map<Integer, Long>> cityEntry : cityFloorStats.entrySet()) {
            String city = cityEntry.getKey();
            Map<Integer, Long> floors = cityEntry.getValue();
            System.out.println("  Город '" + city + "':");
            for (int floor = 1; floor <= 5; floor++) {
                long count = floors.getOrDefault(floor, 0L);
                System.out.println("    Этаж " + floor + ": " + count + " зданий");
            }
        }

        System.out.println("\nАнализ завершен.");
    }
}