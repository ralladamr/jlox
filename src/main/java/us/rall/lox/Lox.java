package us.rall.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Main class for the Lox language. This class includes the entry point for the
 * Lox REPL and some helper methods for reporting errors.
 */
public class Lox {
    private static final Interpreter interpreter = new Interpreter();
    private static boolean hadError = false;
    private static boolean hadRuntimeError = false;

    /**
     * Reports an error message.
     *
     * @param line    The source line where the error occurred.
     * @param message The error message.
     */
    static void error(int line, String message) {
        report(line, "", message);
    }

    /**
     * Reports an error message.
     *
     * @param token   The token being parsed when the error occurred.
     * @param message The error message.
     */
    static void error(Token token, String message) {
        if (token.type() == TokenType.EOF) {
            report(token.line(), " at end", message);
        } else {
            report(token.line(), " at '%s'".formatted(token.lexeme()), message);
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    /**
     * Prints an error message.
     */
    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        if (hadError) {
            return;
        }

        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);
        if (hadError) {
            return;
        }

        interpreter.interpret(statements);
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if (hadError) {
            System.exit(65);
        }
        if (hadRuntimeError) {
            System.exit(70);
        }
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        for (; ; ) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            run(line);
            hadError = false;
        }
    }


    /**
     * Reports a runtime error.
     *
     * @param error The error to report.
     */
    static void runtimeError(RuntimeError error) {
        String message = error.getMessage();
        int line = error.getToken().line();
        System.err.printf("%s\n[line %s]%n", message, line);
        hadRuntimeError = true;
    }
}
