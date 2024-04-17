package LireDernierLigne;
import java.io.IOException;
import java.io.RandomAccessFile;

public class LireDerniereLigneFichier {
    private String nomFichier;

    public LireDerniereLigneFichier(String path){
        this.nomFichier = "src/"+path+"/fichier.txt";
    }

    public String lireLigne(){
        try {
            RandomAccessFile file = new RandomAccessFile(nomFichier, "r");
            long position = file.length();

            // Move the file pointer to the end of the file
            position--;

            StringBuilder lastLine = new StringBuilder();

            // Start reading from the end of the file
            for (long pointer = position; pointer >= 0; pointer--) {
                file.seek(pointer);
                char c = (char) file.read();

                // If it's a newline character, we've reached the end of the last line
                if (c == '\n') {
                    // If the last line is not empty, return it
                    if (lastLine.length() > 0) {
                        return lastLine.reverse().toString();
                    }
                } else {
                    lastLine.append(c);
                }
            }

            // If there's only one line in the file
            if (lastLine.length() > 0) {
                return lastLine.reverse().toString();
            }

            file.close();
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier : " + e.getMessage());
        }
        return ""; // Return empty string if file is empty or an error occurred
    }
}