
package table.commlayer;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import messages.Message;
import table.messageservice.AMessageService;
import table.proxy.MonitoredQueue;

/**
 * @author Tobias Müller
 * @version 0.2
 * @date 06.08.2015
 * 
 * Kommunikationsschnittstelle zwischen Tafeln und Clients
 */
public class TableCommunicationGateway extends Thread {

    private final ConcurrentSkipListMap<String, InetAddress> tableRegister;
    
    public final MonitoredQueue<Message> incomingQueue;
    public final MonitoredQueue<Message> outgoingQueue;
    
    private final ThreadGroup tableCommunicationThreads;

    private final TableRegistrationService tableRegistrationService;
    private final TableCommunicationListener tableCommunicationListener;
    private final ClientCommunicationService clientCommunicationService;
    
    private final String tableIdentifier;
    
    /**
     * Konstruktor des TableCommunicationGateway (TCG)
     * @param tableIdentifier
     * @throws IOException
     */
    public TableCommunicationGateway(String tableIdentifier) throws IOException {
        this.incomingQueue = new MonitoredQueue<>();
        this.outgoingQueue = new MonitoredQueue<>();
        this.tableIdentifier = tableIdentifier;
        this.tableRegister = new ConcurrentSkipListMap<>();
        
        this.tableCommunicationThreads = new ThreadGroup("Table-Communications");
        
        System.out.println(TableCommunicationListener.class.getSimpleName() + " wird konfiguriert...");
        tableCommunicationListener = new TableCommunicationListener(tableCommunicationThreads);
        
        System.out.println(TableRegistrationService.class.getSimpleName() + " wird konfiguriert...");
        tableRegistrationService = new TableRegistrationService(this.tableRegister, this.tableIdentifier, this.tableCommunicationThreads);
        
        System.out.println(ClientCommunicationService.class.getSimpleName() + " wird konfiguriert...");
        clientCommunicationService = new ClientCommunicationService();
    }

    /**
     *
     */
    @Override
    public void run() {
        System.out.println(TableCommunicationListener.class.getSimpleName() + " wird gestartet...");
        tableCommunicationListener.start();
        
        System.out.println(TableRegistrationService.class.getSimpleName() + " wird gestartet...");
        tableRegistrationService.start();

        System.out.println(ClientCommunicationService.class.getSimpleName() + " wird gestartet...");
        clientCommunicationService.start();
    }

    public void SendMessageToTables(Message msg) {
        
    }
    
    public void SendAllMyMessagesToTables(AMessageService msgService) {
        
    }
    
    public void ReveiceMessageFromTable(Message msg) {
        
    }
    
    /**
     * Nachrichtenempfangsdienst (MCS)
     */
//    public class MessageConsumingService extends Thread {
//
//        private final int port;
//        private final ServerSocket serverSocket;
//        
//        /**
//         * Konstruktor
//         * @throws IOException 
//         */
//        public MessageConsumingService() throws IOException {
//            this.port = CommunicationConfiguration.getInstance().getMessageTransportServicePort();
//            this.serverSocket = new ServerSocket(this.port);
//            this.serverSocket.setSoTimeout(60000);
//        }
//
//        @Override
//        public void run() {
//            while (true) {
//                try {
//                    Socket clientSocket = serverSocket.accept();
//
//                    if (!tableRegister.containsValue(clientSocket.getInetAddress())) {
//                        // table unbekannt, füge sie in die Map ein oder ExceptionMessage
//                        // 
//                    } 
//                    
//                    ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
//                    incomingQueue.put((Message)in.readObject());
//                    //
//                } catch (IOException | ClassNotFoundException ex) {
//
//                }
//            }
//        }    
//    }
//
//    /**
//     * NachrichtenPublizierungsdienst (MPS)
//     */
//    public class MessagePublishingService extends Thread {
//
//        private final int port;
//
//        /**
//         * 
//         * @throws IOException 
//         */
//        public MessagePublishingService() throws IOException {
//            this.port = CommunicationConfiguration.getInstance().getMessageTransportServicePort();
//        }
//
//        @Override
//        public void run() {
//            while (true) {
//                Object outgoingMsg;
//
//                Set<Map.Entry<String, InetAddress>> destinations = tableRegister.entrySet();
//
//                try {
//                    outgoingMsg = outgoingQueue.get();
//                    
//                    for (Map.Entry<String, InetAddress> entry : destinations) {
//                        try (Socket socket = new Socket(entry.getValue(), port)) {                            
//                             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
//
//                             out.flush();
//                             System.out.println("Sende Daten an " + entry.toString() + ": " + outgoingMsg);
//                             out.writeObject(outgoingMsg);
//                        }
//                    }
//                } catch (IOException ex) {
//
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(TableCommunicationGateway.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//    }
}
