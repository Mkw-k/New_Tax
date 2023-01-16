package com.mkw.hometax.Accounts;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity(name = "HOME_MEMBER")
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
@ToString
public class Account {

    @Id @GeneratedValue
    private Integer id;

    @Column(unique = true, name = "EMAIL")
    private String email;
    @Column(name = "PWD")
    private String password;
    @Column(name = "NAME")
    private String name;
    @Column(name = "CLASSIFY")
    private String classify;
    @Column(name = "PHONE")
    private String phone;
    @Column(name = "ISSALE")
    private String isSale;
    @Column(name = "MYID")
    private String myId;
    @Column(name = "AUTH")
    private String auth;
    @Column(name = "FILENAME")
    private String fileName;
    @Column(name = "NEWFILENAME")
    private String newFileName;
    @Column(name = "DEL")
    private String del;
    @Column(name = "INPT_DTTM")
    @CreationTimestamp
    private LocalDateTime inptDttm;
    @Column(name = "UPDT_DTTM")
    @UpdateTimestamp
    private LocalDateTime updtDttm;
    @Transient
    private boolean isSaleBool;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<AccuontRole> roles;

    public void update() {
        if (this.isSale.equals("1")) {
            this.isSaleBool = true;
        } else {
            this.isSaleBool = false;
        }
    }

    @PrePersist
    public void prePersist(){
        this.del = this.del == null ? "N" : this.del;
    }
}
