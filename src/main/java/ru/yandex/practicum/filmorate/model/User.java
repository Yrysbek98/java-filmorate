package ru.yandex.practicum.filmorate.model;



import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
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
    @JsonIgnore
    private Set<Integer> friends = new HashSet<>();


    public User(int id, String email, String login, String name, LocalDate birthday) {
    }
}
