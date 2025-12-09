import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileParser {

    public List<AddressRecord> parse(File file) throws Exception {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".xml")) {
            return parseXml(file);
        } else if (name.endsWith(".csv")) {
            return parseCsv(file);
        }
        throw new IllegalArgumentException("Поддерживаются только .xml и .csv файлы");
    }

    private List<AddressRecord> parseXml(File file) throws Exception {
        List<AddressRecord> records = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("<item ")) {
                    String city = extractAttribute(line, "city");
                    String street = extractAttribute(line, "street");
                    String houseStr = extractAttribute(line, "house");
                    String floorStr = extractAttribute(line, "floor");
                    int house = Integer.parseInt(houseStr.trim());
                    int floor = Integer.parseInt(floorStr.trim());
                    records.add(new AddressRecord(city, street, house, floor));
                }
            }
        }
        return records;
    }

    private String extractAttribute(String line, String attr) {
        String pattern = attr + "=\"";
        int start = line.indexOf(pattern);
        if (start == -1) return "";
        start += pattern.length();
        int end = line.indexOf("\"", start);
        if (end == -1) return "";
        return line.substring(start, end);
    }

    private List<AddressRecord> parseCsv(File file) throws Exception {
        List<AddressRecord> records = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = splitCsvLine(line);
                if (parts.length < 4) continue;
                String city = parts[0].replaceAll("^\"|\"$", "");
                String street = parts[1].replaceAll("^\"|\"$", "");
                int house = Integer.parseInt(parts[2].trim());
                int floor = Integer.parseInt(parts[3].trim());
                records.add(new AddressRecord(city, street, house, floor));
            }
        }
        return records;
    }

    private String[] splitCsvLine(String line) {
        java.util.List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ';' && !inQuotes) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }
}