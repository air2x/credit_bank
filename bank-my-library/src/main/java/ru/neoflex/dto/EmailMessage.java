package ru.neoflex.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.neoflex.enums.MessageTheme;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmailMessage {
    private String address;
    private MessageTheme theme;
    private UUID statementId;
    private String text;
}
