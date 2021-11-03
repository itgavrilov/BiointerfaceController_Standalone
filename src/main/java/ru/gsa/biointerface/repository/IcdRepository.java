package ru.gsa.biointerface.repository;

import ru.gsa.biointerface.domain.entity.Icd;

import java.util.List;
import java.util.Optional;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
public interface IcdRepository {
    List<Icd> findAll() throws Exception;

    Optional<Icd> findById(Integer id) throws Exception;

    Icd save(Icd icd) throws Exception;

    void delete(Icd icd) throws Exception;

    boolean existsById(Integer id) throws Exception;
}
