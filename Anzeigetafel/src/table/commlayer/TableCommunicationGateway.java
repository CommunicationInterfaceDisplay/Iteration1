
package table.commlayer;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author Tobias Müller
 * @version 1.0
 * @date 02.07.2015
 * 
 * Kommunikationsschnittstelle zwischen Tafeln und Clients
 */
public class TableCommunicationGateway extends Thread {

    private final ConcurrentSkipListMap<String, InetAddress> tableRegister = new ConcurrentSkipListMap<>();
    private final RegistrationService regService;
    private final MessageConsumingService msgConsumingService;
    private final MessagePublishingService msgPublishingService;
    private final int registrationServicePort = 9876;
    private final int messageTransportServicePort = 12345;
    
    /**
     * Konstruktor des TableCommunicationGateway (TCG)
     * @param pis
     * @param pos
     * @throws IOException
     */
    public TableCommunicationGateway(PipedInputStream pis, PipedOutputStream pos) throws IOException {
        // UDP-Datagramm schicken zur Anmeldung bei allen verfügbaren Tafeln
        
        System.out.println(RegistrationService.class.getSimpleName() + " wird konfiguriert...");
        regService = new RegistrationService(registrationServicePort);

        System.out.println(MessageConsumingService.class.getSimpleName() + " wird konfiguriert...");
        msgConsumingService = new MessageConsumingService(messageTransportServicePort, pos);

        System.out.println(MessagePublishingService.class.getSimpleName() + " wird konfiguriert...");
        msgPublishingService = new MessagePublishingService(messageTransportServicePort, pis);
    }

    @Override
    public void run() {
        System.out.println(RegistrationService.class.getSimpleName() + " wird gestartet...");
        regService.start();

        System.out.println(MessageConsumingService.class.getSimpleName() + " wird gestartet...");
        msgConsumingService.start();

        System.out.println(MessagePublishingService.class.getSimpleName() + " wird gestartet...");
        msgPublishingService.start();
    }

    /**
     * Tafelregistrierungsdienst (RS)
     */
    public class RegistrationService extends Thread {

        final int port;
        DatagramSocket serverSocket;

        /**
        * 
         * @param port
        */
        public RegistrationService(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            try {
                serverSocket = new DatagramSocket(port);

                while (true) {
                    byte[] receiveData = new byte[1024];

                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);

                    String tableId = new String(receivePacket.getData());
                    InetAddress tableIP = receivePacket.getAddress();

                    System.out.println(new String(receivePacket.getData()));
                    System.out.println(receivePacket.getAddress().getHostAddress());

                    //System.out.println(tableRegister.values());
                    if (tableRegister.containsKey(tableId) & tableRegister.containsValue(tableIP)) {
                        System.out.println("table schon angemeldet!");
                        // table war wohl zur laufzeit neugestartet worden! alle globalen nachrichten neu senden!
                    } else {
                        tableRegister.put(tableId, receivePacket.getAddress());
                    }
                }
            } catch (SocketException ex) {
            } catch (IOException ex) {
            } finally {
                serverSocket.close();
            }
        }
    }

    /**
     * Nachrichtenempfangsdienst (MCS)
     */
    public class MessageConsumingService extends Thread {

        private final int port;
        private final ServerSocket serverSocket;
        private final PipedOutputStream pipedOutputStream;

        /**
         * Konstruktor
         * @param port
         * @param pos
         * @throws IOException 
         */
        public MessageConsumingService(int port, PipedOutputStream pos) throws IOException {
            this.port = port;
            this.serverSocket = new ServerSocket(this.port);
            this.serverSocket.setSoTimeout(10000);
            this.pipedOutputStream = pos;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();

                    if (!tableRegister.containsValue(clientSocket.getInetAddress())) {
                        // table unbekannt, füge sie in die Map ein oder ExceptionMessage
                        // 
                    } 
                    
                    ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                    //
                } catch (IOException ex) {

                }
            }
        }    
    }

    /**
     * NachrichtenPublizierungsdienst (MPS)
     */
    public class MessagePublishingService extends Thread {

        private final ObjectInputStream objectInputStream;
        private final int port;

        /**
         * 
         * @param port
         * @param pis
         * @throws IOException 
         */
        public MessagePublishingService(int port, PipedInputStream pis) throws IOException {
            this.port = port;
            this.objectInputStream = new ObjectInputStream(pis);
        }

        @Override
        public void run() {
            //@TODO zwischenpuffern und dann alle nachrichten an alle tables schicken
            Set<Map.Entry<String, InetAddress>> destinations = tableRegister.entrySet();

            for (Map.Entry<String, InetAddress> entry : destinations) {
                try (Socket socket = new Socket(entry.getValue(), port)) {                            
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

                    out.flush();

                    out.writeObject(objectInputStream.readObject());

                } catch (IOException ex) {
                } catch (ClassNotFoundException ex) {
                }
            }
        }
    }
}
