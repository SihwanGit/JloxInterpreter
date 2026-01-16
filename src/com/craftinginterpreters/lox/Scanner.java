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
            scanToken();
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

    //4.5 토큰을 읽어들이는 함수
    private void scanToken() { //4.5 이름 주의해라.
        char c = advance();
        switch(c) {
            //하나의 문자로 구성된 렉심
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;

            //2개 이상의 문자로 구성된 렉심
            //사실 *나 +-도 복합대입연산자나 증감연산자를 추가하면 검사해야됨.
            //지금은 문법상으로 이것들을 정의하지 않아서 구분하지 않은 것.
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG); break; //!과 !=을 구분하기 위해 ! 뒤에 =이 오는지 확인
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS); break;
            case '>' :
                addToken(match('=') ? GREATER_EQUAL : GREATER); break;



            default: //등록되지 않은 토큰은 에러처리 Ex : @ # ^ etc
                //에러를 처리하지 않으면 무한루프에 빠질 수도 있음
                Lox.error(line, "Unexpected character");
                break;
        }
    }

    //4.5 current를 더해가며 차례대로 소스파일의 다음 문자를 읽어 리턴한다.
    //scanTokens(읽은 토큰들을 배열에 저장) -> scanToken(읽은 토큰들을 체크) -> adbance(단어 하나하나 읽음)
    private char advance() {
        return source.charAt(current++);
    }

    //4.5 리터럴(값)이 없은 토큰
    private void addToken(TokenType type) {
        addToken(type, null); //값이 없는 토큰은 값을 null로 처리한다.
    }

    //4.5 리터럴(값)이 있는 토큰
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    //4.5장 2개 이상의 문자로 구성된 렉심을 검사할 때 뒤에 오는 토큰에 따라 T/F값을 반환하는 함수
    //조건부 advance라고 생각하면 편하다
    private boolean match(char expected) {
        if(isAtEnd()) return false; //입력을 다 읽었으면 False를 반환
        if(source.charAt(current) !=  expected) return false; //매개변수로 받은 문자와 다르면
        // Ex match(=)인데 =이 아닌 다른 문자가 뒤에 오면 false를 반환

        current++;
        return true;
    }




}
