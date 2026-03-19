package com.craftinginterpreters.lox;

class Interpreter implements Expr.Visitor<Object> {
    // 7.2 인터프리터도 AstPrinter처럼 비지터 패턴으로 구현

    // 7.2 리터럴 평가
    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value; //리터럴은 값만 리턴한다
        //그렇다고 리터럴이랑 값이 같은 개념인건 아니다.
        //리터럴은 값을 만들어주는 구문조각이고, 값은 계산 결과 만들어진 것이다.
        //리터럴은 코드에 존재하지만, 값은 그 자체로 코드에 등장하지 않는다.
        // 스캐닝 도중 런타임 값을 만들어 토큰의 리터럴에 넣었고,
        // 파서는 이걸 가져와 리터럴 트리 노드에 집어넣었다.
        // 그리고 이 클래스에서 해당 값을 다시 꺼내와 의미론 계산에 사용한다.
    }

    // 7.2 괄호 평가
    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }
    // grouping 노드에는 괄호 안에 포함된 내부 노드의 참조가 있다.
    // grouping은 이 서브식을 재귀적으로 평가해 리턴한다

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

}
