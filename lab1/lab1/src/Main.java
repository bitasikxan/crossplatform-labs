import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введіть повний шлях до текстового файлу (наприклад, C:\\test.txt, БЕЗ ЛАПОК!!!):");
        String filePath = scanner.nextLine();
        boolean isValid;
        do {
            isValid = true;
            try {
                String text = Files.readString(Path.of(filePath));
                analyzeText(text);
            } catch (Exception e) {
                System.out.println("Помилка читання файлу. Перевірте шлях: " + e.getMessage());
                filePath = scanner.nextLine();
                isValid = false;
            }
        } while (!isValid);
    }

    public static void analyzeText(String text) {
        if (text == null || text.trim().isEmpty()) {
            System.out.println("Файл порожній або містить лише пробіли.");
            return;
        }

        // 1. К-сть знаків пунктуації
        int punctuationCount = 0;
        String punctuationMarks = ".,!?:;-\"'()[]{}";
        for (char c : text.toCharArray()) {
            if (punctuationMarks.indexOf(c) != -1) {
                punctuationCount++;
            }
        }

        // 2. К-сть речень
        String[] sentences = text.split("[.!?]+");
        int sentenceCount = sentences.length;

        // 3. К-сть слів
        String lowerText = text.toLowerCase();
        List<String> words = new ArrayList<>();
        Pattern wordPattern = Pattern.compile("[a-zа-яієїґ0-9]+(['-][a-zа-яієїґ0-9]+)*");
        Matcher matcher = wordPattern.matcher(lowerText);

        while (matcher.find()) {
            words.add(matcher.group());
        }
        int totalWords = words.size();

        // 4. Частота слів
        Map<String, Integer> wordFrequency = new HashMap<>();
        int totalWordLength = 0;

        for (String word : words) {
            wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
            totalWordLength += word.length();
        }

        int uniqueWordsCount = wordFrequency.size();

        // 5. Середні значення
        double avgWordLength;
        if (totalWords == 0) avgWordLength = 0;
        else avgWordLength = (double) totalWordLength / totalWords;

        double avgSentenceLength;
        if (sentenceCount == 0) avgSentenceLength = 0;
        else avgSentenceLength = (double) totalWords / sentenceCount;

        // 6. Сортування для пошуку топ-10 слів
        List<Map.Entry<String, Integer>> sortedWords = new ArrayList<>(wordFrequency.entrySet());
        sortedWords.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // Вивід результатів
        System.out.println("\n--- Результати аналізу ---");
        System.out.println("Кількість усіх слів у тексті: " + totalWords);
        System.out.println("Кількість оригінальних слів: " + uniqueWordsCount);
        System.out.println("Кількість речень: " + sentenceCount);
        System.out.println("Кількість знаків пунктуації: " + punctuationCount);
        System.out.printf("Середня довжина слова: %.2f символів\n", avgWordLength);
        System.out.printf("Середня довжина речення: %.2f слів\n", avgSentenceLength);

        System.out.println("Перші 10 слів, які зустрічаються найчастіше:");
        for (int i = 0; i < Math.min(10, sortedWords.size()); i++) {
            System.out.println((i + 1) + ". '" + sortedWords.get(i).getKey() + "' (" + sortedWords.get(i).getValue() + " разів)");
        }
    }
}