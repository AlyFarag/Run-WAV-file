package TCP_server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class TCP_Server {

    public static void main(String[] args) {
        
        try (ServerSocket serverSocket = new ServerSocket(3000)) {
            System.out.println("Server listening on port 3000...");

            while (true) {
                try (Socket socket = serverSocket.accept();
                     Scanner fromClient = new Scanner(socket.getInputStream())) {

                    // Read data from the client and print it line by line
                    while (fromClient.hasNextLine()) {
                        String inputMsg = fromClient.nextLine();

                        // Run the WAV file
                        runWavFile(inputMsg);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }
    
    
    private static void runWavFile(String filePath) {
        try {
            
            
            File file = new File(filePath);

            if (!file.exists()) {
                System.out.println("File not found: " + file.getAbsolutePath());
                return;
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            
            //System.out.println("Press enter to stop playback...");
            
            String response2="";
            
            while(!response2.equals("Q")) {
                System.out.println("Input\nP =  Play \nS = Stop \nQ = Quit");
                System.out.print("Your choice : ");
                Scanner input = new Scanner(System.in);
                
                response2 = input.next();
                response2 = response2.toUpperCase();
                
                switch(response2)
                {
                    case "P":
                        clip.start();
                        break;
                    case "S":
                        clip.stop();
                        break;
                    case "Q":
                        clip.close();
                        break;
                     default:
                         System.out.println("Invalid Input");
                }
            }
            

            // Play the audio
            //clip.start();

            

            // Close the input stream
            audioInputStream.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

