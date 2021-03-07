package proofbuilder.pythonparser;

public class Lexer {

	String text;
	int pos = -1;
	int c;
	int tokenStart;
	String tokenValue;
	
	Lexer(String text) {
		this.text = text;
		eat();
	}
	
	void eat() {
		pos++;
		if (pos == text.length())
			c = 0;
		else
			c = text.charAt(pos);
	}
	
	static boolean isAlpha(int c) {
		return 'a' <= c && c <= 'z' || 'A' <= c && c <= 'Z' || c == '_';
	}
	
	static boolean isDigit(int c) {
		return '0' <= c && c <= '9';
	}
	
	RuntimeException lexerError(String message) {
		return new ParserException(message);
	}
	
	TokenType nextToken() {
		while (c == ' ' || c == '\t' || c == '\n' || c == '\r')
			eat();
		tokenStart = pos;
		if (isAlpha(c)) {
			eat();
			while (isAlpha(c) || isDigit(c))
				eat();
			tokenValue = text.substring(tokenStart, pos);
			switch (tokenValue) {
			case "and": return TokenType.AND;
			default: return TokenType.IDENT;
			}
		}
		if (isDigit(c)) {
			eat();
			while (isDigit(c))
				eat();
			tokenValue = text.substring(tokenStart, pos);
			return TokenType.IDENT;
		}
		switch (c) {
		case 0: return TokenType.EOF;
		case '=': eat(); if (c != '=') throw lexerError("= expected"); eat(); return TokenType.EQ;
		case '<': eat(); if (c != '=') throw lexerError("= expected"); eat(); return TokenType.LE;
		case '(': eat(); return TokenType.LPAREN;
		case ')': eat(); return TokenType.RPAREN;
		default: throw lexerError("Bad character");
		}
	}
}
