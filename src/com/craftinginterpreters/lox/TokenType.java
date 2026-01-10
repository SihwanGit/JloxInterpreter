package com.craftinginterpreters.lox;

// 4.2장 토큰 타입
enum TokenType {
    //단일 문자 토큰
    // ( ) { } , . - + ; / *
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

    //문자 1개 또는 2개짜리 토큰
    // ! != = == > >= < <=
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    //리터럴
    IDENTIFIER, STRING, NUMBER,

    //키워드
    //이 중 NIL은 NULL, NONE이다. 없음을 의미하는 기호다.
    AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
    PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

    EOF
}
