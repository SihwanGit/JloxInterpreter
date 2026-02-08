package com.craftinginterpreters.lox;

import java.util.List;

// 부록2, 교재 진도상으론 5.2와 5.3 사이에 들어간다.
// Expr 매서드 구현

//Expr은 파싱 단계에서 나오는 문법 중 Expr 문법에 속하는 애들을 한번에 묶어서 처리하기 위한 공통 루트 클래스다.
abstract class Expr { 
    interface Visitor<R> {
        R visitAssignExpr(Assign expr);
        R visitBinaryExpr(Binary expr);
        R visitCallExpr(Call expr);
        R visitGetExpr(Get expr);

        R visitGroupingExpr(Grouping expr);
        R visitLiteralExpr(Literal expr);
        R visitLogicalExpr(Logical expr);
        R visitSetExpr(Set expr);

        R visitSuperExpr(Super expr);
        R visitThisExpr(This expr);
        R visitUnaryExpr(Unary expr);
        R visitVariableExpr(Variable expr);
    }
    
    //여기에 각 Expr들이 들어간다.

    //할당 표현식
    static class Assign extends Expr {
        Assign(Token name, Expr value) {
            this.name = name;
            this.value = value;
            //할당문은 변수와 값으로 구성
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }
        final Token name;
        final Expr value;
    }

    //이항 표현식
    static class Binary extends Expr {
        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
            // 이항 연산은 left op right 형태.
            // 할당은 op가 =으로 고정되어 있지만, 얘는 +-*/ 다 가능
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }
        final Expr left;
        final Token operator;
        final Expr right;
    }

    //호출 표현식
    static class Call extends Expr {
        Call(Expr callee, Token paren, List<Expr> arguments) {
            this.callee = callee;
            this.paren = paren;
            this.arguments = arguments;
            // 함수 호출은 이름(callee) + 괄호 + 실인자 형태
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitCallExpr(this);
        }
        final Expr callee;
        final Token paren;
        final List<Expr> arguments;
    }

    //get 표현식
    //얘는 12.4절에 프로퍼티 엑세스를 설명하면서 나온다.
    //간단히 다루자면, 클래스의 객체를 사용할 때 '객체이름.매서드' 할 때의 .역할임.
    //객체들의 맴버를 호출하는 역할.
    static class Get extends Expr {
        Get(Expr object, Token name) {
            this.object = object;
            this.name = name;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGetExpr(this);
        }
        final Expr object;
        final Token name;
    }

    // 그룹핑 표현식
    static class Grouping extends Expr {
        Grouping(Expr expression) {
            this.expression = expression;
            // ( )는 고정이고 그 사이에 expression이 나오는 형태
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }
        final Expr expression;
    }

    //리터럴 표현식
    static class Literal extends Expr {
        Literal(Object value) {
            this.value = value;
            //리터럴은 값만 들어오면 됨.
            //값의 타입은 떄에따라 다르므로 Object로 설정
            //Token 클래스에서도 literal은 Object로 설정했었음
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        final Object value;
    }

    //논리 표현식
    static class Logical extends Expr {
        Logical(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
            //논리식은 right 논리연산op right로 구성
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
        }
        final Expr left;
        final Token operator;
        final Expr right;
    }

    //set 표현식
    //get과 마찬가지로 12.4절의 프로퍼티 부분에서 나오는 내용이다.
    //get이 프로퍼티 엑세스였다면, set은 프로퍼티 할당의 표현식이다
    static class Set extends Expr {
        Set(Expr object, Token name, Expr value) {
            this.object = object;
            this.name = name;
            this.value = value;
        }
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitSetExpr(this);
        }
        final Expr object;
        final Token name;
        final Expr value;
    }

    //super Expression
    //13.3절에 나오는 super 표현식이다.
    static class Super extends Expr {
        Super(Token keyword, Token method) {
            this.keyword = keyword;
            this.method = method;
        }
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitSuperExpr(this);
        }
        final Token keyword;
        final Token method;
    }

    //this 표현식
    //12.6절에 나오는 this Expression이다.
    static class This extends Expr {
        This(Token keyword) {
            this.keyword = keyword;
        }
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitThisExpr(this);
        }
        final Token keyword;
    }

    //단항 표현식
    static class Unary extends Expr {
        Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }
        final Token operator;
        final Expr right;
    }
    
    abstract <R> R accept(Visitor<R> visitor);
}
