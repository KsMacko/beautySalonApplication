package com.example.demo.Repositories;

import com.example.demo.Entities.Master;
import com.example.demo.Entities.SalonService;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GenericSearch<T> {

    @PersistenceContext
    protected EntityManager entityManager;

    public List<T> searchByFields(Class<T> type, String query) {
        Session session = entityManager.unwrap(Session.class);
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = cb.createQuery(type);
        Root<T> root = criteriaQuery.from(type);
        List<Predicate> predicates = new ArrayList<>();

        for (Field field : type.getDeclaredFields()) {
            if (isSearchableField(field)) {
                predicates.add(cb.like(root.get(field.getName()), "%" + query + "%"));
            }
        }

        if (type.equals(Master.class)) {
            Join<Master, SalonService> serviceJoin = root.join("service", JoinType.LEFT);
            predicates.add(cb.like(serviceJoin.get("name"), "%" + query + "%"));
        }

        criteriaQuery.select(root).where(cb.or(predicates.toArray(new Predicate[0])));
        return session.createQuery(criteriaQuery).getResultList();
    }

    private boolean isSearchableField(Field field) {
        return field.getType().equals(String.class) && !field.isAnnotationPresent(Id.class)
                && !field.getName().equals("hashPassword")
                && !field.isAnnotationPresent(ManyToOne.class)
                && !field.isAnnotationPresent(OneToMany.class)
                && !field.isAnnotationPresent(OneToOne.class);
    }
}
