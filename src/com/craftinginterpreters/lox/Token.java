package com.craftinginterpreters.lox;

//4.2장 Token 클래스
public class Token {
    final TokenType type;
    final String lexeme;
    final Object literal; //identifier, string, number
    final int line;

    //스캐너는 리터럴의 각 문자를 탐색할 때 값의 텍스트 표현을 나중에 인터프리터가 사용할 라이브 런타임 객체로 변환
    //lexeme은 문자 그대로를 받고, literal에는 해당하는 값이 들어간다.
    //Ex : [lox] 123.45 -> lexeme = "123.45", literal = 123.45
    //Ex2: [lox] hello -> lexeme = "hello", literal = "hello"
    //Ex3: [lox] NIL -> lexeme = "NIL", literal = NULL
    //literal이 Object 타입인 이유는 literal에는 여러 타입의 값들이 들어올 수 있기 때문이다.
    //4.5장에 보면 literal이 없는 토큰들은 값을 null로 설정한다.

    //Token 생성자
    Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }

}
