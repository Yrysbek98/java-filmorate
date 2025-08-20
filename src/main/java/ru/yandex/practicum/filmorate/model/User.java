package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.Nullable;


import java.time.LocalDate;
@Data
@AllArgsConstructor
public class User {
    private int id;
    @Email
    private String email;
    @NotBlank
    private String login;
    @Nullable
    private String name;
    @Past
    private LocalDate birthday;
}
