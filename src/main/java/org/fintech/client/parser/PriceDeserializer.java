package org.fintech.client.parser;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testcontainers.shaded.com.google.common.primitives.Doubles.min;
@Slf4j

public class PriceDeserializer extends JsonDeserializer<Integer>{
    private final List<String> regexes = List.of(
            "\\b(?:от)?\\s*((?:\\d{1,3}\\s*)*){1}\\s*(?:руб(лей)?)?\\s*(?:до|.)\\s*((\\d{1,3}\\s*)*){1}\\s*руб(?:лей)?\\b",
            "\\b(?:от)?\\s*((?:\\d{1,3}\\s*)*){1}\\s*руб(?:лей)?\\s*(?:до|.)\\s*((\\d{1,3}\\s*)*){1}\\s*(?:руб(лей)?)?\\b",
            "\\b((\\d{1,3}\\s*)*){1}\\s*руб(?:лей)?\\b"
    );
    @Override
    public Integer deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        if(jsonParser.getText() == null || jsonParser.getText().isEmpty()){
            return null;
        }

        for(String regex : regexes) {
            Pattern pattern = Pattern
                    .compile(regex,
                            Pattern.UNICODE_CHARACTER_CLASS | Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(jsonParser.getText());
            Integer ans = null;
            while (matcher.find()) {
                String number = matcher.group(1).replace(" ", "");
                try {
                    if (ans == null)
                        ans = Integer.parseInt(number);
                    else
                        ans = (int) min(ans, Integer.parseInt(number));
                } catch (NumberFormatException ex) {
                }

            }
            if(ans != null) {
                return ans;
            }
        }
        return null;

    }
}
