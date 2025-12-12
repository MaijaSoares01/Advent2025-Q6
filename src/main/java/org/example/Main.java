package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        String inputFile = "src/main/resources/MathHW.txt";
        ArrayList<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

            int R = lines.size();
            if (R == 0) {
                System.out.println("Empty input");
                return;
            }
            int C = lines.get(0).length();

            // Convert input into 2D char array (grid)
            char[][] G = new char[R][C];
            for (int i = 0; i < R; i++) {
                String row = lines.get(i);
                // Pad with spaces if row shorter than C
                if (row.length() < C) {
                    row = String.format("%-" + C + "s", row);
                }
                G[i] = row.toCharArray();
            }

            long part1 = 0;
            long part2 = 0;
            int startCol = 0;

            // Iterate columns + 1 for the last problem trigger
            for (int cc = 0; cc <= C; cc++) {
                boolean isBlank = true;
                if (cc < C) {
                    for (int r = 0; r < R; r++) {
                        if (G[r][cc] != ' ') {
                            isBlank = false;
                            break;
                        }
                    }
                }

                if (isBlank) {
                    // Solve problem from startCol to cc-1

                    // Operator is at bottom row, startCol column
                    char op = G[R - 1][startCol];
                    if (op != '+' && op != '*') {
                        System.err.println("Unknown operator: " + op);
                        return;
                    }

                    // Part 1: read numbers top->down left->right
                    long p1Score = (op == '+') ? 0 : 1;
                    for (int r = 0; r < R - 1; r++) {
                        long p1Num = 0;
                        for (int c = startCol; c < cc; c++) {
                            if (G[r][c] != ' ') {
                                p1Num = p1Num * 10 + (G[r][c] - '0');
                            }
                        }
                        if (op == '+') {
                            p1Score += p1Num;
                        } else {
                            p1Score *= p1Num;
                        }
                    }
                    part1 += p1Score;

                    // Part 2: read numbers right->left, top->down
                    long p2Score = (op == '+') ? 0 : 1;
                    for (int c = cc - 1; c >= startCol; c--) {
                        long n = 0;
                        for (int r = 0; r < R - 1; r++) {
                            if (G[r][c] != ' ') {
                                n = n * 10 + (G[r][c] - '0');
                            }
                        }
                        if (op == '+') {
                            p2Score += n;
                        } else {
                            p2Score *= n;
                        }
                    }
                    part2 += p2Score;

                    // Update start of next problem (skip separator)
                    startCol = cc + 1;
                }
            }

            System.out.println("Grand total (normal math)     = " + part1);
            System.out.println("Grand total (cephalopod math) = " + part2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
