import java.io.DataOutputStream;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class VoWiFiClient {

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 9876;

        try {
            // Audio format settings
            AudioFormat audioFormat = getAudioFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            // Create a socket to connect to the server
            Socket socket = new Socket(serverAddress, serverPort);
            System.out.println("Connected to server: " + serverAddress);

            // Get the output stream to send data to the server
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            // Create a thread for capturing and sending audio
            Thread sendThread = new Thread(() -> {
                try {
                    byte[] buffer = new byte[1024];
                    int bytesRead;

                    while (true) {
                        bytesRead = targetDataLine.read(buffer, 0, buffer.length);
                        outputStream.write(buffer, 0, bytesRead);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            sendThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static AudioFormat getAudioFormat() {
        float sampleRate = 8000.0F;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }
}
