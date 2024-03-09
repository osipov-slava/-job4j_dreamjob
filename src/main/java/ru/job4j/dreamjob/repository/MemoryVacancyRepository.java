package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Vacancy;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@ThreadSafe
public class MemoryVacancyRepository implements VacancyRepository {

    private final AtomicInteger nextId = new AtomicInteger(0);

    private final Map<Integer, Vacancy> vacancies = new ConcurrentHashMap<>();

    private MemoryVacancyRepository() {
        LocalDateTime localDateTime = LocalDateTime.now();
        save(new Vacancy(0, "Intern Java Developer", "Min experience 0 years, office, $300", localDateTime, true, 1, 0));
        save(new Vacancy(0, "Junior Java Developer", "Min experience 0 years, office, $800", localDateTime, true, 2, 0));
        save(new Vacancy(0, "Junior+ Java Developer", "Min experience 1 years, office, $1500", localDateTime, false, 3, 0));
        save(new Vacancy(0, "Middle Java Developer", "Min experience 2 years, hybrid, $2500", localDateTime, false, 1, 0));
        save(new Vacancy(0, "Middle+ Java Developer", "Min experience 3 years, remote or office, $3500", localDateTime, true, 1, 0));
        save(new Vacancy(0, "Senior Java Developer", "Min experience 3 years, remote or office, $5000", localDateTime, true, 2, 0));
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId.incrementAndGet());
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public boolean deleteById(int id) {
        return vacancies.remove(id) != null;
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(vacancy.getId(), (id, oldVacancy) -> {
            return new Vacancy(
                    oldVacancy.getId(), vacancy.getTitle(), vacancy.getDescription(),
                    vacancy.getCreationDate(), vacancy.getVisible(), vacancy.getCityId(), vacancy.getFileId()
            );
        }) != null;
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