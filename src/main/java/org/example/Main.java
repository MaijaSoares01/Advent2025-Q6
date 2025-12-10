package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/MathHW.txt"))) {

            // Part 1: Normal math variables
            String[] num1 = null;
            String[] num2 = null;
            String[] num3 = null;
            String[] num4 = null;
            String[] ops = null;
            long grandTotalNormal = 0;

            // Read exactly 5 lines for part 1 (normal math)
            num1 = splitAndClean(reader.readLine()).toArray(new String[0]);
            num2 = splitAndClean(reader.readLine()).toArray(new String[0]);
            num3 = splitAndClean(reader.readLine()).toArray(new String[0]);
            num4 = splitAndClean(reader.readLine()).toArray(new String[0]);
            ops = splitAndClean(reader.readLine()).toArray(new String[0]);

            // Compute normal math grand total
            if (!(num2.length == num1.length && num3.length == num1.length && num4.length == num1.length && ops.length == num1.length)) {
                System.out.println("Mismatch in problem counts in normal math input");
            } else {
                for (int i = 0; i < num1.length; i++) {
                    long one = Long.parseLong(num1[i]);
                    long two = Long.parseLong(num2[i]);
                    long three = Long.parseLong(num3[i]);
                    long four = Long.parseLong(num4[i]);
                    String op = ops[i];
                    if (op.equals("+")) {
                        grandTotalNormal += (one + two + three + four);
                    } else if (op.equals("*")) {
                        grandTotalNormal += (one * two * three * four);
                    } else {
                        System.out.println("Unknown operator in normal math part: " + op);
                    }
                }
            }

            // Part 2: Cephalopod math
            try (BufferedReader reader2 = new BufferedReader(new FileReader("src/main/resources/MathHW.txt"))) {
                // Read exactly 5 lines again for part 2
                String line1 = reader2.readLine();
                String line2 = reader2.readLine();
                String line3 = reader2.readLine();
                String line4 = reader2.readLine();
                String opLine = reader2.readLine();

                if (line1 == null || line2 == null || line3 == null || line4 == null || opLine == null) {
                    System.out.println("Not enough lines for cephalopod math");
                    return;
                }

                int maxLen = Math.max(
                        Math.max(line1.length(), line2.length()),
                        Math.max(Math.max(line3.length(), line4.length()), opLine.length())
                );

                line1 = padRight(line1, maxLen);
                line2 = padRight(line2, maxLen);
                line3 = padRight(line3, maxLen);
                line4 = padRight(line4, maxLen);
                opLine = padRight(opLine, maxLen);

                long grandTotalCeph = 0;
                int blockCount = 0;
                int col = 0;

                while (col < maxLen) {
                    // Skip empty columns (problem separators)
                    while (col < maxLen && isEmptyColumn(line1, line2, line3, line4, opLine, col)) {
                        col++;
                    }
                    if (col >= maxLen) break;

                    int startCol = col;
                    while (col < maxLen && !isEmptyColumn(line1, line2, line3, line4, opLine, col)) {
                        col++;
                    }
                    int endCol = col - 1;

                    blockCount++;

                    // Build 4 numbers from digits in this block (right to left, digits stacked top to bottom)
                    String[] numberStrs = new String[4];
                    for (int i = 0; i < 4; i++) numberStrs[i] = "";

                    // Read columns from right to left
                    for (int c = endCol; c >= startCol; c--) {
                        for (int row = 0; row < 4; row++) {
                            char ch = charAtSafe(row, c, line1, line2, line3, line4);
                            if (ch != ' ') {
                                numberStrs[row] += ch;
                            }
                        }
                    }
                    for (int i = 0; i < 4; i++) {
                        if (numberStrs[i].isEmpty()) numberStrs[i] = "0";
                    }

                    long[] numbers = new long[4];
                    try {
                        for (int i = 0; i < 4; i++) {
                            numbers[i] = Long.parseLong(numberStrs[i]);
                        }
                    } catch (NumberFormatException e) {
                        System.out.printf("Failed to parse numbers in block #%d at cols %d-%d%n", blockCount, startCol, endCol);
                        continue;
                    }

                    // Operator is the last non-space character in the bottom row (opLine) in the block
                    char op = opLine.charAt(endCol);
                    if (op == ' ') {
                        for (int c = endCol; c >= startCol; c--) {
                            if (opLine.charAt(c) != ' ') {
                                op = opLine.charAt(c);
                                break;
                            }
                        }
                    }

                    long result;
                    if (op == '+') {
                        result = 0;
                        for (long num : numbers) result += num;
                    } else if (op == '*') {
                        result = 1;
                        for (long num : numbers) result *= num;
                    } else {
                        System.out.printf("Unknown operator '%c' in block #%d%n", op, blockCount);
                        continue;
                    }

                    grandTotalCeph += result;
                }

                System.out.println("Grand total (normal math) = " + grandTotalNormal);
                System.out.println("Grand total (cephalopod math) = " + grandTotalCeph);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> splitAndClean(String line) {
        return Arrays.stream(line.trim().split("\\s+"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private static boolean isEmptyColumn(String line1, String line2, String line3, String line4, String opLine, int col) {
        return isSpaceAt(line1, col) &&
                isSpaceAt(line2, col) &&
                isSpaceAt(line3, col) &&
                isSpaceAt(line4, col) &&
                isSpaceAt(opLine, col);
    }

    private static boolean isSpaceAt(String line, int col) {
        return col >= line.length() || line.charAt(col) == ' ';
    }

    private static char charAtSafe(int row, int col, String l1, String l2, String l3, String l4) {
        switch (row) {
            case 0: return col < l1.length() ? l1.charAt(col) : ' ';
            case 1: return col < l2.length() ? l2.charAt(col) : ' ';
            case 2: return col < l3.length() ? l3.charAt(col) : ' ';
            case 3: return col < l4.length() ? l4.charAt(col) : ' ';
            default: return ' ';
        }
    }

    private static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
}
