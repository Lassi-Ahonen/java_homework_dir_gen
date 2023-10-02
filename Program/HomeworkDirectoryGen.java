import java.io.*;
import java.nio.file.*;

/**
 * This program generates directories (and template files) for our Java course
 * homework.
 * 
 * Decided to create this because our teacher likes to see us suffer creating
 * 10+ directories by hand after each leacture. <3
 * 
 * If you have any issues leave them in my Github repo and i'll try to fix them.
 * 
 * @version 1.0
 * @author Lassi Ahonen - 23TIKO3
 */
public class HomeworkDirectoryGen {

    public static void main(String[] args) {
        HomeworkDirectoryGen program = new HomeworkDirectoryGen();
        Options options = program.getOptions();

        try {
            program.createExerciseDirectory(options);
            program.createExercises(options);
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Contact Jussi for more information about this error.");
            return;
        }

        System.out.println("\nSuccessfully generated directories.");
    }

    /**
     * Options and path validation for creating directories.
     */
    private class Options {

        public int exerciseAmount;
        public String exerciseDirName;

        private Path m_OutputPath;
        private Path m_TemplatePath;
        private Path m_ExercisePath;

        /**
         * Gets output directory path.
         * 
         * @return Path.
         */
        public Path getOutputPath() {
            return this.m_OutputPath;
        }

        /**
         * Sets and validates output directory path.
         * 
         * @param path Path to set.
         * @return true when path is valid, otherwise false.
         */
        public boolean setOutputPath(String path) {
            Path temp = Path.of(sanitizePath(path));
            if (validatePath(temp, false)) {
                this.m_OutputPath = temp;
                return true;
            }
            return false;
        }

        /**
         * Gets template file path.
         * 
         * @return Path.
         */
        public Path getTemplatePath() {
            return this.m_TemplatePath;
        }

        /**
         * Sets and validates template file path.
         * 
         * @param path Path to set.
         * @return true when path is valid, otherwise false.
         */
        public boolean setTemplatePath(String path) {
            Path temp = Path.of(sanitizePath(path));
            if (validatePath(temp, true)) {
                this.m_TemplatePath = temp;
                return true;
            }
            return false;
        }

        /**
         * Gets exercise directory path.
         * 
         * @return Path.
         */
        public Path getExercisePath() {
            return this.m_ExercisePath;
        }

        /**
         * Sets and validates exercise directory path.
         * 
         * @param path Path to set.
         * @return true when path is valid, otherwise false.
         */
        public boolean setExercisePath(Path path) {
            if (validatePath(path, false)) {
                this.m_ExercisePath = path;
                return true;
            }
            return false;
        }

        /**
         * Has user specified a template file to use.
         * 
         * @return true when template file is present, otherwise false.
         */
        public boolean isTemplatePathSet() {
            return this.m_TemplatePath != null;
        }

        /**
         * Checks if path exists and has valid file/directory.
         * 
         * @param path   Path to check.
         * @param isFile true = checks if path has file | false = checks for directory.
         * @return true when path is valid, otherwise false.
         */
        private boolean validatePath(Path path, boolean isFile) {
            boolean flag = isFile ? Files.isRegularFile(path) : Files.isDirectory(path);
            return Files.exists(path) && flag ? true : false;
        }

        /**
         * Removes possible quotation marks from the path.
         * 
         * @param path Path to sanitize.
         * @return Sanitized path.
         */
        private String sanitizePath(String path) {
            String sanitizedPath = path;
            sanitizedPath = sanitizedPath.replaceAll("^\"|\"$", "");
            sanitizedPath = sanitizedPath.replaceAll("^\'|\'$", "");
            return sanitizedPath;
        }

    }

    /**
     * Asks user for options, creates and validates user given inputs.
     * 
     * @return Options object containing valid path information and options.
     */
    private Options getOptions() {
        Console console = System.console();
        Options options = new Options();

        // Ask folder where user wants files to be generated at
        while (true) {
            boolean isPathValid = options.setOutputPath(console.readLine("\nInput output path [homework directory]: "));
            if (!isPathValid) {
                System.out.println("Invalid output path, try again!");
                continue;
            }

            break;
        }

        // Ask weekly excercise number
        options.exerciseDirName = console.readLine("\nInput root directory name to generate [01, 02... etc]: ");

        // Ask how many directories to generate in total
        while (true) {
            try {
                int amount = Integer
                        .parseInt(console.readLine("\nHow many exercises to generate? [e01, e02... etc]: "));
                if (amount <= 0) {
                    System.out.println("Cannot generate less or equal to 0 number of directories!");
                    continue;
                }
                if (amount > 99) {
                    System.out.println(
                            "Cannot generate more than 99 directories, go ask Jussi why we can't have more than 99 exercises :(");
                    continue;
                }
                options.exerciseAmount = amount;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, try again!");
                continue;
            }
            break;
        }

        // Ask user if they want to generate template java file also
        while (true) {
            System.out.println(
                    "\nOptional: Generates a template file for each exercise, you can leave this empty if none should be generated.");
            String optTemplate = console.readLine("Input path of template file to generate [press enter to skip]: ");

            // Left empty, stop asking for input
            if (optTemplate.isBlank()) {
                break;
            }

            boolean isPathValid = options.setTemplatePath(optTemplate);

            if (!isPathValid) {
                System.out.println("Invalid template path, try again!");
                continue;
            }

            break;
        }

        return options;
    }

    /**
     * Creates a new exercises folder with user specified name. Usually 01, 02
     * etc...
     * If one already exists, uses that instead.
     * 
     * @param options Path info.
     * @throws IOException Thrown when there is a problem creating the directory.
     */
    private void createExerciseDirectory(Options options) throws IOException {
        Path exercisePath = Paths.get(options.getOutputPath().toString(), options.exerciseDirName);
        Files.createDirectories(exercisePath);
        if (!options.setExercisePath(exercisePath)) {
            throw new IOException("Exercise directory could not be created.");
        }
    }

    /**
     * Creates a new directory for user specified amount of exercises. Named e01,
     * e02 etc...
     * Optionally creates a template file inside the created directory.
     * If file already exists with the same name, template will not be generated.
     * 
     * @param options Path info.
     * @throws IOException Thrown when there is a problem creating directory or
     *                     file.
     */
    private void createExercises(Options options) throws IOException {
        String basePath = options.getExercisePath().toString();
        boolean shouldGenerateTemplates = options.isTemplatePathSet();

        String templateName = "Main.java";
        byte[] buffer = null;

        // Read template to memory
        if (shouldGenerateTemplates) {
            templateName = options.getTemplatePath().getFileName().toString();
            // Way better than trying to fiddle with different charsets and encodings
            buffer = Files.readAllBytes(options.getTemplatePath());
        }

        // Generate directories (and templates)
        for (int i = 0; i < options.exerciseAmount; i++) {
            int current = i + 1;
            String formattedName = String.format("e%02d", current);
            Path exercisePath = Paths.get(basePath, formattedName);
            Files.createDirectories(exercisePath);

            // Check if we should skip generating template
            if (!shouldGenerateTemplates || buffer == null) {
                continue;
            }

            // Creates a new file if one does not exist yet and writes buffer in it
            Path filePath = Paths.get(exercisePath.toString(), templateName);
            File file = new File(filePath.toString());
            if (file.createNewFile()) {
                try (FileOutputStream stream = new FileOutputStream(file);) {
                    try (DataOutputStream binaryWriter = new DataOutputStream(stream);) {
                        binaryWriter.write(buffer);
                    }
                }
            }
        }
    }

}