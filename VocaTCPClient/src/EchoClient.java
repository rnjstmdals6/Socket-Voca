import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class EchoClient {
    final static int serverPort = 3005;
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            InetAddress ipaddr = InetAddress.getByName("localhost");
            // 다른 호스트에 접속 요청하는 소켓 생성 (아이피와 포트번호) connect 요청
            Socket s = new Socket(ipaddr, serverPort);
            System.out.println("Client is connected to the English Quiz server.");
            // UTF-8 사용 시 데이터 송수신
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            // 클라이언트 유저 이름 송신
            System.out.print("Name : ");
            String name = scanner.next();
            dos.writeUTF(name);

            while (true) {
                // 퀴즈를 풀 준비가 되었는 지 확인
                System.out.println("Are you ready for quiz test(yes/no)?");
                String answer = scanner.next();
                // 준비가 안되어있으면 클라이언트 종료, yes 이외의 답변을 받으면 다시 확인
                if(answer.equals("no"))
                    break;
                else if(!answer.equals("yes"))
                    continue;
                try {
                    dos.writeUTF(answer);
                    System.out.println("Quiz test is started.");
                    // 문제 수를 문자로 수신함
                    String num = dis.readUTF();
                    // 받은 숫자를 정수형으로 형변환
                    int number = Integer.parseInt(num);
                    scanner.nextLine();
                    // 문제를 수신하고 정답을 송신하는 반복문
                    for (int i = 0; i < number; i++) {
                        String question = dis.readUTF();
                        System.out.println("Question : " + question);
                        String response = scanner.nextLine();
                        dos.writeUTF(response);
                    }
                    // 서버로부터 최종점수를 수신하고 출력함
                    String score = dis.readUTF();
                    int result = Integer.parseInt(score);
                    System.out.println("Your score : " + result);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



























