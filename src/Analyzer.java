import java.util.*;

public class Analyzer {

    public Map<String, Object> analyze(List<AddressRecord> records) {
        Map<AddressRecord, Long> duplicates = new HashMap<>();
        Map<String, Map<Integer, Long>> cityFloorStats = new HashMap<>();

        for (AddressRecord record : records) {
            duplicates.merge(record, 1L, Long::sum);

            cityFloorStats.computeIfAbsent(record.getCity(), k -> new HashMap<>());
            Map<Integer, Long> floorMap = cityFloorStats.get(record.getCity());
            floorMap.merge(record.getFloor(), 1L, Long::sum);
        }

        duplicates.entrySet().removeIf(entry -> entry.getValue() <= 1);

        Map<String, Object> result = new HashMap<>();
        result.put("total", (long) records.size());
        result.put("duplicates", duplicates);
        result.put("cityFloorStats", cityFloorStats);

        return result;
    }
}