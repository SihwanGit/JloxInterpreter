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

    //7.2 매개변수로 들어온 expr을 그냥 돌려보내는 헬퍼 매서드
    //추후 lvalue / rvalue를 구현할 때도 쓰인다고 합니다
    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    //7.2 단항식 평가
    //단항식은 먼저 오른쪽 expr을 계산후 연산자가 -면 right에 -를 곱해준다.
    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);
        //먼저 피연산자 right를 평가(evaluate) 후 그 결과에 단항연산자를 적용한다.
        switch (expr.operator.type) {
            case BANG:
                return !isTruthy(right);
                // 우변에 불리안이 오지 않는 경우에 대비한 에러처리와 암묵적 변환을 지원한다
            case MINUS:
                return -(double)right;
            // -연산자를 적용하려면 피연산자가 숫자여야한다.
            //자바는 정적으로 타입을 알 수 없기 때문에 (double)로 캐스팅을 해줬다.
            //단항연산자를 계산할 때 변수의 타입을 바꾸기 때문에 Lox가 동적 타입 언어다.
            //캐스팅에 실패할 수도 있으니 그에 따른 에러 처리도 추가할 예정이다.
        }
        //실행되지 않는 코드
        return null;
    }

    //7.2 암묵적 변환을 실시하는 매서드
    private boolean isTruthy(Object object) {
        if(object == null) return false;
        if(object instanceof Boolean) return (boolean)object;
        //타입이 불리안이면 그 값 그대로 리턴
        return true;
        //Lox언어는 null만 false와 null은 false로, 나머지는 전부 true로 한다.
        //이는 루비 규칙과 같다.
    }


}
