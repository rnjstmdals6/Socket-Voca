import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Vector;

public class UDPServer {
    public static int number;
    public static Vector<ClientList> clients = new Vector<>();

    public static void main(String[] args) throws IOException{
        Scanner scanner = new Scanner(System.in);
        // 서버가 시작하기 전 출제할 문제수를 입력받음
        System.out.print("The number of words in a quiz : ");
        number = scanner.nextInt();
        DatagramSocket socket = new DatagramSocket(9000);
        System.out.println("English server is waiting...");
        try {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);

            // 데이터 최초수신, 이름정보를 수신함
            socket.receive(packet);
            String name = new String(packet.getData());
            // 수신한 패킷을 통해 클라이언트의 정보를 출력함
            System.out.println("New client is connected : Socket[addr=" + packet.getAddress() + ",port="+packet.getPort());
            System.out.println("Creating a new Quiz Maker for this client : " + name.trim());
            System.out.println("Adding this client to active client list");

            // 쓰레드 생성
            ClientList cl = new ClientList(socket, name, packet);
            clients.add(cl);
            Thread thread = new Thread(cl);
            thread.start();
        }catch (SocketException e){
            e.printStackTrace();
        }
    }
}
