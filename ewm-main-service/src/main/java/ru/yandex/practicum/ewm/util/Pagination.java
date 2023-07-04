package ru.yandex.practicum.ewm.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class Pagination extends PageRequest {

    public Pagination(int page, int size, String  field) {
        super(page / size, size, Sort.by(field).ascending());
    }
}
