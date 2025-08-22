package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;


import java.time.LocalDate;

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
    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthday;

    @JsonIgnore
    public String getDisplayName() {
        return (name == null || name.isBlank()) ? login : name;
    }
}
