package cuneiform;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class NameExtractor {
    private final List<Name> names;

    public NameExtractor() {
        names = readNames("names.txt");
    }

    private List<Name> readNames(String path) {
        List<Name> output = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() == false && trimmed.startsWith("//") == false) {
                    output.add(new Name(trimmed));
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return output;
    }

    public void print(PrintStream output) {
        for (Name name : names) {
            name.print(output);
            output.println();
        }
    }

    public void process(Tablet tablet) {
        for (Name name : names) {
            if (tabletContains(tablet, name)) {
                tablet.addName(name.name);
                name.addTablet(tablet);
            }
        }
    }

    private boolean tabletContains(Tablet tablet, Name name) {
        for (TabletSection sect : tablet.sections) {
            for (String line : sect.lines) {
                String[] parts = line.split(" ");
                for (String part : parts) {
                    if (part.equalsIgnoreCase(name.name)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

class Name {
    public final String        name;
    private final List<Tablet> tablets = new ArrayList<>();

    public Name(String name) {
        this.name = name;
    }

    public void addTablet(Tablet t) {
        tablets.add(t);
    }

    public void print(PrintStream output) {
        output.format("name: %-20s appearing in %d tablets%n", name, tablets.size());
        for (Tablet t : tablets) {
            String month = (t.foundMonth == null) ? ("") : (t.foundMonth.date.canonical);
            String year = (t.foundYear == null) ? ("") : (t.foundYear.date.canonical);
            output.format("  %-40s %-20s %s%n", t.name, year, month);
        }
    }
}