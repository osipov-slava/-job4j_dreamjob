package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserService userService;

    private UserController userController;

    @BeforeEach
    public void initServices() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    public void whenRequestRegisterPage() {
        var view = userController.getRegistrationPage();

        assertThat(view).isEqualTo("users/register");
    }

    @Test
    public void whenPostRegisterUserThenRedirectVacanciesPage() {
        var user = new User(1, "email", "name", "password");
        var optionalUser = Optional.of(user);
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(userService.save(userArgumentCaptor.capture())).thenReturn(optionalUser);

        var model = new ConcurrentModel();
        var view = userController.register(user, model);
        var actualUser = userArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualUser).isEqualTo(user);
    }

    @Test
    public void whenPostRegisterExistedUserThenRedirectErrorPageWithMessage() {
        var expectedMessage = "Пользователь с такой почтой уже существует";
        when(userService.save(any())).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.register(new User(), model);
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    public void whenRequestLoginPage() {
        var view = userController.getLoginPage();

        assertThat(view).isEqualTo("users/login");
    }
}
