package marcos.devsoftware.skywars.utility;

import java.util.HashMap;
import java.util.Map;

public class Replacer {

    private final Map<CharSequence, CharSequence> replacers;

    public Replacer() {
        this.replacers = new HashMap<>();
    }

    public Replacer add(CharSequence key, Object value) {
        replacers.put(key, value.toString());
        return this;
    }

    public String replace(String message) {
        for (Map.Entry<CharSequence, CharSequence> entry : replacers.entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue());
        }

        return MessageUtility.format(message);
    }
}