package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;


import java.time.LocalDate;

@NoArgsConstructor
@Data
public class User {
    private int id;
    @Email(message = "Некорректный email")
    @NotBlank(message = "Email обязателен")
    private String email;
    @NotBlank(message = "Логин обязателен")
    @Pattern(regexp = "^\\S+$", message = "Логин не должен содержать пробелов")
    private String login;
    @Nullable
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;


    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }


}
