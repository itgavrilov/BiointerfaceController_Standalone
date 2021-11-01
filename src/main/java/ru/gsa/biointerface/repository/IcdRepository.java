package ru.gsa.biointerface.repository;

import ru.gsa.biointerface.domain.entity.Icd;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
public interface IcdRepository {
    void insert(Icd icd) throws Exception;

    Icd getById(Long id) throws Exception;

    void update(Icd icd) throws Exception;

    void delete(Icd icd) throws Exception;

    List<Icd> getAll() throws Exception;
}
