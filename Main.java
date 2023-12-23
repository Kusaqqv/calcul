import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Main {
    private JFrame frame;
    private JTextField textField;

    public Main() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Calculator");
        frame.setBounds(100, 100, 250, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        textField = new JTextField();
        textField.setHorizontalAlignment(SwingConstants.RIGHT);
        frame.getContentPane().add(textField, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 4));

        String[] buttonLabels = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", "C", "=", "+"
        };

        for (String label : buttonLabels) {
            JButton button = new JButton(label);

            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String input = textField.getText();
                    String buttonLabel = button.getText();

                    if (input.equals("Error")) {
                        textField.setText("");
                    }

                    if (buttonLabel.equals("=")) {
                        try {
                            double result = evaluateExpression(input);
                            textField.setText(String.valueOf(result));
                        } catch (ArithmeticException ex) {
                            textField.setText("Error");
                        }
                    } else if (buttonLabel.equals("C")) {
                        textField.setText("");
                    } else if (buttonLabel.equals("DEL")) {
                        if (!input.isEmpty()) {
                            textField.setText(input.substring(0, input.length() - 1));
                        }
                    } else {
                        textField.setText(input + buttonLabel);
                    }
                }
            });

            panel.add(button);
        }

        frame.getContentPane().add(panel, BorderLayout.CENTER);
    }

    private double evaluateExpression(String expression) throws ArithmeticException {
        try {
            return new Object() {
                int pos = -1, ch;

                void nextChar() {
                    ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
                }

                boolean eat(int charToEat) {
                    while (ch == ' ') nextChar();
                    if (ch == charToEat) {
                        nextChar();
                        return true;
                    }
                    return false;
                }

                double parse() {
                    nextChar();
                    double x = parseExpression();
                    if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                    return x;
                }

                double parseExpression() {
                    double x = parseTerm();
                    for (;;) {
                        if (eat('+')) x += parseTerm();
                        else if (eat('-')) x -= parseTerm();
                        else return x;
                    }
                }

                double parseTerm() {
                    double x = parseFactor();
                    for (;;) {
                        if (eat('*')) x *= parseFactor();
                        else if (eat('/')) {
                            double divisor = parseFactor();
                            if (divisor == 0) {
                                throw new ArithmeticException("Cannot divide by zero");
                            }
                            x /= divisor;
                        } else return x;
                    }
                }

                double parseFactor() {
                    if (eat('+')) return parseFactor();
                    if (eat('-')) return -parseFactor();

                    double x;
                    int startPos = this.pos;
                    if (eat('(')) {
                        x = parseExpression();
                        eat(')');
                    } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                        while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                        x = Double.parseDouble(expression.substring(startPos, this.pos));
                    } else {
                        throw new RuntimeException("Unexpected: " + (char)ch);
                    }

                    return x;
                }
            }.parse();
        } catch (RuntimeException ex) {
            throw new ArithmeticException("Invalid expression");
        }
    }

    public void show() {
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Main calculator = new Main();
        calculator.show();
    }
}
