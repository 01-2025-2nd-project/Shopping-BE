package com.github.farmplus.repository.role;

import jakarta.persistence.*;
import lombok.*;

@Getter

@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of="roleId")
@Builder
@Entity
@Table(name="role")
public class Role{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="role_id")
    private Integer roleId;
    @Column(name="role_name",length=20,nullable = false)
    private String roleName;
}

