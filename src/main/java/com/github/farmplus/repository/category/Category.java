package com.github.farmplus.repository.category;

import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of="categoryId")
@Builder
@Entity
@Table(name="category")
public class Category {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="category_id")
    private Integer categoryId;
    @Column(name="category_name", length = 100, nullable = false)
    private String categoryName;


}
