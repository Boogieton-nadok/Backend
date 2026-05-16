package com.boogieton.nadok.domain.book.service;

import com.boogieton.nadok.domain.book.dto.BookDto.*;
import com.boogieton.nadok.domain.book.entity.Book;
import com.boogieton.nadok.domain.book.exception.BookResponseCode;
import com.boogieton.nadok.domain.book.repository.BookRepository;
import com.boogieton.nadok.domain.mainstudy.entity.MainStudy;
import com.boogieton.nadok.domain.mainstudy.repository.MainStudyRepository;
import com.boogieton.nadok.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final MainStudyRepository mainStudyRepository;

    @Value("${aladin.api.key}")
    private String aladinApiKey;

    private static final String ALADIN_SEARCH_URL = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx";

    public List<BookSearchRes> searchBooks(String keyword) {
        try {
            String url = UriComponentsBuilder.fromUriString(ALADIN_SEARCH_URL)
                    .queryParam("ttbkey", aladinApiKey)
                    .queryParam("Query", keyword)
                    .queryParam("QueryType", "Title")
                    .queryParam("MaxResults", 10)
                    .queryParam("start", 1)
                    .queryParam("SearchTarget", "Book")
                    .queryParam("output", "js")
                    .queryParam("Version", "20131101")
                    .build(false)
                    .toUriString();

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("item");

            if (items == null || items.isEmpty()) {
                throw new BaseException(BookResponseCode.BOOK_SEARCH_EMPTY);
            }

            List<BookSearchRes> result = new ArrayList<>();
            for (Map<String, Object> item : items) {
                result.add(BookSearchRes.builder()
                        .title((String) item.get("title"))
                        .author((String) item.get("author"))
                        .isbn((String) item.get("isbn13"))
                        .coverUrl((String) item.get("cover"))
                        .publisher((String) item.get("publisher"))
                        .pageCount(null)
                        .bookIntro((String) item.get("description"))
                        .build());
            }
            return result;
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("알라딘 API 검색 실패: {}", e.getMessage());
            throw new BaseException(BookResponseCode.BOOK_SEARCH_FAILED);
        }
    }

    public BookDetailRes getBookDetail(String isbn, Long userId) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BaseException(BookResponseCode.BOOK_NOT_FOUND));

        Optional<MainStudy> mainStudy = mainStudyRepository.findByUserUserIdAndBookIsbn(userId, isbn);

        if (mainStudy.isPresent()) {
            MainStudy ms = mainStudy.get();
            return BookDetailRes.of(book, ms.getReadingStatus(), ms.getStartDate(), ms.getEndDate(), true);
        }
        return BookDetailRes.from(book);
    }

}
