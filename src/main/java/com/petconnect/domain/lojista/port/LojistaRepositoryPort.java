package com.petconnect.domain.lojista.port;

import com.petconnect.domain.lojista.entity.Lojista;
import java.util.List;

public interface LojistaRepositoryPort {
    Lojista save(Lojista lojista);
    List<Lojista> findAll();
}
