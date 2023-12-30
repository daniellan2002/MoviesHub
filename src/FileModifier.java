import java.io.*;

public class FileModifier {
    public static void main(String[] args) {
        try {
            // Open the input file for reading
            File inputFile = new File("moviedata.sql");
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));

            // Open the output file for writing
            File outputFile = new File("onlyStars.sql");
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            // Loop over each line in the input file
            String line;
            while ((line = reader.readLine()) != null) {
                // Check if the line starts with "INSERT INTO stars (id, name)"
                if (line.startsWith("INSERT INTO stars (id, name)")) {
                    // Write the line to the output file
                    writer.write(line);
                    writer.newLine();
                }
            }

            // Close the input and output files
            reader.close();
            writer.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
