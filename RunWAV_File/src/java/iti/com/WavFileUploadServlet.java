
package iti.com;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.PrintWriter;
import java.net.Socket;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

@WebServlet("/WavFileUploadServlet")
@MultipartConfig
public class WavFileUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            
            // Get the uploaded WAV file as a part
            Part wavPart = request.getPart("wavFile");
            String fileName = getFileName(wavPart);

            //Define directory to save the WAV file
            String uploadDir = getServletContext().getRealPath("/") + "uploads/";
            Path uploadPath = Path.of(uploadDir);

            // Create the directory if it doesn't exist
            Files.createDirectories(uploadPath);

            // Save the WAV file to the server
            Path filePath = uploadPath.resolve(fileName);
            try (InputStream inputStream = wavPart.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            // Apply the TCP chatting protocol
            try (Socket socket = new Socket("localhost", 3000);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                // Send the WAV file path to the server
                out.println(filePath.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            response.getWriter().println("WAV file uploaded successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            
        }
    }

    //Make the file part final to enforce the user from changing the filename
    private String getFileName(final Part part) {
        final String partHeader = part.getHeader("content-disposition");
        for (String content : partHeader.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}