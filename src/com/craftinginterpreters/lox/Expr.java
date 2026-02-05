package com.craftinginterpreters.lox;

import java.util.List;

// 부록2, 교재 진도상으론 5.2와 5.3 사이에 들어간다.
// Expr 매서드 구현
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
    
    abstract <R> R accept(Visitor<R> visitor);
}
