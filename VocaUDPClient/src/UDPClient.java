import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class UDPClient {
    UDPClient(){
        // UDP 클라이언트를 시작(연결되었다고 출력하지만 실제로 연결된것은 아닙니다)
        System.out.println("Client is connedted to the English Quiz server.");
        Scanner scanner = new Scanner(System.in);
        // 소켓 객체를 생성
        try(DatagramSocket socket = new DatagramSocket()){ // 임의의 포트에 생성가능
            InetAddress address = InetAddress.getByName("localhost");
            byte[] data; // 데이터를 담을 바이트배열 초기화
            // 화면에서 이름을 입력받고 데이터에 담고 패킷으로 서버에 전송
            System.out.print("Name : ");
            String message = scanner.nextLine();
            data = message.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, address, 9000);
            socket.send(packet);
            // 퀴즈 문제 수에대한 데이터 수신
            byte[] recvData = new byte[1024];
            DatagramPacket packet1 = new DatagramPacket(recvData, recvData.length);
            socket.receive(packet1);
            String num = new String(packet1.getData());
            int number = Integer.parseInt(num.trim());

            while (true){
                // 퀴즈를 풀건 지 확인하고 응답이 yes이면 데이터를 전송
                System.out.println("Are you ready for quiz test(yes/no)?");
                String answer = scanner.nextLine();
                if(answer.equalsIgnoreCase("no")) break;
                else if(!answer.equalsIgnoreCase("yes")) continue;
                data = answer.getBytes();
                DatagramPacket packet2 = new DatagramPacket(data, data.length, address, 9000);
                socket.send(packet2);

                // 퀴즈 문제 시작
                System.out.println("Quiz test is started.");
                for(int i = 0; i < number; i++){ // 문제 수만큼 반복
                    // 서버로부터 문제를 수신
                    byte[] rData = new byte[1024];
                    DatagramPacket packet3 = new DatagramPacket(rData, rData.length);
                    socket.receive(packet3);
                    String question = new String(packet3.getData()).trim();
                    System.out.println("Question : " + question);
                    // 서버에 문제의 답을 송신
                    byte[] nData;
                    String response = scanner.nextLine();
                    nData = response.getBytes();
                    packet = new DatagramPacket(nData, nData.length, address, 9000);
                    socket.send(packet);
                }
                // 최종 점수를 서버로부터 받고 출력함
                byte[] nData = new byte[1024];
                DatagramPacket packet4 = new DatagramPacket(nData, recvData.length);
                socket.receive(packet4);
                String score = new String(packet4.getData());
                int result = Integer.parseInt(score.trim());
                System.out.println("Your score : " + result);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new UDPClient();
    }








































}
