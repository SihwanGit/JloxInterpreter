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

    // 6.2 동등식 : equality -> comparison ( ( "!=" | "==" ) comparison ) *
    private Expr equality() {
        Expr expr = comparison(); //먼저 비교식으로 시작

        //( )*은 0번 이상 반복을 의미하며, while로 구현한다.
        while (match(BANG_EQUAL, EQUAL_EQUAL)) { //만약 첫 comparison 뒤에 !=이나 ==이 오면
            Token operator = previous(); //op는 해당 연산자로 설정하고,
            Expr right = comparison(); //right는 그 뒤에 나오는 비교문으로 설정한다.
            expr = new Expr.Binary(expr, operator, right); //그리고 com op com을 하나의 Expr로 묶은뒤 반복한다.
        }
        //아직 match, previous은 구현하지 않았다.
        //4장의 Scanner 구현을 미루어봤을 떄, match는 다음 토큰이 문법에서 요구하는 토큰이 맞는지 검사하는
        //조건부 룩어헤드 메서드다. 맞다면 advance로 소모하기 때문에 previous라는 이전 토큰을 가리키는
        //매서드로 방금 소모된 토큰을 op를 설정해준다.
        //또한 두번쨰 comparison부터는 right지만 처음은 left가 아닌 expr로 명명한 이유는 com이 하나만 올 수도 있기 때문이다.

        return expr;
    }

    //6.2 매치 매서드
    //다음에 나오는 토큰이 문법이 요구하는 토큰인지 검사하는 조건부 룩어헤드
    private boolean match(TokenType... types) { //같은 타입의 매개변수가 여러개 가능하면 이렇게 쓰는구나
        for(TokenType type : types) {
            if(check(type)) { //만약 check(type)이 내가 찾는 토큰 타입들에 속한다면
                advance(); //소비시키고 true 반환
                return true;
            }
        }
        return false; //없으면 false 반환
    }

    // 6.2 check method
    private boolean check(TokenType type) {
        if(isAtEnd()) return false;
        return peek().type == type;
        //룩어헤드인 peek를 통해 다음에 나올 토큰이 매개변수로 받은 토큰과 동일한지 체크
        //만약에 match의 types가 전부 끝나버린다면, isAtEnd()가 true가 되면서 check는 false를 리턴한다.
    }

    //6.2 advance method for consuming Token Stream
    private Token advance() {
        if(!isAtEnd()) current++; //until Token Stream is EOF, consume Token
        return previous();
    }
    //4장에서 한 Scanner의 advance랑 똑같으니 설명은 생략

    private boolean isAtEnd() {
        return peek().type == EOF;
        //if next token is EOF, return true
    }

    private Token peek() {
        return tokens.get(current);
        //다음 토큰을 읽되, 소비하지는 않은 LOOKAHEAD 매서드
    }

    private Token previous() {
        return tokens.get(current-1);
        //peek는 다음 토큰을 읽어주고, previous는 직전에 소비된 토큰을 반환
    }

    // 6.2 비교식 : comparison -> term ( ( ">" | ">=" | "<" | "<=" ) term )*
    private Expr comparison() {
        Expr expr = term();

        while (match(GREATER_EQUAL, GREATER, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    } //realization is similar to equality

    //6.2 항 : term -> factor ( ( "+" | "-" ) factor )*
    private Expr term() {
        Expr expr factor();

        while(match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    //6.2 인수 : factor -> unary ( ( "*" | "/" ) unary )*
    private Expr factor() {
        Expr expr = unary();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    //6.2 단항연산 : unary -> ( "!" | "-" ) unary | primary
    private Expr unary() {
        if(match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    } //만약 not이나 -가 나오면 그걸 Unary로 묶어서 처리하고, 없으면 그냥 수식을 진행해라.


}
