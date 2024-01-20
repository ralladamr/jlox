package us.rall.loxutil;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output_directory>");
            System.exit(64);
        }
        String outputDir = args[0];
        defineAst(outputDir, "Expr", Arrays.asList(
                "Binary   : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal  : Object value",
                "Unary    : Token operator, Expr right"));
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
        String path = "%s/%s.java".formatted(outputDir, baseName);
        try (PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8)) {
            writer.println("package us.rall.lox;");
            writer.println();
            writer.println("import java.util.List;");
            writer.println();
            writer.println("abstract class %s {".formatted(baseName));
            writer.println(indent("abstract <T> T accept(Visitor<T> visitor);", 1));
            writer.println();
            defineVisitor(writer, baseName, types);

            // The AST classes.
            for (String type : types) {
                String className = type.split(":")[0].trim();
                String fields = type.split(":")[1].trim();
                defineType(writer, baseName, className, fields);
            }

            writer.println("}");
        }
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println(indent("interface Visitor<T> {", 1));
        String arg = baseName.toLowerCase();
        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            String declaration = "T visit%s%s(%s %s);".formatted(typeName, baseName, typeName, arg);
            writer.println(indent(declaration, 2));
        }
        writer.println(indent("}", 1));
        writer.println();
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        String declaration = "static class %s extends %s {".formatted(className, baseName);
        writer.println(indent(declaration, 1));

        // Fields.
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            writer.println(indent("final %s;".formatted(field), 2));
        }
        writer.println();

        // Constructor.
        writer.println(indent("%s(%s) {".formatted(className, fieldList), 2));

        // Store parameters in fields.
        for (String field : fields) {
            String name = field.split(" +")[1];
            writer.println(indent("this.%s = %s;".formatted(name, name), 3));
        }
        writer.println(indent("}", 2));

        // Visitor pattern.
        writer.println();
        writer.println(indent("@Override", 2));
        writer.println(indent("<T> T accept(Visitor<T> visitor) {", 2));
        writer.println(indent("return visitor.visit%s%s(this);".formatted(className, baseName), 3));
        writer.println(indent("}", 2));

        // End class.
        writer.println(indent("}", 1));
        writer.println();
    }

    private static String indent(String text, int level) {
        final String TAB = " ".repeat(4);
        int count = Math.max(0, level);
        return "%s%s".formatted(TAB.repeat(count), text);
    }
}
