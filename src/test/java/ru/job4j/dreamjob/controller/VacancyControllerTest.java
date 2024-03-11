package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.VacancyService;

import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class VacancyControllerTest {

    private VacancyService vacancyService;

    private CityService cityService;

    private VacancyController vacancyController;

    private MultipartFile testFile;

    @BeforeEach
    public void initServices() {
        vacancyService = mock(VacancyService.class);
        cityService = mock(CityService.class);
        vacancyController = new VacancyController(vacancyService, cityService);
        testFile = new MockMultipartFile("testFile.img", new byte[]{1, 2, 3});
    }

    @Test
    public void whenRequestVacancyListPageThenGetPageWithVacancies() {
        var vacancy1 = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var vacancy2 = new Vacancy(2, "test2", "desc2", now(), false, 3, 4);
        var expectedVacancies = List.of(vacancy1, vacancy2);
        when(vacancyService.findAll()).thenReturn(expectedVacancies);

        var model = new ConcurrentModel();
        var view = vacancyController.getAll(model);
        var actualVacancies = model.getAttribute("vacancies");

        assertThat(view).isEqualTo("vacancies/list");
        assertThat(actualVacancies).isEqualTo(expectedVacancies);
    }

    @Test
    public void whenRequestVacancyCreationPageThenGetPageWithCities() {
        var city1 = new City(1, "Москва");
        var city2 = new City(2, "Санкт-Петербург");
        var expectedCities = List.of(city1, city2);
        when(cityService.findAll()).thenReturn(expectedCities);

        var model = new ConcurrentModel();
        var view = vacancyController.getCreationPage(model);
        var actualVacancies = model.getAttribute("cities");

        assertThat(view).isEqualTo("vacancies/create");
        assertThat(actualVacancies).isEqualTo(expectedCities);
    }

    @Test
    public void whenPostVacancyWithFileThenSameDataAndRedirectToVacanciesPage() throws Exception {
        var vacancy = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var vacancyArgumentCaptor = ArgumentCaptor.forClass(Vacancy.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(vacancyService.save(vacancyArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(vacancy);

        var model = new ConcurrentModel();
        var view = vacancyController.create(vacancy, testFile, model);
        var actualVacancy = vacancyArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualVacancy).isEqualTo(vacancy);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
    }

    @Test
    public void whenSomeExceptionThrownThenGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Failed to write file");
        when(vacancyService.save(any(), any())).thenThrow(expectedException);

        var model = new ConcurrentModel();
        var view = vacancyController.create(new Vacancy(), testFile, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenRequestVacancyByIdThenGetPageWithOneVacancy() {
        var expectedVacancy = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var expectedOptional = Optional.of(expectedVacancy);
        when(vacancyService.findById(1)).thenReturn(expectedOptional);

        var model = new ConcurrentModel();
        var view = vacancyController.getById(model, 1);
        var actualVacancy = model.getAttribute("vacancy");

        assertThat(view).isEqualTo("vacancies/one");
        assertThat(actualVacancy).isEqualTo(expectedVacancy);
    }

    @Test
    public void whenRequestVacancyByUnknownIdThenGetErrorPageWithMessage() {
        var expectedMessage = "Вакансия с указанным идентификатором не найдена";
        when(vacancyService.findById(anyInt())).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = vacancyController.getById(model, 1);
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    public void whenPostUpdateVacancyThenRedirectToVacanciesPage() throws Exception {
        var vacancy = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var vacancyArgumentCaptor = ArgumentCaptor.forClass(Vacancy.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(vacancyService.update(vacancyArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(true);

        var model = new ConcurrentModel();
        var view = vacancyController.update(vacancy, testFile, model);
        var actualVacancy = vacancyArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualVacancy).isEqualTo(vacancy);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
    }

    @Test
    public void whenRequestUpdateUnknownVacancyThenGetErrorPageWithMessage() {
        var expectedMessage = "Вакансия с указанным идентификатором не найдена";
        when(vacancyService.update(any(), any())).thenReturn(false);

        var model = new ConcurrentModel();
        var view = vacancyController.update(new Vacancy(), testFile, model);
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    public void whenRequestUpdateAndSomeExceptionThrownThenGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Some Exception");
        when(vacancyService.update(any(), any())).thenThrow(expectedException);

        var model = new ConcurrentModel();
        var view = vacancyController.update(new Vacancy(), testFile, model);
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenRequestDeleteVacancyByIdThenRedirectToVacanciesPage() {
        when(vacancyService.deleteById(anyInt())).thenReturn(true);

        var model = new ConcurrentModel();
        var view = vacancyController.delete(model, anyInt());

        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenRequestDeleteUnknownVacancyThenGetErrorPageWithMessage() {
        var expectedMessage = "Вакансия с указанным идентификатором не найдена";
        when(vacancyService.deleteById(anyInt())).thenReturn(false);

        var model = new ConcurrentModel();
        var view = vacancyController.delete(model, anyInt());
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

}