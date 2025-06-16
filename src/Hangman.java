import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class Hangman {

    private static final String GAME_STATE_WIN = "Победа!";
    private static final String GAME_STATE_LOSS = "Проигрыш...";
    private static final String GAME_STATE_NOT_FINISHED = "Игра не окончена";
    private final static String START = "1";
    private final static String QUIT = "2";
    private static final int MAX_ERRORS_COUNT = 6;
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        List<String> words = new ArrayList<>();

        while (true) {
            System.out.println("1. Новая игра");
            System.out.println("2. Выход");
            System.out.print("Выберите вариант: ");
            String input = SCANNER.nextLine();

            switch (input) {
                case START:
                    words = load(words);

                    if (words == null) {
                        return;
                    }

                    startGameRound(words);
                    break;
                case QUIT:
                    return;
                default:
                    System.out.printf("Вы должны ввести число %s или %s.", START, QUIT);
            }
        }
    }

    private static List<String> load(List<String> words) {
        if (words.isEmpty()) {
            try {
                words = readWords();
            } catch (IOException e) {
                System.out.println("Файл со словами не найден. Работа программы завершена.");
                return null;
            }
        }
        return words;
    }

    private static List<String> readWords() throws IOException {
        Scanner scanner = new Scanner(Paths.get("src/words.txt"));
        List<String> words = new ArrayList<>();

        while (scanner.hasNext()) {
            words.add(scanner.nextLine());
        }

        return words;
    }

    private static void startGameRound(List<String> words) {
        char[][] board = createBoard();
        String word = chooseRandomWord(words);
//        System.out.printf("Загаданное слово: %s%n", word); // Раскомментировать, чтобы отобразить загаданное слово
        startGameLoop(board, word);

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

    private static void startGameLoop(char[][] board, String word) {
        int errorsCount = 0;
        int guessedCount = 0;
        Set<Character> incorrectLetters = new TreeSet<>();
        StringBuilder mask = new StringBuilder("_".repeat(word.length()));

        while (true) {
            printGameState(board, errorsCount, mask, incorrectLetters);
            String gameState = createGameState(word, errorsCount, guessedCount);

            if (!Objects.equals(gameState, GAME_STATE_NOT_FINISHED)) {
                System.out.println(gameState);
                System.out.printf("Загаданное слово: %s %n", word);
                return;
            }

            char letter = inputLetter(mask, incorrectLetters);
            int countGuessedLetters = countGuessedLetters(word, letter);

            if (countGuessedLetters > 0) {
                openLetterInMask(word, mask, letter);
                guessedCount += countGuessedLetters;

            } else {
                errorsCount++;
                incorrectLetters.add(letter);
                fillBoardWithManikin(board, errorsCount);
            }
        }
    }

    private static void printGameState(char[][] board, int errorsCount, StringBuilder mask, Set<Character> incorrectLetters) {
        for (char[] chars : board) {
            System.out.print(new String(chars));
            System.out.println();
        }
        System.out.printf("Слово: [%s] %n", mask.toString());
        System.out.printf("Ошибки (%d): %s %n", errorsCount, incorrectLetters.toString());
    }

    private static String createGameState(String word, int errorsCount, int guessCount) {
        if (errorsCount >= MAX_ERRORS_COUNT) {
            return GAME_STATE_LOSS;
        }

        if (guessCount == word.length()) {
            return GAME_STATE_WIN;
        }

        return GAME_STATE_NOT_FINISHED;
    }

    private static char inputLetter(StringBuilder mask, Set<Character> incorrectLetters) {
        while (true) {
            System.out.print("Буква: ");
            String input = SCANNER.nextLine().toLowerCase().trim();

            if (!isRussianLetter(input)) {
                System.out.println("Необходимо ввести одну букву русского алфавита.");
                continue;
            }

            char letter = input.charAt(0);

            if (mask.indexOf(String.valueOf(letter)) != -1) {
                System.out.println("Вы уже вводили данную букву и отгадали её.");
            } else if (incorrectLetters.contains(letter)) {
                System.out.println("Вы уже вводили данную букву. Эта буква не содержится в слове.");
            } else {
                return letter;
            }
        }
    }

    private static boolean isRussianLetter(String input) {
        return input.matches("[а-яё]");
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

    private static void openLetterInMask(String word, StringBuilder mask, char letter) {
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == letter) {
                mask.setCharAt(i, letter);
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
}
