package com.BE.model.entity;

import com.BE.enums.NodeType;
import com.BE.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Entity representing a node in the lesson plan tree structure
 */
@Entity
@Table(name = "lesson_plan_nodes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonPlanNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "lesson_plan_template_id", nullable = false)
    Long lessonPlanTemplateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    LessonPlanNode parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    @Builder.Default
    List<LessonPlanNode> children = new ArrayList<>();

    @Column(nullable = false)
    String title;

    @Column(columnDefinition = "TEXT")
    String content;

    String fieldType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    NodeType type;

    @Column(name = "order_index", nullable = false)
    Integer orderIndex;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    Map<String, Object> metadata;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    Status status = Status.ACTIVE;

    /**
     * Helper method to add a child node
     */
    public void addChild(LessonPlanNode child) {
        children.add(child);
        child.setParent(this);
    }

    /**
     * Helper method to remove a child node
     */
    public void removeChild(LessonPlanNode child) {
        children.remove(child);
        child.setParent(null);
    }

    /**
     * Check if this node is a root node (has no parent)
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * Check if this node is a leaf node (has no children)
     */
    public boolean isLeaf() {
        return children.isEmpty();
    }
}
