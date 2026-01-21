package com.craftinginterpreters.lox;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;
//자바의 정적 임포트. instance 생성 없이 해당 클래스의 매서드를 사용할 수 있다.

//4.4장 Scanner 클래스
class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;
    private int line = 1;
    //4.4장 start, current, line은 Scanner가 현재 코드의 어디를 뒤지는 중인지 기록하는 용도다.
    //4.4장 start, current 필드는 문자열의 위치를 가리키는 offset이다.
    //start는 스캔 중인 렉스의 춧 문자, current는 현재 처리 중인 문자, line 필드는 current가 위치한 줄이다.

    //4.7 키워드를 처리하기 위한 맵
    private static final Map<String, TokenType> keywords;
    static {
        keywords = new HashMap<>(); //키워드는 맵으로 정의한다.
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }

    Scanner(String source) { //run함수에서 매개변수로 받은 source를 this.source로 설정함
        this.source = source;
    }

    //4.4장 스캐너 클래스의 핵심루프
    List<Token> scanTokens() {
        while(!isAtEnd()) {
            //다음 렉심의 시작 부분에 있다.
            start = current;
            //사용자가 입력한 코드가 문자열 형태로 들어오면 그걸 앞에서부터 쭉 읽는다.
            //start는 고정되어 있고 current가 쭉 앞으로 가면서 읽는다.
            //current가 멈추면 start부터 current까지가 하나의 토큰이 되고, 다시 start = current로 둘의 위치를 맞춘다.
            //이걸 문장이 끝날때까지 계속 반복한다.
            scanToken();
        }
        //소스코드를 처음부터 끝까지 읽어 문자가 없을 떄까지 토큰을 추가한다.

        tokens.add(new Token(EOF, "", null, line));
        //마지막에 Eof, "" null, line을  가진 토큰을 추가한다.
        //EOF 토큰을 붙여 더 깔끔하게 만든다. (꼭 넣을 필요는 없다.)
        return tokens;
    }

    //4.4장 문자를 모두 소비했는지 체크하는 헬퍼 메서드
    private boolean isAtEnd() {
        return current >= source.length();
    }

    //4.5 토큰을 읽어들이는 함수
    private void scanToken() { //4.5 이름 주의해라.
        char c = advance();
        switch(c) {
            //하나의 문자로 구성된 렉심
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;

            //2개 이상의 문자로 구성된 렉심
            //사실 *나 +-도 복합대입연산자나 증감연산자를 추가하면 검사해야됨.
            //지금은 문법상으로 이것들을 정의하지 않아서 구분하지 않은 것.
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG); break; //!과 !=을 구분하기 위해 ! 뒤에 =이 오는지 확인
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS); break;
            case '>' :
                addToken(match('=') ? GREATER_EQUAL : GREATER); break;

            //4.6장 '/'는 나눗셈, // 주석이 있기 때문에 별도로 처리해야됨.
            // //이 나오면 해당 라인을 전부 주석으로 처리해서 제외시켜야됨.
            // /=와 /* */ 주석은 정의하지 않았기 때문에 제외
            case '/':
                if(match('/')) {
                    //주석은 줄 끝까지 이어진다
                    while(peek() != '\n' && !isAtEnd()) advance();
                    //끝나거나 강제개행 전까지 advance만 수행하고 addToken은 호출하지 않는다.
                } else if(match('*')) { //4장 연습문제 4번
                    int level = 0; // 주석 중첩을 위한 레벨
                    while(!(peek() == '*' && peekNext() == '/' && level == 0) && !isAtEnd()) {
                        // 만약 조건을 while(peek() != '*' && peekNext() != '/') 이렇게 작성하면,
                        // *만 나왔을 떄 peekNext는 /이 아니지만 peek != '*'가 F가 되면서 while문이 깨진다.
                        // 그래서 조건을 !(peek == '*' && peekNext == '/')으로 설정해야, 중간에 *가 와도 안깨진다.
                        // 이후 */이 나오고 level일 때만 종료되게끔 규칙을 수정한다.

                        if(peek() == '/' && peekNext() == '*') { //도중에 /* 이 또 나오면 level을 올린다.
                            level++;
                        }
                        if(peek() == '*' && peekNext() == '/' && level != 0) { // 다시 */ 이 나오면 level 감소
                            level--;
                        }
                        if(peek() == '\n') line++; //peek가 강제 개행 문자면 line++
                        advance(); // 토큰은 만들지 않고 소모
                    }
                    // *와 /를 룩어헤드로 확인했기 떄문에 */는 그대로 source에 남아있다.
                    // 따라서 advance()를 두번 실행해 처리해줘야한다.
                    advance();
                    advance();
                } else {
                    addToken(SLASH);
                }
                break;

            // 공백은 무시한다.
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;

            //4.6 문자열 리터럴
            case '"': string(); break;

            //에러를 처리하지 않으면 무한루프에 빠질 수도 있음
            default:
                if(isDigit(c)) { //4.6 숫자는 0~9를 다 switch로 처리하면 귀찮으니까 default로 빼서 처리
                    number();
                } else if(isAlpha(c)) { //4.7 id와 키워드로 처리
                    identifier();
                }
                else { //4.5 등록되지 않은 토큰은 에러처리 Ex : @ # ^ etc
                    Lox.error(line, "Unexpected character");
                }
                break;
        }
    }

    //4.5 current를 더해가며 차례대로 소스파일의 다음 문자를 읽어 리턴한다.
    //scanTokens(읽은 토큰들을 배열에 저장) -> scanToken(읽은 토큰들을 체크) -> adbance(단어 하나하나 읽음)
    //current가 무조건 증가하기 때문에 한번 읽은 문자는 다시 읽지 않음. 그것을 책에서는 "소비했다"고 표현한다.
    //읽은 문자를 "소비"해서 다시 인식이 되지 않게 함. (정의되지 않은 문자도 포함)
    private char advance() {
        return source.charAt(current++);
    }

    //4.5 리터럴(값)이 없은 토큰
    private void addToken(TokenType type) {
        addToken(type, null); //값이 없는 토큰은 값을 null로 처리한다.
    }

    //4.5 리터럴(값)이 있는 토큰
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        //우리가 입력받은 코드 문자열 source에서 start부터 current까지 서브스트링으로 분리
        tokens.add(new Token(type, text, literal, line));
    }

    //4.5장 2개 이상의 문자로 구성된 렉심을 검사할 때 뒤에 오는 토큰에 따라 T/F값을 반환하는 함수
    //조건부 advance라고 생각하면 편하다.
    //4.6 match는 true일 땐 advance, false일 땐 peek라서 LOOKAHEAD로 볼 수도 있다.
    private boolean match(char expected) {
        if(isAtEnd()) return false; //입력을 다 읽었으면 False를 반환
        if(source.charAt(current) !=  expected) return false; //매개변수로 받은 문자와 다르면
        // Ex match(=)인데 =이 아닌 다른 문자가 뒤에 오면 false를 반환

        current++; //같은 토큰으로 묶일 예정이니까 ++로 소비처리
        return true;
    }

    //4.6 peek는 advance와 비슷하나 문자를 소비하지 않는다.
    //advance는 읽고 버리면, 얘는 그냥 읽기만 함. 이를 LOOKAHEAD라고 부른다.
    private char peek() {
        if(isAtEnd()) return '\0';
        return source.charAt(current); //advance는 current++이였는데, 얘는 그냥 current만 했다.
    }
    //peek(int n) return source.charAt(current + n)으로 n번째 뒤의 peek로도 구현 가능

    // 4.6 string literal 처리를 위한 string 함수
    private void string() {
        while(peek() != '"' && !isAtEnd()) { //문자열의 끝을 나타내는 "는 소모되면 안되므로 peek() 사용
            if(peek() == '\n') line++;
            //록스는 \n이 나와도 "가 나오기 전까지는 문자열로 인식한다. (그래도 line은 갱신해야 된다)
            //언어에 따라 이걸 금지하는 언어도 있다.
            advance(); //문자열 끝부분의 쌍따옴표가 나오기 전까지 advance
        }

        if(isAtEnd()) {
            Lox.error(line, "Unterminated string"); //뒤쪽에 "가 안나오면 끝나지 않는 문자열 에러
            return;
        }

        //닫는 큰따옴표
        advance();

        //앞뒤 끝따옴표 제거
        //이전까지는 토큰이 여러개의 문자로 이루어진 경우 시작부터 끝까지 묶어서 토큰을 만듦
        //그러나 문자열 리터럴은 시작과 끝을 제외한 " "의 안쪽만 묶어서 토큰으로 만듦 (lexeme은 " " 포함)
        String value = source.substring(start+1, current-1);
        addToken(STRING, value);
    }
    
    //4.6 매개변수 c가 숫자인지 문자인지 구분
    //자바 표준 라이브러리의 isDigit을 써도 되지만 그건 록스에 없는 기능도 제공해서 꼬일 수도 있으니 새로 만든 것
    private boolean isDigit(char c) { return c >= '0' && c <= '9'; }
    
    //4.6 숫자로 변환하는 코드
    private void number() {
        while(isDigit(peek())) advance(); //peek로 확인한 다음 문자가 숫자가 맞으면 진행
        
        //소수부를 피크하는 코드
        if(peek() == '.' && isDigit(peekNext())) { //그럼 상식적으로 peekNext는 peek의 다음 lexeme을 읽는 거겠죠?
            advance(); //dot를 소모
            while(isDigit(peek())) advance(); //숫자로 처리
        }
        
        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
        //start부터 current까지 읽은 문자열을 double형 NUMBER 리터럴로 토큰 생성.
    }
    
    //4.6 peek의 다음 lexeme을 읽는 함수
    //peek와 마찬가지로 LookAhead로써 lexeme을 소모하지는 않는다.
    //숫자 리터럴을 처리할 때 peek로 dot를 인식하면 그 뒤에 숫자가 나오는지 확인해야됨.
    //록스는 123. 이나 .123같은 후행/선행은 지원하지 않는다.
    //사실 이 함수를 만들지 않고 peek()가 미리 Lookahead할 문자 개수를 매개변수로 받는 식으로 구현해도 됨.
    private char peekNext() {
        if(current +1 >= source.length()) return '\0'; //다다음 문자가 끝나는 지점인 경우
        return source.charAt(current+1);
    }

    //4.7 identifier에 사용할 수 있는 문자 : 알파벳대소문자 + 언더바
    private boolean isAlpha( char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric( char c) {
        return isAlpha(c) || isDigit(c);
    }

    //4.7 identifier 토큰 생성
    private void identifier() {
        while(isAlphaNumeric(peek())) advance();
        //다음 문자가 알파벳 또는 숫자라면, 계속 소비한다.

        String text = source.substring(start, current); //읽은 문자들을 text에 저장
        TokenType type = keywords.get(text); //text에 저장된 문장이 키워드면 type은 키워드다.
        if(type == null) type = IDENTIFIER; //그렇지 않다면 식별자다.
        addToken(type); //식별자or키워드로 토큰을 생성한다
    }

}
