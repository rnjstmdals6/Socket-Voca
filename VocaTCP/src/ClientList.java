import java.io.*;
import java.net.Socket;
import java.util.*;

public class ClientList implements Runnable{

    private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    private Socket s;
    private boolean isSignin;
    private static HashMap<String, String> word = new HashMap<>();

    public ClientList(Socket s, String name, DataInputStream dis,
                      DataOutputStream dos){
        this.name = name;
        this.dis = dis;
        this.dos = dos;
        this.s = s;
        isSignin = true;
    }
    // 영단어 메모장을 읽는 함수
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
            // 영어 메모장을 읽고 word 해시맵에 저장함
            loadFile("C:\\Users\\Ksm\\Downloads\\voca1800.txt");
            // 랜덤으로 문제를 뽑기위한 Collection
            Set<String> keySet = word.keySet();
            List<String> keyList = new ArrayList<>(keySet);
            int size = keyList.size();
            // 서버가 시작될 때 입력된 문제 수의 변수 number를 받아옴
            int number = EchoServer.number;
            while(true) {
                try {
                    // 클라이언트가 문제를 풀 준비 여부에대해 수신함
                    String answer = dis.readUTF();
                    if(answer.equals("no"))
                        break;
                } catch (Exception e){
                    break;
                }
                // 문제 수를 클라이언트에 송신
                String num = Integer.toString(number);
                dos.writeUTF(num);

                int score = 0; // 점수를 초기화
                String[][] narr = new String[number][]; // 랜덤으로 뽑은 문제들을 담아둘 문자열 배열

                System.out.println("============= Quiz words =============");
                // 클라이언트로 퀴즈문제를 송신하는 반복문
                for(int i = 0; i < number; i++)
                {
                    // 랜덤으로 문제의 인덱스를 정하고 파싱을 하는 과정
                    int randIdx = new Random().nextInt(size);
                    String randomKey = keyList.get(randIdx);
                    String randomValue = word.get(randomKey);
                    String[] correct = randomValue.split(", ");
                    narr[i] = new String[correct.length];
                    for(int j = 0; j < correct.length; j++){
                        narr[i][j] = correct[j];
                    }
                    // 랜덤으로 정해진 문제를 출력하고 클라이언트에 송신함
                    dos.writeUTF(randomKey);
                    System.out.println(randomKey + " : " + randomValue);
                }
                System.out.println("======================================");
                // 클라이언트로부터 답을 수신하는 반복문
                for (int i = 0; i < number; i++) {
                    String response = dis.readUTF();
                    System.out.println("Received : " + response);
                    // 클라이언트가 보낸 답과 정답이 일치하는 지 확인하는 과정
                    for(int j = 0; j < narr[i].length; j++){
                        // 정답이 맞으면 점수를 10점 추가함
                        if(narr[i][j].equals(response)) score += 10;
                    }
                }
                // 최종점수를 출력하고 클라이언트로 송신함
                System.out.println("Sent the final score : " + score);
                String result = Integer.toString(score);
                dos.writeUTF(result);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
