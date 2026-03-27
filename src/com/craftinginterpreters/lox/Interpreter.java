package com.craftinginterpreters.lox;

class Interpreter implements Expr.Visitor<Object> {
    // 7.2 인터프리터도 AstPrinter처럼 비지터 패턴으로 구현


    //7.4 인터프리터 매서드
    void interpret(Expr expression) {
        try {
            Object value = evaluate(expression);
            System.out.println(stringify(value));
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }
    // Expr에 대한 구문 트리를 가져와 평가.
    // 성공시 evaluate의 결과값을 해당 객체에게 리턴
    // 그 후 사용자에게 해당 값을 문자열로 변환해 보여준다.

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
                checkNumberOperand(expr.operator, right);
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

    private boolean isEqual(Object a,  Object b) {
        if(a == null && b == null) return true; //둘 다 널이면 true
        if(a==null) return false; //a가 널이면 false
        return a.equals(b); //나머지는 eqaul로 같은지 검사해서 결과 반환
    }

    //7.4 록스 값을 문자열로 변환하는 매서드
    private String stringify(Object object) {
        if(object == null) return "nil";

        if(object instanceof Double) { //실수면 문자열로 변환
            String text = object.toString();
            if(text.endsWith(".0")) {
                text = text.substring(0, text.length()-2);
            } //만약 실수가 2.0처럼 .0으로 끝나면 .과 0은 출력하지 않는다.
            //록스는 정수타입이 없어서 실수도 정수로 계산하기 떄문에 소수점을 지워주는 부분을 추가했다.
            return text;
        }

        return object.toString();
    }


    //7.3 지금까지의 에러처리는 문법이나 토큰같은 정적인 에러였다.
    //그러나 이제부터는 런타임 에러를 다룬다.
    //해당 checkNumberOperand 매서드는 피연산자의 타입이 숫자인지 따지고, 아니면 에러처리한다.
    private void checkNumberOperand(Token operator, Object operand) {
        if(operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number");
    }

    //7.3 이항연산자의 체크넘버
    private void checkNumberOperands(Token operator, Object left, Object right) {
        if(left instanceof Double && right instanceof Double) return; //두 피연산자가 모두 Number면 정상
        throw new RuntimeError(operator, "Operands must be numbers");
    } //해당 코드는 Double인지만 체크해서 +의 String 접합은 체크 못함.

    //7.2 이항연산자 평가
    //7.3 사칙연산과 비교연산에서 피연산자가 Double이 아닌 경우에 대한 에러 추가
    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        //좌변과 우항의 Expr을 먼저 계산(평가)

        switch (expr.operator.type) {
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
            //비교 연산자는 보이는대로 계산하면 된다.
            //산술은 입력과 타입이 같은 값을 만들어내지만,
            //비교는 입력 타입에 관계없이 무조건 불리안 값을 만든다.
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;
            // -/*는 그냥 피연산자끼리 계산
            // 계산과정에서 side effect가 일어나도 인지할 수 있으므로 구현상세는 아니다.
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left - (double)right;
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return (double)left / (double)right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
            case PLUS:
                if(left instanceof Double && right instanceof Double)
                    return (double)left + (double)right;
                if(left instanceof String && right instanceof String)
                    return (String)left + (String)right;
                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings");
            // return 으로 끝나는 경우에는 break안붙여도 됨.
            // 그런데 PLUS는 if가 둘다 false인 경우 실행이 안되니 break를 붙인다
            // plus는 숫자면 더하고, 문자열이면 서로 연결하기 때문에 double와 string으로 나눴다.
            // 하스켈, 펄, 루아, 스몰토크 같은 언어는 문자열 연결 연산자를 따로 정의했다고 한다.
                // 7.3 +는 이미 double과 string에 대한 타입검사를 수행하고
                // checkNumberOperands 매서드를 쓸 필요 없이 직접 number도 string도 아닌 케이스에 대한 에러를 삽입함.
                // 기존에는 에러처리가 없어서 break를 썼어야했지만 이제는 지워도 됨.
        }
        // 실행되지 않는 코드
        return null;
    }

}
