import com.craftinginterpreters.lox.Lox;
import com.craftinginterpreters.lox.AstPrinter;

public class Main {
    public static void main(String[] args) throws Exception {
        // args를 비워서 전달하면 Lox는 runPrompt()를 실행한다

        //4장의 Scanner 코드
        //6장의 Expression Parsing 코드 추가
        Lox.main(new String[0]);

        //5장의 AstPrinter 코드. 해당 코드는 나중에 지워도 됨.
        //AstPrinter.main(args);
    }
}
