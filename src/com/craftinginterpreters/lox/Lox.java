package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
    public static void main(String[] args) throws IOException {
        if(args.length > 1) { //4장. 입력된 문장이 1보다 크다면
            System.out.println("Usage : jlox [script]"); //실행 출력
            System.exit(64);
        } else if (args.length == 1) { //4장. 길이가 1이면 File을 실행 (파일을 통해 문장을 받는 것도 가능)
            runFile(args[0]);
        } else {
            runPrompt(); //프롬프트에 입력
        }
    }

    //4장. Jlox를 기동할 떄 파일 경로를 지정해 스크립트 파일을 실행
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path)); //파일 읽기
        run(new String(bytes, Charset.defaultCharset())); //charset에 넣은 뒤 실행
    }

    //4장. Jlox를 기동할 때 파일이 아닌 프롬프트로 직접 입력해 실행
    // 이런 대화형 프롬프트를 interactive prompt, REPL(레블)이라고 부른다.
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in); //입력
        BufferedReader reader = new BufferedReader(input);

        for(;;) {
            System.out.print(" > "); //>을 띄우고 line별로 읽기
            String line = reader.readLine(); //사용자가 명령줄에 입력한 코드를 읽어 리턴
            if(line == null) break; //종료 조건. Crtl + D를 누르면 종료된다
            run(line); //코드를 읽어 한줄 씩 실행
        }
    }

    //4장. Scanner를 import하라고 뜨는데 이거 받으면 안된다.
    // 여기있는 스캐너는 앞으로 따로 만들거임. 만드는게 아니면 그떄가서 추가하는 걸로하샘.
    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens(); //입력된 토큰들을 리스트에 저장

        //지금은 단순히 토큰을 출력한다.
        for(Token token : tokens) {
            System.out.println(token);
        }
    }

}
