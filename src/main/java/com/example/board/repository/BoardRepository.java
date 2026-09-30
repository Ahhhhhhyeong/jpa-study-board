package com.example.board.repository;

import org.springframework.stereotype.Repository;

import com.example.board.entity.Board;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository 
public class BoardRepository {

    @PersistenceContext 
    private EntityManager em;

    public void save(Board board) {
        em.persist(board);
    }

    public Board findById(Long id){
        return em.find(Board.class, id);
    }

    public void delete(Board board) {
        em.remove(board);
    }
}