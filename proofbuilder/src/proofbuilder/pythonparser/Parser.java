package proofbuilder.pythonparser;

import java.util.Map;

import proofbuilder.coq.Application;
import proofbuilder.coq.Constant;
import proofbuilder.coq.Term;

public class Parser {

	Map<String, Constant> constants;
	Lexer lexer;
	TokenType tokenType;
	
	public Parser(Map<String, Constant> constants, String text) {
		this.constants = constants;
		this.lexer = new Lexer(text);
		eat();
	}
	
	void eat() {
		tokenType = lexer.nextToken();
	}
	
	RuntimeException parseError(String message) {
		return new ParserException(message);
	}

	String expectIdent() {
		if (tokenType != TokenType.IDENT)
			throw parseError("Identifier expected");
		String result = lexer.tokenValue;
		eat();
		return result;
	}
	
	void expect(TokenType tokenType) {
		if (this.tokenType != tokenType)
			throw parseError(tokenType + " expected");
		eat();
	}
	
	Term parsePrimaryExpression() {
		switch (tokenType) {
		case IDENT -> {
			String identifier = lexer.tokenValue;
			eat();
			if (!constants.containsKey(identifier))
				throw parseError("NameError: name '" + identifier + "' is not defined");
			return constants.get(identifier);
		}
		default -> throw parseError("Identifier expected");
		}
	}
	
	Term parsePostfixExpression() {
		Term term = parsePrimaryExpression();
		for (;;) {
			switch (tokenType) {
			case LPAREN -> {
				eat();
				term = new Application(term, parseExpression());
//				while (tokenType == TokenType.COMMA) {
//					eat();
//					term = new Application(term, parseExpression());
//				}
				expect(TokenType.RPAREN);
			}
			default -> {
				return term;
			}
			}
		}
	}
	
	Term parseRelationalExpression() {
		Term term = parsePostfixExpression();
		switch (tokenType) {
		case EQ, LE -> {
			TokenType operator = this.tokenType;
			eat();
			Term term2 = parsePostfixExpression();
			return new Application(new Application(constants.get("#" + operator.toString()), term), term2);
		}
		default -> {
			return term;
		}
		}
	}
	
	Term parseConjunction() {
		Term term = parseRelationalExpression();
		if (tokenType == TokenType.AND) {
			eat();
			Term term2 = parseConjunction();
			return new Application(new Application(constants.get("#AND"), term), term2);
		}
		return term;
	}
	
	Term parseExpression() {
		return parseConjunction();
	}
	
	public static Term parseExpression(Map<String, Constant> constants, String text) {
		Parser parser = new Parser(constants, text);
		Term result = parser.parseExpression();
		if (parser.tokenType != TokenType.EOF)
			throw parser.parseError("End of expression expected");
		return result;
	}
}
