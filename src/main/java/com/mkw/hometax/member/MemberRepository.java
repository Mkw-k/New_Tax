package com.mkw.hometax.member;

import com.mkw.hometax.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Deprecated
@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, String> {

}
