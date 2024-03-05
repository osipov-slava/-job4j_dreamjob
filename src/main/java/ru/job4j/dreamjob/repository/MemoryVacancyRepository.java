package ru.job4j.dreamjob.repository;

import net.jcip.annotations.GuardedBy;
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

    @GuardedBy("this")
    private final AtomicInteger nextId = new AtomicInteger(0);

    @GuardedBy("this")
    private final Map<Integer, Vacancy> vacancies = new ConcurrentHashMap<>();

    private MemoryVacancyRepository() {
        LocalDateTime localDateTime = LocalDateTime.now();
        save(new Vacancy(0, "Intern Java Developer", "Min experience 0 years, office, $300", localDateTime));
        save(new Vacancy(0, "Junior Java Developer", "Min experience 0 years, office, $800", localDateTime));
        save(new Vacancy(0, "Junior+ Java Developer", "Min experience 1 years, office, $1500", localDateTime));
        save(new Vacancy(0, "Middle Java Developer", "Min experience 2 years, hybrid, $2500", localDateTime));
        save(new Vacancy(0, "Middle+ Java Developer", "Min experience 3 years, remote or office, $3500", localDateTime));
        save(new Vacancy(0, "Senior Java Developer", "Min experience 3 years, remote or office, $5000", localDateTime));
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
        return vacancies.computeIfPresent(vacancy.getId(),
                (id, oldVacancy) -> new Vacancy(oldVacancy.getId(), vacancy.getTitle(), vacancy.getDescription(), vacancy.getCreationDate())) != null;
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