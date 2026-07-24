package com.craftinginterpreters.lox;

//5.4장 pretty print를 위한 AST 출력기 생성
//연산자 우선순위나 AST가 제대로 생성됐는지를 검사하는 디버거
//Expr 추상 클래스는 부록 2에 나온다.
public class AstPrinter implements Expr.Visitor<String>{
    String print(Expr expr) {
        return expr.accept(this);
    }

    //5.4장 각 연산들의 비지트 매서드 추가
    //각 서브식마다 accept를 호출하면서 스스로를 인수로 넘긴다.
    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
        // 이항연산은 op, left, right를 출력

        // 6장 연습문제 1 : comma 식도 Binary 연산에 속한다.
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
        // 괄호가 있는 연산은 ( "group", 수식 ) 형태로 표현
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if(expr.value == null) return "nil";
        return expr.value.toString();
        //리터럴의 경우 없으면 nil, 있으면 해당 값을 toString()으로 문자열로 바꿔 출력
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
        //단항 연산자들은 op와 operand를 괄호로 묶어 출력
    }

    //5.4장 AST로 구성된 수식의 양 끝을 ( )로 묶는 매서드
    // (name Expr...) 과 같은 형태로 출력된다.
    // name은 위 visit 매서드들의 op와 "group" 부분에 해당한다.
    // name이 해당 구문의 AST에서 부모 노드 부분이고, 뒤에 나오는 Expr...들이 자식노드에 속한다.
    // (부모 자식) 형태에서 자식의 Expr이 다시 재귀적으로 parenthesize를 호출하는 형태
    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for(Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

    //테스트용 main 메서드 (이후에는 삭제해도 무관함)
    //해당 구문은 -123 * (45.67)로 이를 print 함수로 출력하면 아래와 같다.
    //(* (- 123) (group 45.67))
    public static void main(String[] args) {
        Expr expression = new Expr.Binary(
                //Expr.Binary는 opr1 op opr2 형태 (각 opr도 Expr이므로 그 안에 수식을 또 넣을 수 있다.)
                //opr1
                new Expr.Unary(
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Expr.Literal(123)
                ),
                //op
                new Token(TokenType.STAR, "*", null, 1),
                //opr2
                new Expr.Grouping(
                        new Expr.Literal(45.67)
                )
        );

        System.out.println(new AstPrinter().print(expression));
    }
}
