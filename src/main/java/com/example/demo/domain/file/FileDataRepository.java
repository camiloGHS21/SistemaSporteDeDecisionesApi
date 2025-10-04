package com.example.demo.domain.file;

import com.example.demo.domain.file.FileData;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;


@Repository
public interface FileDataRepository extends JpaRepository<FileData, Long> {

    @Override
    long count();

    @Override
    void delete(FileData entity);

    @Override
    void deleteAll(Iterable<? extends FileData> entities);

    @Override
    void deleteAllById(Iterable<? extends Long> ids);

    @Override
    void deleteAllByIdInBatch(Iterable<Long> ids);

    @Override
    void deleteAllInBatch();

    @Override
    void deleteById(Long id);

    @Override
    void deleteInBatch(Iterable<FileData> entities);

    @Override
    <S extends FileData> boolean exists(Example<S> example);

    @Override
    boolean existsById(Long id);

    @Override
    List<FileData> findAll();

    @Override
    <S extends FileData> List<S> findAll(Example<S> example);

    @Override
    <S extends FileData> Page<S> findAll(Example<S> example, Pageable pageable);

    @Override
    List<FileData> findAllById(Iterable<Long> ids);

    @Override
    <S extends FileData> Optional<S> findOne(Example<S> example);


    @Override
    void flush();

    @Override
    @Deprecated
    FileData getOne(Long id);

    @Override
    FileData getById(Long id);

    @Override
    FileData getReferenceById(Long id);

    @Override
    <S extends FileData> S save(S entity);

    @Override
    <S extends FileData> List<S> saveAll(Iterable<S> entities);

    @Override
    <S extends FileData> List<S> saveAllAndFlush(Iterable<S> entities);

    @Override
    <S extends FileData> S saveAndFlush(S entity);

    boolean existsByFileName(String fileName);

    boolean existsByFileHash(String fileHash);
}
