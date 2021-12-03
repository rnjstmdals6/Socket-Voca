import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class EchoServer {

    public static int number; // 문제 수
    public static Vector<ClientList> clients = new Vector<>();

    public static void main(String[] args) throws IOException{

        Scanner scanner = new Scanner(System.in);
        // 문제 수를 입력받음
        System.out.print("The number of words in a quiz : ");
        number = scanner.nextInt();
        // ServerSocket 객체 생성
        ServerSocket ss = new ServerSocket(3005);

        while(true) {
            System.out.println("English server is waiting...");
            Socket client = ss.accept(); // client 접속을 waiting하다 접속되면 해당 클라이언트와 통신할 소켓 생성
            // UTF-8 사용 시 데이터 송수신
            DataInputStream dis = new DataInputStream(client.getInputStream());
            DataOutputStream dos = new DataOutputStream(client.getOutputStream());
            // 연결된 클라이언트의 정보를 출력
            System.out.println("New client is connected : " + client);

            // 클라이언트 유저이름 수신후 클라이언트 리스트에 추가
            String name = dis.readUTF();
            System.out.println("Creating a new Quiz Maker for this client : " + name);
            System.out.println("Adding this client to active client list");

            // 쓰레드 생성
            ClientList cl = new ClientList(client, name, dis, dos);
            clients.add(cl);
            Thread thread = new Thread(cl);
            thread.start();
        }
    }
}





































