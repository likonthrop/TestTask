package ru.anisimov.testtask.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * @author Anton Anisimov
 * @version 1.0
 */

@Entity
@Table(name = "table_statistic", schema = "public", catalog = "web_statistic_db")
public class EntityStatistic {
    private long id;
    private String userId;
    private String pageId;
    private Timestamp visitDate;

    public EntityStatistic(String userId, String pageId) {
        this.userId = userId;
        this.pageId = pageId;
        visitDate = new Timestamp(System.currentTimeMillis());
    }

    public EntityStatistic() {
    }

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "user_id", nullable = false, length = -1)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "page_id", nullable = false, length = -1)
    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    @Basic
    @Column(name = "visit_date", nullable = false)
    public Timestamp getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Timestamp visitDate) {
        this.visitDate = visitDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityStatistic statistic = (EntityStatistic) o;
        return id == statistic.id &&
                visitDate == statistic.visitDate &&
                Objects.equals(userId, statistic.userId) &&
                Objects.equals(pageId, statistic.pageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, pageId, visitDate);
    }
}
