package com.craftinginterpreters.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;


// 5.2장 구문 트리 구현의 자동화를 위한 스크립트 클래스
// Expr.java 파일을 출력한다.
public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if(args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];
        defineAst(outputDir, "Expr", Arrays.asList(
                "Binary     : Expr left, Token operator, Expr right",
                "Grouping   : Expr expression",
                "Literal    : Object value",
                "Unary      : Token operator, Expr right"
        ));
    }

    // 5.2장 베이스 클래스 Expr 클래스를 출력하는 일을 한다.
    private static void defineAst(
            String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package com.craftinginterpreters.lox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("abstract class " + baseName + " {");

        //5.3 비지터 패턴 적용
        defineVisitor(writer, baseName, types);

        //5.2장 AST 클래스
        for(String type : types) {
            String className = type.split(":")[0].trim(); //split는 문자열을 자르는 함수
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        //5.3장 비지터 패턴을 위한 베이스 accept 메서드
        writer.println();
        writer.println("    abstract <R> R accept(Visitor<R> visitor);");

        writer.println("}");
        writer.close();
    }

    // 5.2장 클래스 바디에 각 필드를 선언하고, 각 필드를 매개변수로 받는 클래스 생성자를 정의
    private static void defineType(
        PrintWriter writer, String baseName, String className,  String fieldList) {
        writer.println("    static class " + className + " extends " + baseName + " {");

        //생성자
        writer.println("    " + className + "(" + fieldList + ") {");

        //매개변수를 필드에 저장
        String[] fields = fieldList.split(", ");
        for(String field : fields) {
            String name = field.split(" ")[1];
            writer.println("     this." +name + " = " + name + ";");
        }

        writer.println("    }");

        //5.3장 비지터 패턴
        writer.println();
        writer.println("    @Override");
        writer.println("    <R> R accept(Visitor<R> visitor) {");
        writer.println("        return visitor.visit" +
                className + baseName + "(this);");
        writer.println("    }");

        //필드
        writer.println();
        for(String field : fields) {
            writer.println("     final " + field + ";");
        }

        writer.println("    }");
    }

    //5.3장 비지터 패턴을 위한 defineVisitor
    //비지터 패턴이란 타입과 알고리즘을 분리시키는 기법이다.
    private static void defineVisitor(
            PrintWriter writer, String baseName, List<String> types) {
        writer.println("    interface Visitor<R> {");

        for(String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("    R visit" + typeName + baseName + "(" +
                    typeName + " " + baseName.toLowerCase() + ");");
        }

        writer.println("    }");
    }
}
