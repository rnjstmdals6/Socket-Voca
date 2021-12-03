import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.util.*;

public class ClientList implements Runnable{
    private static HashMap<String, String> word = new HashMap<>();
    private String name;
    private DatagramSocket s;
    private DatagramPacket p;
    private boolean isSignin;

    public ClientList(DatagramSocket s, String name, DatagramPacket p){
        this.name = name;
        this.s = s;
        this.p = p;
        isSignin = true;
    }
    // 영단어 메모장을 읽어오는 함수
    public static boolean loadFile(String path) throws IOException {
        word.clear();
        File file = new File(path);
        if (!file.exists()) return false;
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.replaceFirst(",", "/");
            String[] split = line.split("/");
            // if (!addVoca(split[0].toLowerCase(), split[1]))
            word.put(split[0].toLowerCase(), split[1]);
        }
        reader.close();
        return true;
    }

    @Override
    public void run() {
        try {
            // 영단어 메모장의 정보들을 word 해시맵 변수에 담음
            loadFile("C:\\Users\\Ksm\\Downloads\\voca1800.txt");
            // 랜덤으로 영어단어 뽑고 저장하기 위한 Collection
            Set<String> keySet = word.keySet();
            List<String> keyList = new ArrayList<>(keySet);
            byte[] data; // 데이터를 담을 바이트배열
            int size = keyList.size(); // 영어단어의 수
            InetAddress address = p.getAddress(); // 클라이언트의 IP 주소
            int port = p.getPort(); // 클라이언트의 포트
            int number = UDPServer.number; // 출제할 문제수
            // 출제할 문제수를 클라이언트에 송신
            String num = Integer.toString(UDPServer.number);
            data = num.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            s.send(packet);

            while (true){
                // 문제를 풀 준비가 되어있는지 여부를 수신함
                s.receive(p);
                String message = new String(p.getData());
                // 준비가 안되어있다면 종료
                if(message.equalsIgnoreCase("no")) break;
                String[][] narr = new String[number][]; // 랜덤으로 뽑은 문제를 저장할 문자열 배열선언
                System.out.println("============= Quiz words =============");
                for(int i = 0; i < number; i++){ // 출제할 문제 수만큼 반복
                    // 랜덤으로 인덱스를 정하고 문자를 파싱하여 문자열 배열에 저장
                    int randIdx = new Random().nextInt(size);
                    String randomKey = keyList.get(randIdx);
                    String randomValue = word.get(randomKey);
                    String[] correct = randomValue.split(", ");
                    narr[i] = new String[correct.length];
                    for(int j = 0; j < correct.length; j++){
                        narr[i][j] = correct[j];
                    }
                    // 랜덤으로 정한 문제를 클라이언트에 전송
                    byte[] nData;
                    nData = randomKey.getBytes();
                    DatagramPacket packet3 = new DatagramPacket(nData, nData.length, address, port);
                    s.send(packet3);
                    System.out.println(randomKey + " : " + randomValue);
                }
                System.out.println("======================================");
                int score = 0; // 최종 점수 초기화
                // 클라이언트로부터 퀴즈에 대한 답을 수신
                for(int i = 0; i < number; i++){
                    byte[] recvData = new byte[1024];
                    DatagramPacket packet1 = new DatagramPacket(recvData, recvData.length);
                    s.receive(packet1);
                    String response = new String(packet1.getData());
                    System.out.println("Received : " + response.trim());
                    // 답이 맞는지 확인함
                    for(int j = 0; j < narr[i].length; j++){
                        if(narr[i][j].equals(response.trim())) score += 10;
                    }
                }
                // 최종점수를 출력하고 클라이언트에 데이터전송
                System.out.println("Sent the final score : " + score);
                String result = Integer.toString(score);
                data = result.getBytes();
                DatagramPacket packet4 = new DatagramPacket(data, data.length, address, port);
                s.send(packet4);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
