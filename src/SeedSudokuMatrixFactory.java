import java.util.Random;

public final class SeedSudokuMatrixFactory {

    private static final int seedSudokuArrays[][][] = {
            { 		{ 1, 2, 3, 4, 5, 6, 7, 8, 9 },
                    { 4, 5, 6, 7, 8, 9, 1, 2, 3 },
                    { 7, 8, 9, 1, 2, 3, 4, 5, 6 },
                    { 2, 1, 4, 3, 6, 5, 8, 9, 7 },
                    { 3, 6, 5, 8, 9, 7, 2, 1, 4 },
                    { 8, 9, 7, 2, 1, 4, 3, 6, 5 },
                    { 5, 3, 1, 6, 4, 2, 9, 7, 8 },
                    { 6, 4, 2, 9, 7, 8, 5, 3, 1 },
                    { 9, 7, 8, 5, 3, 1, 6, 4, 2 } },

            { 		{ 3, 9, 4, 5, 1, 7, 6, 2, 8 },
                    { 5, 1, 7, 6, 2, 8, 3, 9, 4 },
                    { 6, 2, 8, 3, 9, 4, 5, 1, 7 },
                    { 9, 3, 5, 4, 7, 1, 2, 8, 6 },
                    { 4, 7, 1, 2, 8, 6, 9, 3, 5 },
                    { 2, 8, 6, 9, 3, 5, 4, 7, 1 },
                    { 1, 4, 3, 7, 5, 9, 8, 6, 2 },
                    { 7, 5, 9, 8, 6, 2, 1, 4, 3 },
                    { 8, 6, 2, 1, 4, 3, 7, 5, 9 } },

            { 		{ 7, 6, 1, 9, 8, 4, 2, 3, 5 },
                    { 9, 8, 4, 2, 3, 5, 7, 6, 1 },
                    { 2, 3, 5, 7, 6, 1, 9, 8, 4 },
                    { 6, 7, 9, 1, 4, 8, 3, 5, 2 },
                    { 1, 4, 8, 3, 5, 2, 6, 7, 9 },
                    { 3, 5, 2, 6, 7, 9, 1, 4, 8 },
                    { 8, 1, 7, 4, 9, 6, 5, 2, 3 },
                    { 4, 9, 6, 5, 2, 3, 8, 1, 7 },
                    { 5, 2, 3, 8, 1, 7, 4, 9, 6 } },

            {		{ 7, 1, 5, 4, 3, 6, 2, 9, 8 },
                    { 4, 3, 6, 2, 9, 8, 7, 1, 5 },
                    { 2, 9, 8, 7, 1, 5, 4, 3, 6 },
                    { 1, 7, 4, 5, 6, 3, 9, 8, 2 },
                    { 5, 6, 3, 9, 8, 2, 1, 7, 4 },
                    { 9, 8, 2, 1, 7, 4, 5, 6, 3 },
                    { 3, 5, 7, 6, 4, 1, 8, 2, 9 },
                    { 6, 4, 1, 8, 2, 9, 3, 5, 7 },
                    { 8, 2, 9, 3, 5, 7, 6, 4, 1 } } };

    private SeedSudokuMatrixFactory() {
    }

    //randomly choose one of the seed
    public static int[][] retrieveSeedSudokuArrayByRandom() {
        int randomInt = new Random().nextInt(seedSudokuArrays.length);
        return seedSudokuArrays[randomInt];
    }
}