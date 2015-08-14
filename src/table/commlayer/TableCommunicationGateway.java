
package table.commlayer;

import table.proxy.Pipe;
import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import messages.Message;

/**
 * @author Tobias Müller
 * @version 0.2
 * @date 06.08.2015
 * 
 * Kommunikationsschnittstelle zwischen Tafeln und Clients
 */
public class TableCommunicationGateway extends Thread {

    private final ConcurrentSkipListMap<String, InetAddress> tableRegister = new ConcurrentSkipListMap<>();

    
    private final RegistrationService regService;
    private final MessageConsumingService msgConsumingService;
    private final MessagePublishingService msgPublishingService;
    private final ClientCommunicationService clientCommunicationService;
    private final int registrationServicePort = 9876;
    private final int messageTransportServicePort = 21000;
    private final int clientCommunicationSerivcePort = 20000;
    
    /**
     * Konstruktor des TableCommunicationGateway (TCG)
     * @param msgConPipe
     * @param msgPubPipe
     * @throws IOException
     */
    public TableCommunicationGateway(Pipe msgConPipe, Pipe msgPubPipe) throws IOException {
        // UDP-Datagramm schicken zur Anmeldung bei allen verfügbaren Tafeln
        
        System.out.println(RegistrationService.class.getSimpleName() + " wird konfiguriert...");
        regService = new RegistrationService(registrationServicePort);

        System.out.println(MessageConsumingService.class.getSimpleName() + " wird konfiguriert...");
        msgConsumingService = new MessageConsumingService(messageTransportServicePort, msgConPipe);

        System.out.println(MessagePublishingService.class.getSimpleName() + " wird konfiguriert...");
        msgPublishingService = new MessagePublishingService(messageTransportServicePort, msgPubPipe);
        
        System.out.println(ClientCommunicationService.class.getSimpleName() + " wird konfiguriert...");
        clientCommunicationService = new ClientCommunicationService(clientCommunicationSerivcePort);
    }

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
        private final Pipe msgConsumingPipe;
        /**
         * Konstruktor
         * @param port
         * @param msgConsumingPipe
         * @throws IOException 
         */
        public MessageConsumingService(int port, Pipe msgConsumingPipe) throws IOException {
            this.port = port;
            this.serverSocket = new ServerSocket(this.port);
            this.serverSocket.setSoTimeout(10000);
            this.msgConsumingPipe = msgConsumingPipe;
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
                    msgConsumingPipe.put(in.readObject());
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
        private final Pipe msgPublishingPipe;

        /**
         * 
         * @param port
         * @param msgPublishingPipe
         * @throws IOException 
         */
        public MessagePublishingService(int port, Pipe msgPublishingPipe) throws IOException {
            this.port = port;
            this.msgPublishingPipe = msgPublishingPipe;
        }

        @Override
        public void run() {
            while (true) {
               //@TODO zwischenpuffern und dann alle nachrichten an alle tables schicken
            Set<Map.Entry<String, InetAddress>> destinations = tableRegister.entrySet();

            try {
                //überlegen, ob Pipes sinnvoll sind... oder doch besser ein Shared Medium benutzen (Collection)
                Message msg = (Message)msgPublishingPipe.get();
            
                System.out.println("Sende Daten...");
                for (Map.Entry<String, InetAddress> entry : destinations) {
                    try (Socket socket = new Socket(entry.getValue(), port)) {                            
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

                        out.flush();

                        out.writeObject(msg);
                    }
                }
            } catch (IOException ex) {
                //Exception wird dauerhalt ausgelöst -> in der Pipe muss gewartet werden, bis neues element verfügbar
            } catch (ClassNotFoundException ex) {
                
            } 
            }
            
        }
    }
    
    private class ClientCommunicationService extends Thread {

        private final int port;
        private final ServerSocket serverSocket;
        
        public ClientCommunicationService(int port) throws IOException {
            this.port = port;
            this.serverSocket = new ServerSocket(this.port);
        }
    }

}
