package com.example.demo.Services;

import com.example.demo.Repositories.GenericSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GenericSearchService<T> {
    @Autowired private GenericSearch<T> abstractGenericSearchRepository;

    public List<T> search(Class<T> type, String query) {
        return abstractGenericSearchRepository.searchByFields(type, query);
    }
}
