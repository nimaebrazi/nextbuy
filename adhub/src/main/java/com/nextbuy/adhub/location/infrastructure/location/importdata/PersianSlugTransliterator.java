package com.nextbuy.adhub.location.infrastructure.location.importdata;

import com.ibm.icu.text.Transliterator;
import com.nextbuy.adhub.location.domain.SlugValidator;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PersianSlugTransliterator {

    private static final Pattern PARENTHESES = Pattern.compile("^(.*?)\\s*\\(([^)]+)\\)\\s*$");

    private static final Map<String, String> KNOWN_WORDS = Map.ofEntries(
            Map.entry("ایران", "iran"),
            Map.entry("تهران", "tehran"),
            Map.entry("اصفهان", "isfahan"),
            Map.entry("شیراز", "shiraz"),
            Map.entry("تبریز", "tabriz"),
            Map.entry("مشهد", "mashhad"),
            Map.entry("آذربایجان", "azarbaijan"),
            Map.entry("شرقی", "sharghi"),
            Map.entry("غربی", "gharbi"),
            Map.entry("ایلام", "ilam"),
            Map.entry("کردستان", "kurdistan"),
            Map.entry("سیروان", "sirvan"),
            Map.entry("اردبیل", "ardabil"),
            Map.entry("بوشهر", "bushehr"),
            Map.entry("یزد", "yazd"),
            Map.entry("کرمان", "kerman"),
            Map.entry("گیلان", "gilan"),
            Map.entry("مازندران", "mazandaran"),
            Map.entry("هرمزگان", "hormozgan"),
            Map.entry("قزوین", "qazvin"),
            Map.entry("قم", "qom"),
            Map.entry("البرز", "alborz"),
            Map.entry("چهارمحال", "chaharmahal"),
            Map.entry("بختیاری", "bakhtiari"),
            Map.entry("خراسان", "khorasan"),
            Map.entry("جنوبی", "jonoubi"),
            Map.entry("شمالی", "shomali"),
            Map.entry("رضوی", "razavi"),
            Map.entry("سمنان", "semnan"),
            Map.entry("زنجان", "zanjan"),
            Map.entry("لرستان", "lorestan"),
            Map.entry("مرکزی", "markazi"),
            Map.entry("همدان", "hamedan"),
            Map.entry("چهاردانگه", "chahardangeh"),
            Map.entry("بابک", "babak"),
            Map.entry("شهر", "shahr")
    );

    private final Transliterator arabicToLatin = Transliterator.getInstance("Arabic-Latin");

    public String transliterate(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required for transliteration");
        }

        String trimmed = normalizePersian(name.trim());
        Matcher matcher = PARENTHESES.matcher(trimmed);
        if (matcher.matches()) {
            String base = slugifySegment(matcher.group(1));
            String suffix = slugifySegment(matcher.group(2));
            return SlugValidator.validateAndNormalize(base + "-" + suffix);
        }

        return SlugValidator.validateAndNormalize(slugifySegment(trimmed));
    }

    private String slugifySegment(String text) {
        String[] words = text.split("[\\s\\-–—]+");
        StringBuilder slug = new StringBuilder();

        for (String word : words) {
            if (word.isBlank()) {
                continue;
            }
            if (!slug.isEmpty()) {
                slug.append('-');
            }
            slug.append(transliterateWord(word));
        }

        return stripEdges(slug.toString());
    }

    private String transliterateWord(String word) {
        String known = KNOWN_WORDS.get(word);
        if (known != null) {
            return known;
        }

        String latin = arabicToLatin.transliterate(word);
        latin = Normalizer.normalize(latin, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        latin = latin.toLowerCase(Locale.ROOT);

        StringBuilder builder = new StringBuilder();
        boolean lastHyphen = false;
        for (int index = 0; index < latin.length(); index++) {
            char character = latin.charAt(index);
            if ((character >= 'a' && character <= 'z') || (character >= '0' && character <= '9')) {
                builder.append(character);
                lastHyphen = false;
            } else if (!lastHyphen && !builder.isEmpty()) {
                builder.append('-');
                lastHyphen = true;
            }
        }

        String result = stripEdges(builder.toString());
        if (result.isBlank()) {
            throw new IllegalArgumentException("Could not transliterate word: " + word);
        }
        return result;
    }

    private static String normalizePersian(String text) {
        return text
                .replace('ي', 'ی')
                .replace('ك', 'ک')
                .replace('ة', 'ه')
                .replace('ؤ', 'و')
                .replace('إ', 'ا')
                .replace('أ', 'ا')
                .replace('ٱ', 'ا')
                .replace("‌", " ");
    }

    private static String stripEdges(String slug) {
        int start = 0;
        int end = slug.length();
        while (start < end && slug.charAt(start) == '-') {
            start++;
        }
        while (end > start && slug.charAt(end - 1) == '-') {
            end--;
        }
        return slug.substring(start, end);
    }
}
