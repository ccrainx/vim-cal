import java.util.*;

public class main {
    private static final String[][] NUMPAD = {
        {"7", "8", "9", "/"},
        {"4", "5", "6", "*"},
        {"1", "2", "3", "-"},
        {"0", ".", "=", "+"}
    };

    private static int cursorRow = 0;
    private static int cursorCol = 0;
    private static StringBuilder expression = new StringBuilder();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printGrid();
            System.out.print("Move (h/j/k/l), Enter=select, x=backspace, q=quit: ");
            String input = scanner.nextLine();

            if (input.isEmpty()) { // Enter key pressed
                String selected = NUMPAD[cursorRow][cursorCol];
                if (selected.equals("=")) {
                    calculate();
                } else {
                    expression.append(selected);
                }
            } else {
                switch (input) {
                    case "h": cursorCol = Math.max(0, cursorCol - 1); break;
                    case "l": cursorCol = Math.min(NUMPAD[0].length - 1, cursorCol + 1); break;
                    case "k": cursorRow = Math.max(0, cursorRow - 1); break;
                    case "j": cursorRow = Math.min(NUMPAD.length - 1, cursorRow + 1); break;
                    case "x": backspace(); break;
                    case "q": running = false; break;
                    default:
                        // Invalid key now prints in red
                        System.out.println("\u001B[31mInvalid key: " + input + "\u001B[0m");
                        break;
                }
            }
        }
        scanner.close();
        System.out.println("Goodbye!");
    }

    private static void backspace() {
        if (expression.length() > 0) {
            expression.setLength(expression.length() - 1);
        }
    }

    private static void printGrid() {
        System.out.println("\nCurrent expression: " + expression);
        for (int r = 0; r < NUMPAD.length; r++) {
            for (int c = 0; c < NUMPAD[r].length; c++) {
                if (r == cursorRow && c == cursorCol) {
                    System.out.print("[" + NUMPAD[r][c] + "] ");
                } else {
                    System.out.print(" " + NUMPAD[r][c] + "  ");
                }
            }
            System.out.println();
        }
    }

    private static void calculate() {
        String expr = expression.toString().trim();
        if (expr.isEmpty()) {
            System.out.println("Nothing to evaluate!");
            return;
        }

        // remove trailing operators/dot
        while (expr.length() > 0 && "+-*/.".indexOf(expr.charAt(expr.length() - 1)) != -1) {
            expr = expr.substring(0, expr.length() - 1);
        }
        if (expr.isEmpty()) {
            System.out.println("Nothing to evaluate!");
            return;
        }

        try {
            List<String> rpn = toRPN(expr);
            double result = evalRPN(rpn);
            if (result == (int) result) {
                System.out.println("Result: " + (int) result);
            } else {
                System.out.println("Result: " + result);
            }
            expression.setLength(0);
        } catch (ArithmeticException ae) {
            System.out.println("\u001B[31mError: " + ae.getMessage() + "\u001B[0m"); // divide by zero
        } catch (Exception e) {
            System.out.println("\u001B[31mError in expression: " + expression + "\u001B[0m"); // other errors
        }
    }

    private static List<String> toRPN(String expr) {
        List<String> output = new ArrayList<>();
        Deque<String> ops = new ArrayDeque<>();

        int i = 0;
        while (i < expr.length()) {
            char ch = expr.charAt(i);
            if (Character.isWhitespace(ch)) { i++; continue; }

            if (ch == '.' || Character.isDigit(ch)) {
                int j = i + 1;
                while (j < expr.length() && (Character.isDigit(expr.charAt(j)) || expr.charAt(j) == '.')) j++;
                String num = expr.substring(i, j);
                if (num.chars().filter(c -> c == '.').count() > 1) throw new IllegalArgumentException("Bad number");
                output.add(num);
                i = j;
                continue;
            }

            if (isOperator(ch)) {
                String op = String.valueOf(ch);
                while (!ops.isEmpty() && isOperator(ops.peek().charAt(0))
                        && ((isLeftAssociative(op) && precedence(op) <= precedence(ops.peek()))
                            || (!isLeftAssociative(op) && precedence(op) < precedence(ops.peek())))) {
                    output.add(ops.pop());
                }
                ops.push(op);
                i++;
                continue;
            }

            if (ch == '(') { ops.push("("); i++; continue; }
            if (ch == ')') {
                while (!ops.isEmpty() && !ops.peek().equals("(")) output.add(ops.pop());
                if (ops.isEmpty()) throw new IllegalArgumentException("Mismatched parentheses");
                ops.pop();
                i++;
                continue;
            }

            throw new IllegalArgumentException("Invalid char: " + ch);
        }

        while (!ops.isEmpty()) {
            String op = ops.pop();
            if (op.equals("(") || op.equals(")")) throw new IllegalArgumentException("Mismatched parentheses");
            output.add(op);
        }
        return output;
    }

    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private static int precedence(String op) {
        switch (op) {
            case "+": case "-": return 1;
            case "*": case "/": return 2;
        }
        return 0;
    }

    private static boolean isLeftAssociative(String op) {
        return true;
    }

    private static double evalRPN(List<String> rpn) {
        Deque<Double> stack = new ArrayDeque<>();
        for (String token : rpn) {
            if (token.isEmpty()) continue;
            if (isOperator(token.charAt(0)) && token.length() == 1) {
                if (stack.size() < 2) throw new IllegalArgumentException("Bad expression");
                double b = stack.pop();
                double a = stack.pop();
                switch (token) {
                    case "+": stack.push(a + b); break;
                    case "-": stack.push(a - b); break;
                    case "*": stack.push(a * b); break;
                    case "/":
                        if (b == 0) throw new ArithmeticException("Divide by zero");
                        stack.push(a / b);
                        break;
                }
            } else {
                stack.push(Double.parseDouble(token));
            }
        }
        if (stack.size() != 1) throw new IllegalArgumentException("Bad expression");
        return stack.pop();
    }
}
