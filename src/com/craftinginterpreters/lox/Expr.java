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
    static class Assign extends Expr {
        Assign(Token name, Expr value) {
            this.name = name;
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }
        final Token name;
        final Expr value;
    }
    
    abstract <R> R accept(Visitor<R> visitor);
}
