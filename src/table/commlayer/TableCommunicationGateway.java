
package table.commlayer;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import table.proxy.Monitor;

/**
 * @author Tobias Müller
 * @version 0.2
 * @date 06.08.2015
 * 
 * Kommunikationsschnittstelle zwischen Tafeln und Clients
 */
public class TableCommunicationGateway extends Thread {

    private final ConcurrentSkipListMap<String, InetAddress> tableRegister = new ConcurrentSkipListMap<>();
    public final Monitor<Object> incomingQueue;
    public final Monitor<Object> outgoingQueue;
    
    private final RegistrationService regService;
    private final MessageConsumingService msgConsumingService;
    private final MessagePublishingService msgPublishingService;
    private final ClientCommunicationService clientCommunicationService;
    private final int registrationServicePort = 9876;
    private final int messageTransportServicePort = 21000;
    private final int clientCommunicationSerivcePort = 20000;
    
    private final String tableIdentifier;
    
    /**
     * Konstruktor des TableCommunicationGateway (TCG)
     * @param tableIdentifier
     * @throws IOException
     */
    public TableCommunicationGateway(String tableIdentifier) throws IOException {
        this.incomingQueue = new Monitor<>();
        this.outgoingQueue = new Monitor<>();
        this.tableIdentifier = tableIdentifier;
        
        DatagramPacket datagramPacket = new DatagramPacket(tableIdentifier.getBytes(), tableIdentifier.getBytes().length);
        String broadcastAddr = "255.255.255.0";
        DatagramSocket datagramSocket = new DatagramSocket(new InetSocketAddress(InetAddress.getLocalHost(), registrationServicePort));
        // UDP-Datagramm schicken zur Anmeldung bei allen verfügbaren Tafeln
        
        System.out.println(RegistrationService.class.getSimpleName() + " wird konfiguriert...");
        regService = new RegistrationService(registrationServicePort);

        System.out.println(MessageConsumingService.class.getSimpleName() + " wird konfiguriert...");
        msgConsumingService = new MessageConsumingService(messageTransportServicePort);

        System.out.println(MessagePublishingService.class.getSimpleName() + " wird konfiguriert...");
        msgPublishingService = new MessagePublishingService(messageTransportServicePort);
        
        System.out.println(ClientCommunicationService.class.getSimpleName() + " wird konfiguriert...");
        clientCommunicationService = new ClientCommunicationService(clientCommunicationSerivcePort);
    }

    /**
     *
     */
    @Override
    public void run() {
        System.out.println(RegistrationService.class.getSimpleName() + " wird gestartet...");
        regService.start();

        System.out.println(MessageConsumingService.class.getSimpleName() + " wird gestartet...");
        msgConsumingService.start();

        System.out.println(MessagePublishingService.class.getSimpleName() + " wird gestartet...");
        msgPublishingService.start();
        
        System.out.println(ClientCommunicationService.class.getSimpleName() + " wird gestartet...");
        clientCommunicationService.start();
    }

    /**
     * Tafelregistrierungsdienst (TRS)
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
                        //...
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
        
        /**
         * Konstruktor
         * @param port
         * @throws IOException 
         */
        public MessageConsumingService(int port) throws IOException {
            this.port = port;
            this.serverSocket = new ServerSocket(this.port);
            this.serverSocket.setSoTimeout(10000);
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
                    incomingQueue.put(in.readObject());
                    //
                } catch (IOException | ClassNotFoundException ex) {

                }
            }
        }    
    }

    /**
     * NachrichtenPublizierungsdienst (MPS)
     */
    public class MessagePublishingService extends Thread {

        private final int port;

        /**
         * 
         * @param port
         * @throws IOException 
         */
        public MessagePublishingService(int port) throws IOException {
            this.port = port;
        }

        @Override
        public void run() {
            while (true) {
                Object outgoingMsg;

                //@TODO zwischenpuffern und dann alle nachrichten an alle tables schicken
                Set<Map.Entry<String, InetAddress>> destinations = tableRegister.entrySet();

                try {
                    outgoingMsg = outgoingQueue.get();

                    for (Map.Entry<String, InetAddress> entry : destinations) {
                        try (Socket socket = new Socket(entry.getValue(), port)) {                            
                             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

                             out.flush();
                             System.out.println("Sende Daten an " + entry.toString() + ": " + outgoingMsg);
                             out.writeObject(outgoingMsg);
                        }
                    }
                } catch (IOException ex) {
                     //Exception wird dauerhalt ausgelöst -> in der Pipe muss gewartet werden, bis neues element verfügbar
                } catch (InterruptedException ex) {
                    Logger.getLogger(TableCommunicationGateway.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
