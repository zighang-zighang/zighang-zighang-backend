package com.github.zighang_zighang.domain.bookmark.service;

import com.github.zighang_zighang.domain.bookmark.entity.Bookmark;
import com.github.zighang_zighang.domain.bookmark.exceptions.BookmarkExceptions;
import com.github.zighang_zighang.domain.bookmark.repository.BookmarkRepository;
import com.github.zighang_zighang.domain.recruitment.dto.response.RecruitmentResponse;
import com.github.zighang_zighang.domain.recruitment.entity.RecruitmentView;
import com.github.zighang_zighang.domain.recruitment.repository.RecruitmentRepository;
import com.github.zighang_zighang.domain.recruitment.repository.RecruitmentViewRepository;
import com.github.zighang_zighang.domain.recruitment.util.aspect.RecruitmentExist;
import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.global.response.PageInfo;
import com.github.zighang_zighang.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final RecruitmentRepository recruitmentRepository;
    private final RecruitmentViewRepository recruitmentViewRepository;
    private final BookmarkRepository bookmarkRepository;

    @Cacheable(value = "bookmarks", key = "#user.id + '-' + #page + '-' + #size")
    @Transactional(readOnly = true)
    public PageResponse<RecruitmentResponse> getBookmarks(User user, Integer page, Integer size) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Bookmark> bookmarkPage = bookmarkRepository.findAllByUser(user, pageRequest);

        Set<UUID> ids = bookmarkPage.getContent().stream()
                .map(Bookmark::getRecruitmentId)
                .collect(Collectors.toSet());

        Map<UUID, Long> count = recruitmentViewRepository.findAll().stream()
                .filter(rv -> ids.contains(rv.getRecruitmentId()))
                .collect(Collectors.groupingBy(RecruitmentView::getRecruitmentId, Collectors.counting()));

        return PageResponse.of(
                bookmarkPage.stream()
                        .map(Bookmark::getRecruitmentId)
                        .map(recruitmentRepository::findById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(r -> RecruitmentResponse.from(r, count.getOrDefault(r.getId(), 0L).intValue(), true))
                        .toList(),
                PageInfo.of(size, page, bookmarkPage.getTotalElements())
        );
    }

    @CacheEvict(value = "bookmarks", allEntries = true)
    @Transactional
    @RecruitmentExist("#recruitmentId")
    public void addBookmark(User user, UUID recruitmentId) {

        if (bookmarkRepository.existsByUserAndRecruitmentId(user, recruitmentId)) {

            throw BookmarkExceptions.ALREADY_ADDED.toException();
        }

        bookmarkRepository.save(
                Bookmark.builder()
                        .user(user)
                        .recruitmentId(recruitmentId)
                        .build()
        );
    }

    @CacheEvict(value = "bookmarks", key = "#user.id + '-*'")
    @Transactional
    @RecruitmentExist("#recruitmentId")
    public void removeBookmark(User user, UUID recruitmentId) {

        bookmarkRepository.delete(
                bookmarkRepository.findByUserAndRecruitmentId(user, recruitmentId)
                        .orElseThrow(BookmarkExceptions.NOT_FOUND::toException)
        );
    }
}
