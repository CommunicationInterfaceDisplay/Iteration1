
package display.commlayer;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 *
 * @author Tobias Mueller
 */
public class DisplayGateway extends Thread {
    private final ConcurrentSkipListMap<String, InetAddress> tableAddrMap = new ConcurrentSkipListMap<>();
    private final RegistrationService regService;
    private final MessageConsumingService msgConsumingService;
    private final MessagePublishingService msgPublishingService;
    
    public DisplayGateway(PipedInputStream pis, PipedOutputStream pos) throws IOException {
        // UDP-Datagramm schicken zur Anmeldung bei allen verfügbaren Tafeln

        
        System.out.println(RegistrationService.class.getSimpleName() + " wird konfiguriert...");
        regService = new RegistrationService(9876);

        System.out.println(MessageConsumingService.class.getSimpleName() + " wird konfiguriert...");
        msgConsumingService = new MessageConsumingService(12345, pos);

        System.out.println(MessagePublishingService.class.getSimpleName() + " wird konfiguriert...");
        msgPublishingService = new MessagePublishingService(12345, pis);
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

    public class RegistrationService extends Thread {

        final int port;
        DatagramSocket serverSocket;

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

                    //System.out.println(tableAddrMap.values());
                    if (tableAddrMap.containsKey(tableId) & tableAddrMap.containsValue(tableIP)) {
                        System.out.println("table schon angemeldet!");
                        // table war wohl zur laufzeit neugestartet worden! alle globalen nachrichten neu senden!
                    } else {
                        tableAddrMap.put(tableId, receivePacket.getAddress());
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
     *
     */
    public class MessageConsumingService extends Thread {

        private final int port;
        private final ServerSocket serverSocket;
        private final PipedOutputStream pipedOutputStream;

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

                    if (!tableAddrMap.containsValue(clientSocket.getInetAddress())) {
                        // table unbekannt, füge sie in die Map ein oder ExceptionMessage
                        // 
                    } 
                    
                    ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                    
                } catch (IOException ex) {

                }
            }
        }
        
        
    }

    public class MessagePublishingService extends Thread {

        private final ObjectInputStream objectInputStream;
        final int port;

        public MessagePublishingService(int port, PipedInputStream pis) throws IOException {
            this.port = port;
            this.objectInputStream = new ObjectInputStream(pis);
        }

        @Override
        public void run() {
            //@TODO zwischenpuffern und dann alle nachrichten an alle tables schicken
            Set<Map.Entry<String, InetAddress>> destinations = tableAddrMap.entrySet();

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
