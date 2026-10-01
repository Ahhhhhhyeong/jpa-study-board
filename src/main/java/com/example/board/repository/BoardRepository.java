package com.example.board.repository;

import org.springframework.stereotype.Repository;

import com.example.board.entity.Board;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

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

    public List<Board> findPage(int offset, int limit) {
        return em.createQuery("select b from Board b order by b.createdAt desc, b.id desc", Board.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public long count() {
        return em.createQuery("select count(b) from Board b", Long.class).getSingleResult();
    }

    public void delete(Board board) {
        em.remove(board);
    }
}
