package com.eduplatform.service.impl;

import com.eduplatform.service.StudentService;
import com.eduplatform.domain.Student;
import com.eduplatform.repository.StudentRepository;
import com.eduplatform.repository.search.StudentSearchRepository;
import com.eduplatform.service.dto.StudentDTO;
import com.eduplatform.service.mapper.StudentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Student.
 */
@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final StudentRepository studentRepository;

    private final StudentMapper studentMapper;

    private final StudentSearchRepository studentSearchRepository;

    public StudentServiceImpl(StudentRepository studentRepository, StudentMapper studentMapper, StudentSearchRepository studentSearchRepository) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
        this.studentSearchRepository = studentSearchRepository;
    }

    /**
     * Save a student.
     *
     * @param studentDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public StudentDTO save(StudentDTO studentDTO) {
        log.debug("Request to save Student : {}", studentDTO);
        Student student = studentMapper.toEntity(studentDTO);
        student = studentRepository.save(student);
        StudentDTO result = studentMapper.toDto(student);
        studentSearchRepository.save(student);
        return result;
    }

    /**
     * Get all the students.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> findAll() {
        log.debug("Request to get all Students");
        return studentRepository.findAll().stream()
            .map(studentMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one student by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public StudentDTO findOne(Long id) {
        log.debug("Request to get Student : {}", id);
        Student student = studentRepository.findOne(id);
        return studentMapper.toDto(student);
    }

    /**
     * Delete the student by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Student : {}", id);
        studentRepository.delete(id);
        studentSearchRepository.delete(id);
    }

    /**
     * Search for the student corresponding to the query.
     *
     * @param query the query of the search
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> search(String query) {
        log.debug("Request to search Students for query {}", query);
        return StreamSupport
            .stream(studentSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(studentMapper::toDto)
            .collect(Collectors.toList());
    }
}
