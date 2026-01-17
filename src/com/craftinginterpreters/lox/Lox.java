package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
    static boolean hadError = false; //4.1 에러 여부 체크

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
        run(new String(bytes, Charset.defaultCharset()));//charset에 넣은 뒤 실행

        //4.1장 종료 코드로 에러를 식별한다.
        if(hadError) System.exit(65); //파일을 받고나서 에러가 있으면 종료.
        //이 코드의 위치가 여기가 아닐 수 있는데 그건 나중에 수정하면 됨.
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
            hadError = false; //4.1장 에러가 발생한 줄만 종료시키기 위해 분리
        }
    }

    //4장. Scanner를 import하라고 뜨는데 이거 받으면 안된다.
    // 여기있는 스캐너는 앞으로 따로 만들거임. 만드는게 아니면 그떄가서 추가하는 걸로하샘.
    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens(); //입력된 토큰들을 리스트에 저장

        //지금은 단순히 토큰을 출력한다.
        for(Token token : tokens) {
            System.out.println(token); //토큰 출력 (토큰Type, text, 리터럴의 쌍으로 출력됨. Token 클래스에서 그렇게 정의함)
        }
    }

    static void error(int line, String message) { //4.1장 error를 관리하는 함수
        report(line, "", message); //line과 메세지를 report로 전달
    }

    //4.1장 에러가 발생한 줄을 알려주고, haddError 값을 true로 바꿔 코드를 종료한다.
    //Lox는 에러가 발생한 라인만 표시한다.
    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

}
