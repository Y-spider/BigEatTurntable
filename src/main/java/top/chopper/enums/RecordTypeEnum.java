package top.chopper.enums;

public enum RecordTypeEnum {
    IN("in", "收入"),
    OUT("out", "支出");

    private final String code;
    private final String text;

    RecordTypeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() { return code; }
    public String getText() { return text; }

    public static String codeOfText(String text) {
        if (text == null) return null;
        for (RecordTypeEnum t : values()) {
            if (t.text.equals(text)) return t.code;
        }
        return text; // 回退原值
    }

    public static String textOfCode(String code) {
        if (code == null) return null;
        for (RecordTypeEnum t : values()) {
            if (t.code.equals(code)) return t.text;
        }
        return code; // 回退原值
    }
}
