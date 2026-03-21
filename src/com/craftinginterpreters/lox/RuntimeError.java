package com.craftinginterpreters.lox;

//7.3 Lox의 런타임 에러를 잡기위한 클래스
//자바의 ClassCastException과 달리 RuntimeError가 발생한 사용자 코드의 위치를 식별하는 토큰을 보관
public class RuntimeError extends RuntimeException {
    final Token token;

    RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}
