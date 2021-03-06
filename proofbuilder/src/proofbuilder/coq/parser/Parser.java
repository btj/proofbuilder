package proofbuilder.coq.parser;

import java.util.ArrayList;
import java.util.Map;

import proofbuilder.coq.Constant;
import proofbuilder.coq.HolesContext;
import proofbuilder.coq.Lambda;
import proofbuilder.coq.Product;
import proofbuilder.coq.Term;
import proofbuilder.coq.Variable;

public class Parser {
	
	Lexer lexer;
	TokenType tokenType;
	HolesContext holesContext;
	Map<String, Constant> constants;
	ArrayList<String> context = new ArrayList<>();
	
	Parser(HolesContext holesContext, Map<String, Constant> constants, String text) {
		this.holesContext = holesContext;
		this.constants = constants;
		lexer = new Lexer(text);
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
	
	Term parseProducts() {
		if (tokenType == TokenType.LPAREN) {
			eat();
			String boundVariable = expectIdent();
			expect(TokenType.COLON);
			Term domain = parseTerm();
			expect(TokenType.RPAREN);
			context.add(boundVariable);
			Term range = parseProducts();
			context.remove(context.size() - 1);
			return new Product(boundVariable, domain, range);
		} else {
			expect(TokenType.COMMA);
			return parseTerm();
		}
	}
	
	Term parseLambdas() {
		if (tokenType == TokenType.LPAREN) {
			eat();
			String boundVariable = expectIdent();
			expect(TokenType.COLON);
			Term domain = parseTerm();
			expect(TokenType.RPAREN);
			context.add(boundVariable);
			Term body = parseLambdas();
			context.remove(context.size() - 1);
			return new Lambda(boundVariable, domain, body);
		} else {
			expect(TokenType.FAT_ARROW);
			return parseTerm();
		}
	}
	
	Term tryParsePrimaryTerm() {
		switch (tokenType) {
		case IDENT -> {
			int index = context.lastIndexOf(lexer.tokenValue);
			if (0 <= index) {
				eat();
				return new Variable(context.size() - 1 - index);
			}
			Constant constant = constants.get(lexer.tokenValue);
			if (constant != null) {
				eat();
				return constant;
			}
			throw parseError("Unbound name");
		}
		case FORALL -> {
			eat();
			if (tokenType == TokenType.LPAREN)
				return parseProducts();
			String boundVariable = expectIdent();
			expect(TokenType.COLON);
			Term domain = parseTerm();
			expect(TokenType.COMMA);
			context.add(boundVariable);
			Term range = parseTerm();
			context.remove(context.size() - 1);
			return new Product(boundVariable, domain, range);
		}
		case FUN -> {
			eat();
			if (tokenType == TokenType.LPAREN)
				return parseLambdas();
			String boundVariable = expectIdent();
			expect(TokenType.COLON);
			Term domain = parseTerm();
			expect(TokenType.FAT_ARROW);
			context.add(boundVariable);
			Term body = parseTerm();
			context.remove(context.size() - 1);
			return new Lambda(boundVariable, domain, body);
		}
		case TYPE -> {
			eat();
			int level = 0;
			if (tokenType == TokenType.LPAREN) {
				eat();
				if (tokenType != TokenType.NATURAL)
					throw parseError("Number expected");
				level = Integer.parseInt(lexer.tokenValue);
				eat();
				expect(TokenType.RPAREN);
			}
			return Term.type(level);
		}
		case PROP -> {
			eat();
			return Term.prop;
		}
		case LPAREN -> {
			eat();
			Term result = parseTerm();
			expect(TokenType.RPAREN);
			return result;
		}
		case QUES -> {
			eat();
			return holesContext.createHole();
		}
		default -> {
			return null;
		}
		}
	}
	
	Term parsePrimaryTerm() {
		Term result = tryParsePrimaryTerm();
		if (result == null)
			throw parseError("Term expected");
		return result;
	}
	
	Term parseApplication() {
		Term result = parsePrimaryTerm();
		for (;;) {
			Term argument = tryParsePrimaryTerm();
			if (argument == null)
				return result;
			result = Term.app(result, argument);
		}
	}
	
	Term parseTerm() {
		Term result = parseApplication();
		if (tokenType == TokenType.THIN_ARROW) {
			eat();
			return Term.impl(result, parseTerm());
		}
		return result;
	}
	
	public static Term parseTerm(HolesContext holesContext, Map<String, Constant> constants, String text) {
		Parser parser = new Parser(holesContext, constants, text);
		Term result = parser.parseTerm();
		if (parser.tokenType != TokenType.EOF)
			throw parser.parseError("Bad token");
		return result;
	}

}
