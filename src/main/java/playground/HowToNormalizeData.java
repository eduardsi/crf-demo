package playground;

import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.vdurmont.emoji.EmojiParser.removeAllEmojis;
import static org.apache.commons.lang3.StringUtils.stripAccents;
import static org.apache.commons.text.WordUtils.capitalizeFully;

public class HowToNormalizeData {

    public static void main(String[] args) {
        var fullName = "\uD83D\uDE80 sören ilić \uD83D\uDE04";

        System.out.println(normalizeWithNesting(fullName));
        System.out.println(normalizeSimplyVariables(fullName));
        System.out.println(normalizeWithComposition(fullName));
        System.out.println(normalizeWithStream(fullName));
        System.out.println(normalizeWithOptional(fullName));
        System.out.println(normalizeWithLambdas(fullName));
    }

    private static String normalizeWithNesting(String fullName) {
        return removeAllEmojis((capitalizeFully(stripAccents(fullName))));
    }

    private static String normalizeSimplyVariables(String fullName) {
        var fullNameNoAccents = stripAccents(fullName);
        var fullNameCapitalized = capitalizeFully(fullNameNoAccents);
        var fullNameNoEmojis = removeAllEmojis(fullNameCapitalized);
        return fullNameNoEmojis;
    }


    private static String normalizeWithComposition(String fullName) {
        Function<String, String> stripAccents = StringUtils::stripAccents;
        Function<String, String> capitalize = WordUtils::capitalizeFully;
        Function<String, String> removeEmojis = EmojiParser::removeAllEmojis;
        return stripAccents
                .andThen(capitalize)
                .andThen(removeEmojis)
                .apply(fullName);
    }

    private static String normalizeWithStream(String fullName) {
        return Stream.of(fullName)
                .map(StringUtils::stripAccents)
                .map(WordUtils::capitalizeFully)
                .map(EmojiParser::removeAllEmojis)
                .findFirst()
                .get();
    }

    private static String normalizeWithOptional(String fullName) {
        return Optional.of(fullName)
                .map(StringUtils::stripAccents)
                .map(WordUtils::capitalizeFully)
                .map(EmojiParser::removeAllEmojis)
                .orElseThrow();
    }

    private static String normalizeWithLambdas(String fullName) {
        return Stream.of(fullName)
                .map(it -> stripAccents(it))
                .map(it -> capitalizeFully(it))
                .map(it -> removeAllEmojis(it))
                .findFirst()
                .get();
    }

}
