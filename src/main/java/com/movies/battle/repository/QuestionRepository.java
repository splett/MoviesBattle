package com.movies.battle.repository;

import org.springframework.data.repository.CrudRepository;

import com.movies.battle.model.Question;

public interface QuestionRepository extends CrudRepository<Question, Long>{
}
