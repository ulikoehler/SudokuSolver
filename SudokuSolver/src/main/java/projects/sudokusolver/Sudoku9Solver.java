package projects.sudokusolver;

import com.google.common.collect.Sets;
import java.io.InputStream;
import java.util.Set;
import projects.sudokusolver.Sudoku9.SudokuField;

/**
 *
 * @author uli
 */
public class Sudoku9Solver {

    public static void main(String[] args) throws Exception {
        InputStream in =
                Sudoku9Solver.class.getResourceAsStream("/wikipedia.sud");
        Sudoku9 sudoku = new Sudoku9(in);
        calculatePossibilities(sudoku);
        findSinglePossibilities(sudoku);
        System.out.println(sudoku.toString());
    }

    /**
     * Iteratetively discover fields with only one possibility left and
     * therefore solve easy sudokus
     */
    private static void findSinglePossibilities(Sudoku9 sudoku) {
        mainloop:
        while (true) {
            for (int i = 0; i < sudoku.getNumRows(); i++) {
                SudokuField[] row = sudoku.getRow(i);
                for (int j = 0; j < row.length; j++) {
                    SudokuField field = row[j];
                    if (field.getContent() == -1 && field.getNumPossibilities()
                            == 1) {
                        System.out.println("Found single possibility at index ("
                                + (i + 1) + ", " + (j + 1) + "): "
                                + (sudoku.getAlphabet().charAt(field.getSinglePossibility())));
                        field.setContent(field.getSinglePossibility());
                        calculatePossibilities(sudoku);
                        continue mainloop;
                    }
                }
            }
            //No single possibility found, so stop the iteration
            break mainloop;
        }
    }

    private static void calculatePossibilities(Sudoku9 sudoku) {
        //Rows
        for (int i = 0; i < sudoku.getNumRows(); i++) {
            SudokuField[] row = sudoku.getRow(i);
//            System.out.println("Row " + (i + 1));
            updatePossibilities(row);
        }
        //Column
        for (int i = 0; i < sudoku.getNumCols(); i++) {
            SudokuField[] col = sudoku.getColumn(i);
//            System.out.println("Col " + (i + 1));
            updatePossibilities(col);
        }
        //Subfield
        for (int i = 0; i < sudoku.getNumFields(); i++) {
            SudokuField[] subfield = sudoku.getSubfield(i);
//            System.out.println("Subfield " + (i + 1));
            updatePossibilities(subfield);
        }
    }

    /**
     * For a specific set of correlated fields, removes possibilities of numbers
     * that already occur in the correlated field
     * @param fields 
     */
    private static void updatePossibilities(SudokuField[] fields) {
        Set<Integer> setInRow = Sets.newTreeSet(); //All numbers (indices in alphabet) which are contained
        //Find all numbers being set in the row
        for (SudokuField field : fields) {
            if (field.getContent() != -1) {
                setInRow.add(field.getContent());
            }
        }
        //Remove the possibility of the found numbers from all fields
        for (int i = 0; i < fields.length; i++) {
            SudokuField field = fields[i];
            for (int possibilityToBeRemoved : setInRow) {
                if (field.getContent() == -1) { //No need to set possibilities for fields already having content
                    field.setPossibility(possibilityToBeRemoved, false);
//                    System.out.println("    Removing possibility for " + (possibilityToBeRemoved + 1) + " at index " + (i + 1));
                }
            }
        }
    }
}
