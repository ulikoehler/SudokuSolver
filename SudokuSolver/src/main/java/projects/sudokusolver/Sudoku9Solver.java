package projects.sudokusolver;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.InputStream;
import java.util.List;
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
        //Read the sudoku
        Sudoku9 sudoku = new Sudoku9(in);
        while (!sudoku.isSolved()) {
            calculatePossibilities(sudoku);
            findSinglePossibilities(sudoku);
        }
        //Print the final solution
        System.out.println(sudoku.toString());
        System.out.println(sudoku.isSolved());
    }
    
    private List<Sudoku9> splitSudokuTree() {
        List<Sudoku9> solutions = Lists.newLinkedList();
        //Identify the field with the lowest number of possible solutions
//        int bestColumn
        return solutions;
    }

    /**
     * Iteratetively discover fields with only one possibility left and
     * therefore solve easy sudokus
     */
    private static boolean findSinglePossibilities(Sudoku9 sudoku) {
        boolean foundPossibility = false;
        mainloop:
        while (true) {
            for (int i = 0; i < sudoku.getNumRows(); i++) {
                SudokuField[] row = sudoku.getRow(i);
                for (int j = 0; j < row.length; j++) {
                    SudokuField field = row[j];
                    if (field.getContent() == -1 && field.getNumPossibilities()
                            == 1) {
//                        System.out.println("Found single possibility at index ("
//                                + (i + 1) + ", " + (j + 1) + "): "
//                                + (sudoku.getAlphabet().charAt(field.getSinglePossibility())));
                        field.setContent(field.getSinglePossibility());
                        calculatePossibilities(sudoku);
                        foundPossibility = true;
                        continue mainloop;
                    }
                }
            }
            //No single possibility found, so stop the iteration
            break mainloop;
        }
        return foundPossibility;
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
