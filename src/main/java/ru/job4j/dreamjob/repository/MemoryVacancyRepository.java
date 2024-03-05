package ru.job4j.dreamjob.repository;

import ru.job4j.dreamjob.model.Vacancy;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MemoryVacancyRepository implements VacancyRepository {

    private static final MemoryVacancyRepository INSTANCE = new MemoryVacancyRepository();

    private int nextId = 1;

    private final Map<Integer, Vacancy> vacancies = new HashMap<>();

    private MemoryVacancyRepository() {
        LocalDateTime localDateTime = LocalDateTime.now();
        save(new Vacancy(0, "Intern Java Developer", "Min experience 0 years, office, $300", localDateTime));
        save(new Vacancy(0, "Junior Java Developer", "Min experience 0 years, office, $800", localDateTime));
        save(new Vacancy(0, "Junior+ Java Developer", "Min experience 1 years, office, $1500", localDateTime));
        save(new Vacancy(0, "Middle Java Developer", "Min experience 2 years, hybrid, $2500", localDateTime));
        save(new Vacancy(0, "Middle+ Java Developer", "Min experience 3 years, remote or office, $3500", localDateTime));
        save(new Vacancy(0, "Senior Java Developer", "Min experience 3 years, remote or office, $5000", localDateTime));
    }

    public static MemoryVacancyRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId++);
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public boolean deleteById(int id) {
        return vacancies.remove(id) != null;
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(vacancy.getId(),
                (id, oldVacancy) -> new Vacancy(oldVacancy.getId(), vacancy.getTitle(),
                                                vacancy.getDescription(), vacancy.getCreationDate())) != null;
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return Optional.ofNullable(vacancies.get(id));
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancies.values();
    }
}