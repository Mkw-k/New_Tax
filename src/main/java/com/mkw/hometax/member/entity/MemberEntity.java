package com.mkw.hometax.member.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
* MemberEntity
* <pre>
* 회원에 대한 정보를 저장 및 관리하는 테이블 엔터티
* </pre>
* @author : K
* @class : MemberEntity
* @date : 2022-05-28
* <pre>
* No Date        Time       Author  Desc
* 1  2022-05-28  오전 10:13  K       최초작성
* </pre>
*/
@Entity
@Table(name = "HOME_MEMBER")
@Builder @AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "myId")
public class MemberEntity implements Serializable {
    @Column(name = "HOMESEQ")
    private String homeSeq;
    @Column(name = "NAME")
    private String name;
    @Column(name = "CLASSIFY")
    private String classify;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "PHONE")
    private String phone;
    @Column(name = "PWD")
    private String pwd;
    @Column(name = "ISSALE")
    private String isSale;
    @Id
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

}
