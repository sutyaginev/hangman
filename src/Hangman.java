import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class Hangman {

    private static final String GAME_STATE_WIN = "Победа!";
    private static final String GAME_STATE_LOSS = "Проигрыш...";
    private static final String GAME_STATE_NOT_FINISHED = "Игра не окончена";
    public static final int MAX_ERRORS_COUNT = 6;
    public static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        List<String> words = new ArrayList<>();

        while (true) {
            System.out.print("\n1. Новая игра\n2. Выход\nВыберите вариант: ");
            String input = SCANNER.nextLine();
            switch (input) {
                case "1":
                    if (words.isEmpty()) {
                        words = writeWordsToList();
                    }

                    startGameRound(words);
                    break;
                case "2":
                    return;
                default:
                    System.out.println("Вы должны ввести число 1 или 2.");
            }
        }
    }

    private static void startGameRound(List<String> words) {
        char[][] board = createBoard();
        String word = chooseRandomWord(words);
//        System.out.printf("Загаданное слово: %s%n", word); // Раскомментировать, чтобы отобразить загаданное слово
        startGameLoop(board, word);

    }

    private static void startGameLoop(char[][] board, String word) {
        int errorsCount = 0;
        int guessedCount = 0;
        Set<Character> incorrectLetters = new TreeSet<>();
        StringBuilder encryptedWord = new StringBuilder("_".repeat(word.length()));

        while (true) {
            printGameState(board, errorsCount, encryptedWord, incorrectLetters);
            String gameState = checkGameState(word, errorsCount, guessedCount);

            if (!Objects.equals(gameState, GAME_STATE_NOT_FINISHED)) {
                System.out.println(gameState);
                System.out.printf("Загаданное слово: %s%n", word);
                return;
            }

            char letter = inputLetter(encryptedWord, incorrectLetters);
            int countGuessedLetters = countGuessedLetters(word, letter);

            if (countGuessedLetters > 0) {
                fillEncryptedWord(word, encryptedWord, letter);
                guessedCount += countGuessedLetters;

            } else {
                errorsCount++;
                incorrectLetters.add(letter);
                fillBoardWithManikin(board, errorsCount);
            }
        }
    }

    private static void printGameState(char[][] board, int errorsCount, StringBuilder encryptedWord, Set<Character> incorrectLetters) {
        for (char[] chars : board) {
            System.out.print(new String(chars));
            System.out.println();
        }
        System.out.printf("Слово: [%s]%n", encryptedWord.toString());
        System.out.printf("Ошибки (%d): %s%n", errorsCount, incorrectLetters.toString());
    }

    private static String checkGameState(String word, int errorsCount, int guessCount) {
        if (errorsCount >= MAX_ERRORS_COUNT) {
            return GAME_STATE_LOSS;
        }

        if (guessCount == word.length()) {
            return GAME_STATE_WIN;
        }

        return GAME_STATE_NOT_FINISHED;
    }

    private static int countGuessedLetters(String word, char letter) {
        int sum = 0;

        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == letter) {
                sum++;
            }
        }

        return sum;
    }

    private static void fillEncryptedWord(String word, StringBuilder encryptedWord, char letter) {
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == letter) {
                encryptedWord.setCharAt(i, letter);
            }
        }
    }

    private static void fillBoardWithManikin(char[][] board, int errorsCount) {
        switch (errorsCount) {
            case 1:
                board[2][3] = 'o';
                break;
            case 2:
                board[3][3] = '|';
                break;
            case 3:
                board[3][2] = '/';
                break;
            case 4:
                board[3][4] = '\\';
                break;
            case 5:
                board[4][2] = '/';
                break;
            case 6:
                board[4][4] = '\\';
                break;
        }
    }

    private static char inputLetter(StringBuilder encryptedWord, Set<Character> incorrectLetters) {
        while (true) {
            System.out.print("Буква: ");
            String inputString = SCANNER.nextLine().toLowerCase().trim();

            if (!inputString.matches("[а-яё]")) {
                System.out.println("Необходимо ввести одну букву русского алфавита.");
                continue;
            }

            char letter = inputString.charAt(0);

            if (encryptedWord.indexOf(String.valueOf(letter)) != -1) {
                System.out.println("Вы уже вводили данную букву и отгадали её.");
            } else if (incorrectLetters.contains(letter)) {
                System.out.println("Вы уже вводили данную букву. Эта буква не содержится в слове.");
            } else {
                return letter;
            }
        }
    }

    private static char[][] createBoard() {
        return new char[][]{
                "---- ".toCharArray(),
                "|  | ".toCharArray(),
                "|    ".toCharArray(),
                "|    ".toCharArray(),
                "|    ".toCharArray(),
                "|    ".toCharArray()
        };
    }

    private static String chooseRandomWord(List<String> words) {
        int randomWordNumber = new Random().nextInt(words.size());
        return words.get(randomWordNumber);
    }

    private static List<String> writeWordsToList() {
        List<String> words = new ArrayList<>();

        try (Scanner scanner = new Scanner(Paths.get("src/words.txt"))) {
            while (scanner.hasNext()) {
                words.add(scanner.nextLine());
            }
        } catch (IOException e) {
            System.out.println("File not found" + e.getMessage());
        }

        return words;
    }
}
