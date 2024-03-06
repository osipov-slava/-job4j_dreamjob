package ru.job4j.dreamjob.repository;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@ThreadSafe
public class MemoryCandidateRepository implements CandidateRepository {

    private final AtomicInteger nextId = new AtomicInteger(0);

    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    private MemoryCandidateRepository() {
        LocalDateTime localDateTime = LocalDateTime.now();
        save(new Candidate(0, "Ivan Petrov", "2 years experience", localDateTime, 1, 0));
        save(new Candidate(0, "John White", "5 years experience", localDateTime, 2, 0));
        save(new Candidate(0, "James Brown", "1 years experience", localDateTime, 2, 0));
        save(new Candidate(0, "Da Li", "2 years experience", localDateTime, 3, 0));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId.incrementAndGet());
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(), (id, oldVacancy) -> {
            return new Candidate(
                    oldVacancy.getId(), candidate.getName(), candidate.getDescription(),
                    candidate.getCreationDate(), candidate.getCityId(), candidate.getFileId());
        }) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
