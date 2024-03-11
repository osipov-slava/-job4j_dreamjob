package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class IndexControllerTest {

    @Test
    public void whenRequestIndexThenRedirectIndexPage() {
        var indexController = new IndexController();
        var view = indexController.getIndex();

        assertThat(view).isEqualTo("index");
    }
}
