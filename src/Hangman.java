import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class Hangman {

    private static final String GAME_STATE_WIN = "Победа!";
    private static final String GAME_STATE_LOSS = "Проигрыш...";
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
                    System.out.printf("Вы должны ввести число %s или %s. \n", START, QUIT);
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
        String word = chooseRandomWord(words);
//        System.out.printf("Загаданное слово: %s \n", word); // Раскомментировать, чтобы отобразить загаданное слово
        startGameLoop(word);

    }

    private static String chooseRandomWord(List<String> words) {
        int randomWordNumber = new Random().nextInt(words.size());
        return words.get(randomWordNumber);
    }

    private static void startGameLoop(String word) {
        int errorsCount = 0;
        int guessedCount = 0;
        Set<Character> incorrectLetters = new TreeSet<>();
        StringBuilder mask = new StringBuilder("_".repeat(word.length()));

        while (true) {
            printGameState(errorsCount, mask, incorrectLetters);

            if (isWin(word, guessedCount)) {
                System.out.println(GAME_STATE_WIN + "\n");
                return;
            }

            if (isLose(errorsCount)) {
                System.out.println(GAME_STATE_LOSS);
                System.out.printf("Загаданное слово: %s \n \n", word);
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
            }
        }
    }

    private static void printGameState(int errorsCount, StringBuilder mask, Set<Character> incorrectLetters) {
        System.out.print(Board.getPicture(errorsCount));
        System.out.printf("Слово: [%s] \n", mask.toString());
        System.out.printf("Ошибки (%d): %s \n \n", errorsCount, incorrectLetters.toString());
    }

    private static boolean isWin(String word, int guessCount) {
        return guessCount == word.length();
    }

    private static boolean isLose(int errorsCount) {
        return errorsCount >= MAX_ERRORS_COUNT;
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
}
