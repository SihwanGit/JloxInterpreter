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
    
    abstract <R> R accept(Visitor<R> visitor);
}
