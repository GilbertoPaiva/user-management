package com.petconnect.domain.tutor.port;

import com.petconnect.domain.tutor.entity.Tutor;
import java.util.List;

public interface TutorRepositoryPort {
    Tutor save(Tutor tutor);
    List<Tutor> findAll();
} 