package testclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class TestClient {

    private static final String SERVER_URL = "http://localhost:8080/api/documenti";

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("CLIENT DI TEST - CRUD DOCUMENTALE");
        while (true) {
            System.out.println("\n1) Upload documento\n2) Ricerca documento\n3) Download documento\n4) Esci");
            System.out.print("Scelta: ");
            String scelta = scanner.nextLine();

            switch (scelta) {
                case "1":
                    uploadDocumento(scanner);
                    break;
                case "2":
                    ricercaDocumento(scanner);
                    break;
                case "3":
                    downloadDocumento(scanner);
                    break;
                case "4":
                    System.exit(0);
                default:
                    System.out.println("Opzione non valida");
            }
        }
    }

    private static void uploadDocumento(Scanner scanner) throws IOException {
        System.out.print("Percorso file da caricare: ");
        String filePath = scanner.nextLine();

        System.out.print("Autore: ");
        String autore = scanner.nextLine();

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File non trovato.");
            return;
        }

        String boundary = Long.toHexString(System.currentTimeMillis());
        URL url = new URL(SERVER_URL + "/salva");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (
            OutputStream output = conn.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true)
        ) {
            // --- Parte file
            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(file.getName()).append("\"\r\n");
            writer.append("Content-Type: application/octet-stream\r\n\r\n");
            writer.flush();

            try (FileInputStream inputStream = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                output.flush();
            }
            writer.append("\r\n").flush();

            // --- Parte autore
            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"autore\"\r\n\r\n");
            writer.append(autore).append("\r\n").flush();

            // --- Fine multipart
            writer.append("--").append(boundary).append("--\r\n").flush();
        }

        int responseCode = conn.getResponseCode();
        System.out.println("Risposta server: " + responseCode);
        if (responseCode == 200 || responseCode == 201) {
            System.out.println("Upload avvenuto con successo.");
        } else {
            System.out.println("Errore nell'upload.");
            printError(conn);
        }
    }

    private static void ricercaDocumento(Scanner scanner) throws IOException {
        System.out.print("Inserisci titolo (o parte): ");
        String query = scanner.nextLine();

        URL url = new URL(SERVER_URL + "/ricerca?titolo=" + query);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        printResponse(conn);
    }

    private static void downloadDocumento(Scanner scanner) throws IOException {
        System.out.print("Inserisci ID documento da scaricare: ");
        String id = scanner.nextLine();

        URL url = new URL(SERVER_URL + "/" + id + "/download");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            // Estrai nome file da Content-Disposition
            String contentDisposition = conn.getHeaderField("Content-Disposition");
            String fileName = "documento_sconosciuto";

            if (contentDisposition != null && contentDisposition.contains("filename=")) {
                fileName = contentDisposition.split("filename=")[1].replace("\"", "").trim();
            }

            // Percorso cartella Downloads dell'utente
            String userHome = System.getProperty("user.home");
            File downloadDir = new File(userHome, "Downloads");
            if (!downloadDir.exists()) {
                downloadDir.mkdirs();
            }

            File downloadedFile = new File(downloadDir, fileName);

            try (InputStream in = conn.getInputStream();
                 FileOutputStream out = new FileOutputStream(downloadedFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            System.out.println("✅ Download completato: " + downloadedFile.getAbsolutePath());
        } else {
            System.out.println("Errore durante il download.");
            printError(conn);
        }
    }

    private static void printResponse(HttpURLConnection conn) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    private static void printError(HttpURLConnection conn) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.err.println(line);
            }
        }
    }
}

