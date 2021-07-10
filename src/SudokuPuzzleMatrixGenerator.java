import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SudokuPuzzleMatrixGenerator {

    //take a random seed
    private int[][] sampleArray = SeedSudokuMatrixFactory.retrieveSeedSudokuArrayByRandom();

    //method to generate puzzle
    public int[][] generateSudokuArray() {
        List<Integer> randomList = buildRandomList();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                for (int k = 0; k < 9; k++) {

                    if (sampleArray[i][j] == randomList.get(k)) {
                        //replacing each column of sample array with a new number but retaning the sudoku format
                        sampleArray[i][j] = randomList.get((k + 1) % 9);
                        break;
                    }
                }
            }
        }
        return sampleArray;
    }

    //method to produce a random list of 1-9
    private List<Integer> buildRandomList() {
        List<Integer> result = Arrays.asList( 1, 2, 3, 4, 5, 6, 7, 8, 9 );

        //list will be shuffled upon being called
        //to increase randomness
        Collections.shuffle(result);
        return result;
    }

}