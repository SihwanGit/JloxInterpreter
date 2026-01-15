package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;
//자바의 정적 임포트. instance 생성 없이 해당 클래스의 매서드를 사용할 수 있다.

//4.4장 Scanner 클래스
class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;
    private int line = 1;
    //4.4장 start, current, line은 Scanner가 현재 코드의 어디를 뒤지는 중인지 기록하는 용도다.
    //4.4장 start, current 필드는 문자열의 위치를 가리키는 offset이다.
    //start는 스캔 중인 렉스의 춧 문자, current는 현재 처리 중인 문자, line 필드는 current가 위치한 줄이다.

    Scanner(String source) { //run함수에서 매개변수로 받은 source를 this.source로 설정함
        this.source = source;
    }

    //4.4장 스캐너 클래스의 핵심루프
    List<Token> scanTokens() {
        while(!isAtEnd()) {
            //다음 렉심의 시작 부분에 있다.
            start = current;
            scanTokens();
        }
        //소스코드를 처음부터 끝까지 읽어 문자가 없을 떄까지 토큰을 추가한다.

        tokens.add(new Token(EOF, "", null, line));
        //마지막에 Eof, "" null, line을  가진 토큰을 추가한다.
        //EOF 토큰을 붙여 더 깔끔하게 만든다. (꼭 넣을 필요는 없다.)
        return tokens;
    }

    //4.4장 문자를 모두 소비했는지 체크하는 헬퍼 메서드
    private boolean isAtEnd() {
        return current >= source.length();
    }

}
