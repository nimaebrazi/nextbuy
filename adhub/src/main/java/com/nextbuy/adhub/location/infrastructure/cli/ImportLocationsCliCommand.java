package com.nextbuy.adhub.location.infrastructure.cli;

import com.nextbuy.adhub.location.application.command.importdata.ImportLocationHierarchyCommand;
import com.nextbuy.adhub.location.application.command.importdata.ImportLocationHierarchyCommandHandler;
import com.nextbuy.adhub.location.application.command.importdata.ImportLocationHierarchyResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("cli")
public class ImportLocationsCliCommand implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ImportLocationsCliCommand.class);
    private static final String DEFAULT_FILE = "classpath:data/location-sample.json";

    private final ImportLocationHierarchyCommandHandler handler;

    public ImportLocationsCliCommand(ImportLocationHierarchyCommandHandler handler) {
        this.handler = handler;
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length == 0 || isHelp(args)) {
            printUsage();
            System.exit(args.length == 0 ? 1 : 0);
            return;
        }

        if (!"import-locations".equals(args[0])) {
            System.err.println("Unknown command: " + args[0]);
            printUsage();
            System.exit(1);
            return;
        }

        CliOptions options = parseOptions(args);
        if (options.help()) {
            printUsage();
            System.exit(0);
            return;
        }

        try {
            ImportLocationHierarchyResult result = handler.handle(
                    new ImportLocationHierarchyCommand(options.filePath(), options.dryRun())
            );
            log.info(
                    "Imported: {} country, {} provinces, {} cities",
                    result.countriesCreated(),
                    result.provincesCreated(),
                    result.citiesCreated()
            );
            log.info(
                    "Skipped:  {} country, {} provinces, {} cities (already exist)",
                    result.countriesSkipped(),
                    result.provincesSkipped(),
                    result.citiesSkipped()
            );
            if (options.dryRun()) {
                log.info("Dry run complete — no data was written");
            }
        } catch (IOException exception) {
            log.error("Failed to read location file: {}", options.filePath(), exception);
            System.exit(1);
        } catch (RuntimeException exception) {
            log.error("Location import failed", exception);
            System.exit(1);
        }
    }

    private static CliOptions parseOptions(String[] args) {
        String filePath = DEFAULT_FILE;
        boolean dryRun = false;
        boolean help = false;

        for (int index = 1; index < args.length; index++) {
            String arg = args[index];
            switch (arg) {
                case "--file", "-f" -> {
                    if (index + 1 >= args.length) {
                        throw new IllegalArgumentException("Missing value for " + arg);
                    }
                    filePath = args[++index];
                }
                case "--dry-run" -> dryRun = true;
                case "--help", "-h" -> help = true;
                default -> throw new IllegalArgumentException("Unknown option: " + arg);
            }
        }

        return new CliOptions(filePath, dryRun, help);
    }

    private static boolean isHelp(String[] args) {
        for (String arg : args) {
            if ("--help".equals(arg) || "-h".equals(arg)) {
                return true;
            }
        }
        return false;
    }

    private static void printUsage() {
        List<String> lines = new ArrayList<>();
        lines.add("Usage: import-locations --file <path> [--dry-run]");
        lines.add("");
        lines.add("Arguments:");
        lines.add("  --file, -f     Path to JSON (default: classpath:data/location-sample.json)");
        lines.add("  --dry-run      Validate and count without writing to DB");
        lines.add("  --help, -h     Show usage");
        lines.forEach(System.out::println);
    }

    private record CliOptions(String filePath, boolean dryRun, boolean help) {
    }
}
