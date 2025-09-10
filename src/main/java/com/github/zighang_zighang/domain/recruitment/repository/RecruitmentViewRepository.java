package com.github.zighang_zighang.domain.recruitment.repository;

import com.github.zighang_zighang.domain.recruitment.entity.RecruitmentView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RecruitmentViewRepository extends JpaRepository<RecruitmentView, UUID> {

    @Query(value = """
            SELECT recruitment_id
            FROM (
                SELECT
                    rv.recruitment_id,
                    (COALESCE(views.view_count, 0) * :viewWeight +
                     COALESCE(bookmarks.bookmark_count, 0) * :bookmarkWeight +
                     COALESCE(applications.application_count, 0) * :applicationWeight) AS popularity_score
                FROM recruitment_view rv
                LEFT JOIN (
                    SELECT rv2.recruitment_id, COUNT(*) AS view_count
                    FROM recruitment_view rv2
                    WHERE rv2.created_at >= :viewCutoff
                    GROUP BY rv2.recruitment_id
                ) views ON rv.recruitment_id = views.recruitment_id
                LEFT JOIN (
                    SELECT b.recruitment_id, COUNT(*) AS bookmark_count
                    FROM bookmark b
                    WHERE b.created_at >= :bookmarkCutoff
                    GROUP BY b.recruitment_id
                ) bookmarks ON rv.recruitment_id = bookmarks.recruitment_id
                LEFT JOIN (
                    SELECT ra.recruitment_id, COUNT(*) AS application_count
                    FROM recruitment_application ra
                    WHERE ra.created_at >= :applicationCutoff
                    GROUP BY ra.recruitment_id
                ) applications ON rv.recruitment_id = applications.recruitment_id
                GROUP BY rv.recruitment_id
            ) ranked
            ORDER BY popularity_score DESC
            """, nativeQuery = true)
    List<byte[]> findPopularRecruitmentIds(
            @Param("viewCutoff") LocalDateTime viewCutoff,
            @Param("bookmarkCutoff") LocalDateTime bookmarkCutoff,
            @Param("applicationCutoff") LocalDateTime applicationCutoff,
            @Param("viewWeight") double viewWeight,
            @Param("bookmarkWeight") double bookmarkWeight,
            @Param("applicationWeight") double applicationWeight
    );
}
