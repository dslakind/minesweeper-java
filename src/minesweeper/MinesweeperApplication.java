package minesweeper;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;



public class MinesweeperApplication {
    private List<MineField> fields;

    public MinesweeperApplication() {
        this.fields = new ArrayList<MineField>();
    }

    void run(Scanner source, PrintStream output) {
        Objects.requireNonNull(source, "Input source cannot be null");
        Objects.requireNonNull(output, "Output stream cannot be null");
        MineFieldReader reader = new MineFieldReader(source);
        fields = reader.readFields();
        outputFields(output);
    }

    private void outputFields(PrintStream output) {
        Objects.requireNonNull(output, "Output stream cannot be null");
        for (int i = 0; i < fields.size(); i++) {
            output.print(SolutionFormatter.format(fields.get(i), i + 1));

            if (i < fields.size() - 1) {
                output.println();
            }
        }
    }


    public static void main(String[] args) {
        Scanner source = new Scanner(System.in);
        
        MinesweeperApplication minesweeper = new MinesweeperApplication();
        minesweeper.run(source, System.out);

        
    }
}
