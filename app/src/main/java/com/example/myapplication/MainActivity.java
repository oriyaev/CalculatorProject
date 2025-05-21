package com.example.myapplication;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    TextView textViewLiveResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textMathCalc);
        textViewLiveResult = findViewById(R.id.textViewLiveResult);
    }

    public void onDigitClick(View view) {
        Button button = (Button) view;
        String digit = button.getText().toString();
        String current = textView.getText().toString();

        // If the user presses ".", check if the current number already has one
        if (digit.equals(".")) {
            // Split the current input by operators to isolate the last number
            String[] parts = current.split("[+\\-*/]");
            String lastNumber = parts.length > 0 ? parts[parts.length - 1] : "";

            if (lastNumber.contains(".")) {
                return; // Block input if the last number already has a decimal
            }

            // If the lastNumber is empty (e.g. user presses "." after operator), start with "0."
            if (lastNumber.isEmpty()) {
                digit = "0.";
            }
        }

        textView.setText(current + digit);
        updateLiveResult();
    }

    public void onOperatorClick(View view) {
        Button button = (Button) view;
        String operator = button.getText().toString();
        String current = textView.getText().toString();

        if (current.endsWith("+") || current.endsWith("-") ||
                current.endsWith("*") || current.endsWith("/")) {
            current = current.substring(0, current.length() - 1);
        }

        textView.setText(current + operator);
        updateLiveResult();
    }

    private void updateLiveResult() {
        String expression = textView.getText().toString();

        try {
            double liveResult = eval(expression);
            textViewLiveResult.setText("= " + liveResult);
        } catch (Exception e) {
            textViewLiveResult.setText("");
        }
    }

    public void onEqualsClick(View view) {
        String expression = textViewLiveResult.getText().toString();

        try {
            double result = eval(expression);
            textViewLiveResult.setText(String.valueOf(result));
        } catch (Exception e) {
            textViewLiveResult.setText("Error");
        }
    }

    public void onBackspaceClick(View view) {
        String current = textView.getText().toString();
        if (!current.isEmpty()) {
            textView.setText(current.substring(0, current.length() - 1));
        }
        updateLiveResult();
    }

    public void onClearClick(View view) {
        textView.setText("");
        textViewLiveResult.setText("");
    }

    private double eval(String expression) {
        if (expression.isEmpty()) return 0;
        expression = expression.replaceAll("[^0-9+\\-*/.]", "");

        ArrayList<String> tokens = new ArrayList<>();
        StringBuilder number = new StringBuilder();

        for (char c : expression.toCharArray()) {
            if (Character.isDigit(c) || c == '.') {
                number.append(c);
            } else {
                tokens.add(number.toString());
                tokens.add(String.valueOf(c));
                number.setLength(0);
            }
        }
        tokens.add(number.toString());

        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).equals("*") || tokens.get(i).equals("/")) {
                double left = Double.parseDouble(tokens.get(i - 1));
                double right = Double.parseDouble(tokens.get(i + 1));
                double result = tokens.get(i).equals("*") ? left * right : (right != 0 ? left / right : 0);

                tokens.set(i - 1, String.valueOf(result));
                tokens.remove(i); // Remove operator
                tokens.remove(i); // Remove right operand
                i--; // Stay in same position
            }
        }

        double result = Double.parseDouble(tokens.get(0));
        for (int i = 1; i < tokens.size(); i += 2) {
            String op = tokens.get(i);
            double num = Double.parseDouble(tokens.get(i + 1));

            if (op.equals("+")) result += num;
            if (op.equals("-")) result -= num;
        }

        return result;
    }
}