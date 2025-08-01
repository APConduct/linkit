package org.linkit

/** Token types for the expression parser */
sealed class Token {
    data class Number(val value: Double) : Token()
    data class Identifier(val name: String) : Token()
    data class Operator(val op: String) : Token()
    object LeftParen : Token()
    object RightParen : Token()
    object EOF : Token()
}

/** Exception thrown when parsing fails */
class ParseException(message: String) : Exception(message)

/** Tokenizer for mathematical expressions */
class Tokenizer(private val input: String) {
    private var position = 0
    private val length = input.length

    fun tokenize(): List<Token> {
        val tokens = mutableListOf<Token>()

        while (position < length) {
            skipWhitespace()
            if (position >= length) break

            val token =
                    when (val char = input[position]) {
                        '(' -> {
                            position++
                            Token.LeftParen
                        }
                        ')' -> {
                            position++
                            Token.RightParen
                        }
                        '+', '-', '*', '/', '^', '%' -> {
                            position++
                            Token.Operator(char.toString())
                        }
                        in '0'..'9', '.' -> parseNumber()
                        in 'a'..'z', in 'A'..'Z', '_' -> parseIdentifier()
                        else ->
                                throw ParseException(
                                        "Unexpected character: $char at position $position"
                                )
                    }
            tokens.add(token)
        }

        tokens.add(Token.EOF)
        return tokens
    }

    private fun skipWhitespace() {
        while (position < length && input[position].isWhitespace()) {
            position++
        }
    }

    private fun parseNumber(): Token.Number {
        val start = position
        var hasDecimal = false

        while (position < length) {
            val char = input[position]
            when {
                char.isDigit() -> position++
                char == '.' && !hasDecimal -> {
                    hasDecimal = true
                    position++
                }
                else -> break
            }
        }

        val numberStr = input.substring(start, position)
        val value =
                numberStr.toDoubleOrNull()
                        ?: throw ParseException("Invalid number: $numberStr at position $start")

        return Token.Number(value)
    }

    private fun parseIdentifier(): Token.Identifier {
        val start = position

        while (position < length && (input[position].isLetterOrDigit() || input[position] == '_')) {
            position++
        }

        val identifier = input.substring(start, position)
        return Token.Identifier(identifier)
    }
}

/**
 * Recursive descent parser for mathematical expressions Grammar: expression := term (('+' | '-')
 * term)* term := factor (('*' | '/' | '%') factor)* factor := power ('^' power)* power := unary |
 * primary unary := ('-' | function_name) primary primary := number | identifier | '(' expression
 * ')'
 */
class ExpressionParser(private val tokens: List<Token>) {
    private var position = 0

    fun parse(): Expr {
        val expr = parseExpression()
        if (currentToken() != Token.EOF) {
            throw ParseException("Unexpected token after expression: ${currentToken()}")
        }
        return expr
    }

    private fun currentToken(): Token = if (position < tokens.size) tokens[position] else Token.EOF

    private fun consume(): Token {
        val token = currentToken()
        position++
        return token
    }

    private fun parseExpression(): Expr {
        var left = parseTerm()

        while (true) {
            when (val token = currentToken()) {
                is Token.Operator -> {
                    when (token.op) {
                        "+" -> {
                            consume()
                            val right = parseTerm()
                            left = Expr.BinaryOp(left, Operation.Binary.Add, right)
                        }
                        "-" -> {
                            consume()
                            val right = parseTerm()
                            left = Expr.BinaryOp(left, Operation.Binary.Subtract, right)
                        }
                        else -> break
                    }
                }
                else -> break
            }
        }

        return left
    }

    private fun parseTerm(): Expr {
        var left = parseFactor()

        while (true) {
            when (val token = currentToken()) {
                is Token.Operator -> {
                    when (token.op) {
                        "*" -> {
                            consume()
                            val right = parseFactor()
                            left = Expr.BinaryOp(left, Operation.Binary.Multiply, right)
                        }
                        "/" -> {
                            consume()
                            val right = parseFactor()
                            left = Expr.BinaryOp(left, Operation.Binary.Divide, right)
                        }
                        "%" -> {
                            consume()
                            val right = parseFactor()
                            left = Expr.BinaryOp(left, Operation.Binary.Modulo, right)
                        }
                        else -> break
                    }
                }
                else -> break
            }
        }

        return left
    }

    private fun parseFactor(): Expr {
        var left = parsePower()

        while (true) {
            when (val token = currentToken()) {
                is Token.Operator -> {
                    when (token.op) {
                        "^" -> {
                            consume()
                            val right = parsePower()
                            left = Expr.BinaryOp(left, Operation.Binary.Power, right)
                        }
                        else -> break
                    }
                }
                else -> break
            }
        }

        return left
    }

    private fun parsePower(): Expr {
        return parseUnary()
    }

    private fun parseUnary(): Expr {
        when (val token = currentToken()) {
            is Token.Operator -> {
                when (token.op) {
                    "-" -> {
                        consume()
                        val operand = parseUnary()
                        return Expr.UnaryOp(Operation.Unary.Negate, operand)
                    }
                    "+" -> {
                        consume()
                        return parseUnary() // Unary plus, just return the operand
                    }
                }
            }
            is Token.Identifier -> {
                // Check if it's a function call
                val name = token.name.lowercase()
                when (name) {
                    "sin", "cos", "tan", "sqrt", "ln", "log10", "abs" -> {
                        consume()
                        val operand = parsePrimary()
                        val operation =
                                when (name) {
                                    "sin" -> Operation.Unary.Sin
                                    "cos" -> Operation.Unary.Cos
                                    "tan" -> Operation.Unary.Tan
                                    "sqrt" -> Operation.Unary.Sqrt
                                    "ln" -> Operation.Unary.Ln
                                    "log10" -> Operation.Unary.Log10
                                    "abs" -> Operation.Unary.Abs
                                    else -> throw ParseException("Unknown function: $name")
                                }
                        return Expr.UnaryOp(operation, operand)
                    }
                }
            }
            else -> {
                // Not a unary operator or function, fall through to parsePrimary
            }
        }

        return parsePrimary()
    }

    private fun parsePrimary(): Expr {
        when (val token = currentToken()) {
            is Token.Number -> {
                consume()
                return Expr.Number(token.value)
            }
            is Token.Identifier -> {
                consume()
                return Expr.Variable(token.name.uppercase())
            }
            is Token.LeftParen -> {
                consume() // consume '('
                val expr = parseExpression()
                when (currentToken()) {
                    is Token.RightParen -> consume() // consume ')'
                    else -> throw ParseException("Expected ')' but found: ${currentToken()}")
                }
                return expr
            }
            else -> throw ParseException("Expected number, identifier, or '(' but found: $token")
        }
    }
}

/** Main parser class that combines tokenizing and parsing */
class Parser {
    fun parse(input: String): Expr {
        if (input.isBlank()) {
            throw ParseException("Empty expression")
        }

        val tokenizer = Tokenizer(input)
        val tokens = tokenizer.tokenize()
        val parser = ExpressionParser(tokens)
        return parser.parse()
    }
}
