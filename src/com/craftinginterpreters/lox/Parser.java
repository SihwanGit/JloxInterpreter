package com.craftinginterpreters.lox;

import java.util.List;
import static com.craftinginterpreters.lox.TokenType.*;

// 6.2장 : Parser 클래스
public class Parser {
    private final List<Token> tokens;
    //6.2 파서도 스캐너처럼 나열된 입력을 소비한다.
    //그러나 스캐너는 source code string을 읽으며, 파서는 토큰 스트림을 읽는다.
    private int current = 0;
    //scanner가 문자열을 start와 current로 끊었던 것처럼 파서도 파싱할 토큰을 current로 가리킨다.
    
    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // 6.2 록스 언어의 연산자 문법은 6.1장을 참고.
    // expression 중 가장 상위 계층(우선순위가 가장 낮은)인 동등식부터 파생된다.
    private Expr expression() {
        return equality();
    }
}
