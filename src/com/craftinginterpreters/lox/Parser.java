package com.craftinginterpreters.lox;

import java.util.List;
import static com.craftinginterpreters.lox.TokenType.*;

// 6.2장 : Parser 클래스
public class Parser {
    private static class ParseError extends RuntimeException { }
    //파서를 해체(unwind)하려고 사용하는 센티널 클래스다.
    //파서 내부의 호출자 메서드로 해체 여부를 스스로 결정한다

    private final List<Token> tokens;
    //6.2 파서도 스캐너처럼 나열된 입력을 소비한다.
    //그러나 스캐너는 source code string을 읽으며, 파서는 토큰 스트림을 읽는다.
    private int current = 0;
    //scanner가 문자열을 start와 current로 끊었던 것처럼 파서도 파싱할 토큰을 current로 가리킨다.
    
    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    //6.4 method for starting parser
    Expr parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }
    //6장 기준으로 Expression밖에 구현이 안되어 있는 상태고, Stmt를 추가하면 수정할 예정
    //6장 기준 단일식 파싱 기능만 있다.

    // 6.2 록스 언어의 연산자 문법은 6.1장을 참고.
    // expression 중 가장 상위 계층(우선순위가 가장 낮은)인 동등식부터 파생된다.
    private Expr expression() {
        return comma();
    }

    // 6장 연습문제 1번 comma 식 : comma -> equality ( "," equality )*
    private Expr comma() {
        //먼저 예상부터 하자면, equality가 무조건 한번은 나오니까 expr = equality를 한번 찍고,
        //그 다음 while을 이용해서 만약 뒤에 match(comma)면 operator은 "," right는 equality()로 시작 후
        //다시 Expr로 묶을 것이다.

        Expr expr = equality(); //먼저 한번 나오고

        while (match(COMMA)) { // ","가 안나올 떄까지 반복
            Token operator = previous(); // op == comma
            Expr right = equality(); // right == equality();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
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

    //6.3 다음 토큰이 기대하는 타입인지 체크하는 로직
    private Token consume(TokenType type, String message) {
        if(check(type)) return advance();
        throw error(peek(), message);
    }
    //매개변수로 받은 토큰이 나왔는지 검사하고, 안나왔으면 에러 메세지를 송출
    //match()와 구현이 유사하다.

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

    //6.3 error 리포트 매서드
    private ParseError error(Token token, String message) {
        Lox.error(token, message); //해당 코드를 Lox.java에 추가
        return new ParseError();
    } //에러가 발생하면 parseError 객체를 생성한다.
    //에러를 throw하지 않고 return으로 처리하는 이유는 해체 여부를 스스로 결정하기 위해서다.
    //예를들어 어떤 에러는 파서를 이상한 상태로 만들지 않고, 동기화가 필요없는 점에서 생긴다.
    //이 경우 그냥 에러를 리포트하고 갈 길을 간다. Ex : 함수에 너무 많은 메서드가 들어오는 경우 등

    private void synchronize() {
        advance(); //일단 한번은 소비

        //동기화는 ; } \n같은 문장의 끝부분과, CLASS, FUN 등 새 문장이 시작되는 부분에서 이루어진다.
        //에러가 난 라인을 무시하고 새 문장이 시작할 것 같은 지점에서 다시 Parsing을 이어가는 것이다.
        while(!isAtEnd()) {
            if(previous().type == SEMICOLON) return;

            switch(peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }
            advance(); //경계가 되지 않는 토큰들은 그냥 소비 (계단식 에러가 일어나는 부분은 자연스럽게 제거됨)
        }
    }
    //참고로 6장 시점에선 해당 메서드를 사용하지 않는다. 자세한건 8장부터 다룬다고 한다.

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
        Expr expr = factor();

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

    //6.2 기본식 : primary -> number | string | "true" | "false" | "nil" | "(" expression ")"
    private Expr primary() {
        //true, false, nil은 토큰이 있으니 그걸로 처리
        if(match(FALSE)) return new Expr.Literal(false);
        if(match(TRUE)) return new Expr.Literal(true);
        if(match(NIL))  return new Expr.Literal(null);

        //숫자와 문자열은 리터럴로 처리
        if(match(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);
        }

        //여는 괄호가 나오면 위에서 했던 대로 하면 됨.
        //다만 primary는 연산자의 접합이 없기 때문에 while이 아니라 if를 사용한다
        if(match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }
        //여는 괄호의 뒤에는 반드시 닫는 괄호가 나와야한다.
        //따라서 consume을 사용하여 RIGHT_PAREN을 검사하고 나오지 않으면 에러로 처리한다.

        throw error(peek(), "Expect expression. ");
        //매치되는 표현식이 하나도 없어서 표현식을 시작할 수 없는 경우에 대한 에러처리
    }
}
