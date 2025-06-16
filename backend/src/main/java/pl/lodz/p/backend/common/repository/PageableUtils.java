package pl.lodz.p.backend.common.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import pl.lodz.p.backend.common.controller.PageableRequest;


public class PageableUtils {
    public static  Pageable convertToSpringPageable(PageableRequest pageableRequest) {
        int page = pageableRequest.getPage() != null ? pageableRequest.getPage() : 0;
        int size = pageableRequest.getSize() != null ? pageableRequest.getSize() : 10;
        Sort sort = Sort.unsorted(); // Domyślnie brak sortowania

        if (pageableRequest.getSortField() != null && !pageableRequest.getSortField().isEmpty()) {
            String sortField = pageableRequest.getSortField();
            // Konwersja String na Sort.Direction
            Sort.Direction sortDirection = Sort.Direction.fromString(pageableRequest.getSortDirection().name());

            // KLUCZOWA LOGIKA: unikamy LOWER() dla typów niefunkcjonujących z nim
            if ("bookedDate".equals(sortField) || "finishedAppointmentDate".equals(sortField) || "status".equals(sortField)) {
                // Dla dat i statusu (enum), sortujemy bezpośrednio po polu
                sort = Sort.by(sortDirection, sortField);
            } else {
                // Dla innych pól (zakładamy, że to stringi, np. imię/nazwisko fryzjera),
                // pozwalamy Spring Data JPA na domyślne zachowanie.
                // Jeśli chcesz case-insensitive sortowanie dla stringów tutaj,
                // wymagałoby to bardziej zaawansowanych rozwiązań (np. custom SQL function).
                // Ale dla błędu 'lower(bytea)' to jest wystarczające.
                sort = Sort.by(sortDirection, sortField);
            }
        }
        // Pamiętaj, że backend oczekuje 1-indeksowanej strony, a frontend wysyła 0-indeksowaną (page + 1)
        // Więc tutaj PageRequest.of oczekuje z powrotem 0-indeksowanej strony.
        return PageRequest.of(page - 1 , size, sort);
    }
}
